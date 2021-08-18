package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.FormField;

public class WrapFormField extends FormField {

	private static final long serialVersionUID = -4604394687890476004L;

	public static final WrapCopier<FormField, WrapFormField> outCopier = WrapCopierFactory.wo(FormField.class,
			WrapFormField.class, null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapFormField, FormField> inCopier = WrapCopierFactory.wi(WrapFormField.class,
			FormField.class, null, JpaObject.FieldsUnmodifyExcludeId, false);

}
