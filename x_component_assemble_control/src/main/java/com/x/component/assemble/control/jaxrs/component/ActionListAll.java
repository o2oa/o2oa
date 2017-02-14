package com.x.component.assemble.control.jaxrs.component;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.component.assemble.control.Business;
import com.x.component.assemble.control.jaxrs.wrapout.WrapOutComponent;
import com.x.component.core.entity.Component;

public class ActionListAll extends ActionBase {
	protected List<WrapOutComponent> execute(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.component().list();
		List<Component> components = new ArrayList<Component>(emc.list(Component.class, ids));
		List<WrapOutComponent> wraps = outCopier.copy(components);
		SortTools.asc(wraps, false, "order");
		return wraps;
	}
}
