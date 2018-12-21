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
import com.x.okr.entity.OkrConfigSecretary;
import com.x.okr.entity.OkrConfigSecretary_;

public class OkrConfigSecretaryFactory extends AbstractFactory {

	public OkrConfigSecretaryFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的OkrConfigSecretary应用信息对象" )
	public OkrConfigSecretary get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrConfigSecretary.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrConfigSecretary应用信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSecretary.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrConfigSecretary> root = cq.from( OkrConfigSecretary.class);
		cq.select(root.get(OkrConfigSecretary_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrConfigSecretary应用信息列表" )
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
	//@MethodDescribe( "根据秘书姓名查询配置好的秘书信息ID列表" )
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

	//@MethodDescribe( "根据秘书姓名和被代理员工姓名查询配置好的秘书信息ID列表" )
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
		return em.createQuery( cq.where(p) ).getResultList();
	}
	/**
	 * 查询秘书代理领导身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctLeaderIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSecretary.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrConfigSecretary> root = cq.from(OkrConfigSecretary.class);
		
		Predicate p = cb.isNotNull( root.get( OkrConfigSecretary_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrConfigSecretary_.leaderIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrConfigSecretary_.leaderIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrConfigSecretary_.leaderIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从领导秘书配置信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrConfigSecretary> listErrorIdentitiesInConfigSecretary(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSecretary.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSecretary> cq = cb.createQuery( OkrConfigSecretary.class );
		Root<OkrConfigSecretary> root = cq.from( OkrConfigSecretary.class );
		Predicate p = cb.isNotNull(root.get( OkrConfigSecretary_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrConfigSecretary_.id ), recordId ) );
		}
		
		Predicate p_leaderIdentity = cb.isNotNull(root.get( OkrConfigSecretary_.leaderIdentity ));
		p_leaderIdentity = cb.and( p_leaderIdentity, cb.equal( root.get( OkrConfigSecretary_.leaderIdentity ), identity ) );
		Predicate p_secretaryIdentity = cb.isNotNull(root.get( OkrConfigSecretary_.secretaryIdentity ));
		p_secretaryIdentity = cb.and( p_secretaryIdentity, cb.equal( root.get( OkrConfigSecretary_.secretaryIdentity ), identity ) );
		Predicate p_identity = cb.or( p_leaderIdentity, p_secretaryIdentity );
		p = cb.and( p, p_identity );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByIdentities(List<String> identities) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSecretary.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrConfigSecretary> root = cq.from(OkrConfigSecretary.class);
		Predicate p = root.get( OkrConfigSecretary_.secretaryName ).in( identities );
		cq.select(root.get( OkrConfigSecretary_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
}