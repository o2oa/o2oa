package com.x.processplatform.assemble.surface.factory.element;

import java.util.Optional;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Process;

public class BeginFactory extends ElementFactory {

	public BeginFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Begin pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public Begin pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Begin.class);
	}

	public Begin getWithProcess(Process process) throws Exception {
		Begin o = null;
		CacheCategory cacheCategory = new CacheCategory(Begin.class);
		CacheKey cacheKey = new CacheKey("getWithProcess", process.getId());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			o = (Begin) optional.get();
		} else {
			o = this.entityManagerContainer().firstEqual(Begin.class, Activity.process_FIELDNAME, process.getId());
			if (null != o) {
				this.entityManagerContainer().get(Begin.class).detach(o);
				CacheManager.put(cacheCategory, cacheKey, o);
			}
		}
		return o;
	}
}
