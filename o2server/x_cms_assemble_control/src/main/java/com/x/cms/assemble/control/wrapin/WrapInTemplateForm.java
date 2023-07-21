package com.x.cms.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.TemplateForm;

public class WrapInTemplateForm extends TemplateForm {

	private static final long serialVersionUID = 2091352200751493447L;
	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsUnmodify);

}
