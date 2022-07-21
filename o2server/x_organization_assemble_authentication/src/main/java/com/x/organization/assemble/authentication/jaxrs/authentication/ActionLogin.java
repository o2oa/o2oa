//package com.x.organization.assemble.authentication.jaxrs.authentication;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.gson.JsonElement;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.annotation.FieldDescribe;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.gson.GsonPropertyObject;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.organization.assemble.authentication.Business;
//import com.x.organization.core.entity.Person;
//
//@Deprecated(forRemoval = true)
//class ActionLogin extends BaseAction {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(ActionLogin.class);
//
//	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
//			JsonElement jsonElement) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			ActionResult<Wo> result = new ActionResult<>();
//			Business business = new Business(emc);
//			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//			Wo wo = new Wo();
//			check(wi);
//			LOGGER.debug("user:{}, try to login.", wi.getCredential());
//			String password = wi.getPassword();
//			if (Config.token().isInitialManager(wi.getCredential())) {
//				if (!Config.token().verifyPassword(wi.getCredential(), password)) {
//					throw new ExceptionPersonNotExistOrInvalidPassword();
//				}
//				wo = this.manager(request, response, wi.getCredential(), Wo.class);
//			} else {
//				// 普通用户登录,也有可能拥有管理员角色.增加相同标识(name允许重复)的认证
//				List<String> people = this.listWithCredential(business, wi.getCredential());
//				Person person = null;
//				if (people.isEmpty()) {
//					throw new ExceptionPersonNotExistOrInvalidPassword();
//				} else if (people.size() == 1) {
//					person = this.personLogin(business, people.get(0), password);
//				} else {
//					person = this.peopleLogin(business, people, password);
//				}
//				if (null == person) {
//					throw new ExceptionPersonNotExistOrInvalidPassword();
//				} else {
//					wo = this.user(request, response, business, person, Wo.class);
//				}
//			}
//			result.setData(wo);
//			return result;
//		}
//	}
//
//	private void check(Wi wi) throws ExceptionCredentialEmpty, ExceptionPasswordEmpty {
//		if (StringUtils.isEmpty(wi.getCredential())) {
//			throw new ExceptionCredentialEmpty();
//		}
//		if (StringUtils.isEmpty(wi.getPassword())) {
//			throw new ExceptionPasswordEmpty();
//		}
//	}
//
//	public static class Wi extends GsonPropertyObject {
//
//		private static final long serialVersionUID = -6099815091986193292L;
//
//		@FieldDescribe("凭证")
//		private String credential;
//
//		@FieldDescribe("密码")
//		private String password;
//
//		public String getPassword() {
//			return password;
//		}
//
//		public void setPassword(String password) {
//			this.password = password;
//		}
//
//		public String getCredential() {
//			return credential;
//		}
//
//		public void setCredential(String credential) {
//			this.credential = credential;
//		}
//
//	}
//
//	public static class Wo extends AbstractWoAuthentication {
//
//		private static final long serialVersionUID = -5397186305200946501L;
//
//	}
//}
