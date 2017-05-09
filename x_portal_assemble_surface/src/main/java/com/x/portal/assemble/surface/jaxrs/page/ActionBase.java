package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.portal.assemble.surface.wrapout.WrapOutPage;
import com.x.portal.core.entity.Page;

import net.sf.ehcache.Ehcache;

abstract class ActionBase {

	static BeanCopyTools<Page, WrapOutPage> outCopier = BeanCopyToolsBuilder.create(Page.class, WrapOutPage.class, null,
			WrapOutPage.Excludes);

	Ehcache pageCache = ApplicationCache.instance().getCache(Page.class);

}
