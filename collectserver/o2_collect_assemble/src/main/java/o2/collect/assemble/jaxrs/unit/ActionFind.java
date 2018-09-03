package o2.collect.assemble.jaxrs.unit;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.DeviceType;
import o2.collect.core.entity.Device_;
import o2.collect.core.entity.Unit;

class ActionFind extends BaseAction {
	
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name, String accountName, String deviceName,
			DeviceType deviceType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Unit unit = emc.flag(name, Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(name);
			}
			Account account = business.account().getWithNameUnitObject(accountName, unit.getId());
			if (null == account) {
				throw new ExceptionAccountNotExist(accountName);
			}
			Device device = this.getDevice(business, deviceName, account.getId());
			emc.beginTransaction(Device.class);
			if (null == device) {
				device = new Device();
				device.setName(deviceName);
				device.setAccount(account.getId());
				device.setUnit(unit.getId());
				device.setConnectTime(new Date());
				emc.persist(device, CheckPersistType.all);
			} else {
				device.setDeviceType(deviceType);
				device.setConnectTime(new Date());
			}
			emc.commit();
			Wo wo = Wo.copier.copy(unit);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class,
				JpaObject.singularAttributeField(Unit.class, true, true), null);

	}

	private Device getDevice(Business business, String name, String accountId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Device> cq = cb.createQuery(Device.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.name), name);
		p = cb.and(p, cb.equal(root.get(Device_.account), accountId));
		cq.select(root).where(p);
		List<Device> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
}
