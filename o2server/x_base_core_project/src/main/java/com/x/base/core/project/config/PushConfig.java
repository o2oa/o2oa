package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.lang3.BooleanUtils;

public class PushConfig extends ConfigObject {

    private static final String O2_app_key_default = "9aca7cc20fe0cc987cd913ca";
    private static final String O2_master_secret_default = "96ee7e2e0daffd51bac57815";


    public static PushConfig defaultInstance() {
        return new PushConfig();
    }
    public PushConfig() {
        this.enable = false;
        this.appKey = O2_app_key_default;
        this.masterSecret = O2_master_secret_default;
    }


    @FieldDescribe("是否启用.")
    private Boolean enable;
    @FieldDescribe("极光推送应用的AppKey")
    private String appKey;
    @FieldDescribe("极光推送应用的Master Secret")
    private String masterSecret;

    public Boolean getEnable() {
        return BooleanUtils.isTrue(this.enable);
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }
}
