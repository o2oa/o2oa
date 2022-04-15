package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class JpushConfig extends ConfigObject {

    private static final String O2_app_key_default = "9aca7cc20fe0cc987cd913ca";
    private static final String O2_master_secret_default = "96ee7e2e0daffd51bac57815";
    private static final String O2_apns_path_default = "configSample/o2oa_apns.p12";
    private static final String O2_apns_password_default = "1209";

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
        this.huaweiPushEnable = false;
        this.huaweiPushConfig = HuaweiPushConfig.defaultInstance();
        this.apnsKeystorePassword = O2_apns_password_default;
        this.apnsKeystorePath = O2_apns_path_default;

    }


    @FieldDescribe("是否启用.")
    private Boolean enable;
    @FieldDescribe("极光推送应用的AppKey")
    private String appKey;
    @FieldDescribe("极光推送应用的Master Secret")
    private String masterSecret;
    @FieldDescribe("是否开启华为推送，已废弃")
    private Boolean huaweiPushEnable;
    @FieldDescribe("华为推送的配置，已废弃")
    private HuaweiPushConfig huaweiPushConfig;
    @FieldDescribe("苹果推送证书路径")
    private String apnsKeystorePath;
    @FieldDescribe("苹果推送证书的密码")
    private String apnsKeystorePassword;


    /**
     * 获取给自助打包生成的 外部包名 的app使用的config
     * @return
     */
    public JpushConfig getOuterApplicationJpushConfig() {
        JpushConfig config = new JpushConfig();
        config.setAppKey(O2_app_key_outer);
        config.setMasterSecret(O2_master_secret_outer);
        config.setEnable(true);
        config.setHuaweiPushConfig(HuaweiPushConfig.outerPackInstance());
        return config;
    }


    /**
     * 获取苹果推送证书文件
     * @return
     * @throws Exception
     */
    public File getAPNSKeystoreFilePath() throws Exception {
        return new File(Config.base(), apnsKeystorePath);
    }

    public String getApnsKeystorePath() {
        return apnsKeystorePath;
    }

    public void setApnsKeystorePath(String apnsKeystorePath) {
        this.apnsKeystorePath = apnsKeystorePath;
    }

    public String getApnsKeystorePassword() {
        return apnsKeystorePassword;
    }

    public void setApnsKeystorePassword(String apnsKeystorePassword) {
        this.apnsKeystorePassword = apnsKeystorePassword;
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

    public Boolean getHuaweiPushEnable() {
        return huaweiPushEnable == null ? false : huaweiPushEnable;
    }

    public void setHuaweiPushEnable(Boolean huaweiPushEnable) {
        this.huaweiPushEnable = huaweiPushEnable;
    }

    public HuaweiPushConfig getHuaweiPushConfig() {
        return huaweiPushConfig;
    }

    public void setHuaweiPushConfig(HuaweiPushConfig huaweiPushConfig) {
        this.huaweiPushConfig = huaweiPushConfig;
    }
}
