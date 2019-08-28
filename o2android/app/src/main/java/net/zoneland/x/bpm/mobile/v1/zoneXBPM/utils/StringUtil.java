package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by FancyLou on 2015/11/4.
 */
public class StringUtil {

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern phone = Pattern
            .compile("^(1)\\d{10}$");
    private final static Pattern telephone = Pattern.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");

    private final static Pattern HK_MACAO_phone = Pattern.compile("^((\\+00)?(852|853)\\d{8})$");
    private final static Pattern ipPattern = Pattern
            .compile("((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}");


    private static final String domainStr = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|cc|biz|info|cn|co|io|tech|me|nl|eu|xyz|mobi|website|world|tv|la|love|technology|club|online|store|studio)\\b()*";
    private static final Pattern domainPattern = Pattern.compile(domainStr, Pattern.CASE_INSENSITIVE);


    /**
     * 是否url
     *
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
//        String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
        String regex = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
        return match(regex, str);
    }

    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 截取数字
     */
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }


    /**
     * @param str
     * @param defValue default value
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 是否有中文
     *
     * @param string
     * @return
     */
    public static boolean hasChinese(String string) {
        Pattern chineseP = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = chineseP.matcher(string);
        if (matcher.find()) {
            return true;
        }

        return false;
    }

    /**
     * 去掉Uri 获取的路径前缀
     * 一般都是 file:///  或 content:/// 开头的
     *
     * @param filePath
     * @return
     */
    public static String removeFilePathPrefix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        if (filePath.startsWith("file://")) {
            return filePath.substring(7, filePath.length());
        }
        if (filePath.startsWith("content://")) {
            return filePath.substring(10, filePath.length());
        }
        return filePath;
    }

    /**
     * 获取顶级域名
     * @param url
     * @return
     */
    public static String getTopDomain(String url) {
        String result = url;
        try {
            Matcher matcher = domainPattern.matcher(url);
            matcher.find();
            result = matcher.group();
        } catch (Exception e) {
//            System.out.println("[getTopDomain ERROR]====>");
//            e.printStackTrace();
        }
        return result;
    }


    /**
     * 验证是否是ip地址
     *
     * @param ip
     * @return
     */
    public static boolean isIp(CharSequence ip) {
        if (isEmpty(ip)) {
            return false;
        }
        return ipPattern.matcher(ip).matches();
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     */
    public static boolean isEmail(CharSequence email) {
        if (isEmpty(email))
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是不是一个合法的手机号码
     */
    public static boolean isPhone(CharSequence phoneNum) {
        if (isEmpty(phoneNum))
            return false;
        return phone.matcher(phoneNum).matches();
    }

    /**
     * 判断是否是手机号码 包括香港和澳门的
     * 香港 852(8位)
     * 澳门 853(8位)
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneWithHKandMACAO(CharSequence phoneNum) {
        if (isEmpty(phoneNum)) {
            return false;
        }
        if (phone.matcher(phoneNum).matches() || HK_MACAO_phone.matcher(phoneNum).matches() ) {
            return true;
        }else  {
            return false;
        }
    }

    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


}
