package com.x.jpush.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.jpush.assemble.control.AbstractFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.PushDevice;
import com.x.jpush.core.entity.PushDevice_;
import com.x.jpush.core.entity.SampleEntityClassName;
import com.x.jpush.core.entity.SampleEntityClassName_;

/**
 * 示例数据表基础功能服务类
 */
public class SampleEntityClassNameFactory extends AbstractFactory {
	
	public SampleEntityClassNameFactory( Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的SampleEntityClassName信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SampleEntityClassName get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, SampleEntityClassName.class, ExceptionWhen.none);
	}
	
	/**
	 * 根据ID列示指定的SampleEntityClassName信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<SampleEntityClassName> list( List<String> ids, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(SampleEntityClassName.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleEntityClassName> cq = cb.createQuery(SampleEntityClassName.class);
		Root<SampleEntityClassName> root = cq.from( SampleEntityClassName.class);
		Predicate p = root.get(SampleEntityClassName_.id).in(ids);
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 列示全部的SampleEntityClassName信息列表
	 * @param maxCount  返回的最大条目数
	 * @return
	 * @throws Exception
	 */
	public List<SampleEntityClassName> listAll( Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(SampleEntityClassName.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleEntityClassName> cq = cb.createQuery(SampleEntityClassName.class);
		Root<SampleEntityClassName> root = cq.from( SampleEntityClassName.class);
		//根据数据更新时间降序
		cq.orderBy( cb.desc( root.get( SampleEntityClassName_.updateTime ) ) );
		return em.createQuery(cq).setMaxResults(maxCount).getResultList();
	}

	/**
	 * 根据unique 查询设备
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
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<PushDevice> listJpushDevice(String person) throws Exception {
		return listDevice(person, PushDevice.PUSH_TYPE_JPUSH);
	}

	/**
	 * 查询用户的华为推送的设备列表
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