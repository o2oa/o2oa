package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Route;

public class WrapRoute extends Route {

	private static final long serialVersionUID = 6333586002792120317L;

	public static final WrapCopier<Route, WrapRoute> outCopier = WrapCopierFactory.wo(Route.class, WrapRoute.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapRoute, Route> inCopier = WrapCopierFactory.wi(WrapRoute.class, Route.class, null,
			JpaObject.FieldsUnmodifyExcludeId, false);
}
