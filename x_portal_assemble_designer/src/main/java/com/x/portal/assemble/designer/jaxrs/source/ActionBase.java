package com.x.portal.assemble.designer.jaxrs.source;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInSource;
import com.x.portal.assemble.designer.wrapout.WrapOutSource;
import com.x.portal.core.entity.Source;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends AbstractJaxrsAction {

	static Ehcache cache = ApplicationCache.instance().getCache(Source.class);

	static BeanCopyTools<Source, WrapOutSource> outCopier = BeanCopyToolsBuilder.create(Source.class,
			WrapOutSource.class, null, WrapOutSource.Excludes);

	static BeanCopyTools<WrapInSource, Source> inCopier = BeanCopyToolsBuilder.create(WrapInSource.class, Source.class,
			null, WrapInSource.Excludes);

	static BeanCopyTools<WrapInSource, Source> updateCopier = BeanCopyToolsBuilder.create(WrapInSource.class,
			Source.class, null, JpaObject.FieldsUnmodifies);

	void checkName(Business business, Source source) throws Exception {
		if (StringUtils.isEmpty(source.getName())) {
			throw new NameEmptyException();
		}
		String id = business.source().getWithNameWithPortal(source.getName(), source.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(source.getId(), id))) {
			throw new NameDuplicateException(source.getName());
		}
		id = business.source().getWithAliasWithPortal(source.getName(), source.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(source.getId(), id))) {
			throw new NameDuplicateWithAliasException(source.getName());
		}
	}

	void checkAlias(Business business, Source source) throws Exception {
		if (StringUtils.isEmpty(source.getAlias())) {
			return;
		}
		String id = business.source().getWithAliasWithPortal(source.getAlias(), source.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(source.getId(), id))) {
			throw new AliasDuplicateException(source.getAlias());
		}
		id = business.source().getWithNameWithPortal(source.getAlias(), source.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(source.getId(), id))) {
			throw new AliasDuplicateWithNameException(source.getAlias());
		}
	}
}