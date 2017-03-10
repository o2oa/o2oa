package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Form;

@Wrap(Form.class)
public class WrapInForm extends Form {

	private static final long serialVersionUID = 4289841165185269299L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

	private List<WrapInFormField> formFieldList;

	static {
		Excludes.add(DISTRIBUTEFACTOR);
		Excludes.add("updateTime");
		Excludes.add("createTime");
		Excludes.add("sequence");
		// Excludes.add("lastUpdatePerson");
		// Excludes.add("lastUpdateTime");
	}

	public List<WrapInFormField> getFormFieldList() {
		return formFieldList;
	}

	public void setFormFieldList(List<WrapInFormField> formFieldList) {
		this.formFieldList = formFieldList;
	}

}