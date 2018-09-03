package o2.collect.assemble.jaxrs.unit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.wx.pwd.CheckStrength;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;

import o2.base.core.project.config.Config;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionResetPassword extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionResetPassword.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Unit unit = this.unitExist(business, wi.getName(), null);
			if (null == unit) {
				throw new ExceptionUnitNotExist(wi.getName());
			}
			if (StringUtils.isEmpty(wi.getPassword())) {
				throw new ExceptionPasswordEmpty();
			}
			boolean tag = true;
			Wo wo = new Wo();
			wo.setValue(false);
			if (effectivePerson.isNotManager()) {
				/* 管理员可以跳过这部分的设置 */
				if (!ListTools.contains(unit.getControllerMobileList(), wi.getMobile())) {
					logger.print("手机:{},不在登记的具有管理权限的手机中,组织:{}.", wi.getMobile(), unit.getName());
					tag = false;
					// throw new ExceptionMobileNotInControllerMobileList(wi.getMobile());
				}
				if (CheckStrength.checkPasswordStrength(wi.getPassword()) < 4) {
					logger.print("修改密码强度不足,组织:{}.", unit.getName());
					tag = false;
				}
				if (!business.validateCode(wi.getMobile(), wi.getCodeAnswer(), null, true)) {
					logger.print("验证码错误,手机:{},组织:{}.", wi.getMobile(), unit.getName());
					tag = false;
				}
			}
			if (tag) {
				business.entityManagerContainer().beginTransaction(Unit.class);
				unit.setPassword(Crypto.encrypt(wi.getPassword(), Config.token().getKey()));
				business.entityManagerContainer().check(unit, CheckPersistType.all);
				business.entityManagerContainer().commit();
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("密码")
		private String password;

		@FieldDescribe("管理手机号")
		private String mobile;

		@FieldDescribe("短信验证码")
		private String codeAnswer;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getCodeAnswer() {
			return codeAnswer;
		}

		public void setCodeAnswer(String codeAnswer) {
			this.codeAnswer = codeAnswer;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
