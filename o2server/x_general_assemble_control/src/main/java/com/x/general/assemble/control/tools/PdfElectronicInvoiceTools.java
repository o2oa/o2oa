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


public class PdfElectronicInvoiceTools {

    private PdfElectronicInvoiceTools(){}

    /**
     * 电子发票解析
     *
     * @param allText
     * @param pageWidth
     * @param doc
     * @param firstPage
     * @return
     * @throws IOException
     */
    public static Invoice getFullElectronicInvoice(String allText, int pageWidth, PDDocument doc,
            PDPage firstPage, Invoice invoice) throws IOException {
        extractInvoiceTitle(invoice, allText);
        extractTaxpayerCodes(invoice, allText);
        extractDrawer(invoice, allText);
        extractAmounts(invoice, allText);
        extractTotalAmount(invoice, allText);
        extractInvoiceType(invoice, allText);

        try (PDDocument document = doc) {
            PDFKeyWordPosition kwp = new PDFKeyWordPosition();
            Map<String, List<Position>> positionListMap = kwp.getCoordinate(
                    Arrays.asList("机器编号", "税率", "价税合计", "合计", "开票日期", "规格型号",
                            "车牌号", "单价", "开户行及账号", "密", "码", "区"), document);

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            PDFTextStripperByArea detailStripper = new PDFTextStripperByArea();
            detailStripper.setSortByPosition(true);

            setupRegions(stripper, detailStripper, positionListMap, pageWidth);

            stripper.extractRegions(firstPage);
            detailStripper.extractRegions(firstPage);

            extractDetails(invoice, detailStripper.getTextForRegion("detail"));
        }

        return invoice;
    }

    private static void extractInvoiceTitle(Invoice invoice, String allText) {
        String reg = "发票号码:(?<number>\\d{20})|:(?<date>\\d{4}年\\d{2}月\\d{2}日)|[购买]名称:(?<buyerName>[a-zA-Z0-9()\\u4e00-\\u9fa5]+公司)|[销售]名称:(?<sellerName>[a-zA-Z0-9()\\u4e00-\\u9fa5]+公司)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        while (matcher.find()) {
            if (matcher.group("number") != null) {
                invoice.setNumber(matcher.group("number"));
            } else if (matcher.group("date") != null) {
                invoice.setDate(matcher.group("date"));
            } else if (matcher.group("buyerName") != null) {
                invoice.setBuyerName(matcher.group("buyerName"));
            } else if (matcher.group("sellerName") != null) {
                invoice.setSellerName(matcher.group("sellerName"));
            }
        }
    }

    private static void extractTaxpayerCodes(Invoice invoice, String allText) {
        String reg = "纳税人识别号:([\\dA-Z]{18})";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        int i = 0;
        while (matcher.find()) {
            if (i == 0) {
                invoice.setBuyerCode(matcher.group(1));
                i++;
            } else {
                invoice.setSellerCode(matcher.group(1));
            }
        }
    }

    private static void extractDrawer(Invoice invoice, String allText) {
        String reg = "开票人:(?<drawer>\\S*)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        if (matcher.find()) {
            invoice.setDrawer(matcher.group("drawer"));
        }
    }

    private static void extractAmounts(Invoice invoice, String allText) {
        String reg = "合计¥(?<amount>\\d+(\\.\\d*)?)¥(?<taxAmount>\\d+(\\.\\d*)?)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        if (matcher.find()) {
            try {
                invoice.setAmount(Double.valueOf(matcher.group("amount")));
            } catch (NumberFormatException e) {
                // Log the exception
            }
            try {
                invoice.setTaxAmount(Double.valueOf(matcher.group("taxAmount")));
            } catch (NumberFormatException e) {
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

    private static void extractInvoiceType(Invoice invoice, String allText) {
        Pattern type00Pattern = Pattern.compile("\\((.*发票?)\\)");
        Matcher m00 = type00Pattern.matcher(allText);
        if (m00.find()) {
            invoice.setTitle("电子发票（" + m00.group(1) + "）");
            invoice.setType(m00.group(1));
        } else if (allText.contains("专用发票")) {
            invoice.setTitle("电子发票（增值税专用发票）");
            invoice.setType("专用发票");
        } else {
            invoice.setTitle("电子发票（普通发票）");
            invoice.setType("普通发票");
        }
    }

    private static void setupRegions(PDFTextStripperByArea stripper,
            PDFTextStripperByArea detailStripper, Map<String, List<Position>> positionListMap,
            int pageWidth) {
        Position taxRate = positionListMap.get("税率").get(0);
//        Position totalAmount = positionListMap.get("价税合计").get(0);
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

        int x = Math.round(model.getX()) - 13;
        int y = Math.round(taxRate.getY()) + 5;
        int h = Math.round(amount.getY()) - Math.round(taxRate.getY()) - 25;

        detailStripper.addRegion("detail", new Rectangle(0, y, pageWidth, h));
        stripper.addRegion("detailName", new Rectangle(0, y, x, h));
        stripper.addRegion("detailPrice", new Rectangle(x, y, pageWidth, h));
    }

    private static void extractDetails(Invoice invoice, String detailText) {
        List<String> skipList = new ArrayList<>();
        List<InvoiceDetail> detailList = new ArrayList<>();
        String[] detailPriceStringArray = detailText.replace("　", " ").replace(" ", " ")
                .replace("\r", "").split("\\n");

        for (String detailString : detailPriceStringArray) {
            if (detailString.contains("合") && detailString.contains("计")) {
                break;
            }
            InvoiceDetail detail = new InvoiceDetail();
            detail.setName("");
            String[] itemArray = StringUtils.split(detailString, " ");
            if (itemArray.length == 2) {
                if (invoice.getAmount() == null) {
                    try {
                        if (itemArray[0].contains("¥")) {
                            invoice.setAmount(Double.valueOf(itemArray[0].replace("¥", "")));
                        }
                        if (itemArray[1].contains("¥")) {
                            invoice.setTaxAmount(Double.valueOf(itemArray[1].replace("¥", "")));
                        }
                    } catch (NumberFormatException e) {
                        // Log the exception
                    }
                }
            } else if (itemArray.length > 2) {
                try {
                    detail.setAmount(Double.valueOf(itemArray[itemArray.length - 3]));
                    String taxRate = itemArray[itemArray.length - 2];
                    if (taxRate.contains("免税")
                            || taxRate.contains("不征税") || taxRate.contains("出口零税率")
                            || taxRate.contains("普通零税率") || !taxRate.contains("%")) {
                        detail.setTaxRate(0D);
                        detail.setTaxAmount(0D);
                    } else {
                        detail.setTaxRate(Double.parseDouble(taxRate.replace("%", "")));
                        detail.setTaxAmount(Double.valueOf(itemArray[itemArray.length - 1]));
                    }
                    if (itemArray.length > 3) {
                        detail.setName(itemArray[0]);
                        if (itemArray.length > 4) {
                            for (int j = 1; j < itemArray.length - 3; j++) {
                                if (itemArray[j].matches("^(-?\\d+)(\\.\\d+)?$")) {
                                    if (detail.getCount() == null) {
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
                    }
                    detailList.add(detail);
                } catch (NumberFormatException e) {
                    skipList.add(detailString);
                }
            } else {
                skipList.add(detailString);
            }
        }

        invoice.setDetailList(detailList);
    }

}
