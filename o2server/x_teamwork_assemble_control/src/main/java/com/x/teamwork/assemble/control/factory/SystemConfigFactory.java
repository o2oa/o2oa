package com.x.teamwork.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.SystemConfig;
import com.x.teamwork.core.entity.SystemConfig_;

public class SystemConfigFactory extends AbstractFactory {
	
	public SystemConfigFactory( Business business) throws Exception {
		super(business);
	}

	public SystemConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, SystemConfig.class, ExceptionWhen.none);
	}

	public List<SystemConfig> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(SystemConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemConfig> cq = cb.createQuery(SystemConfig.class);
		@SuppressWarnings("unused")
		Root<SystemConfig> root = cq.from( SystemConfig.class);
		return em.createQuery(cq).getResultList();
	}

	public List<SystemConfig> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<SystemConfig>();
		}
		EntityManager em = this.entityManagerContainer().get(SystemConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemConfig> cq = cb.createQuery(SystemConfig.class);
		Root<SystemConfig> root = cq.from(SystemConfig.class);
		Predicate p = root.get( SystemConfig_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByCode(String code) throws Exception {
		if( code == null || code.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(SystemConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<SystemConfig> root = cq.from(SystemConfig.class);
		Predicate p = cb.equal(root.get(SystemConfig_.configCode),  code);
		cq.select(root.get(SystemConfig_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public SystemConfig getWithConfigCode( String configCode ) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			return null;
		}
		SystemConfig attendanceSetting = null;
		List<SystemConfig> settingList = null;
		EntityManager em = this.entityManagerContainer().get(SystemConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SystemConfig> cq = cb.createQuery(SystemConfig.class);
		Root<SystemConfig> root = cq.from(SystemConfig.class);
		Predicate p = cb.equal(root.get( SystemConfig_.configCode ),  configCode );
		settingList = em.createQuery(cq.where(p)).getResultList();
		if( settingList != null && settingList.size() > 0 ){
			attendanceSetting = settingList.get(0);
		}
		return attendanceSetting;
	}
	
}