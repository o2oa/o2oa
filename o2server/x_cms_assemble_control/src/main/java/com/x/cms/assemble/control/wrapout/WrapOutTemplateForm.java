package com.x.cms.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.cms.assemble.control.Control;
import com.x.processplatform.core.entity.element.TemplateForm;

public class WrapOutTemplateForm extends TemplateForm {

	private static final long serialVersionUID = 1551592776065130757L;
	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Control control;

	public Control getControl() {
		return control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

}
