package o2.collect.assemble.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import o2.collect.assemble.AbstractFactory;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Device_;

public class DeviceFactory extends AbstractFactory {

	public DeviceFactory(Business business) throws Exception {
		super(business);
	}

	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.name), name);
		cq.select(root.get(Device_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public List<String> listWithAccount(String accountId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.account), accountId);
		cq.select(root.get(Device_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithUnit(String unitId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.unit), unitId);
		cq.select(root.get(Device_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}