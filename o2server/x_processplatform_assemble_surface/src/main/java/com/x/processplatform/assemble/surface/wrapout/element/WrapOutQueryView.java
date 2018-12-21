package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.QueryView;

public class WrapOutQueryView extends QueryView {

	private static final long serialVersionUID = 2886873983211744188L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
