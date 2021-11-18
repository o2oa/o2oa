package com.x.base.core.project.tools;
import org.apache.commons.lang3.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author louguodong
 *
 */
public class MD5Tool {

    public static String getMD5(byte[] source) {
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
            // 进制需要 32 个字符
            int k = 0;// 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
                // 进制字符的转换
                byte byte0 = tmp[i];// 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
                // 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

            }
            s = new String(str);// 换后的结果转换为字符串

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    public static String getMD5Str(String source) {
        if (StringUtils.isNotEmpty(source)) {
            return getMD5(source.getBytes());
        } else {
            return "";
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String test = MD5Tool.getMD5("1qaz2wsx".getBytes());
        System.out.println(test);






        /*String str="this is （Tom） and \"Eric\"， this is \"Bruce lee\", he is a chinese, name is \"李小龙\"。";
        Pattern p= Pattern.compile("\"(.*?)\"");
        Matcher m=p.matcher(str);
        while(m.find()){
            System.out.println(m.group());
        }*/


        String str="this is [Tom] and , he is a [李小花], name [is]。";
        Matcher mat = Pattern.compile("(?<=\\[)(\\S+)(?=\\])").matcher(str);
        while(mat.find()){
            System.out.println(mat.group());
        }


        String filetext = "//[张小名] 25分//[李小花] 43分//[王力] 100分";
        Pattern p = Pattern.compile("\\[(.*?)\\]");//正则表达式，取=和|之间的字符串，不包括=和|
        Matcher m = p.matcher(filetext);
        while(m.find()) {
            System.out.println(m.group(1));//m.group(1)不包括这两个字符
        }

        String url = "http://ip:20020/x_meeting_assemble_control/jaxrs/meeting/adf3c245-dbef-41ef-b323-dfb5fae4afb7/checkin";
        Pattern purl = Pattern.compile("x_meeting_assemble_control\\/jaxrs\\/meeting\\/(.*?)\\/checkin");//正则表达式
        Matcher murl = purl.matcher(url);
        if(murl.find()) {
            System.out.println(murl.group(1));//m.group(1)不包括这两个字符
        }

    }

}
