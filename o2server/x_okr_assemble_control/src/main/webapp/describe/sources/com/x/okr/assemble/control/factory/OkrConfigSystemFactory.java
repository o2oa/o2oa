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
	
//	@MethodDescribe( "获取指定Id的OkrConfigSystem实体信息对象" )
	public OkrConfigSystem get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrConfigSystem.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrConfigSystem实体信息列表" )
	public List<OkrConfigSystem> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSystem> cq = cb.createQuery(OkrConfigSystem.class);
		@SuppressWarnings("unused")
		Root<OkrConfigSystem> root = cq.from( OkrConfigSystem.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrConfigSystem实体信息列表" )
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

	//@MethodDescribe( "根据指定的配置编码获取配置的值" )
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
		//System.out.println("SQL:" + em.createQuery(cq.where(p)).toString() );
		List<String> valueList = em.createQuery(cq.where(p)).getResultList();
		if( valueList != null && valueList.size() > 0 ){
			//System.out.println("valueList.get(0):" + valueList.get(0) );
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
	/**
	 * 查询系统配置值身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctValueIdentity( List<String> identities_ok, List<String> identities_error ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrConfigSystem> root = cq.from(OkrConfigSystem.class);
		
		Predicate p = cb.isNotNull( root.get( OkrConfigSystem_.id ) );
		p = cb.and( p, cb.equal( root.get( OkrConfigSystem_.valueType ), "identity") );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrConfigSystem_.configValue ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrConfigSystem_.configValue ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrConfigSystem_.configValue ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从系统参数配置信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrConfigSystem> listErrorIdentitiesInConfigSystem(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrConfigSystem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrConfigSystem> cq = cb.createQuery( OkrConfigSystem.class );
		Root<OkrConfigSystem> root = cq.from( OkrConfigSystem.class );
		Predicate p = cb.isNotNull(root.get( OkrConfigSystem_.configValue ));
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrConfigSystem_.id ), recordId ) );
		}
		//p = cb.and( p, cb.equal( root.get( OkrConfigSystem_.valueType ), "identity") );
		p = cb.and( p, cb.like( root.get( OkrConfigSystem_.configValue ), "%"+identity+"%" ) );		
		return em.createQuery(cq.where(p)).getResultList();
	}
}
