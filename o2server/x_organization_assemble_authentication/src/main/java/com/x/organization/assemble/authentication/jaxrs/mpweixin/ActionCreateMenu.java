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
        String body = "{" +
                "\"button\":[" +
                "  {" +
                "        \"type\":\"view\"," +
                "        \"name\":\"测试打开\"," +
                "        \"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri=http%3A%2F%2Fqywx.o2oa.net%2Fx_desktop%2Fmpweixinsso.html%3Ftype%3Dlogin%26redirect%3Dportalmobile.html%3Fid%3Dxxxxxx&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect\"" +
                "  }," +
                "  { " +
                "        \"type\":\"view\"," +
                "        \"name\":\"测试绑定\"," +
                "        \"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri=http%3A%2F%2Fqywx.o2oa.net%2Fx_desktop%2Fmpweixinsso.html%3Ftype%3Dbind%26redirect%3Dportalmobile.html%3Fid%3Dxxxxxx&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect\"" +
                "  }" +
                " ]" +
                "}";
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
