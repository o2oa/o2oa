package com.x.general.assemble.control.jaxrs.securityclearance;

import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setDefaultSubjectSecurityClearance(Config.ternaryManagement().getDefaultSubjectSecurityClearance());
		wo.setSecurityClearanceEnable(Config.ternaryManagement().getSecurityClearanceEnable());
		wo.setObjectSecurityClearance(Config.ternaryManagement().getObjectSecurityClearance());
		wo.setSubjectSecurityClearance(Config.ternaryManagement().getSubjectSecurityClearance());
		wo.setSystemSecurityClearance(Config.ternaryManagement().getSystemSecurityClearance());
		result.setData(wo);
		return result;
	}

	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7851469182619330809L;

		@FieldDescribe("主体密级标识配置.")
		private Map<String, Integer> subjectSecurityClearance;
		@FieldDescribe("客体密级标识配置.")
		private Map<String, Integer> objectSecurityClearance;
		@FieldDescribe("是否启用密级标识.")
		private Boolean securityClearanceEnable;
		@FieldDescribe("默认主体密级标识.")
		private Integer defaultSubjectSecurityClearance;
		@FieldDescribe("系统密级标识.")
		private Integer systemSecurityClearance;

		public Map<String, Integer> getSubjectSecurityClearance() {
			return subjectSecurityClearance;
		}

		public void setSubjectSecurityClearance(Map<String, Integer> subjectSecurityClearance) {
			this.subjectSecurityClearance = subjectSecurityClearance;
		}

		public Map<String, Integer> getObjectSecurityClearance() {
			return objectSecurityClearance;
		}

		public void setObjectSecurityClearance(Map<String, Integer> objectSecurityClearance) {
			this.objectSecurityClearance = objectSecurityClearance;
		}

		public Boolean getSecurityClearanceEnable() {
			return securityClearanceEnable;
		}

		public void setSecurityClearanceEnable(Boolean securityClearanceEnable) {
			this.securityClearanceEnable = securityClearanceEnable;
		}

		public Integer getDefaultSubjectSecurityClearance() {
			return defaultSubjectSecurityClearance;
		}

		public void setDefaultSubjectSecurityClearance(Integer defaultSubjectSecurityClearance) {
			this.defaultSubjectSecurityClearance = defaultSubjectSecurityClearance;
		}

		public Integer getSystemSecurityClearance() {
			return systemSecurityClearance;
		}

		public void setSystemSecurityClearance(Integer systemSecurityClearance) {
			this.systemSecurityClearance = systemSecurityClearance;
		}

	}

}
