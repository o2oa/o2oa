package o2.collect.assemble.jaxrs.module;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.Module;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Module module = emc.find(id, Module.class);
			if (null == module) {
				throw new ExceptionEntityNotExist(id, Module.class);
			}
			Wo wo = Wo.copier.copy(module);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Module {

		private static final long serialVersionUID = -4000191514240350631L;
		static WrapCopier<Module, Wo> copier = WrapCopierFactory.wo(Module.class, Wo.class, null, Wo.FieldsInvisible);

	}
}