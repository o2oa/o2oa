package com.x.base.core.lc;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.BaseTools;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengjian
 * @date 2025/01/22 16:44
 **/
public class LcTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(LcTools.class);
    private static final String RSA_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCOiK+V+vRFVN2xGG7XvR5bY28SjkmFNXTiwUXfTzzsqZV3ipWpfIG5/Tdn7WnEdeIslhxfhsg1Rga26xNhfyJZ2Um26P2BRmfdPC5dA/sVXnNm3qoMCqLdH4/yInD1Ro5OQtVxy2OpoZ6VoMQHnbKi5rP6UG7DYPZfQxfoXICInsZiAKSE6Ibtg2JOzQL4dId2olUlopsEL7YSuZQTxpSr7AjNf70VMo9bTqSMXQwRV3/UXUlRQ1DvvAGM5SSNborGrN+F438dkRpMDlqmP34kjWyPXHpAXONyyfkdC6gyvgm+6GrMr8KeKq5NhgbKqhByy8VnVOUoEHyNiAo+m0RpAgMBAAECggEAAdcF3TvGF3cG/ZhBJgvM7oUUE/s+O7f68Epqan2NXci6ozZoxC1VR19bbb4E1YJoDwdJCnqk7vsebxINx/5qJRrG9tN/IkIUaSizuIOqpBX6C2PocgtZGt2jcA69wozMnXoDY1qqyK/LakcPAMIqYiv0OtD6/Q6tX4FwN5GqHxlYqnkjYilUpTIuoHEhkGSGpqDDHNT6mrkAXYebHS4R+WbBLJQjZ03TW+9CocAFRPHsTLgWcLeE6pQ7Vn2/IfrUTZYEri9oGHAROFpe4n1JlUyX0IKsI3UH/cpW6Nz6MjORS5DuiStC0MFUSx5P34ztKMNddRR2eN+LIGN11f9p4QKBgQDHtjxot9fd5zQDV6PgFtkUJ6VP7hZy1YhTKraP9bmPtsO723iC8VLtR6Brw8FRZ9dtR9HhlSGHuL4HlD5fnwehhIXcTarerCeo8DG7lCSymVqT8eb4nW/mExlSr5NhQ/02WtDx3NIDdRIVgAji2DpdimBcR1veNipGZZ06QRbtDQKBgQC2tOdg7iKSeQw/TmIF+cm8MEz8QwTDNDhOKzKh1kdf5c6OgkYsY6cmFU8IqsGMX+kAMtuLzbHvUBAoWl2nW/RxTY3Q7SZiwnWmfUWNQnf5XIt5Q73vI3rGDUhX/iWmMpt0ovbCvbItTV1FwTOcbHHnp4Ehk5cev6mxCETrY2r1zQKBgQCFZW0+SSYwO2Gbpet5H+nJs52RISfvdj/DxXECAZD7tIlTLop1dDn+8evub2fuNx1HCkTfl9w4qqaLq3JqLnBLa5h6CYrLh1RKXH3rnE1S2moNDfRINejrVfCBMXOQ8Xft2YVdODEzS/xTTh0hYGrD1kz89lEfwTBFq1P0+EsW9QKBgCrrNgskBYUI4GKipA4pTkVhx0Pe7mh4hN1/8kXoEx4o2qvJWHim9CXTDZzIl+zEvvQn/kDKEglYcmM4TxW4WSSghjbU7pZ2fbKdbNAxFAPExJK44g4h9H4/soXREsEG1FPC0FqQuyE/MjDnoJsXMm6rf38Gr5IujcsjWSU+VmrlAoGAeDrzTSplaXs4w7zN0JDEiGnlfPJicXhZgI7IHLIf/iGrwVH34qMIus4F2YOKJb8LPV9jbouTRkXbfDa6uJAd9dSQztgVsBTMjCmbxZW/PVUEyxOWI4l7qXc5i4ypln0Y2Lt/ittWb1eYlGBw2WSpPCAuLWfyKoY/Bv007aasR60=";

    private static final String PATH_CONFIG_LICENSE = "config/o2.license";
    private static final String PATH_CONFIG_KEY = "config/o2license.key";
    private static LcInfo lc = null;
    private static final List<String> DB_LIST_1 = List.of("oracle", "mysql", "postgresql", "sqlserver", "informix", "db2", "h2");
    private static final List<String> DB_LIST_2 = List.of("oracle", "mysql", "postgresql", "sqlserver", "dm", "kingbase", "oscar", "vastbase", "gbase", "informix", "db2", "h2");
    private static final String TYPE_VERSION = "itai";
    private static synchronized LcInfo license() {
        if (null == LcTools.lc) {
            String base = BaseTools.getBasePath();
            Path p = Paths.get(base, PATH_CONFIG_LICENSE);
            Path keyP = Paths.get(base, PATH_CONFIG_KEY);
            if(Files.exists(p) && Files.exists(keyP)){
                try {
                    String key = Files.readString(keyP, StandardCharsets.UTF_8);
                    String content = Files.readString(p, StandardCharsets.UTF_8);
                    if(StringUtils.isNotBlank(content) && StringUtils.isNotBlank(key)){
                        key = Crypto.decodeAES(key, RSA_KEY);
                        content = Crypto.rsaDecrypt(content, key);
                        LcTools.lc = XGsonBuilder.instance().fromJson(content, LcInfo.class);
                    }
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                }
            }
        }
        return LcTools.lc;
    }

    public static String getInfo() {
        LcInfo lc = license();
        if(lc != null) {
            if(CollectionUtils.isEmpty(lc.getSupportDbList())){
                if(StringUtils.isNotBlank(lc.getVersionType()) && lc.getVersionType().contains(TYPE_VERSION)){
                    lc.setSupportDbList(DB_LIST_2);
                }else{
                    lc.setSupportDbList(DB_LIST_1);
                }
            }
            return XGsonBuilder.toJson(lc);
        }
        return "";
    }

    public static boolean validate() {
        LcInfo lcInfo = license();
        if (null != lcInfo) {
            long expireTime = lcInfo.getExpireTime().getTime();
            long now = System.currentTimeMillis();
            return now < expireTime;
        }
        return false;
    }
}
