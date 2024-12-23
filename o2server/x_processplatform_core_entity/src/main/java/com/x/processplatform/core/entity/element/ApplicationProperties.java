package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.ArrayList;
import java.util.List;

public class ApplicationProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("应用默认表单")
	private String defaultForm;

	@FieldDescribe("流程维护身份,如果无法找到处理身份默认的流程处理身份.")
	private String maintenanceIdentity;

	@FieldDescribe("当前应用下所以流程的流程实例维护者列表")
	private List<String> maintainerList;

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

	public List<String> getMaintainerList() {
		return maintainerList == null ? new ArrayList<>() : maintainerList;
	}

	public void setMaintainerList(List<String> maintainerList) {
		this.maintainerList = maintainerList;
	}
}
