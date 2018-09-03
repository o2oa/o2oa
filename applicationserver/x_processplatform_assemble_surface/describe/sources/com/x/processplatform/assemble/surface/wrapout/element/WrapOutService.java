package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Service;

public class WrapOutService extends Service {

	private static final long serialVersionUID = -8322044803022612130L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
