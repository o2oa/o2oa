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
import com.x.okr.entity.OkrWorkProblemProcessLog;
import com.x.okr.entity.OkrWorkProblemProcessLog_;

/**
 * 类   名：OkrWorkProblemProcessLogFactory<br/>
 * 实体类：OkrWorkProblemProcessLog<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkProblemProcessLogFactory extends AbstractFactory {

	public OkrWorkProblemProcessLogFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrWorkProblemProcessLog实体信息对象" )
	public OkrWorkProblemProcessLog get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkProblemProcessLog.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrWorkProblemProcessLog实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkProblemProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkProblemProcessLog> root = cq.from( OkrWorkProblemProcessLog.class);
		cq.select(root.get(OkrWorkProblemProcessLog_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrWorkProblemProcessLog实体信息列表" )
	public List<OkrWorkProblemProcessLog> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkProblemProcessLog>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkProblemProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkProblemProcessLog> cq = cb.createQuery(OkrWorkProblemProcessLog.class);
		Root<OkrWorkProblemProcessLog> root = cq.from(OkrWorkProblemProcessLog.class);
		Predicate p = root.get(OkrWorkProblemProcessLog_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作的ID和审核层级获取工作审核记录
	 * @param id
	 * @param workAuditLevel
	 * @throws Exception 
	 */
	public OkrWorkProblemProcessLog listByWorkIdAndAuditLevel( String id, Integer workAuditLevel ) throws Exception {
		List<OkrWorkProblemProcessLog> okrWorkProblemProcessLogList = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null!" );
		}
		if( workAuditLevel == null ){
			workAuditLevel = 1;
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkProblemProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkProblemProcessLog> cq = cb.createQuery(OkrWorkProblemProcessLog.class);
		Root<OkrWorkProblemProcessLog> root = cq.from(OkrWorkProblemProcessLog.class);
		Predicate p = cb.equal( root.get( OkrWorkProblemProcessLog_.workId), id );
		p = cb.and( p, cb.equal( root.get( OkrWorkProblemProcessLog_.processLevel), workAuditLevel ));
		okrWorkProblemProcessLogList =  em.createQuery(cq.where(p)).getResultList();
		if( okrWorkProblemProcessLogList != null && okrWorkProblemProcessLogList.size() > 0  ){
			return okrWorkProblemProcessLogList.get(0);
		}else{
			return null;
		}
		
	}

	/**
	 * 根据工作信息ID，获取问题请示处理记录信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作信息ID，获取问题请示处理记录信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkProblemProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkProblemProcessLog> root = cq.from( OkrWorkProblemProcessLog.class);
		Predicate p = cb.equal( root.get(OkrWorkProblemProcessLog_.workId), workId );
		cq.select(root.get( OkrWorkProblemProcessLog_.id) );
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
		EntityManager em = this.entityManagerContainer().get(OkrWorkProblemProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkProblemProcessLog> root = cq.from(OkrWorkProblemProcessLog.class);
		Predicate p = cb.equal( root.get( OkrWorkProblemProcessLog_.centerId ), centerId );
		cq.select(root.get(OkrWorkProblemProcessLog_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listNextWithFilter(String id, Integer count, Object sequence, WrapInFilter wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkProblemProcessLog.class);
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if ( order == null || order.isEmpty() ) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkProblemProcessLog.class.getCanonicalName() + " o where 1=1" );
		if ((null != sequence)) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.reportTitle like  ?" + (index) );
			vs.add( "%" + wrapIn.getTitle() + "%" );
			index++;
		}
		//当前处理人身份
		if ( null != wrapIn.getProcessIdentity() && !wrapIn.getProcessIdentity().isEmpty() ) {
			sql_stringBuffer.append( " and o.processorIdentity =  ?" + (index) );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkProblemProcessLog.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults( count ).getResultList();
	}
	
	
	public List<String> listPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkProblemProcessLog.class );
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkProblemProcessLog.class.getCanonicalName() + " o where 1=1" );
		if ((null != sequence)) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? ">" : "<" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.reportTitle like  ?" + (index) );
			vs.add( "%" + wrapIn.getTitle() + "%" );
			index++;
		}
		//当前处理人身份
		if ( null != wrapIn.getProcessIdentity() && !wrapIn.getProcessIdentity().isEmpty() ) {
			sql_stringBuffer.append( " and o.processorIdentity =  ?" + (index) );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkProblemProcessLog.class);
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
		EntityManager em = this.entityManagerContainer().get( OkrWorkProblemProcessLog.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count( o.id ) FROM " + OkrWorkProblemProcessLog.class.getCanonicalName() + " o where 1=1" );
		
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.reportTitle like  ?" + (index) );
			vs.add( "%" + wrapIn.getTitle() + "%" );
			index++;
		}
		//当前处理人身份
		if ( null != wrapIn.getProcessIdentity() && !wrapIn.getProcessIdentity().isEmpty() ) {
			sql_stringBuffer.append( " and o.processorIdentity =  ?" + (index) );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkProblemProcessLog.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
}
