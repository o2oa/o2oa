package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class ApplicationProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;
	
	@FieldDescribe("应用默认表单")
	private String defaultForm;

	public String getDefaultForm() {
		return defaultForm;
	}

	public void setDefaultForm(String defaultForm) {
		this.defaultForm = defaultForm;
	}

}
