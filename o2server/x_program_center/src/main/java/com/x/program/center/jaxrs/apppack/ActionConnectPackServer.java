package com.x.program.center.jaxrs.apppack;


import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 打包服务器检测功能
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionConnectPackServer extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionConnectPackServer.class);

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
        String token = getPackServerSSOToken();
        if (StringUtils.isEmpty(token)) {
            wo.setStatus(3); // 未认证
        }else {
            wo.setStatus(1001);
        }
        result.setData(wo);
        return result;
    }


    /**
     * 检查collect服务器连接
     * @return
     */
    private Boolean connect() {
        try {
            String url = Config.collect().url(Collect.ADDRESS_COLLECT_ECHO);
            ActionResponse actionResponse = ConnectionAction.get(url, null);
            return Objects.equals(ActionResult.Type.success, actionResponse.getType());
        } catch (Exception e) {
            logger.info("连接collect服务器失败");
            logger.error(e);
            return false;
        }
    }

    /**
     * 检查 collect 服务器认证
     * @param name  collect 账号
     * @param password collect 密码
     * @return 认证成功返回true，否则返回false
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
        @FieldDescribe( "打包服务器地址" )
        private String packServerUrl;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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
