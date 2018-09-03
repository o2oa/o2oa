package o2.collect.assemble.jaxrs.unit;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Device_;
import o2.collect.core.entity.Unit;

class ActionFindOld20180507 extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name, String accountName, String deviceName)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Device device = this.getDeviceWithName(business, deviceName);
			if (null == device) {
				throw new ExceptionDeviceNotExist(deviceName);
			}
			Account account = emc.find(device.getAccount(), Account.class);
			if (null == account) {
				throw new ExceptionAccountNotExist(device.getAccount());
			}
			if (!StringUtils.equals(accountName, account.getName())) {
				throw new ExceptionAccountNotMatch(accountName);
			}
			Unit unit = emc.find(device.getUnit(), Unit.class);
			if (!StringUtils.equals(name, unit.getName())) {
				throw new ExceptionUnitNotMatch(name);
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

	private Device getDeviceWithName(Business business, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Device> cq = cb.createQuery(Device.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.name), name);
		cq.select(root).where(p);
		List<Device> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
}
