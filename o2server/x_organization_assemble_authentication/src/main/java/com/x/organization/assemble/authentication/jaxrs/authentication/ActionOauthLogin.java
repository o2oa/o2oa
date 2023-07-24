package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author ray
 *
 */
class ActionOauthLogin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, String name, String code,
			String redirectUri) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		// 获取oauthClient对象
		OauthClient oauthClient = oauthClient(name);
		Map<String, Object> param = oauthCreateParam(oauthClient, code, redirectUri);
		oauthToken(oauthClient, param);
		oauthCheckAccessToken(param);
		oauthInfo(oauthClient, param);
		Object val = param.get(oauthClient.getInfoCredentialField());
		String credential = Objects.toString(val, "");
		if(val != null && val instanceof Double){
			credential = ((Double)val).longValue()+"";
		}
		oauthCheckCredential(credential);
		LOGGER.info("单点应用：{} 的用户ID：{} 进行oauth认证.", name, credential);
		Wo wo;
		if (Config.token().isInitialManager(credential)) {
			wo = this.manager(request, response, credential, Wo.class);
		} else {
			// 普通用户登录,也有可能拥有管理员角色
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String personId = business.person().getWithCredential(credential);
				if (StringUtils.isEmpty(personId)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				Person o = emc.find(personId, Person.class);
				wo = this.user(request, response, business, o, Wo.class);
			}
		}
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionOauthLogin$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = 5188552190927904546L;

	}
}
