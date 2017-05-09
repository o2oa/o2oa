package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.viewfieldconfig.WrapInViewFieldConfig;
import com.x.cms.core.entity.element.View;

@Wrap(View.class)
public class WrapInView extends View {
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

	private List<WrapInViewFieldConfig> fields = null;

	public List<WrapInViewFieldConfig> getFields() {
		return fields;
	}

	public void setFields(List<WrapInViewFieldConfig> fields) {
		this.fields = fields;
	}

}