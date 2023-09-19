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
		wo.setDefaultSubject(Config.general().getDefaultSubjectSecurityClearance());
		wo.setEnable(Config.general().getSecurityClearanceEnable());
		wo.setObject(Config.general().getObjectSecurityClearance());
		wo.setSubject(Config.general().getSubjectSecurityClearance());
		result.setData(wo);
		return result;
	}

	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7851469182619330809L;

		@FieldDescribe("主体密级标识配置.")
		private Map<Integer, String> subject;
		@FieldDescribe("客体密级标识配置.")
		private Map<Integer, String> object;
		@FieldDescribe("是否启用密级标识.")
		private Boolean enable;
		@FieldDescribe("默认主体密级标识.")
		private Integer defaultSubject;

		public Map<Integer, String> getSubject() {
			return subject;
		}

		public void setSubject(Map<Integer, String> subject) {
			this.subject = subject;
		}

		public Map<Integer, String> getObject() {
			return object;
		}

		public void setObject(Map<Integer, String> object) {
			this.object = object;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public Integer getDefaultSubject() {
			return defaultSubject;
		}

		public void setDefaultSubject(Integer defaultSubject) {
			this.defaultSubject = defaultSubject;
		}

	}

}
