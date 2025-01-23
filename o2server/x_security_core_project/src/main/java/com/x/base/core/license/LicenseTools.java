package com.x.base.core.license;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.Crypto;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengjian
 * @date 2025/01/22 16:44
 **/
public class LicenseTools {
    private static final String RSA_KEY = "/+1WL5oD53CcxBUQB53tIePaXXanU6pQneJTWefKBN9B36nTLt6K98eMTlwOxQKBgQDNtWOgM3CPu4stMzT1At9qJcNCadsGjiUQVLhb5t4fjfZqH6dxHnwMVn7Pl3nVy3vkCDwDT0aEqQgoqmPUG6OuWVpn1mEqBbhp+VJ6IYA23VSmoiMej/5nLxDiRA3HnWzNKNO/hvd9RMN91PYSmjkxMsOU6Vme8E0ujSvAZ3qZfQKBgQC5Pfar3IJjl/TQh/Mkez0RWm7wxNTS+sq1kVSOIM1LlL90GL/El9OtTQ9lGi4Bu1x+1X2HtmR//icLkItPzbu8gZz7covqYs6JoYeVRkedAOMkn/egWZWQnVj9BiaUvREnpjsxUqDRpo54TEe4Xz1YmSi1l/9ySmRiN9w2+Hc6tQKBgQC5AhBt/pWXz7ammk1gporE7t2kF4xTYvhWrbt5fDP98THT1FBF3oL2nbxDfBqpe+8a8YT3/Q6RNW0PFd/e3lTh857pkNemcaqgNQpGqWak8X62t1AapqrJpnzq4wX5p8443iVDNmHUGkowG56MsIQLiXp5rT1bQwhHe9QDoY2UCQKBgDBn/QzaXTDav0kspesnEU8SBp0pEFeiS02euMHeQq4yAZYAqIUf23W6PwzxmqrxG52RY8fQtNKDF1IBL2ELNa3yJIC9LrqkJhqnGFtJnjVoVg5wx24BA/yxA3Fg9VOBxXXBkJPGqXUyBrO7NwM6r8FU9EUp6/xSGMn1OPu4sZfL";

    private static final String PATH_CONFIG_LICENSE = "config/o2.license";
    private static LicenseInfo licenseInfo = null;

    private static synchronized LicenseInfo license() {
        if (null == licenseInfo) {
            String base = BaseTools.getBasePath();
            Path p = Paths.get(base, PATH_CONFIG_LICENSE);
            if(Files.exists(p)){
                try {
                    String content = Files.readString(p, StandardCharsets.UTF_8);
                    if(StringUtils.isNotBlank(content)){
                        content = Crypto.rsaDecrypt(content, RSA_KEY);
                        licenseInfo = XGsonBuilder.instance().fromJson(content, LicenseInfo.class);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return licenseInfo;
    }

    public static String getLicenseInfo() {
        LicenseInfo licenseInfo = license();
        if (null != licenseInfo) {
            return XGsonBuilder.toJson(licenseInfo);
        }
        return null;
    }

    public static boolean validate() {
        LicenseInfo licenseInfo = license();
        if (null != licenseInfo) {
            long expireTime = licenseInfo.getExpireTime().getTime();
            long now = System.currentTimeMillis();
            return now < expireTime;
        }
        return false;
    }
}
