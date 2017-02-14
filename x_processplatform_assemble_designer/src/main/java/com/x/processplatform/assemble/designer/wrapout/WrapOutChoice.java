package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Choice;

@Wrap(Choice.class)
public class WrapOutChoice extends Choice {

	private static final long serialVersionUID = 1985192289895811979L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
