package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Manual;

public class WrapOutManual extends Manual {


	private static final long serialVersionUID = -5145199730047767525L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
