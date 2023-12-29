package com.x.program.center.jaxrs.dingding;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;


/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionSyncOrganizationCallbackUrlRegister extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSyncOrganizationCallbackUrlRegister.class);

    private List<String> tags = new ArrayList<>(Arrays.asList("user_add_org", "user_modify_org", "user_leave_org", "user_active_org", "org_dept_create", "org_dept_modify", "org_dept_remove"));


    ActionResult<Wo> execute(EffectivePerson effectivePerson, boolean enable) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (Config.dingding().getEnable()) {
            RegisterObject registerObject = new RegisterObject();
            registerObject.setAes_key(Config.dingding().getEncodingAesKey());
            registerObject.setCall_back_tag(tags);
            registerObject.setToken(Config.dingding().getToken());
            //获取center服务器地址信息
            CenterServer centerServer = Config.currentNode().getCenter();
            Boolean sslEnable = centerServer.getSslEnable();
            String host = centerServer.getProxyHost();
            int port = centerServer.getProxyPort();
            //回调地址
            String callbackUrl = getApplicationUrl(sslEnable, host, port) + "/x_program_center/jaxrs/dingding/sync/organization/callback";
            registerObject.setUrl(callbackUrl);
            logger.info("注册回调地址 post对象：{}", registerObject.toString());
            //钉钉回调地址注册 url post
            String address;
            if (enable) {
                address = Config.dingding().getOapiAddress() + "/call_back/register_call_back?access_token=" + Config.dingding().corpAccessToken();
            }else {
                address = Config.dingding().getOapiAddress() + "/call_back/update_call_back?access_token=" + Config.dingding().corpAccessToken();
            }
            logger.info("register url :" + address);
            DingdingMessageResp resp = HttpConnection.postAsObject(address, null, registerObject.toString(), DingdingMessageResp.class);
            if (resp.getErrcode() != 0) {
                throw  new ExceptionRegisterCallbackMessage(resp.getErrcode(), resp.getErrmsg());
            }else  {
                Wo wo = new Wo();
                wo.setValue(true);
                result.setData(wo);
            }
        }else {
            throw new ExceptionNotPullSync();
        }

        return result;
    }

    private String getApplicationUrl(Boolean sslEnable, String host, int port) {
        if( sslEnable ) {
            return "https://" + host + ":" + port;
        }else {
            return "http://" + host + ":" + port;
        }
    }


    public static class Wo extends WrapBoolean {
    }

    public static class RegisterObject extends GsonPropertyObject {

        private List<String> call_back_tag;
        private String token;
        private String aes_key;
        private String url;

        public List<String> getCall_back_tag() {
            return call_back_tag;
        }

        public void setCall_back_tag(List<String> call_back_tag) {
            this.call_back_tag = call_back_tag;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAes_key() {
            return aes_key;
        }

        public void setAes_key(String aes_key) {
            this.aes_key = aes_key;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class DingdingMessageResp {

        private Integer errcode;
        private String errmsg;

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }



        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

    }
}
