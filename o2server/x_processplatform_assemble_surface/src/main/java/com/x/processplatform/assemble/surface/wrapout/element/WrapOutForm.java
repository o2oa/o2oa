package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Form;

public class WrapOutForm extends Form {

	private static final long serialVersionUID = 8714459358196550018L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
