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
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrConfigSystem_;

/**
 * 类   名：OkrConfigSystemFactory<br/>
 * 实体类：OkrConfigSystem<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrConfigSystemFactory extends AbstractFactory {

	public OkrConfigSystemFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrConfigSystem实体信息对象" )
	public OkrConfigSystem get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrConfigSystem.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrConfigSystem实体信息列表" )
	public List<OkrConfigSystem> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSystem> cq = cb.createQuery(OkrConfigSystem.class);
		@SuppressWarnings("unused")
		Root<OkrConfigSystem> root = cq.from( OkrConfigSystem.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrConfigSystem实体信息列表" )
	public List<OkrConfigSystem> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrConfigSystem>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSystem> cq = cb.createQuery(OkrConfigSystem.class);
		Root<OkrConfigSystem> root = cq.from(OkrConfigSystem.class);
		Predicate p = root.get(OkrConfigSystem_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe( "根据指定的配置编码获取配置的值" )
	public String getValueWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "config code is null, can not find any system config!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrConfigSystem> root = cq.from(OkrConfigSystem.class);
		Predicate p = cb.equal( root.get( OkrConfigSystem_.configCode ), configCode );
		cq.select(root.get(OkrConfigSystem_.configValue));
		List<String> valueList = em.createQuery(cq.where(p)).getResultList();
		if( valueList != null && valueList.size() > 0 ){
			return valueList.get(0);
		}
		return null;
	}

	public OkrConfigSystem getWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "config code is null, can not find any system config!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSystem> cq = cb.createQuery(OkrConfigSystem.class);
		Root<OkrConfigSystem> root = cq.from(OkrConfigSystem.class);
		Predicate p = cb.equal( root.get( OkrConfigSystem_.configCode ), configCode );
		List<OkrConfigSystem> valueList = em.createQuery(cq.where(p)).getResultList();
		if( valueList != null && valueList.size() > 0 ){
			return valueList.get(0);
		}
		return null;
	}
}
