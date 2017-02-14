package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Manual;

@Wrap(Manual.class)
public class WrapOutManual extends Manual {


	private static final long serialVersionUID = -5145199730047767525L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
