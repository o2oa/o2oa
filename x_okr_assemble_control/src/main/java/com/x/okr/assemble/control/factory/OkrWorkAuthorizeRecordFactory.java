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
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkAuthorizeRecord_;

/**
 * 类   名：OkrWorkAuthorizeRecordFactory<br/>
 * 实体类：OkrWorkAuthorizeRecord<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkAuthorizeRecordFactory extends AbstractFactory {

	public OkrWorkAuthorizeRecordFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrWorkAuthorizeRecord实体信息对象" )
	public OkrWorkAuthorizeRecord get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkAuthorizeRecord.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrWorkAuthorizeRecord实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkAuthorizeRecord> root = cq.from( OkrWorkAuthorizeRecord.class);
		cq.select(root.get(OkrWorkAuthorizeRecord_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrWorkAuthorizeRecord实体信息列表" )
	public List<OkrWorkAuthorizeRecord> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkAuthorizeRecord>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkAuthorizeRecord> cq = cb.createQuery(OkrWorkAuthorizeRecord.class);
		Root<OkrWorkAuthorizeRecord> root = cq.from(OkrWorkAuthorizeRecord.class);
		Predicate p = root.get(OkrWorkAuthorizeRecord_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkAuthorizeRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< String > cq = cb.createQuery( String.class );
		Root< OkrWorkAuthorizeRecord > root = cq.from( OkrWorkAuthorizeRecord.class );
		Predicate p = cb.equal( root.get( OkrWorkAuthorizeRecord_.centerId ), centerId );
		cq.select( root.get( OkrWorkAuthorizeRecord_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据工作信息ID，列示所有的数据信息
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作信息ID，列示所有的数据信息" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkAuthorizeRecord> root = cq.from( OkrWorkAuthorizeRecord.class);
		Predicate p = cb.equal( root.get(OkrWorkAuthorizeRecord_.workId), workId );
		cq.select(root.get( OkrWorkAuthorizeRecord_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	@MethodDescribe( "根据工作ID获取工作最大的授权级别" )
	public Integer getMaxDelegateLevel( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkAuthorizeRecord> cq = cb.createQuery( OkrWorkAuthorizeRecord.class );
		Root<OkrWorkAuthorizeRecord> root = cq.from( OkrWorkAuthorizeRecord.class);		
		
		cq.orderBy( cb.desc( root.get( OkrWorkAuthorizeRecord_.delegateLevel ) ) );	
		
		Predicate p = cb.equal( root.get( OkrWorkAuthorizeRecord_.workId), workId);
		
		List<OkrWorkAuthorizeRecord> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return 0;
		}else{
			return resultList.get(0).getDelegateLevel();
		}
	}

	/**
	 * 根据工作授权人身份获取第一次授权记录信息
	 * @param workId
	 * @param authorizeIdentity
	 * @return
	 * @throws Exception
	 */
	public OkrWorkAuthorizeRecord getFirstAuthorizeRecord( String workId, String authorizeIdentity ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		if( authorizeIdentity == null || authorizeIdentity.isEmpty() ){
			throw new Exception( "authorizeIdentity is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkAuthorizeRecord> cq = cb.createQuery( OkrWorkAuthorizeRecord.class );
		Root<OkrWorkAuthorizeRecord> root = cq.from( OkrWorkAuthorizeRecord.class);
		cq.orderBy( cb.asc( root.get( OkrWorkAuthorizeRecord_.delegateLevel ) ) );	
		Predicate p = cb.equal( root.get( OkrWorkAuthorizeRecord_.workId), workId);
		p = cb.and( p, cb.equal( root.get( OkrWorkAuthorizeRecord_.delegatorIdentity ), authorizeIdentity));
		
		List<OkrWorkAuthorizeRecord> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0);
		}
	}
	
	/**
	 * 根据工作, 承担人获取最后的一次有效授权记录信息
	 * @param workId
	 * @param undertakerIdentity 可以为空
	 * @return
	 * @throws Exception
	 */
	public OkrWorkAuthorizeRecord getLastAuthorizeRecord( String workId, String identity ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkAuthorizeRecord> cq = cb.createQuery( OkrWorkAuthorizeRecord.class );
		Root<OkrWorkAuthorizeRecord> root = cq.from( OkrWorkAuthorizeRecord.class);
		cq.orderBy( cb.desc( root.get( OkrWorkAuthorizeRecord_.delegateLevel ) ) );	
		
		Predicate p = cb.equal( root.get( OkrWorkAuthorizeRecord_.workId ), workId );
		p = cb.and( p, cb.equal( root.get( OkrWorkAuthorizeRecord_.status ), "正常" ));
		
		if( identity != null ){
			Predicate p1 = cb.equal( root.get( OkrWorkAuthorizeRecord_.targetIdentity), identity);
			p1 = cb.or( p1, cb.equal( root.get( OkrWorkAuthorizeRecord_.delegatorIdentity ), identity));
			p = cb.and( p, p1 );
		}
		
		List<OkrWorkAuthorizeRecord> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0);
		}
	}

	public List<String> listByAuthorizor( String workId, String delegatorIdentity, Integer delegateLevel ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		if( delegatorIdentity == null || delegatorIdentity.isEmpty() ){
			throw new Exception( "delegatorIdentity is empty, system can not excute query!" );
		}
		if( delegateLevel == null || delegateLevel == 0 ){
			throw new Exception( "delegateLevel is invalid, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAuthorizeRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkAuthorizeRecord> root = cq.from( OkrWorkAuthorizeRecord.class);
		Predicate p = cb.equal( root.get(OkrWorkAuthorizeRecord_.workId), workId );
		p = cb.and( p, cb.equal( root.get( OkrWorkAuthorizeRecord_.delegatorIdentity ), delegatorIdentity));
		p = cb.and( p, cb.greaterThanOrEqualTo( root.get( OkrWorkAuthorizeRecord_.delegateLevel ), delegateLevel ));
		cq.select(root.get( OkrWorkAuthorizeRecord_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
