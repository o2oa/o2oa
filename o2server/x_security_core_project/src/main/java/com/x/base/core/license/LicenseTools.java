package com.x.base.core.license;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseTools.class);
    private static final String RSA_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCOiK+V+vRFVN2xGG7XvR5bY28SjkmFNXTiwUXfTzzsqZV3ipWpfIG5/Tdn7WnEdeIslhxfhsg1Rga26xNhfyJZ2Um26P2BRmfdPC5dA/sVXnNm3qoMCqLdH4/yInD1Ro5OQtVxy2OpoZ6VoMQHnbKi5rP6UG7DYPZfQxfoXICInsZiAKSE6Ibtg2JOzQL4dId2olUlopsEL7YSuZQTxpSr7AjNf70VMo9bTqSMXQwRV3/UXUlRQ1DvvAGM5SSNborGrN+F438dkRpMDlqmP34kjWyPXHpAXONyyfkdC6gyvgm+6GrMr8KeKq5NhgbKqhByy8VnVOUoEHyNiAo+m0RpAgMBAAECggEAAdcF3TvGF3cG/ZhBJgvM7oUUE/s+O7f68Epqan2NXci6ozZoxC1VR19bbb4E1YJoDwdJCnqk7vsebxINx/5qJRrG9tN/IkIUaSizuIOqpBX6C2PocgtZGt2jcA69wozMnXoDY1qqyK/LakcPAMIqYiv0OtD6/Q6tX4FwN5GqHxlYqnkjYilUpTIuoHEhkGSGpqDDHNT6mrkAXYebHS4R+WbBLJQjZ03TW+9CocAFRPHsTLgWcLeE6pQ7Vn2/IfrUTZYEri9oGHAROFpe4n1JlUyX0IKsI3UH/cpW6Nz6MjORS5DuiStC0MFUSx5P34ztKMNddRR2eN+LIGN11f9p4QKBgQDHtjxot9fd5zQDV6PgFtkUJ6VP7hZy1YhTKraP9bmPtsO723iC8VLtR6Brw8FRZ9dtR9HhlSGHuL4HlD5fnwehhIXcTarerCeo8DG7lCSymVqT8eb4nW/mExlSr5NhQ/02WtDx3NIDdRIVgAji2DpdimBcR1veNipGZZ06QRbtDQKBgQC2tOdg7iKSeQw/TmIF+cm8MEz8QwTDNDhOKzKh1kdf5c6OgkYsY6cmFU8IqsGMX+kAMtuLzbHvUBAoWl2nW/RxTY3Q7SZiwnWmfUWNQnf5XIt5Q73vI3rGDUhX/iWmMpt0ovbCvbItTV1FwTOcbHHnp4Ehk5cev6mxCETrY2r1zQKBgQCFZW0+SSYwO2Gbpet5H+nJs52RISfvdj/DxXECAZD7tIlTLop1dDn+8evub2fuNx1HCkTfl9w4qqaLq3JqLnBLa5h6CYrLh1RKXH3rnE1S2moNDfRINejrVfCBMXOQ8Xft2YVdODEzS/xTTh0hYGrD1kz89lEfwTBFq1P0+EsW9QKBgCrrNgskBYUI4GKipA4pTkVhx0Pe7mh4hN1/8kXoEx4o2qvJWHim9CXTDZzIl+zEvvQn/kDKEglYcmM4TxW4WSSghjbU7pZ2fbKdbNAxFAPExJK44g4h9H4/soXREsEG1FPC0FqQuyE/MjDnoJsXMm6rf38Gr5IujcsjWSU+VmrlAoGAeDrzTSplaXs4w7zN0JDEiGnlfPJicXhZgI7IHLIf/iGrwVH34qMIus4F2YOKJb8LPV9jbouTRkXbfDa6uJAd9dSQztgVsBTMjCmbxZW/PVUEyxOWI4l7qXc5i4ypln0Y2Lt/ittWb1eYlGBw2WSpPCAuLWfyKoY/Bv007aasR60=";
    private static final String RSA_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjoivlfr0RVTdsRhu170eW2NvEo5JhTV04sFF30887KmVd4qVqXyBuf03Z+1pxHXiLJYcX4bINUYGtusTYX8iWdlJtuj9gUZn3TwuXQP7FV5zZt6qDAqi3R+P8iJw9UaOTkLVcctjqaGelaDEB52youaz+lBuw2D2X0MX6FyAiJ7GYgCkhOiG7YNiTs0C+HSHdqJVJaKbBC+2ErmUE8aUq+wIzX+9FTKPW06kjF0MEVd/1F1JUUNQ77wBjOUkjW6KxqzfheN/HZEaTA5apj9+JI1sj1x6QFzjcsn5HQuoMr4JvuhqzK/CniquTYYGyqoQcsvFZ1TlKBB8jYgKPptEaQIDAQAB";

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
                    LOGGER.debug(e.getMessage());
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
        return "";
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
