package com.x.program.center.jaxrs.apppack;


import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 打包服务器检测功能
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionConnectPackServer extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionConnectPackServer.class);

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<Wo>();
        Wo wo = new Wo();
        if (BooleanUtils.isNotTrue(connect())) {
            wo.setStatus(1); // o2云未连接
            result.setData(wo);
            return result;
        }
        if (BooleanUtils.isFalse(Config.collect().getEnable())) {
            wo.setStatus(1); // o2云未启用
            result.setData(wo);
            return result;
        }
        if (BooleanUtils.isNotTrue(validate(Config.collect().getName(), Config.collect().getPassword()))) {
            wo.setStatus(2); // o2云未登录
            result.setData(wo);
            return result;
        }
        String token = login2AppPackServer(Config.collect().getName(), Config.collect().getPassword());
        if (StringUtils.isEmpty(token)) {
            wo.setStatus(3); // 未认证
            result.setData(wo);
            return result;
        }else {
            wo.setStatus(1001);
            wo.setToken(token);
            wo.setPackServerUrl(Config.collect().appPackServerUrl());
            result.setData(wo);
            return result;
        }
    }


    /**
     * 检查collect服务器连接
     * @return
     */
    private Boolean connect() {
        try {
            String url = Config.collect().url(Collect.ADDRESS_COLLECT_ECHO);
            ActionResponse actionResponse = ConnectionAction.get(url, null);
            if (Objects.equals(ActionResult.Type.success, actionResponse.getType())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.info("连接collect服务器失败");
            logger.error(e);
            return false;
        }
    }

    /**
     * 检查 collect 服务器认证
     * @param name
     * @param password
     * @return
     * @throws Exception
     */
    private Boolean validate(String name, String password) {
        try {
            String url = Config.collect().url(Collect.ADDRESS_COLLECT_VALIDATE);
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("password", password);
            ActionResponse resp = ConnectionAction.post(url, null, map);
            return resp.getData(ReturnWoBoolean.class).getValue();
        } catch (Exception e) {
            logger.info("登录collect服务器失败");
            logger.error( e);
        }
        return false;
    }

    /**
     * 登录apppack服务器
     * @param name o2云账号
     * @param password o2云密码
     * @return
     * @throws Exception
     */
    private String login2AppPackServer(String name, String password) {
        try {
            String url = Config.collect().appPackServerApi(Collect.ADDRESS_APPPACK_AUTH);
            Map<String, String> map = new HashMap<>();
            map.put("collectName", name);
            map.put("password", password);
            String result = HttpConnection.postAsString(url, null, XGsonBuilder.instance().toJson(map));
            logger.info("打包服务器认证，结果: " + result);
            Type type = new TypeToken<AppPackResult<AuthTokenData>>() {
            }.getType();
            AppPackResult<AuthTokenData> packResult = XGsonBuilder.instance().fromJson(result, type);
            if (StringUtils.isNotEmpty(packResult.getResult()) && packResult.getResult().equals(AppPackResult.result_success)) {
                return packResult.getData().getToken();
            } else {
                logger.info("打包服务器认证失败");
            }
        } catch (Exception e) {
            logger.info("登录app打包服务器失败");
            logger.error(e);
        }
        return null;
    }

    /**
     * 打包服务器认证返回对象
     */
    public static class AuthTokenData implements Serializable {

        private String collectName;
        private String token;

        public String getCollectName() {
            return collectName;
        }

        public void setCollectName(String collectName) {
            this.collectName = collectName;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe( "连接状态，1：o2云服务未启用， 2：o2云未登录， 3：打包服务器未登录 , 1001：成功" )
        private int status; //   1 o2云服务未启用， 2 o2云未登录， 3 apppack服务未登录 , 1001 成功
        @FieldDescribe( "打包服务器认证token" )
        private String token; // status=1001 会生成token带到前端。
        @FieldDescribe( "打包服务器地址" )
        private String packServerUrl;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPackServerUrl() {
            return packServerUrl;
        }

        public void setPackServerUrl(String packServerUrl) {
            this.packServerUrl = packServerUrl;
        }
    }

    private static class ReturnWoBoolean extends WrapBoolean {
    }
}
