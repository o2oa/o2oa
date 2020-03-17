package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.Log_;

/**
 * 日志管理管理表基础功能服务类
 * 
 * @author O2LEE
 */
public class LogFactory extends AbstractFactory {

	public LogFactory( Business business ) throws Exception {
		super(business);
	}
	
	/**
	 * @param id
	 * @return Log
	 * @throws Exception
	 */
	//@MethodDescribe("获取指定Id的Log日志管理信息对象")
	public Log get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Log.class, ExceptionWhen.none );
	}
	
	/**
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("列示全部的Log日志管理信息ID列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Log> root = cq.from(Log.class);
		cq.select(root.get(Log_.id));
		return em.createQuery(cq).setMaxResults(100).getResultList();
	}
	
	/**
	 * 
	 * @param ids 需要查询的ID列表
	 * @return List：Log
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定Id的Log日志管理信息列表")
//	public List<Log> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( Log.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Log> cq = cb.createQuery( Log.class );
//		Root<Log> root = cq.from( Log.class );
//		Predicate p = root.get(Log_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}

	/**
	 * 列示指定对象的Log日志管理信息ID列表
	 * @param appId
	 * @param categoryId
	 * @param documentId
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定对象的Log日志管理信息ID列表")
	public List<String> listByObject( String appId, String categoryId, String documentId, String fileId) throws Exception {
		if( appId == null && categoryId == null && documentId == null && fileId == null ){
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Log> root = cq.from( Log.class );
		Predicate p = null;
		if( appId != null && !appId.isEmpty() ){
			p = cb.equal(root.get(Log_.appId), appId );
		}
		if( categoryId != null && !categoryId.isEmpty() ){
			if( p == null ){
				p = cb.equal(root.get(Log_.categoryId), categoryId );
			}else{
				p = cb.and( p, cb.equal(root.get(Log_.categoryId), categoryId ) );
			}
		}
		if( documentId != null && !documentId.isEmpty() ){
			if( p == null ){
				p = cb.equal(root.get(Log_.documentId), documentId );
			}else{
				p = cb.and( p, cb.equal(root.get(Log_.documentId), documentId ) );
			}
		}
		if( fileId != null && !fileId.isEmpty() ){
			if( p == null ){
				p = cb.equal(root.get(Log_.fileId), fileId );
			}else{
				p = cb.and( p, cb.equal(root.get(Log_.fileId), fileId ) );
			}
		}
		cq.select(root.get(Log_.id));
		return em.createQuery(cq.where(p)).setMaxResults(100).getResultList();
	}
	
	/**
	 * 列示指定操作级别的Log日志管理信息ID列表
	 * @param operationType
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定操作类型的Log日志管理信息ID列表")
	public List<String> listByOperationLevel(String operationLevel ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Log> root = cq.from( Log.class );
		Predicate p = cb.equal(root.get(Log_.operationLevel), operationLevel );
		cq.select(root.get(Log_.id));
		return em.createQuery(cq.where(p)).setMaxResults(100).getResultList();
	}
	
	/**
	 * 列示指定操作类型的Log日志管理信息ID列表
	 * @param operationType
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("列示指定操作用户的Log日志管理信息ID列表")
	public List<String> listByOperationUser(String operatorUid, String operationType ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Log> root = cq.from( Log.class );
		Predicate p = cb.equal(root.get(Log_.operatorUid), operatorUid );
		p = cb.and( p, cb.equal(root.get(Log_.operationType), operationType ) );
		cq.select(root.get(Log_.id));
		return em.createQuery(cq.where(p)).setMaxResults(100).getResultList();
	}

	public List<String> listOverTime(Date limitDate) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Log> root = cq.from( Log.class );
		Predicate p = cb.lessThan( root.get(Log_.createTime), limitDate );
		cq.select(root.get(Log_.id));
		return em.createQuery(cq.where(p)).setMaxResults(1000000).getResultList();
	}

	public Long getTotal() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Log> root = cq.from( Log.class );
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getRecordIdsWithCount( int maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Log.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Log> root = cq.from( Log.class );
		cq.orderBy( cb.desc( root.get( Log_.createTime )));
		cq.select(root.get(Log_.id));
		return em.createQuery(cq).setMaxResults( maxCount ).getResultList();
	}
}