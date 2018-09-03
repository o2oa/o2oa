package o2.collect.assemble.jaxrs.device;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.DeviceType;
import o2.collect.core.entity.Device_;

public class ActionAccountBind extends BaseAction {

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (effectivePerson.isNotManager()) {
				if (!business.validateCode(wi.getMobile(), wi.getCode(), null, false)) {
					throw new ExceptionInvalidCode();
				}
			}
			String unitId = business.unit().getWithName(wi.getUnit(), null);
			if (StringUtils.isEmpty(unitId)) {
				throw new ExceptionUnitNotExist(wi.getUnit());
			}
			String accountId = business.account().getWithNameUnit(wi.getMobile(), unitId);
			if (StringUtils.isEmpty(accountId)) {
				throw new ExceptionAccountNotExist(wi.getMobile());
			}
			business.entityManagerContainer().beginTransaction(Device.class);
			this.removeExisted(business, wi.getName());
			Device device = new Device();
			device.setUnit(unitId);
			device.setAccount(accountId);
			device.setName(wi.getName());
			device.setDeviceType(wi.getDeviceType());
			business.entityManagerContainer().persist(device, CheckPersistType.all);
			business.entityManagerContainer().commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private String mobile;
		private String code;
		private String unit;
		private String name;
		private DeviceType deviceType;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public DeviceType getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(DeviceType deviceType) {
			this.deviceType = deviceType;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

	}

	public static class Wo extends WrapBoolean {
	}

	private void removeExisted(Business business, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Device> cq = cb.createQuery(Device.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.name), name);
		cq.select(root).where(p);
		List<Device> list = em.createQuery(cq).getResultList();
		if (!list.isEmpty()) {
			for (Device o : list) {
				business.entityManagerContainer().remove(o);
			}
		}
	}

}