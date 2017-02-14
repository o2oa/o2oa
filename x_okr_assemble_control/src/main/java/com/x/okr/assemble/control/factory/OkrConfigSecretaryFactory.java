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
import com.x.okr.entity.OkrConfigSecretary;
import com.x.okr.entity.OkrConfigSecretary_;

public class OkrConfigSecretaryFactory extends AbstractFactory {

	public OkrConfigSecretaryFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe( "获取指定Id的OkrConfigSecretary应用信息对象" )
	public OkrConfigSecretary get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrConfigSecretary.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrConfigSecretary应用信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSecretary.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrConfigSecretary> root = cq.from( OkrConfigSecretary.class);
		cq.select(root.get(OkrConfigSecretary_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrConfigSecretary应用信息列表" )
	public List<OkrConfigSecretary> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrConfigSecretary>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrConfigSecretary.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSecretary> cq = cb.createQuery(OkrConfigSecretary.class);
		Root<OkrConfigSecretary> root = cq.from(OkrConfigSecretary.class);
		Predicate p = root.get(OkrConfigSecretary_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据秘书姓名查询配置好的秘书信息ID列表
	 * @param userName
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据秘书姓名查询配置好的秘书信息ID列表" )
	public List<String> listBySecretaryName( String userName ) throws Exception {
		if( userName == null || userName.isEmpty() ){
			throw new Exception ( "the parameter: 'userName' is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrConfigSecretary.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrConfigSecretary> root = cq.from( OkrConfigSecretary.class);
		cq.select(root.get(OkrConfigSecretary_.id));
		Predicate p = cb.equal( root.get(OkrConfigSecretary_.secretaryName ) , userName);
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe( "根据秘书姓名和被代理员工姓名查询配置好的秘书信息ID列表" )
	public List<String> listIdsByPerson( String name, String leaderName ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception ( "the parameter: 'name' is null!" );
		}
		if( leaderName == null || leaderName.isEmpty() ){
			throw new Exception ( "the parameter: 'leaderName' is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrConfigSecretary.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrConfigSecretary> root = cq.from( OkrConfigSecretary.class);
		cq.select(root.get( OkrConfigSecretary_.id ));
		Predicate p = cb.equal( root.get( OkrConfigSecretary_.secretaryName ) , name );
		p = cb.and( p, cb.equal( root.get( OkrConfigSecretary_.leaderName ), leaderName ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByLeaderIdentity( String name, String leaderIdentity ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception ( "the parameter: 'name' is null!" );
		}
		if( leaderIdentity == null || leaderIdentity.isEmpty() ){
			throw new Exception ( "the parameter: 'leaderIdentity' is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrConfigSecretary.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrConfigSecretary> root = cq.from( OkrConfigSecretary.class);
		cq.select(root.get( OkrConfigSecretary_.id ));
		Predicate p = cb.equal( root.get( OkrConfigSecretary_.secretaryName ) , name );
		p = cb.and( p, cb.equal( root.get( OkrConfigSecretary_.leaderIdentity ), leaderIdentity ));
		//logger.debug( em.createQuery(cq.where(p)).toString() );
		return em.createQuery( cq.where(p) ).getResultList();
	}	
}