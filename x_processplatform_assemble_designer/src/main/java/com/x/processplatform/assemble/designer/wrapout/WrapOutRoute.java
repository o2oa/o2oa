package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Route;

@Wrap(Route.class)
public class WrapOutRoute extends Route {

	private static final long serialVersionUID = 6333586002792120317L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
