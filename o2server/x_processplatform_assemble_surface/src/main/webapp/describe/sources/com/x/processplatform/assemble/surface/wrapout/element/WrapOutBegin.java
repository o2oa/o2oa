package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Begin;

public class WrapOutBegin extends Begin {

	private static final long serialVersionUID = 2446418422019675597L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
