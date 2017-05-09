package com.x.portal.assemble.designer.jaxrs.menu;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInMenu;
import com.x.portal.assemble.designer.wrapout.WrapOutMenu;
import com.x.portal.core.entity.Menu;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends AbstractJaxrsAction {

	static Ehcache cache = ApplicationCache.instance().getCache(Menu.class);

	static BeanCopyTools<Menu, WrapOutMenu> outCopier = BeanCopyToolsBuilder.create(Menu.class, WrapOutMenu.class, null,
			WrapOutMenu.Excludes);

	static BeanCopyTools<WrapInMenu, Menu> inCopier = BeanCopyToolsBuilder.create(WrapInMenu.class, Menu.class, null,
			WrapInMenu.Excludes);

	static BeanCopyTools<WrapInMenu, Menu> updateCopier = BeanCopyToolsBuilder.create(WrapInMenu.class, Menu.class,
			null, JpaObject.FieldsUnmodifies);

	void checkName(Business business, Menu menu) throws Exception {
		if (StringUtils.isEmpty(menu.getName())) {
			throw new NameEmptyException();
		}
		String id = business.menu().getWithNameWithPortal(menu.getName(), menu.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(menu.getId(), id))) {
			throw new NameDuplicateException(menu.getName());
		}
		id = business.menu().getWithAliasWithPortal(menu.getName(), menu.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(menu.getId(), id))) {
			throw new NameDuplicateWithAliasException(menu.getName());
		}
	}

	void checkAlias(Business business, Menu menu) throws Exception {
		if (StringUtils.isEmpty(menu.getAlias())) {
			return;
		}
		String id = business.menu().getWithAliasWithPortal(menu.getAlias(), menu.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(menu.getId(), id))) {
			throw new AliasDuplicateException(menu.getAlias());
		}
		id = business.menu().getWithNameWithPortal(menu.getAlias(), menu.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(menu.getId(), id))) {
			throw new AliasDuplicateWithNameException(menu.getAlias());
		}
	}
}