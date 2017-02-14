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
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTask_;

/**
 * 类   名：OkrTaskFactory<br/>
 * 实体类：OkrTask<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrTaskFactory extends AbstractFactory {

	public OkrTaskFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrTask实体信息对象" )
	public OkrTask get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrTask.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrTask实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from( OkrTask.class);
		cq.select(root.get(OkrTask_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrTask实体信息列表" )
	public List<OkrTask> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrTask>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrTask> cq = cb.createQuery(OkrTask.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = root.get(OkrTask_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe( "根据工作ID，处理人，处理环节名称查询一下是否存在待办信息" )
	public List<String> listIdsByWorkAndTarget(String workId, String targetName, String activityName) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( " workId is null!" );
		}
		if( targetName == null || targetName.isEmpty() ){
			throw new Exception( " targetName is null!" );
		}
		if( activityName == null || activityName.isEmpty() ){
			throw new Exception( " activityName is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = cb.equal( root.get( OkrTask_.workId), workId);
		p = cb.and( p, cb.equal( root.get( OkrTask_.targetName), targetName));
		p = cb.and( p, cb.equal( root.get( OkrTask_.activityName), activityName));
		cq.select(root.get( OkrTask_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe( "查询在中心中工作是否有指定员工的待办信息" )
	public List<String> listIdsByCenterAndPerson(String centerId, String identity, String dynamicObjectType ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		if( identity == null || identity.isEmpty()){
			throw new Exception( " identity is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = cb.equal( root.get( OkrTask_.centerId ), centerId );
		p = cb.and( p, cb.equal( root.get( OkrTask_.targetIdentity ), identity) );
		if( dynamicObjectType != null && !dynamicObjectType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrTask_.dynamicObjectType ), dynamicObjectType) );
		}
		cq.select(root.get(OkrTask_.id));
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
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = cb.equal( root.get( OkrTask_.centerId ), centerId );
		cq.select(root.get(OkrTask_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，列示所有的数据信息
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作信息ID，列示所有的数据信息" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from( OkrTask.class);
		Predicate p = cb.equal( root.get(OkrTask_.workId), workId );
		cq.select(root.get( OkrTask_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByCenterAndIdentityActivity( String dynamicObjectType, String dynamicObjectId, String identity, String processType, String activityName) throws Exception {
		if( dynamicObjectType == null || dynamicObjectType.isEmpty() ){
			throw new Exception( " dynamicObjectType is null!" );
		}
		if( dynamicObjectId == null || dynamicObjectId.isEmpty()){
			throw new Exception( " dynamicObjectId is null!" );
		}
		if( identity == null || identity.isEmpty()){
			throw new Exception( " identity is null!" );
		}
		if( processType == null || processType.isEmpty()){
			throw new Exception( " processType is null!" );
		}
		if( activityName == null || activityName.isEmpty()){
			throw new Exception( " activityName is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrTask.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = cb.equal( root.get( OkrTask_.dynamicObjectType ), dynamicObjectType );
		p = cb.and( p, cb.equal( root.get( OkrTask_.dynamicObjectId ), dynamicObjectId) );
		p = cb.and( p, cb.equal( root.get( OkrTask_.targetIdentity ), identity) );
		p = cb.and( p, cb.equal( root.get( OkrTask_.processType ), processType) );
		p = cb.and( p, cb.equal( root.get( OkrTask_.activityName ), activityName) );
		cq.select(root.get(OkrTask_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByTargetActivityAndObjId( String dynamicObjectType, String dynamicObjectId, String activityName, String processorIdentity ) throws Exception {
		if( dynamicObjectType == null || dynamicObjectType.isEmpty() ){
			throw new Exception( " dynamicObjectType is null!" );
		}
		if( dynamicObjectId == null || dynamicObjectId.isEmpty()){
			throw new Exception( " dynamicObjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrTask.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = cb.equal( root.get( OkrTask_.dynamicObjectType ), dynamicObjectType );
		p = cb.and( p, cb.equal( root.get( OkrTask_.dynamicObjectId ), dynamicObjectId) );
		if( activityName != null && !activityName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrTask_.activityName ), activityName) );
		}
		if( processorIdentity != null && !processorIdentity.isEmpty()){
			p = cb.and( p, cb.equal( root.get( OkrTask_.targetIdentity ), processorIdentity) );
		}
		cq.select(root.get(OkrTask_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listDistinctIdentity(List<String> taskTypeList) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrTask.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from( OkrTask.class);
		cq.distinct(true).select( root.get( OkrTask_.targetIdentity ));
		Predicate p = root.get( OkrTask_.dynamicObjectType ).in( taskTypeList );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据待办类别和用户身份，查询待办数量
	 * @param taskTypeList
	 * @param userIdentity
	 * @return
	 * @throws Exception 
	 */
	public Long getTaskCount(List<String> taskTypeList, String userIdentity, String workTypeName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrTask.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrTask> root = cq.from( OkrTask.class);
		Predicate p = root.get( OkrTask_.dynamicObjectType ).in( taskTypeList );
		p = cb.and( p, cb.equal( root.get( OkrTask_.targetIdentity ), userIdentity ) );
		if( workTypeName != null && !workTypeName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrTask_.workType ), workTypeName ) );
		}
		cq.select( cb.count( root ) );		
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据待办类别和用户身份，查询待办列表
	 * @param taskTypeList
	 * @param userIdentity
	 * @return
	 * @throws Exception 
	 */
	public List<OkrTask> listByTaskType(List<String> taskTypeList, String userIdentity, String workTypeName ) throws Exception {
		List<OkrTask> okrTaskList = null;
		EntityManager em = this.entityManagerContainer().get( OkrTask.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< OkrTask > cq = cb.createQuery( OkrTask.class );
		Root<OkrTask> root = cq.from( OkrTask.class);
		Predicate p = root.get( OkrTask_.dynamicObjectType ).in( taskTypeList );
		p = cb.and( p, cb.equal( root.get( OkrTask_.targetIdentity ), userIdentity ) );
		if( workTypeName != null && !workTypeName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrTask_.workType ), workTypeName ) );
		}
		okrTaskList = em.createQuery(cq.where(p)).getResultList();
		if( okrTaskList == null ){
			return null;
		}else{
			return okrTaskList;
		}
	}

	/**
	 * 根据用户唯一标识来查询用户信息
	 * @param taskTypeList
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public Long getTaskCountByUserName(List<String> taskTypeList, String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrTask.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrTask> root = cq.from( OkrTask.class);
		Predicate p = root.get( OkrTask_.dynamicObjectType ).in( taskTypeList );
		p = cb.and( p, cb.equal( root.get( OkrTask_.targetName ), name ) );
		cq.select( cb.count( root ) );		
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listIdsByReportId(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( " id is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrTask.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTask> root = cq.from(OkrTask.class);
		Predicate p = cb.equal( root.get( OkrTask_.dynamicObjectId), id);
		p = cb.and(p, cb.equal( root.get( OkrTask_.dynamicObjectType), "工作汇报"));
		cq.select(root.get( OkrTask_.id));
		return em.createQuery(cq.where(p)).setMaxResults(5000).getResultList();
	}
}
