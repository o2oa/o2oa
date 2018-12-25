package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Route;
public class WrapOutRoute extends Route {

	private static final long serialVersionUID = 4309969270030957709L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
