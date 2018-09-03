package o2.collect.assemble.jaxrs.unit;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.Unit;

class ActionListPrev extends BaseAction {
	
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			result = this.standardListPrev(Wo.copier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
			return result;
		}
	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class,
				JpaObject.singularAttributeField(Unit.class, true, true), null);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
