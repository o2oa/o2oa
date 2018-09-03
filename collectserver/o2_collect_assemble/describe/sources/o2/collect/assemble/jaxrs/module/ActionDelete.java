package o2.collect.assemble.jaxrs.module;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Module;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Module module = emc.find(id, Module.class);
			if (null == module) {
				throw new ExceptionEntityNotExist(id, Module.class);
			}
			emc.beginTransaction(Module.class);
			emc.remove(module, CheckRemoveType.all);
			emc.commit();
			business.moduleCache().removeAll();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			ApplicationCache.notify(Module.class);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}