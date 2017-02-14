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
import com.x.okr.entity.OkrRolePermission;
import com.x.okr.entity.OkrRolePermission_;

/**
 * 类   名：OkrRolePermissionFactory<br/>
 * 实体类：OkrRolePermission<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrRolePermissionFactory extends AbstractFactory {

	public OkrRolePermissionFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrRolePermission实体信息对象" )
	public OkrRolePermission get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrRolePermission.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrRolePermission实体信息列表" )
	public List<OkrRolePermission> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrRolePermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrRolePermission> cq = cb.createQuery(OkrRolePermission.class);
		Root<OkrRolePermission> root = cq.from( OkrRolePermission.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrRolePermission实体信息列表" )
	public List<OkrRolePermission> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrRolePermission>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrRolePermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrRolePermission> cq = cb.createQuery(OkrRolePermission.class);
		Root<OkrRolePermission> root = cq.from(OkrRolePermission.class);
		Predicate p = root.get(OkrRolePermission_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}
