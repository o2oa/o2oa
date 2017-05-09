package com.x.portal.assemble.designer.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInPage;
import com.x.portal.assemble.designer.wrapout.WrapOutPage;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends AbstractJaxrsAction {

	static Ehcache cache = ApplicationCache.instance().getCache(Page.class);

	static BeanCopyTools<Page, WrapOutPage> outCopier = BeanCopyToolsBuilder.create(Page.class, WrapOutPage.class, null,
			WrapOutPage.Excludes);

	static BeanCopyTools<WrapInPage, Page> inCopier = BeanCopyToolsBuilder.create(WrapInPage.class, Page.class, null,
			WrapInPage.Excludes);

	static BeanCopyTools<WrapInPage, Page> updateCopier = BeanCopyToolsBuilder.create(WrapInPage.class, Page.class, null,
			JpaObject.FieldsUnmodifies);

	void checkName(Business business, Page page) throws Exception {
		if (StringUtils.isEmpty(page.getName())) {
			throw new NameEmptyException();
		}
		String id = business.page().getWithNameWithPortal(page.getName(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new NameDuplicateException(page.getName());
		}
		id = business.page().getWithAliasWithPortal(page.getName(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new NameDuplicateWithAliasException(page.getName());
		}
	}

	void checkAlias(Business business, Page page) throws Exception {
		if (StringUtils.isEmpty(page.getAlias())) {
			return;
		}
		String id = business.page().getWithAliasWithPortal(page.getAlias(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new AliasDuplicateException(page.getAlias());
		}
		id = business.page().getWithNameWithPortal(page.getAlias(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new AliasDuplicateWithNameException(page.getAlias());
		}
	}

	boolean isBecomeFirstPage(Business business, Portal portal, Page page) throws Exception {
		if (business.page().isFirstPage(page)) {
			Page o = business.entityManagerContainer().find(portal.getFirstPage(), Page.class);
			if (null == o) {
				return true;
			}
		}
		return false;
	}
}