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
import com.x.okr.entity.OkrRoleInfo;
import com.x.okr.entity.OkrRoleInfo_;

/**
 * 类   名：OkrRoleInfoFactory<br/>
 * 实体类：OkrRoleInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrRoleInfoFactory extends AbstractFactory {

	public OkrRoleInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrRoleInfo实体信息对象" )
	public OkrRoleInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrRoleInfo.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrRoleInfo实体信息列表" )
	public List<OkrRoleInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrRoleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrRoleInfo> cq = cb.createQuery(OkrRoleInfo.class);
		Root<OkrRoleInfo> root = cq.from( OkrRoleInfo.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrRoleInfo实体信息列表" )
	public List<OkrRoleInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrRoleInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrRoleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrRoleInfo> cq = cb.createQuery(OkrRoleInfo.class);
		Root<OkrRoleInfo> root = cq.from(OkrRoleInfo.class);
		Predicate p = root.get(OkrRoleInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}
