package com.x.cms.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.cms.core.entity.element.Form;

public class WrapOutFormField extends Form {

	private static final long serialVersionUID = -3041412588191150480L;
	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
