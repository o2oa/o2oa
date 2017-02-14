package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Split;

@Wrap(Split.class)
public class WrapOutSplit extends Split {

	private static final long serialVersionUID = 5651378255582879520L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
