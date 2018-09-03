package o2.collect.assemble.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;

import o2.base.core.project.config.Config;
import o2.base.core.project.config.Token;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, String key, String answer,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			logger.debug("{} try to login.", wi.getCredential());
			ActionResult<Wo> result = new ActionResult<>();
			if (StringUtils.isEmpty(wi.getCredential())) {
				throw new ExceptionCredentialEmpty();
			}
			if (StringUtils.isEmpty(wi.getPassword())) {
				throw new ExceptionPasswordEmpty();
			}
			Wo wo = null;
			if (StringUtils.equalsIgnoreCase(wi.getCredential(), Token.defaultInitialManager)) {
				/* 管理员登陆 */
				wo = this.adminAuthentication(request, response, wi.getCredential(), wi.getPassword());
			} else {
				/* 普通用户登录 */
				if (!business.validateCaptcha(key, answer)) {
					throw new ExceptionCaptchaError();
				}
				wo = this.userAuthentication(request, response, business, wi.getCredential(), wi.getPassword());
			}
			result.setData(wo);
			return result;
		}
	}

	private Wo adminAuthentication(HttpServletRequest request, HttpServletResponse response, String credential,
			String password) throws Exception {
		Wo wo = new Wo();
		if (StringUtils.equals(Config.token().getPassword(), password)) {
			HttpToken httpToken = new HttpToken();
			EffectivePerson effectivePerson = new EffectivePerson(Token.defaultInitialManager, TokenType.manager,
					Config.token().getCipher());
			httpToken.setToken(request, response, effectivePerson);
			wo.setToken(effectivePerson.getToken());
			wo.setName(Token.defaultInitialManager);
			wo.setTokenType(TokenType.manager);
		} else {
			throw new ExceptionPasswordNotMatch(credential);
		}
		return wo;
	}

	private Wo userAuthentication(HttpServletRequest request, HttpServletResponse response, Business business,
			String credential, String password) throws Exception {
		Wo wo = new Wo();
		String unitId = business.unit().getWithName(credential, null);
		if (StringUtils.isEmpty(unitId)) {
			throw new ExceptionCredentialNotExist(credential);
		}
		Unit unit = business.entityManagerContainer().find(unitId, Unit.class, ExceptionWhen.not_found);
		if (StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), unit.getPassword())) {
			HttpToken httpToken = new HttpToken();
			EffectivePerson effectivePerson = new EffectivePerson(unit.getName(), TokenType.user,
					Config.token().getCipher());
			httpToken.setToken(request, response, effectivePerson);
			wo.setToken(effectivePerson.getToken());
			wo.setName(unit.getName());
			wo.setTokenType(TokenType.user);
		} else {
			throw new ExceptionPasswordNotMatch(credential);
		}
		return wo;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户凭证")
		private String credential;
		@FieldDescribe("密码")
		private String password;

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("令牌")
		private String token;
		@FieldDescribe("名称")
		private String name;
		@FieldDescribe("令牌类型")
		private TokenType tokenType;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TokenType getTokenType() {
			return tokenType;
		}

		public void setTokenType(TokenType tokenType) {
			this.tokenType = tokenType;
		}

	}
}
