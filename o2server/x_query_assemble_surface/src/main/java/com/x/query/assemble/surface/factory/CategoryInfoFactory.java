package com.x.query.assemble.surface.factory;

import java.util.Optional;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.CategoryInfo;
import com.x.query.assemble.surface.AbstractFactory;
import com.x.query.assemble.surface.Business;

public class CategoryInfoFactory extends AbstractFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryInfoFactory.class);

	public CategoryInfoFactory(Business business) throws Exception {
		super(business);
	}

	public Optional<CategoryInfo> get(String id) {
		CacheCategory cacheCategory = new CacheCategory(CategoryInfo.class);
		CacheKey cacheKey = new CacheKey(id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		CategoryInfo o = null;
		if (optional.isPresent()) {
			return Optional.of((CategoryInfo) optional.get());
		} else {
			try {
				o = this.entityManagerContainer().find(id, CategoryInfo.class);
				if (o != null) {
					this.entityManagerContainer().get(CategoryInfo.class).detach(o);
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
