package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Invoke;

public class WrapOutInvoke extends Invoke {

	private static final long serialVersionUID = -6918382714118518231L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
