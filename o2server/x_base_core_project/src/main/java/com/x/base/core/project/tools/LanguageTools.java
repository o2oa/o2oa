package com.x.base.core.project.tools;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class LanguageTools {

    private static Logger logger = LoggerFactory.getLogger(LanguageTools.class);

    private final static String LANGUAGE_PLATFORM = "language.platform";

    public static String getValue(String key) {
        return getValue(key, null);
    }

    public static String getValue(String key, String locale) {
        if(StringUtils.isBlank(key)){
            return null;
        }
        if(StringUtils.isBlank(locale)){
            try {
                locale = Config.person().getLanguage();
            } catch (Exception e) {
            }
        }
        if(StringUtils.isNotBlank(locale)) {
            locale = StringUtils.replace(locale, "-", "_");
        }
        String baseName = LANGUAGE_PLATFORM;
        String[] keys = StringUtils.split(key,".");
        if(keys.length>2){
            if(!"base".equals(keys[2].toLowerCase())) {
                baseName = "language." + keys[2].toLowerCase();
            }
        }
        return getValue(baseName, key, locale);
    }

    public static String getValue(String baseName ,String key, String locale) {
        String message = null;
        if(StringUtils.isBlank(key)){
            return null;
        }
        try {
            ResourceBundle resourceBundle = null;
            if(StringUtils.isBlank(locale)){
                try {
                    locale = Config.person().getLanguage();
                } catch (Exception e) {
                }
            }
            if(StringUtils.isNotBlank(locale)) {
                locale = StringUtils.replace(locale, "-", "_");
            }
            if(StringUtils.isBlank(locale)){
                resourceBundle = ResourceBundle.getBundle(baseName, Locale.getDefault());
            }else if("zh".equalsIgnoreCase(locale) || "zh_CN".equalsIgnoreCase(locale)) {
                resourceBundle = ResourceBundle.getBundle(baseName, Locale.SIMPLIFIED_CHINESE);
            }else if(locale.toLowerCase().startsWith("en")) {
                resourceBundle = ResourceBundle.getBundle(baseName, Locale.ENGLISH);
            }else {
                String[] locals = StringUtils.split(locale, "_");
                Locale locale1 = null;
                if(locals.length>1){
                    locale1 = new Locale(locals[0].toLowerCase(), locals[1].toUpperCase());
                }else{
                    locale1 = new Locale(locals[0].toLowerCase());
                }
                resourceBundle = ResourceBundle.getBundle(baseName, locale1);
            }
            if(resourceBundle == null){
                resourceBundle = ResourceBundle.getBundle(baseName, Locale.getDefault());
            }
            message = resourceBundle.getString(key);
        } catch (Exception e) {
            return getValueFromBase(key, locale);
        }

        return message;
    }

    public static String getValueFromBase(String key, String locale) {
        String message = null;
        if(StringUtils.isBlank(key)){
            return null;
        }
        try {
            ResourceBundle resourceBundle = null;
            if(StringUtils.isBlank(locale)){
                try {
                    locale = Config.person().getLanguage();
                } catch (Exception e) {
                }
            }
            if(StringUtils.isNotBlank(locale)) {
                locale = StringUtils.replace(locale, "-", "_");
            }
            if(StringUtils.isBlank(locale)){
                resourceBundle = ResourceBundle.getBundle(LANGUAGE_PLATFORM, Locale.getDefault());
            }else if("zh".equalsIgnoreCase(locale) || "zh_CN".equalsIgnoreCase(locale)) {
                resourceBundle = ResourceBundle.getBundle(LANGUAGE_PLATFORM, Locale.SIMPLIFIED_CHINESE);
            }else if(locale.toLowerCase().startsWith("en")) {
                resourceBundle = ResourceBundle.getBundle(LANGUAGE_PLATFORM, Locale.ENGLISH);
            }else{
                String[] locals = StringUtils.split(locale, "_");
                Locale locale1 = null;
                if(locals.length>1){
                    locale1 = new Locale(locals[0].toLowerCase(), locals[1].toUpperCase());
                }else{
                    locale1 = new Locale(locals[0].toLowerCase());
                }
                resourceBundle = ResourceBundle.getBundle(LANGUAGE_PLATFORM, locale1);
            }
            message = resourceBundle.getString(key);
        } catch (Exception e) {
            logger.debug("LanguageTools resourceBundle2 error:"+e.getMessage());
        }

        return message;
    }
}
