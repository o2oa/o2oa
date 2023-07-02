package com.x.cms.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.cms.core.entity.element.View;

public class WrapInView extends View {

	private static final long serialVersionUID = -5237741099036357033L;
	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsUnmodify);

}
