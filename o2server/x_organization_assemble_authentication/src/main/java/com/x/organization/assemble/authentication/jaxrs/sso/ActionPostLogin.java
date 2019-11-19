package com.x.organization.assemble.authentication.jaxrs.sso;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.logger.Audit;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Sso;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionPostLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPostLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getClient())) {
				throw new ExceptionClientEmpty();
			}
			if (StringUtils.isEmpty(wi.getToken())) {
				throw new ExceptionTokenEmpty();
			}
			Sso sso = Config.token().findSso(wi.getClient());
			if (null == sso) {
				throw new ExceptionClientNotExist(wi.getClient());
			}
			if (StringUtils.isEmpty(sso.getKey())) {
				throw new ExceptionEmptyKey();
			}
			String content = null;
			logger.debug("decrypt sso client:{}, token:{}, key:{}.", wi.getClient(), wi.getToken(), sso.getKey());
			try {
				content = Crypto.decrypt(wi.getToken(), sso.getKey());
			} catch (Exception e) {
				throw new ExceptionReadToken(wi.getClient(), wi.getToken());
			}
			String flag = StringUtils.substringBefore(content, "#");
			flag = URLDecoder.decode(flag, "UTF-8");
			String timeString = StringUtils.substringAfter(content, "#");
			if (StringUtils.isEmpty(flag)) {
				throw new ExceptionTokenFlagEmpty();
			}
			Date date = new Date(Long.parseLong(timeString));
			Date now = new Date();
			if (Math.abs((now.getTime() - date.getTime())) >= 60000) {
				throw new ExceptionTokenExpired();
			}
			if (Config.token().isInitialManager(flag)) {
				throw new ExceptionAdmin();
			}

			Business business = new Business(emc);
			String personId = business.person().getWithCredential(flag);
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionPersonNotExist(flag);
			}
			Person person = emc.find(personId, Person.class);
			Wo wo = Wo.copier.copy(person);
			List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
			wo.setRoleList(roles);
			TokenType tokenType = TokenType.user;
			if (business.organization().person().hasRole(person.getDistinguishedName(),
					OrganizationDefinition.Manager)) {
				tokenType = TokenType.manager;
			}
			EffectivePerson effective = new EffectivePerson(wo.getDistinguishedName(), tokenType,
					Config.token().getCipher());
			wo.setToken(effective.getToken());
			HttpToken httpToken = new HttpToken();
			httpToken.setToken(request, response, effective);
			audit.log(person.getDistinguishedName(), "登录");
			result.setData(wo);
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private String token;
		private String client;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

	}

	public static class Wo extends Person {

		private static final long serialVersionUID = 4901269474728548509L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
		static {
			Excludes.add("password");
		}

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null, Excludes);

		private String token;
		private List<String> roleList;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}
	}

}