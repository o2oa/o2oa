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
import com.x.okr.entity.OkrUserInfo;
import com.x.okr.entity.OkrUserInfo_;

/**
 * 类   名：OkrUserInfoFactory<br/>
 * 实体类：OkrUserInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrUserInfoFactory extends AbstractFactory {

	public OkrUserInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrUserInfo实体信息对象" )
	public OkrUserInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrUserInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrUserInfo实体信息列表" )
	public List<OkrUserInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrUserInfo> cq = cb.createQuery(OkrUserInfo.class);
		@SuppressWarnings("unused")
		Root<OkrUserInfo> root = cq.from( OkrUserInfo.class);
		return em.createQuery(cq).getResultList();
	}
	
	public List<String> listAllIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrUserInfo> root = cq.from( OkrUserInfo.class);
		cq.select(root.get(OkrUserInfo_.id) );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrUserInfo实体信息列表" )
	public List<OkrUserInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrUserInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrUserInfo> cq = cb.createQuery(OkrUserInfo.class);
		Root<OkrUserInfo> root = cq.from(OkrUserInfo.class);
		Predicate p = root.get(OkrUserInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示指定userName的OkrUserInfo实体信息列表" )
	public List<OkrUserInfo> listWithPerson( String userName ) throws Exception {
		if( userName == null || userName.isEmpty() ){
			throw new Exception("username is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrUserInfo> cq = cb.createQuery(OkrUserInfo.class);
		Root<OkrUserInfo> root = cq.from(OkrUserInfo.class);
		Predicate p = cb.equal( root.get(OkrUserInfo_.userName), userName );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
