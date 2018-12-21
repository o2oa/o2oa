package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Parallel;

public class WrapOutParallel extends Parallel {

	private static final long serialVersionUID = 3452734679516289443L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
