package com.x.query.assemble.surface.factory;

import java.util.Optional;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.query.assemble.surface.AbstractFactory;
import com.x.query.assemble.surface.Business;

public class AppInfoFactory extends AbstractFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoFactory.class);

	public AppInfoFactory(Business business) throws Exception {
		super(business);
	}

	public Optional<AppInfo> get(String id) {
		CacheCategory cacheCategory = new CacheCategory(AppInfo.class);
		CacheKey cacheKey = new CacheKey(id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		AppInfo o = null;
		if (optional.isPresent()) {
			return Optional.of((AppInfo) optional.get());
		} else {
			try {
				o = this.entityManagerContainer().find(id, AppInfo.class);
				if (o != null) {
					this.entityManagerContainer().get(AppInfo.class).detach(o);
					CacheManager.put(cacheCategory, cacheKey, o);
					return Optional.of(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return Optional.empty();
		}
	}
}
