package com.x.processplatform.assemble.designer.wrapin;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.FormField;

@Wrap(FormField.class)
public class WrapInFormField extends FormField {

	private static final long serialVersionUID = -4951139918340180031L;
	public static List<String> Excludes = JpaObject.FieldsUnmodifies;

	static {
		Excludes.add(DISTRIBUTEFACTOR);
		Excludes.add("updateTime");
		Excludes.add("createTime");
		Excludes.add("sequence");
		Excludes.add("lastUpdatePerson");
		Excludes.add("lastUpdateTime");
	}

}