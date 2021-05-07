package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 3/8/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class MPweixin extends ConfigObject  {

    @FieldDescribe("是否启用.")
    private Boolean enable;
    @FieldDescribe("微信开放平台appid")
    private String appid;
    @FieldDescribe("微信开放平台appSecret")
    private String appSecret = "";
    @FieldDescribe("微信公众号配的token")
    private String token = "";
    @FieldDescribe("微信公众号配的encodingAesKey")
    private String encodingAesKey="";
    @FieldDescribe("微信公众号测试菜单的门户地址")
    private String portalId = "";

    @FieldDescribe("是否启用公众号模版消息")
    private Boolean messageEnable;
    @FieldDescribe("公众号模版消息id")
    private String tempMessageId;
    @FieldDescribe("模版字段")
    private List<MPweixinMessageTemp> fieldList;






    public static final String default_apiAddress = "https://api.weixin.qq.com";
    public static final Boolean default_enable = false;

    public static MPweixin defaultInstance() {
        return new MPweixin();
    }


    public MPweixin() {
        this.enable = default_enable;
        this.appid = "";
        this.appSecret = "";
        this.token = "";
        this.encodingAesKey = "";
        this.portalId = "";
        this.messageEnable = false;
        this.tempMessageId = "";
        this.fieldList = new ArrayList<>();
    }

    /**
     * 根据code获取accessToken openid 等信息
     * @param code
     * @return
     * @throws Exception
     */
    public WeixinAuth2AccessResp mpAuth2(String code) throws Exception {
        String address = default_apiAddress + "/sns/oauth2/access_token?appid=" + this.getAppid() + "&secret="
                + this.getAppSecret()+"&code="+code+"&grant_type=authorization_code";
        WeixinAuth2AccessResp resp = HttpConnection.getAsObject(address, null, WeixinAuth2AccessResp.class);
        if (resp.getErrcode()  != null && resp.getErrcode() != 0) {
            throw new ExceptionMPWeixinAccessToken(resp.getErrcode(), resp.getErrmsg());
        }
        return resp;
    }

    private static String cacheAccessToken;
    private static Date cacheAccessTokenDate;


    ///cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
    //获取accesstoken
    public String accessToken() throws Exception {
        if ((StringUtils.isNotEmpty(cacheAccessToken) && (null != cacheAccessTokenDate))
                && (cacheAccessTokenDate.after(new Date()))) {
            return cacheAccessToken;
        } else {
            String address = default_apiAddress + "/cgi-bin/token?grant_type=client_credential&appid=" + this.getAppid() + "&secret="
                    + this.getAppSecret();
            System.out.println(address);
            WeixinAuth2AccessResp resp = HttpConnection.getAsObject(address, null, WeixinAuth2AccessResp.class);
            if (resp.getErrcode() != null && resp.getErrcode() != 0) {
                throw new ExceptionMPWeixinAccessToken(resp.getErrcode(), resp.getErrmsg());
            }
            cacheAccessToken = resp.getAccess_token();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 90);
            cacheAccessTokenDate = cal.getTime();
            return cacheAccessToken;
        }
    }


    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

    public String getPortalId() {
        return portalId;
    }

    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }


    public Boolean getMessageEnable() {
        return messageEnable;
    }

    public void setMessageEnable(Boolean messageEnable) {
        this.messageEnable = messageEnable;
    }

    public String getTempMessageId() {
        return tempMessageId;
    }

    public void setTempMessageId(String tempMessageId) {
        this.tempMessageId = tempMessageId;
    }

    public List<MPweixinMessageTemp> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<MPweixinMessageTemp> fieldList) {
        this.fieldList = fieldList;
    }

    /**
     * 微信使用code获取accessToken openid 等数据
     */
    public static class WeixinAuth2AccessResp extends GsonPropertyObject {
        private String access_token;
        private Integer expires_in;
        private String refresh_token;
        private String openid;
        private String scope;
        private String unionid;
        //错误信息code
        private Integer errcode;
        private String errmsg;


        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

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

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }
}
