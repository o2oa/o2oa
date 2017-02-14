package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Begin;

@Wrap(Begin.class)
public class WrapOutBegin extends Begin {

	private static final long serialVersionUID = 1183685344142941533L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
