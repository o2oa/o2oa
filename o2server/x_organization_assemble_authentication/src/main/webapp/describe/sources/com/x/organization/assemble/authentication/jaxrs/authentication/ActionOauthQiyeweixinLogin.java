package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionOauthQiyeweixinLogin extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(ActionOauthQiyeweixinLogin.class);


    ActionResult<ActionOauthQiyeweixinLogin.Wo> execute(HttpServletRequest request, HttpServletResponse response,
                                                        EffectivePerson effectivePerson, String code) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Audit audit = logger.audit(effectivePerson);
            ActionResult<ActionOauthQiyeweixinLogin.Wo> result = new ActionResult<>();
            Business business = new Business(emc);

            String url = Config.qiyeweixin().getApiAddress() + "/cgi-bin/user/getuserinfo?access_token="
                    + Config.qiyeweixin().corpAccessToken() + "&code=" + code;
            String str = HttpConnection.getAsString(url, null);
            logger.debug("企业微信获取用户 return:{}", str);
            JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
            String userId = jsonElement.getAsJsonObject().get("UserId").getAsString();

            logger.info("credential:{}", userId);
            if (StringUtils.isEmpty(userId)) {
                throw new ExceptionOauthEmptyCredential();
            }
            Wo wo = new Wo();
            if (Config.token().isInitialManager(userId)) {
                wo = this.manager(request, response, business, Wo.class);
            } else {
                /* 普通用户登录,也有可能拥有管理员角色 */
                String personId = business.person().getWithCredential(userId);
                if (StringUtils.isEmpty(personId)) {
                    throw new ExceptionPersonNotExistOrInvalidPassword();
                }
                Person o = emc.find(personId, Person.class);
                wo = this.user(request, response, business, o, Wo.class);
                audit.log(o.getDistinguishedName(), "登录");
            }
            result.setData(wo);
            return result;
        }

    }

    public static class Wo extends AbstractWoAuthentication {

        private static final long serialVersionUID = -1473824515272368422L;

        private String url;
        private String method;
        private String parameter;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }
    }

}
