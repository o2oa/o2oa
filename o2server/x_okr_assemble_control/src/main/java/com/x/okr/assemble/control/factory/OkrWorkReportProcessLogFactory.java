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

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrWorkReportProcessLog;
import com.x.okr.entity.OkrWorkReportProcessLog_;

/**
 * 类   名：OkrWorkReportProcessLogFactory<br/>
 * 实体类：OkrWorkReportProcessLog<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportProcessLogFactory extends AbstractFactory {

	public OkrWorkReportProcessLogFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrWorkReportProcessLog实体信息对象" )
	public OkrWorkReportProcessLog get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkReportProcessLog.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrWorkReportProcessLog实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportProcessLog> root = cq.from( OkrWorkReportProcessLog.class);
		cq.select(root.get(OkrWorkReportProcessLog_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkReportProcessLog实体信息列表" )
	public List<OkrWorkReportProcessLog> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkReportProcessLog>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportProcessLog> cq = cb.createQuery(OkrWorkReportProcessLog.class);
		Root<OkrWorkReportProcessLog> root = cq.from(OkrWorkReportProcessLog.class);
		Predicate p = root.get(OkrWorkReportProcessLog_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，获取汇报处理记录信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
//	@MethodDescribe( "根据工作信息ID，获取汇报处理记录信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportProcessLog> root = cq.from( OkrWorkReportProcessLog.class);
		Predicate p = cb.equal( root.get(OkrWorkReportProcessLog_.workId), workId );
		cq.select(root.get( OkrWorkReportProcessLog_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
//	@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportProcessLog> root = cq.from(OkrWorkReportProcessLog.class);
		Predicate p = cb.equal( root.get( OkrWorkReportProcessLog_.centerId ), centerId );
		cq.select(root.get( OkrWorkReportProcessLog_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> listNextWithFilter(String id, Integer count, Object sequence, WrapInFilter wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportProcessLog.class);
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if ( order == null || order.isEmpty() ) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkReportProcessLog.class.getCanonicalName() + " o where 1=1" );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportProcessLog.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults( count ).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> listPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportProcessLog.class );
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkReportProcessLog.class.getCanonicalName() + " o where 1=1" );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportProcessLog.class);
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
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportProcessLog.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count( o.id ) FROM " + OkrWorkReportProcessLog.class.getCanonicalName() + " o where 1=1" );
		
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportProcessLog.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

	/**
	 * 根据reportId, 处理人，环节名称 以及处理状态 确定一条处理记录
	 * @param id
	 * @param processorIdentity
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByReportIdAndProcessor(String reportId, String activityName, String processorIdentity, String processStatus ) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception( " reportId is null!" );
		}
		if( activityName == null || activityName.isEmpty() ){
			throw new Exception( " activityName is null!" );
		}
		if( processorIdentity == null || processorIdentity.isEmpty() ){
			throw new Exception( " processorIdentity is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportProcessLog> root = cq.from(OkrWorkReportProcessLog.class);
		Predicate p = cb.equal( root.get( OkrWorkReportProcessLog_.workReportId ), reportId );
		p = cb.and( p, cb.equal( root.get( OkrWorkReportProcessLog_.activityName ), activityName));
		p = cb.and( p, cb.equal( root.get( OkrWorkReportProcessLog_.processorIdentity ), processorIdentity));
		p = cb.and( p, cb.equal( root.get( OkrWorkReportProcessLog_.processStatus ), processStatus));
		
		cq.select(root.get( OkrWorkReportProcessLog_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByReportId(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( " id is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportProcessLog> root = cq.from(OkrWorkReportProcessLog.class);
		Predicate p = cb.equal( root.get( OkrWorkReportProcessLog_.workReportId ), id );
		cq.select(root.get( OkrWorkReportProcessLog_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询工作汇报处理者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctProcessorIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkReportProcessLog> root = cq.from(OkrWorkReportProcessLog.class);
		Predicate p = cb.isNotNull( root.get( OkrWorkReportProcessLog_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportProcessLog_.processorIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportProcessLog_.processorIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkReportProcessLog_.processorIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从工作汇报处理日志信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportProcessLog> listErrorIdentitiesInReportProcessLog(String identity) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportProcessLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportProcessLog> cq = cb.createQuery( OkrWorkReportProcessLog.class );
		Root<OkrWorkReportProcessLog> root = cq.from( OkrWorkReportProcessLog.class );
		Predicate p = cb.isNotNull(root.get( OkrWorkReportProcessLog_.id ));		
		Predicate p_processorIdentity = cb.isNotNull(root.get( OkrWorkReportProcessLog_.processorIdentity ));
		p_processorIdentity = cb.and( p_processorIdentity, cb.equal( root.get( OkrWorkReportProcessLog_.processorIdentity ), identity ) );		
		p = cb.and( p, p_processorIdentity );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
