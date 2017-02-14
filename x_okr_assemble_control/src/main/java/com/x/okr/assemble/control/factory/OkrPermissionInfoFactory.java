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
import com.x.okr.entity.OkrPermissionInfo;
import com.x.okr.entity.OkrPermissionInfo_;

/**
 * 类   名：OkrPermissionInfoFactory<br/>
 * 实体类：OkrPermissionInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrPermissionInfoFactory extends AbstractFactory {

	public OkrPermissionInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrPermissionInfo实体信息对象" )
	public OkrPermissionInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrPermissionInfo.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrPermissionInfo实体信息列表" )
	public List<OkrPermissionInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrPermissionInfo> cq = cb.createQuery(OkrPermissionInfo.class);
		Root<OkrPermissionInfo> root = cq.from( OkrPermissionInfo.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrPermissionInfo实体信息列表" )
	public List<OkrPermissionInfo> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrPermissionInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrPermissionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrPermissionInfo> cq = cb.createQuery(OkrPermissionInfo.class);
		Root<OkrPermissionInfo> root = cq.from(OkrPermissionInfo.class);
		Predicate p = root.get(OkrPermissionInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}
