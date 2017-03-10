package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.TemplateForm;

@Wrap(TemplateForm.class)
public class WrapOutTemplateFormSimple extends WrapOutTemplateForm {

	private static final long serialVersionUID = 2735294690359613313L;

	static {
		Excludes.add("data");
		Excludes.add("mobileData");
	}

}
