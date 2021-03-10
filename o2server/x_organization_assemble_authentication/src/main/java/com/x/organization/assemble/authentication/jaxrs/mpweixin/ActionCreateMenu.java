package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.MPweixin;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 3/4/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionCreateMenu extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCreateMenu.class);


    //https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect


    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (Config.mPweixin() == null || BooleanUtils.isFalse(Config.mPweixin().getEnable())) {
            throw new ExceptionConfigError();
        }
        String accessToken = Config.mPweixin().accessToken();
        logger.info("accessToken: "+accessToken);
        Config.mPweixin();
        String createUrl = MPweixin.default_apiAddress + "/cgi-bin/menu/create?access_token="+accessToken;
        logger.info("url: "+createUrl);
        String appId = Config.mPweixin().getAppid();
        String baseUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId;
        String httpProtocol = Config.currentNode().getCenter().getHttpProtocol();
        if (StringUtils.isEmpty(httpProtocol)) {
            throw new ExceptionNoConfigArguments("对外http访问协议为空！");
        }
        String host = Config.currentNode().getWeb().getProxyHost();
        if (StringUtils.isEmpty(host)) {
            throw new ExceptionNoConfigArguments("代理Host为空！");
        }
        Integer port = Config.currentNode().getWeb().getProxyPort() == null? 80: Config.currentNode().getWeb().getProxyPort();
        //"%3A"+port+ 有没有端口 是不同的安全域名
        String ssoUrl = httpProtocol+"%3A%2F%2F"+host+"%2Fx_desktop%2Fmpweixinsso.html%3Ftype%3D";
        String ssoOpenUrl = ssoUrl + "login";
        String ssoBindUrl = ssoUrl + "bind";
        String portalId = Config.mPweixin().getPortalId();
        String openUrl = baseUrl;
        if (StringUtils.isEmpty(portalId)) {
            openUrl += "&redirect_uri="+ssoOpenUrl+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        }else {
            openUrl += "&redirect_uri="+ssoOpenUrl+"%26redirect%3Dportalmobile.html%3Fid%3D"+portalId+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        }
        String bindUrl = baseUrl+"&redirect_uri="+ssoBindUrl+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        String body = "{" +
                "\"button\":[" +
                "  {" +
                "        \"type\":\"view\"," +
                "        \"name\":\"测试打开\"," +
                "        \"url\":\""+openUrl+"\"" +
                "  }," +
                "  { " +
                "        \"type\":\"view\"," +
                "        \"name\":\"测试绑定\"," +
                "        \"url\":\""+bindUrl+"\"" +
                "  }" +
                " ]" +
                "}";
        logger.info("测试菜单：" + body);
        WeixinResp resp = HttpConnection.postAsObject(createUrl, null, body, WeixinResp.class);
        if (resp.getErrcode() != null && resp.getErrcode() == 0) {
            logger.info("保存菜单成功！");
        }else {
            logger.info(resp.toString());
        }
        Wo wo = new Wo();
        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    public static class Wo extends WrapBoolean {

    }

    public static class WeixinResp  extends GsonPropertyObject {
        private String errmsg;
        private Integer errcode;

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
