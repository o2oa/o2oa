package com.x.query.service.processing.factory;

import java.util.Optional;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.service.processing.AbstractFactory;
import com.x.query.service.processing.Business;

public class ApplicationFactory extends AbstractFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationFactory.class);

	public ApplicationFactory(Business business) throws Exception {
		super(business);
	}

	public Optional<Application> get(String id) {
		CacheCategory cacheCategory = new CacheCategory(Application.class);
		CacheKey cacheKey = new CacheKey(id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		Application o = null;
		if (optional.isPresent()) {
			return Optional.of((Application) optional.get());
		} else {
			try {
				o = this.entityManagerContainer().find(id, Application.class);
				if (o != null) {
					this.entityManagerContainer().get(Application.class).detach(o);
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
