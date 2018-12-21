package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Merge;

public class WrapOutMerge extends Merge {

	private static final long serialVersionUID = 5007599746571282452L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
