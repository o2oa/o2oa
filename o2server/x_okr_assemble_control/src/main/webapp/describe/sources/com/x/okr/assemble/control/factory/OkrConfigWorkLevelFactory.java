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
import com.x.okr.entity.OkrConfigWorkLevel;
import com.x.okr.entity.OkrConfigWorkLevel_;

/**
 * 类   名：OkrConfigWorkLevelFactory<br/>
 * 实体类：OkrConfigWorkLevel<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrConfigWorkLevelFactory extends AbstractFactory {

	public OkrConfigWorkLevelFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrConfigWorkLevel实体信息对象" )
	public OkrConfigWorkLevel get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrConfigWorkLevel.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrConfigWorkLevel实体信息列表" )
	public List<OkrConfigWorkLevel> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigWorkLevel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigWorkLevel> cq = cb.createQuery(OkrConfigWorkLevel.class);
		@SuppressWarnings("unused")
		Root<OkrConfigWorkLevel> root = cq.from( OkrConfigWorkLevel.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrConfigWorkLevel实体信息列表" )
	public List<OkrConfigWorkLevel> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrConfigWorkLevel>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrConfigWorkLevel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigWorkLevel> cq = cb.createQuery(OkrConfigWorkLevel.class);
		Root<OkrConfigWorkLevel> root = cq.from(OkrConfigWorkLevel.class);
		Predicate p = root.get(OkrConfigWorkLevel_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}
