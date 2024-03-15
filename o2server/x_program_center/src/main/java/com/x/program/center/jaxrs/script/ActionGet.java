package com.x.program.center.jaxrs.script;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.Script;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			CacheKey cacheKey = new CacheKey(id);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new ExceptionEntityNotExist(id);
				}
				wo = Wo.copier.copy(script);
				CacheManager.put(cache, cacheKey, wo);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -1185168961925443180L;
		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
