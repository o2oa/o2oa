package o2.collect.assemble.jaxrs.device;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

public class ActionListWithUnit extends BaseAction {

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String unitId = business.unit().flag(flag);
			if (StringUtils.isEmpty(unitId)) {
				throw new ExceptionUnitNotExist(flag);
			}
			Unit unit = emc.find(unitId, Unit.class);
			if ((!effectivePerson.isManager()) && (!effectivePerson.isUser(unit.getName()))) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<String> ids = business.device().listWithUnit(unitId);
			List<Wo> wos = Wo.copier.copy(business.entityManagerContainer().list(Device.class, ids));
			result.setData(wos);
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
