package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengjian
 * @date 2024/08/01 13:38
 **/
public class Strings {

    public static int utf8Length(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        return str.getBytes(StandardCharsets.UTF_8).length;
    }

    public static String utf8SubString(String str, int len) {
        if (len < 0 || StringUtils.isEmpty(str) || utf8Length(str) <= len) {
            return str;
        }
        byte[] bs = Arrays.copyOf(str.getBytes(StandardCharsets.UTF_8), len);
        try {
            for (int i = 0; i < len; i++) {
                ByteArrayInputStream input = new ByteArrayInputStream(Arrays.copyOf(bs, len - i));
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                String value = IOUtils.toString(reader);
                if (utf8Length(value) <= len) {
                    return value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 截断超长文件名,尽量保留后缀
     *
     * @param str
     * @param len
     * @return
     */
    public static String utf8FileNameSubString(String str, int len) {
        if (len < 0 || StringUtils.isEmpty(str) || utf8Length(str) <= len) {
            return str;
        }
        String extension = StringUtils.substringAfterLast(str, ".");
        if (StringUtils.isNotEmpty(extension)) {
            extension = "." + extension;
            String name = StringUtils.substringBeforeLast(str, ".");
            int nameLength = len - utf8Length(extension);
            if (nameLength <= 0) {
                return utf8SubString(extension, len);
            } else {
                return utf8SubString(name, nameLength) + extension;
            }
        }
        return utf8SubString(str, len);
    }

}
