package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			String credential = wi.getCredential();
			logger.debug("user:{}, try to login.", credential);
			String password = wi.getPassword();
			if (StringUtils.isEmpty(credential)) {
				throw new ExceptionCredentialEmpty();
			}
			if (StringUtils.isEmpty(password)) {
				throw new ExceptionPasswordEmpty();
			}
			if (Config.token().isInitialManager(credential)) {
				if (!StringUtils.equals(Config.token().getPassword(), password)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				wo = this.manager(request, response, business, Wo.class);
			} else {
				/** 普通用户登录,也有可能拥有管理员角色 */
				String personId = business.person().getWithCredential(credential);
				if (StringUtils.isEmpty(personId)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				Person o = emc.find(personId, Person.class);
				/** 先判断是否使用superPermission登录 */
				if (BooleanUtils.isTrue(Config.person().getSuperPermission())
						&& StringUtils.equals(Config.token().getPassword(), password)) {
					logger.warn("user: {} use superPermission.", credential);
				} else if (!StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), o.getPassword())) {
					/* 普通用户认证密码 */
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				wo = this.user(request, response, business, o, Wo.class);
				audit.log(o.getDistinguishedName());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("凭证")
		private String credential;

		@FieldDescribe("密码")
		private String password;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -5397186305200946501L;

	}
}