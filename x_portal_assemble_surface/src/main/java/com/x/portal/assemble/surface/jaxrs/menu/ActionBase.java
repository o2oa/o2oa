package com.x.portal.assemble.surface.jaxrs.menu;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.portal.assemble.surface.wrapout.WrapOutMenu;
import com.x.portal.core.entity.Menu;

abstract class ActionBase {

	static BeanCopyTools<Menu, WrapOutMenu> outCopier = BeanCopyToolsBuilder.create(Menu.class,
			WrapOutMenu.class, null, WrapOutMenu.Excludes);

}
