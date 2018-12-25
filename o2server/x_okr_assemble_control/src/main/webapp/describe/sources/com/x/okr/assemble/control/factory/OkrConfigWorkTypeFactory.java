package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrConfigWorkType;
import com.x.okr.entity.OkrConfigWorkType_;

/**
 * 类   名：OkrConfigWorkTypeFactory<br/>
 * 实体类：OkrConfigWorkType<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrConfigWorkTypeFactory extends AbstractFactory {

	public OkrConfigWorkTypeFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrConfigWorkType实体信息对象" )
	public OkrConfigWorkType get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrConfigWorkType.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrConfigWorkType实体信息列表" )
	public List<OkrConfigWorkType> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigWorkType.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigWorkType> cq = cb.createQuery(OkrConfigWorkType.class);
		@SuppressWarnings("unused")
		Root<OkrConfigWorkType> root = cq.from( OkrConfigWorkType.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrConfigWorkType实体信息列表" )
	public List<OkrConfigWorkType> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrConfigWorkType>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrConfigWorkType.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigWorkType> cq = cb.createQuery(OkrConfigWorkType.class);
		Root<OkrConfigWorkType> root = cq.from(OkrConfigWorkType.class);
		Predicate p = root.get(OkrConfigWorkType_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}
