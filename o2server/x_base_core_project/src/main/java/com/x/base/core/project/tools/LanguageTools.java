package com.x.base.core.project.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageTools {

    public static String getValueByKey(String key) {
        return getValueByKey(key, null);
    }

    public static String getValueByKey(String key, String locale) {
        if(StringUtils.isBlank(key)){
            return null;
        }
        ResourceBundle resourceBundle = null;
        if(StringUtils.isBlank(locale)){
            resourceBundle = ResourceBundle.getBundle("language.platform", Locale.getDefault());
        }else if("zh".equalsIgnoreCase(locale) || "zh_CN".equalsIgnoreCase(locale)) {
            resourceBundle = ResourceBundle.getBundle("language.platform", Locale.SIMPLIFIED_CHINESE);
        }else if(locale.toLowerCase().startsWith("en")) {
            resourceBundle = ResourceBundle.getBundle("language.platform", Locale.ENGLISH);
        }else if("zh_HK".equalsIgnoreCase(locale) || "zh_TW".equalsIgnoreCase(locale)) {
            resourceBundle = ResourceBundle.getBundle("language.platform", Locale.TRADITIONAL_CHINESE);
        }

        return resourceBundle.getString(key);
    }
}
