package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo_;

/**
 * 类   名：OkrWorkReportBaseInfoFactory<br/>
 * 实体类：OkrWorkReportBaseInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportBaseInfoFactory extends AbstractFactory {

	public OkrWorkReportBaseInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrWorkReportBaseInfo实体信息对象" )
	public OkrWorkReportBaseInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkReportBaseInfo.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrWorkReportBaseInfo实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		cq.select(root.get(OkrWorkReportBaseInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrWorkReportBaseInfo实体信息列表" )
	public List<OkrWorkReportBaseInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkReportBaseInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery(OkrWorkReportBaseInfo.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = root.get(OkrWorkReportBaseInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作ID，查询该工作的最大汇报次序
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作ID，查询该工作的最大汇报次序" )
	public Integer getMaxReportCount( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);		
		cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.reportCount) ) );	
		
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId), workId);
		
		List<OkrWorkReportBaseInfo> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return 0;
		}else{
			return resultList.get(0).getReportCount();
		}
	}

	/**
	 * 根据工作信息ID，获取汇报基础信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作信息ID，获取汇报基础信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkReportBaseInfo_.workId), workId );
		cq.select(root.get( OkrWorkReportBaseInfo_.id) );
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
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.centerId ), centerId );
		cq.select(root.get( OkrWorkReportBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OkrWorkReportBaseInfo> listNextWithFilter(String id, Integer count, Object sequence, WrapInFilter wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportBaseInfo.class);
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if ( order == null || order.isEmpty() ) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkReportBaseInfo.class.getCanonicalName() + " o where 1=1" );
		if ((null != sequence)) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.centerTitle like  ?" + (index) );
			vs.add( "%" + wrapIn.getTitle() + "%" );
			index++;
		}
		//当前处理人身份
		if ( null != wrapIn.getProcessIdentity() && !wrapIn.getProcessIdentity().isEmpty() ) {
			sql_stringBuffer.append( " and o.currentProcessorIdentity =  ?" + (index) );
			vs.add( wrapIn.getProcessIdentity() );
			index++;
		}
		//工作处理状态
		if (null != wrapIn.getProcessStatusList() && wrapIn.getProcessStatusList().size() > 0) {
			sql_stringBuffer.append( " and o.processStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0) {
			sql_stringBuffer.append( " and o.status in ( ?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "DESC" : "ASC" ));
		
		//logger.debug( sql_stringBuffer.toString() );
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportBaseInfo.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults( count ).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<OkrWorkReportBaseInfo> listPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportBaseInfo.class );
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkReportBaseInfo.class.getCanonicalName() + " o where 1=1" );
		if ((null != sequence)) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? ">" : "<" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.centerTitle like  ?" + (index) );
			vs.add( "%" + wrapIn.getTitle() + "%" );
			index++;
		}
		//当前处理人身份
		if ( null != wrapIn.getProcessIdentity() && !wrapIn.getProcessIdentity().isEmpty() ) {
			sql_stringBuffer.append( " and o.currentProcessorIdentity =  ?" + (index) );
			vs.add( wrapIn.getProcessIdentity() );
			index++;
		}
		//工作处理状态
		if (null != wrapIn.getProcessStatusList() && wrapIn.getProcessStatusList().size() > 0) {
			sql_stringBuffer.append( " and o.processStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0) {
			sql_stringBuffer.append( " and o.status in ( ?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "DESC" : "ASC" ));
		
		//logger.debug( sql_stringBuffer.toString() );
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportBaseInfo.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults( count ).getResultList();
	}
	
	/**
	 * 查询符合的信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportBaseInfo.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count( o.id ) FROM " + OkrWorkReportBaseInfo.class.getCanonicalName() + " o where 1=1" );
		
		//对象类别，是中心工作，还是普通工作，如果是中心工作，那么普通的工作ID应该是为空的
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.centerTitle like  ?" + (index) );
			vs.add( "%" + wrapIn.getTitle() + "%" );
			index++;
		}
		//当前处理人身份
		if ( null != wrapIn.getProcessIdentity() && !wrapIn.getProcessIdentity().isEmpty() ) {
			sql_stringBuffer.append( " and o.currentProcessorIdentity =  ?" + (index) );
			vs.add( wrapIn.getProcessIdentity() );
			index++;
		}
		//工作处理状态
		if (null != wrapIn.getProcessStatusList() && wrapIn.getProcessStatusList().size() > 0) {
			sql_stringBuffer.append( " and o.processStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0) {
			sql_stringBuffer.append( " and o.status in ( ?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
				
		//logger.debug( sql_stringBuffer.toString() );
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportBaseInfo.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

	/**
	 * 根据WorkId查询该工作所有汇报中已经提交的最后一次汇报的内容，如果没有则返回NULL
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo getLastSubmitReport( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);		
		cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.reportCount) ) );
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId), workId);
		List<OkrWorkReportBaseInfo> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0);
		}
	}

	/**
	 * 根据条件查询汇报ID列表
	 * @param workId
	 * @param activityName
	 * @param processStatus
	 * @param processIdentity
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByWorkId(String workId, String activityName, String processStatus, String processorIdentity ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId), workId );
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.status ), "正常" ) );
		if( processorIdentity != null && !processorIdentity.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.currentProcessorIdentity ), processorIdentity ) );
		}
		if( activityName != null && !activityName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.activityName ), activityName ) );
		}
		if( processStatus != null && !processStatus.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.processStatus ), processStatus ) );
		}
		cq.select(root.get( OkrWorkReportBaseInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据工作ID获取该工作最后一次工作汇报
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo getLastReportBaseInfo(String workId) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is empty, system can not excute query!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportBaseInfo> cq = cb.createQuery( OkrWorkReportBaseInfo.class );
		Root<OkrWorkReportBaseInfo> root = cq.from( OkrWorkReportBaseInfo.class);
		cq.orderBy( cb.desc( root.get( OkrWorkReportBaseInfo_.submitTime ) ) );	
		
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId ), workId );
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.status ), "正常" ));
		
		List<OkrWorkReportBaseInfo> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0);
		}
	}

	public List<String> listProcessingReportIdsByWorkId(String workId) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( " workId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportBaseInfo> root = cq.from(OkrWorkReportBaseInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportBaseInfo_.workId ), workId );
		p = cb.and( p, cb.notEqual( root.get( OkrWorkReportBaseInfo_.activityName ), "已完成" ));
		p = cb.and( p, cb.equal( root.get( OkrWorkReportBaseInfo_.status ), "正常" ));
		cq.select(root.get( OkrWorkReportBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
