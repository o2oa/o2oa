package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Manual;

@Wrap(Manual.class)
public class WrapOutManual extends Manual {

	private static final long serialVersionUID = 4037202596279188116L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
	
}
