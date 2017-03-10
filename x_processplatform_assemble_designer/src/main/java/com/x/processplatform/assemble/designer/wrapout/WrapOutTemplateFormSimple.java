package com.x.processplatform.assemble.designer.wrapout;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.TemplateForm;

@Wrap(TemplateForm.class)
public class WrapOutTemplateFormSimple extends WrapOutTemplateForm {

	private static final long serialVersionUID = 2735294690359613313L;

	static {
		Excludes.add("data");
		Excludes.add("mobileData");
	}

}
