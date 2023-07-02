package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSUserInfo;
import com.x.bbs.entity.BBSUserInfo_;

/**
 * 类   名：BBSUserInfoFactory<br/>
 * 实体类：BBSUserInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-08-10 17:17:26
**/
public class BBSUserInfoFactory extends AbstractFactory {

	public BBSUserInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSUserInfo实体信息对象" )
	public BBSUserInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSUserInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSUserInfo实体信息列表" )
	public List<BBSUserInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSUserInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserInfo> cq = cb.createQuery(BBSUserInfo.class);
		Root<BBSUserInfo> root = cq.from(BBSUserInfo.class);
		Predicate p = root.get(BBSUserInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示指定姓名的BBSUserInfo实体信息列表" )
	public List<BBSUserInfo> listByUserName( String userName ) throws Exception {
		if( userName == null || userName.isEmpty() ){
			throw new Exception( "userName is null!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserInfo> cq = cb.createQuery(BBSUserInfo.class);
		Root<BBSUserInfo> root = cq.from(BBSUserInfo.class);
		Predicate p = cb.equal( root.get(BBSUserInfo_.userName), userName );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
