package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Parallel;

@Wrap(Parallel.class)
public class WrapOutParallel extends Parallel {

	private static final long serialVersionUID = 75933203079688664L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
