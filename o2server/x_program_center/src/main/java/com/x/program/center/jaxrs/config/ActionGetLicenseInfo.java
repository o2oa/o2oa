package com.x.program.center.jaxrs.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

class ActionGetLicenseInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetLicenseInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception{

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Wo wo = new Wo();
		wo.setVersion(Config.version());
		try {
			Class<?> licenseToolsCls = Class.forName("com.x.base.core.lc.LcTools");
			String info = (String) MethodUtils.invokeStaticMethod(licenseToolsCls, "getInfo");
			if(StringUtils.isNotBlank(info)){
				wo = XGsonBuilder.instance().fromJson(info, Wo.class);
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("客户名称.")
		private String name;
		@FieldDescribe("顶层组织名称.")
		private String unitName;
		@FieldDescribe("邮箱.")
		private String email;
		@FieldDescribe("版本号.")
		private String version;
		@FieldDescribe("版本类型.")
		private String versionType;
		@FieldDescribe("授权模式.")
		private String model;
		@FieldDescribe("授权时间.")
		private Date startTime;
		@FieldDescribe("授权到期时间.")
		private Date expireTime;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getVersionType() {
			return versionType;
		}

		public void setVersionType(String versionType) {
			this.versionType = versionType;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getExpireTime() {
			return expireTime;
		}

		public void setExpireTime(Date expireTime) {
			this.expireTime = expireTime;
		}
	}

}
