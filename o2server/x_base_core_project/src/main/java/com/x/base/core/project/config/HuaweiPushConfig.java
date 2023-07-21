package com.x.base.core.project.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;

public class HuaweiPushConfig extends ConfigObject {

    private static final String O2_app_id_default = "100016851";
    private static final String O2_app_secret_default = "b3ad9287e8d1d16d0aad8fde66e59118";
    // outer 包的 华为推送 appId和secret
    private static final String O2_app_id_outer = "105181971";
    private static final String O2_app_secret_outer = "59862104d3080cfda20bbe6881db88327f015e53646ae675d71358cc81918fc3";
    // 获取accesssToken的url
    private static final String huawei_get_token_url = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
    // 华为推送消息接口url
    private static final String huawei_push_message_url = "https://push-api.cloud.huawei.com/v1/{0}/messages:send";


    public static HuaweiPushConfig defaultInstance() {
        return new HuaweiPushConfig();
    }
    public HuaweiPushConfig() {
        this.appId = O2_app_id_default;
        this.appSecret = O2_app_secret_default;
    }

    /**
     * 外部包的时候使用的华为推送key
     * @return
     */
    public static HuaweiPushConfig outerPackInstance() {
        HuaweiPushConfig config = new HuaweiPushConfig();
        config.setAppId(O2_app_id_outer);
        config.setAppSecret(O2_app_secret_outer);
        return config;
    }



    @FieldDescribe("华为推送应用的appId")
    private String appId;
    @FieldDescribe("华为推送应用的appSecret")
    private String appSecret;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }





    // accessToken 有一段时间可以通用，缓存下来
    private static String cacheAccessToken;
    private static Date cacheAccessTokenDate;


    /**
     * 推送消息的url
     * @return
     */
    public String getPushUrl() {
        return MessageFormat.format(huawei_push_message_url, this.getAppId());
    }


    /**
     * 华为accessToken 获取
     * @return
     * @throws Exception
     */
    public String accessToken() throws Exception  {
        if ((StringUtils.isNotEmpty(cacheAccessToken) && (null != cacheAccessTokenDate))
                && (cacheAccessTokenDate.after(new Date()))) {
            return cacheAccessToken;
        } else {
            List<NameValuePair> heads = new ArrayList<>();
            heads.add(new NameValuePair("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"));
            HuaweiAccessTokenResp resp = HttpConnection.postAsObject(huawei_get_token_url, heads, this.createRequestBody(), HuaweiAccessTokenResp.class);
            cacheAccessToken = resp.getAccess_token();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 90);
            cacheAccessTokenDate = cal.getTime();
            return cacheAccessToken;
        }
    }

    private String createRequestBody() {
        return MessageFormat.format("grant_type=client_credentials&client_secret={0}&client_id={1}", this.getAppSecret(), this.getAppId());
    }


    public static class HuaweiAccessTokenResp extends GsonPropertyObject {
        private String access_token;
        private Integer expires_in;
        private String token_type;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Integer expires_in) {
            this.expires_in = expires_in;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }
    }

}
