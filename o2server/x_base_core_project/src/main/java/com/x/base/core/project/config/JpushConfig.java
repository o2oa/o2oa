package com.x.base.core.project.config;

import com.google.gson.JsonObject;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

public class JpushConfig extends ConfigObject {

    private static final String O2_app_key_default = "9aca7cc20fe0cc987cd913ca";
    private static final String O2_master_secret_default = "96ee7e2e0daffd51bac57815";


    // 自助打包的outer包使用的key
    private static final String O2_app_key_outer = "24a4af5965d2c325b33c243d";
    private static final String O2_master_secret_outer = "a7b5689399307b29957e7dce";


    public static JpushConfig defaultInstance() {
        return new JpushConfig();
    }
    public JpushConfig() {
        this.enable = true;
        this.appKey = O2_app_key_default;
        this.masterSecret = O2_master_secret_default;
    }


    @FieldDescribe("是否启用.")
    private Boolean enable;
    @FieldDescribe("极光推送应用的AppKey")
    private String appKey;
    @FieldDescribe("极光推送应用的Master Secret")
    private String masterSecret;
    // "thirdPartyChannel": { "xiaomi": {"channel_id": ""}, "huawei": { "importance": "NORMAL", "category": "WORK" } }
    @FieldDescribe("第三方通道参数配置")
    private Map<String, JsonObject> thirdPartyChannel;





    /**
     * 获取给自助打包生成的 外部包名 的app使用的config
     * @return
     */
    public JpushConfig getOuterApplicationJpushConfig() {
        JpushConfig config = new JpushConfig();
        config.setAppKey(O2_app_key_outer);
        config.setMasterSecret(O2_master_secret_outer);
        config.setEnable(true);
        return config;
    }


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

    public Map<String, JsonObject> getThirdPartyChannel() {
        return thirdPartyChannel;
    }

    public void setThirdPartyChannel(Map<String, JsonObject> thirdPartyChannel) {
        this.thirdPartyChannel = thirdPartyChannel;
    }
}
