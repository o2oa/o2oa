package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Split;

public class WrapOutSplit extends Split {

	private static final long serialVersionUID = 2746872526189840000L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
