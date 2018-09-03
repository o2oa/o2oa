package o2.collect.assemble.jaxrs.device;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.Device;

class ActionListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, null, null, null, null,
					null, null, null, null, true, DESC);
			return result;
		}
	}

	public static class Wo extends Device {

		private static final long serialVersionUID = 6060455361328632654L;

		static WrapCopier<Device, Wo> copier = WrapCopierFactory.wo(Device.class, Wo.class,
				JpaObject.singularAttributeField(Device.class, true, true), null);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
