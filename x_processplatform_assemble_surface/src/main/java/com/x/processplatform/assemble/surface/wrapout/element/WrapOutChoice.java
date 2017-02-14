package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Choice;

@Wrap(Choice.class)
public class WrapOutChoice extends Choice {

	private static final long serialVersionUID = -1907168588535775375L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
