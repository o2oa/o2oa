package com.x.general.assemble.control.tools;

import com.x.general.core.entity.Invoice;
import com.x.general.core.entity.InvoiceDetail;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class PdfRegularInvoiceTools {

    private PdfRegularInvoiceTools(){}

    /**
     * 带密码区发票解析
     * @param allText 发票内容
     * @param pageWidth 发票页宽
     * @param doc 发票pdf文档
     * @param firstPage 发票第一页
     * @param invoice 发票对象
     */
    public static void getRegularInvoice(String allText, int pageWidth, PDDocument doc,
            PDPage firstPage, Invoice invoice) throws IOException {
        extractInvoiceTitle(invoice, allText);
        extractAmounts(invoice, allText);
        extractTotalAmount(invoice, allText);
        extractPersonnelInfo(invoice, allText);
        determineInvoiceType(invoice, allText);

        PDFKeyWordPosition kwp = new PDFKeyWordPosition();
        Map<String, List<Position>> positionListMap = kwp
                .getCoordinate(Arrays.asList("机器编号", "税率", "价税合计", "合计", "开票日期",
                        "规格型号", "车牌号", "单价", "开户行及账号", "密", "码", "区"), doc);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDFTextStripperByArea detailStripper = new PDFTextStripperByArea();
        detailStripper.setSortByPosition(true);
        setRegion(positionListMap, stripper, detailStripper, pageWidth);
        stripper.extractRegions(firstPage);
        detailStripper.extractRegions(firstPage);
        doc.close();

        processBuyerAndSeller(stripper, invoice);

        processDetailList(detailStripper, invoice);
    }

    private static void extractInvoiceTitle(Invoice invoice, String allText) {
        String reg = "机器编号:(?<machineNumber>\\d{12})|发票代码:(?<code>\\d*)|发票号码:(?<number>\\d*)|:(?<date>\\d{4}年\\d{2}月\\d{2}日)"
                        + "|校验码:(?<checksum>\\d{20}|\\S{4,})";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        while (matcher.find()) {
            if (matcher.group("code") != null) {
                invoice.setCode(matcher.group("code"));
            } else if (matcher.group("number") != null) {
                invoice.setNumber(matcher.group("number"));
            } else if (matcher.group("date") != null) {
                invoice.setDate(matcher.group("date"));
            }
        }
    }

    private static void extractAmounts(Invoice invoice, String allText) {
        String reg = "合计¥?(?<amount>\\d+(\\.\\d*)?)(?:¥?(?<taxAmount>\\S*)|\\*+)\\s";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        if (matcher.find()) {
            try {
                invoice.setAmount(Double.valueOf(matcher.group("amount")));
            } catch (Exception e) {
                // Log the exception
            }
            try {
                invoice.setTaxAmount(Double.valueOf(matcher.group("taxAmount")));
            } catch (Exception e) {
                invoice.setTaxAmount(0D);
            }
        }
    }

    private static void extractTotalAmount(Invoice invoice, String allText) {
        String reg = "价税合计\\((大写)\\)(?<amountString>\\S*)\\((小写)\\)¥?(?<amount>\\S*)\\s";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        if (matcher.find()) {
            invoice.setTotalAmountString(matcher.group("amountString"));
            try {
                invoice.setTotalAmount(Double.valueOf(matcher.group("amount")));
            } catch (NumberFormatException e) {
                invoice.setTotalAmount(0D);
            }
        }
    }

    private static void extractPersonnelInfo(Invoice invoice, String allText) {
        String reg = "收款人:(?<payee>\\S*)复核:(?<reviewer>\\S*)开票人:(?<drawer>\\S*)销售方";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        if (matcher.find()) {
            invoice.setDrawer(matcher.group("drawer"));
        }
    }

    private static void determineInvoiceType(Invoice invoice, String allText) {
        if (allText.contains("通行费") && allText.contains("车牌号")) {
            invoice.setType("通行费");
        }
        Pattern type00Pattern = Pattern.compile("(?<p>\\S*)通发票");
        Matcher m00 = type00Pattern.matcher(allText);
        if (m00.find()) {
            String title = m00.group("p").replaceAll("(?:国|统|一|发|票|监|制)", "") + "通发票";
            invoice.setTitle(title.contains("增值税") ? title : "增值税普通发票");
            if (invoice.getType() == null) {
                invoice.setType("增值税普通发票");
            }
        } else {
            Pattern type01Pattern = Pattern.compile("(?<p>\\S*)用发票");
            Matcher m01 = type01Pattern.matcher(allText);
            if (m01.find()) {
                String title = m01.group("p").replaceAll("(?:国|统|一|发|票|监|制)", "") + "用发票";
                invoice.setTitle(title.contains("增值税") ? title : "增值税专用发票");
                if (invoice.getType() == null) {
                    invoice.setType("增值税专用发票");
                }
            }
        }
    }

    private static void setRegion(Map<String, List<Position>> positionListMap,
            PDFTextStripperByArea stripper, PDFTextStripperByArea detailStripper, int pageWidth) {
        Position machineNumber;
        if (!positionListMap.get("机器编号").isEmpty()) {
            machineNumber = positionListMap.get("机器编号").get(0);
        } else {
            machineNumber = positionListMap.get("开票日期").get(0);
            machineNumber.setY(machineNumber.getY() + 30);
        }
        Position taxRate = positionListMap.get("税率").get(0);
        Position totalAmount = positionListMap.get("价税合计").get(0);
        Position amount = positionListMap.get("合计").get(0);
        Position model = null;
        if (!positionListMap.get("规格型号").isEmpty()) {
            model = positionListMap.get("规格型号").get(0);
        } else if (!positionListMap.get("车牌号").isEmpty()) {
            model = positionListMap.get("车牌号").get(0);
            model.setX(model.getX() - 15);
        } else {
            model = positionListMap.get("单价").get(0);
        }

        List<Position> account = positionListMap.get("开户行及账号");
        Position buyer;
        Position seller;
        if (account.size() < 2) {
            buyer = new Position(51, 122);
            seller = new Position(51, 341);
        } else {
            buyer = account.get(0);
            seller = account.get(1);
        }

        int maqX = 370;
        List<Position> miList = positionListMap.get("密");
        List<Position> maList = positionListMap.get("码");
        List<Position> quList = positionListMap.get("区");
        for (Position mi : miList) {
            float x1 = mi.getX();
            for (Position ma : maList) {
                float x2 = ma.getX();
                if (Math.abs(x1 - x2) < 5) {
                    for (Position qu : quList) {
                        float x3 = qu.getX();
                        if (Math.abs(x2 - x3) < 5) {
                            maqX = Math.round((x1 + x2 + x3) / 3);
                        }
                    }
                }
            }
        }
        setDetailRegions(detailStripper, stripper, model, taxRate, amount, pageWidth);
        setPasswordRegion(stripper, maqX, machineNumber, taxRate, pageWidth);
        setBuyerSellerRegions(stripper, buyer, seller, machineNumber, totalAmount, maqX);
    }

    private static void setDetailRegions(PDFTextStripperByArea detailStripper,
            PDFTextStripperByArea stripper, Position model, Position taxRate, Position amount,
            int pageWidth) {
        int x = Math.round(model.getX()) - 13;
        int y = Math.round(taxRate.getY()) + 5;
        int h = Math.round(amount.getY()) - Math.round(taxRate.getY()) - 25;
        detailStripper.addRegion("detail", new Rectangle(0, y, pageWidth, h));
        stripper.addRegion("detailName", new Rectangle(0, y, x, h));
        stripper.addRegion("detailPrice", new Rectangle(x, y, pageWidth, h));
    }

    private static void setPasswordRegion(PDFTextStripperByArea stripper, int maqX,
            Position machineNumber, Position taxRate, int pageWidth) {
        int x = maqX + 10;
        int y = Math.round(machineNumber.getY()) + 10;
        int w = pageWidth - maqX - 10;
        int h = Math.round(taxRate.getY() - 5) - y;
        stripper.addRegion("password", new Rectangle(x, y, w, h));
    }

    private static void setBuyerSellerRegions(PDFTextStripperByArea stripper, Position buyer,
            Position seller, Position machineNumber, Position totalAmount, int maqX) {
        setBuyerOrSellerRegion(stripper, buyer, machineNumber, maqX, "buyer");
        setBuyerOrSellerRegion(stripper, seller, totalAmount, maqX, "seller");
    }

    private static void setBuyerOrSellerRegion(PDFTextStripperByArea stripper, Position reference,
            Position base, int maqX, String regionName) {
        int x = Math.round(reference.getX()) - 15;
        int y = Math.round(base.getY()) + 10;
        int w = maqX - x - 5;
        int h = Math.round(reference.getY()) - y + 20;
        stripper.addRegion(regionName, new Rectangle(x, y, w, h));
    }

    private static void processBuyerAndSeller(PDFTextStripperByArea stripper, Invoice invoice) {
        String reg = "名称:(?<name>\\S*)|纳税人识别号:(?<code>\\S*)|地址、电话:(?<address>\\S*)|开户行及账号:(?<account>\\S*)|电子支付标识:(?<account2>\\S*)";
        String buyer = replace(stripper.getTextForRegion("buyer"));
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(buyer);
        while (matcher.find()) {
            if (matcher.group("name") != null) {
                invoice.setBuyerName(matcher.group("name"));
            } else if (matcher.group("code") != null) {
                invoice.setBuyerCode(matcher.group("code"));
            } else if (matcher.group("address") != null) {
                invoice.setBuyerAddress(matcher.group("address"));
            } else if (matcher.group("account") != null) {
                invoice.setBuyerAccount(matcher.group("account"));
            } else if (matcher.group("account2") != null) {
                invoice.setBuyerAccount(matcher.group("account2"));
            }
        }

        String seller = replace(stripper.getTextForRegion("seller"));
        pattern = Pattern.compile(reg);
        matcher = pattern.matcher(seller);
        while (matcher.find()) {
            if (matcher.group("name") != null) {
                invoice.setSellerName(matcher.group("name"));
            } else if (matcher.group("code") != null) {
                invoice.setSellerCode(matcher.group("code"));
            } else if (matcher.group("address") != null) {
                invoice.setSellerAddress(matcher.group("address"));
            } else if (matcher.group("account") != null) {
                invoice.setSellerAccount(matcher.group("account"));
            }
        }
    }

    private static void processDetailList(PDFTextStripperByArea detailStripper, Invoice invoice) {
        List<String> skipList = new ArrayList<>();
        List<InvoiceDetail> detailList = new ArrayList<>();
        String[] detailPriceStringArray = detailStripper.getTextForRegion("detail")
                .replace("　", " ").replace(" ", " ")
                .replace("\r", "").split("\\n");
        for (String detailString : detailPriceStringArray) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setName("");
            String[] itemArray = StringUtils.split(detailString, " ");
            if (2 == itemArray.length) {
                detail.setAmount(Double.valueOf(itemArray[0]));
                detail.setTaxAmount(Double.valueOf(itemArray[1]));
                detailList.add(detail);
            } else if (2 < itemArray.length) {
                detail.setAmount(Double.valueOf(itemArray[itemArray.length - 3]));
                String taxRate = itemArray[itemArray.length - 2];
                if (taxRate.indexOf("免税") > 0 || taxRate.indexOf("不征税") > 0
                        || taxRate.indexOf("出口零税率") > 0
                        || taxRate.indexOf("普通零税率") > 0 || taxRate.indexOf("%") < 0) {
                    detail.setTaxRate(0D);
                    detail.setTaxAmount(0D);
                } else {
                    detail.setTaxRate(Double.parseDouble(taxRate.replace("%", "")));
                    detail.setTaxAmount(Double.valueOf(itemArray[itemArray.length - 1]));
                }
                if (itemArray.length > 3) {
                    detail.setName(itemArray[0]);
                }
                if (itemArray.length > 4) {
                    for (int j = 1; j < itemArray.length - 3; j++) {
                        if (itemArray[j].matches("^(-?\\d+)(\\.\\d+)?$")) {
                            if (null == detail.getCount()) {
                                detail.setCount(Double.valueOf(itemArray[j]));
                            } else {
                                detail.setPrice(Double.valueOf(itemArray[j]));
                            }
                        } else {
                            if (itemArray.length >= j + 1 && !itemArray[j + 1].matches(
                                    "^(-?\\d+)(\\.\\d+)?$")) {
                                detail.setUnit(itemArray[j + 1]);
                                detail.setModel(itemArray[j]);
                                j++;
                            } else if (itemArray[j].length() > 2) {
                                detail.setModel(itemArray[j]);
                            } else {
                                detail.setUnit(itemArray[j]);
                            }
                        }
                    }
                }
                detailList.add(detail);
            } else {
                skipList.add(detailString);
            }
        }
        invoice.setDetailList(detailList);
    }

    public static String replace(String str) {
        return str.replace(" ", "").replace("　", "").replace("：", ":").replace(" ", "");
    }
}
