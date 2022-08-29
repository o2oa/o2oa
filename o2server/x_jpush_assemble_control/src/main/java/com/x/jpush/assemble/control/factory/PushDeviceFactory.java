package com.x.jpush.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.jpush.assemble.control.AbstractFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.PushDevice;
import com.x.jpush.core.entity.PushDevice_;

/**
 * 示例数据表基础功能服务类
 */
public class PushDeviceFactory extends AbstractFactory {

	public PushDeviceFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 根据unique 查询设备
	 * 
	 * @param unique
	 * @return
	 * @throws Exception
	 */
	public PushDevice findDeviceByUnique(String unique) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PushDevice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PushDevice> query = cb.createQuery(PushDevice.class);
		Root<PushDevice> root = query.from(PushDevice.class);
		Predicate p = cb.equal(root.get(PushDevice_.unique), unique);
		query.select(root).where(p);
		List<PushDevice> list = em.createQuery(query).getResultList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 设备是否存在
	 * 
	 * @param unique md5(deviceType+deviceId+pushType+person)
	 * @return
	 * @throws Exception
	 */
	public boolean existDeviceUnique(String unique) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PushDevice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PushDevice> query = cb.createQuery(PushDevice.class);
		Root<PushDevice> root = query.from(PushDevice.class);
		Predicate p = cb.equal(root.get(PushDevice_.unique), unique);
		query.select(root).where(p);
		List<PushDevice> list = em.createQuery(query).getResultList();
		if (list != null && !list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 查询用户的极光推送的设备列表
	 * 
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<PushDevice> listJpushDevice(String person) throws Exception {
		return listDevice(person, PushDevice.PUSH_TYPE_JPUSH);
	}

	/**
	 * 查询用户的华为推送的设备列表
	 * 
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<PushDevice> listHuaweiDevice(String person) throws Exception {
		return listDevice(person, PushDevice.PUSH_TYPE_HUAWEI);
	}

	private List<PushDevice> listDevice(String person, String pushType) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PushDevice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PushDevice> query = cb.createQuery(PushDevice.class);
		Root<PushDevice> root = query.from(PushDevice.class);
		Predicate p = cb.equal(root.get(PushDevice_.person), person);
		p = cb.and(p, cb.equal(root.get(PushDevice_.pushType), pushType));
		query.select(root).where(p);
		return em.createQuery(query).getResultList();
	}

}