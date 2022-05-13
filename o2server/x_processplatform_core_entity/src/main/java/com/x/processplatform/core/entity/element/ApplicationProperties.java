package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class ApplicationProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("应用默认表单")
	private String defaultForm;

	@FieldDescribe("流程维护身份,如果无法找到处理身份默认的流程处理身份.")
	private String maintenanceIdentity;

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
	}

	public String getDefaultForm() {
		return defaultForm;
	}

	public void setDefaultForm(String defaultForm) {
		this.defaultForm = defaultForm;
	}

}
