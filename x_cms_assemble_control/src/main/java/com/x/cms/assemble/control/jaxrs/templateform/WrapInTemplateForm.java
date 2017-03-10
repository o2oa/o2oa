package com.x.cms.assemble.control.jaxrs.templateform;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.TemplateForm;

@Wrap(TemplateForm.class)
public class WrapInTemplateForm extends TemplateForm {

	private static final long serialVersionUID = 2091352200751493447L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}