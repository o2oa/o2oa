package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Merge;

@Wrap(Merge.class)
public class WrapOutMerge extends Merge {

	private static final long serialVersionUID = 2668844008956239077L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
