package com.x.general.assemble.control.tools;

import com.x.general.core.entity.Invoice;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chengjian
 **/
public class PdfRailwayInvoiceTools {

    public static final String KEY = "铁路电子客票";

    private PdfRailwayInvoiceTools(){}

    public static void getInvoice(String allText, Invoice invoice) {
        String reg = "发票号码:(?<number>\\d{20})|开票日期:(?<date>\\d{4}年\\d{2}月\\d{2}日)|票价:¥(?<totalAmount>\\d+(\\.\\d*)?)|购买方名称:(?<buyerName>[a-zA-Z0-9()\\u4e00-\\u9fa5]+公司)|统一社会信用代码:(?<buyerCode>[\\dA-Z]{18})|电子客票号:(?<code>\\d*)|\\*\\d{4}(?<rider>.+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(allText);
        while (matcher.find()) {
            if (matcher.group("number") != null) {
                invoice.setNumber(matcher.group("number"));
            } else if (matcher.group("code") != null) {
                invoice.setCode(matcher.group("code"));
            } else if (matcher.group("date") != null) {
                invoice.setDate(matcher.group("date"));
            } else if (matcher.group("buyerName") != null) {
                invoice.setBuyerName(matcher.group("buyerName"));
            } else if (matcher.group("buyerCode") != null) {
                invoice.setBuyerCode(matcher.group("buyerCode"));
            } else if (matcher.group("totalAmount") != null) {
                invoice.setTotalAmount(Double.valueOf(matcher.group("totalAmount")));
            } else if (matcher.group("rider") != null) {
                invoice.setRider(matcher.group("rider"));
            }
        }
        String reg2 = "(\\p{IsHan}+站)\\s*[A-Za-z\\d]*\\s*(\\p{IsHan}+站)";
        Pattern pattern2 = Pattern.compile(reg2);
        Matcher matcher2 = pattern2.matcher(allText);
        if(matcher2.find()){
            invoice.setStartStation(matcher2.group(1));
            invoice.setEndStation(matcher2.group(2));
        }
        String reg3 = "(?<detail>\\d{4}年\\d{2}月\\d{2}日\\d{2}:\\d{2}开\\S*)";
        Pattern pattern3 = Pattern.compile(reg3);
        Matcher matcher3 = pattern3.matcher(allText);
        if(matcher3.find()){
            invoice.setDetail(matcher3.group("detail"));
        }
        invoice.setType(KEY);
        invoice.setTitle("电子发票("+KEY+")");
    }
}
