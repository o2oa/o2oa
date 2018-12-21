package com.x.calendar.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.calendar.assemble.control.AbstractFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_Setting;
import com.x.calendar.core.entity.Calendar_Setting_;


/**
 * 系统配置信息表基础功能服务类
 * @author O2LEE
 */
public class Calendar_SettingFactory extends AbstractFactory {
	
	public Calendar_SettingFactory( Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的Calendar_Setting配置信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Calendar_Setting.class, ExceptionWhen.none);
	}
	
	/**
	 * 列示全部的Calendar_Setting配置信息列表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public List<Calendar_Setting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar_Setting> cq = cb.createQuery(Calendar_Setting.class);
		Root<Calendar_Setting> root = cq.from( Calendar_Setting.class);
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 列示指定Id的Calendar_Setting配置信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_Setting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<Calendar_Setting>();
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar_Setting> cq = cb.createQuery(Calendar_Setting.class);
		Root<Calendar_Setting> root = cq.from(Calendar_Setting.class);
		Predicate p = root.get( Calendar_Setting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据CODE列示指定Id的Calendar_Setting配置信息列表
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsByCode(String code) throws Exception {
		if( code == null || code.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_Setting> root = cq.from(Calendar_Setting.class);
		Predicate p = cb.equal(root.get(Calendar_Setting_.configCode),  code);
		cq.select(root.get(Calendar_Setting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据配置编码查询配置信息对象
	 * @param configCode
	 * @return
	 * @throws Exception
	 */
	public Calendar_Setting getWithConfigCode( String configCode ) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			return null;
		}
		Calendar_Setting attendanceSetting = null;
		List<Calendar_Setting> settingList = null;
		EntityManager em = this.entityManagerContainer().get(Calendar_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar_Setting> cq = cb.createQuery(Calendar_Setting.class);
		Root<Calendar_Setting> root = cq.from(Calendar_Setting.class);
		Predicate p = cb.equal(root.get( Calendar_Setting_.configCode ),  configCode );
		settingList = em.createQuery(cq.where(p)).getResultList();
		if( settingList != null && settingList.size() > 0 ){
			attendanceSetting = settingList.get(0);
		}
		return attendanceSetting;
	}
	
}