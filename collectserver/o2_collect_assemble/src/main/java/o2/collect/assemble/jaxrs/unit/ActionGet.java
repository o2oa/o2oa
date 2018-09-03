package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.Unit;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Unit unit = emc.flag(flag, Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(flag);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotUser(unit.getName())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			Wo wo = Wo.copier.copy(unit);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
