package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrPersonPermission;
import com.x.okr.entity.OkrPersonPermission_;

/**
 * 类   名：OkrPersonPermissionFactory<br/>
 * 实体类：OkrPersonPermission<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrPersonPermissionFactory extends AbstractFactory {

	public OkrPersonPermissionFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrPersonPermission实体信息对象" )
	public OkrPersonPermission get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrPersonPermission.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrPersonPermission实体信息列表" )
	public List<OkrPersonPermission> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrPersonPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrPersonPermission> cq = cb.createQuery(OkrPersonPermission.class);
		Root<OkrPersonPermission> root = cq.from( OkrPersonPermission.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrPersonPermission实体信息列表" )
	public List<OkrPersonPermission> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrPersonPermission>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrPersonPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrPersonPermission> cq = cb.createQuery(OkrPersonPermission.class);
		Root<OkrPersonPermission> root = cq.from(OkrPersonPermission.class);
		Predicate p = root.get(OkrPersonPermission_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe( "根据目标对象、对象类别、对象代码来列示所有的角色信息ID" )
	public List<String> listIdsByTargetAndObject( String targetObjectKey, String targetObjectType, String objectType, String objectCode ) throws Exception {
		if( targetObjectKey == null || targetObjectKey.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( OkrPersonPermission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrPersonPermission> root = cq.from(OkrPersonPermission.class);
		Predicate p = cb.equal( root.get( OkrPersonPermission_.targetObjectKey ), targetObjectKey );
		if( targetObjectType != null && !targetObjectType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrPersonPermission_.targetObjectType ), targetObjectType ));
		}
		if( objectType != null && !objectType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrPersonPermission_.objectType ), objectType ));
		}
		if( objectCode != null && !objectCode.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrPersonPermission_.objectCode ), objectCode ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
}
