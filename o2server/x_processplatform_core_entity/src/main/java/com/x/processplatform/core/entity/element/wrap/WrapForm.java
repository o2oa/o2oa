package com.x.processplatform.core.entity.element.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Form;

public class WrapForm extends Form {

	private static final long serialVersionUID = -7244816539971035609L;

	public static WrapCopier<Form, WrapForm> outCopier = WrapCopierFactory.wo(Form.class, WrapForm.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapForm, Form> inCopier = WrapCopierFactory.wi(WrapForm.class, Form.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

	private List<WrapFormField> formFieldList = new ArrayList<>();

	public List<WrapFormField> getFormFieldList() {
		return formFieldList;
	}

	public void setFormFieldList(List<WrapFormField> formFieldList) {
		this.formFieldList = formFieldList;
	}

}
