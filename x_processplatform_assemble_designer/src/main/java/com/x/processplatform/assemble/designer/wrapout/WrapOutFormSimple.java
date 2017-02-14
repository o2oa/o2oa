package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Form;

@Wrap(Form.class)
public class WrapOutFormSimple extends WrapOutForm {

	private static final long serialVersionUID = -7495725325510376323L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	static {
		Excludes.add("data");
		Excludes.add("mobileData");
	}

}
