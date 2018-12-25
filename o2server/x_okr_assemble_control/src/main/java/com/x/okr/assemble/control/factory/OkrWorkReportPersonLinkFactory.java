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
import com.x.okr.assemble.control.jaxrs.WorkPersonSearchFilter;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportPersonLink_;

/**
 * 类   名：OkrWorkReportPersonLinkFactory<br/>
 * 实体类：OkrWorkReportPersonLink<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportPersonLinkFactory extends AbstractFactory {

	public OkrWorkReportPersonLinkFactory(Business business) throws Exception {
		super(business);
	}
	
//	@MethodDescribe( "获取指定Id的OkrWorkReportPersonLink实体信息对象" )
	public OkrWorkReportPersonLink get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkReportPersonLink.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrWorkReportPersonLink实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportPersonLink> root = cq.from( OkrWorkReportPersonLink.class);
		cq.select(root.get(OkrWorkReportPersonLink_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkReportPersonLink实体信息列表" )
	public List<OkrWorkReportPersonLink> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkReportPersonLink>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportPersonLink> cq = cb.createQuery(OkrWorkReportPersonLink.class);
		Root<OkrWorkReportPersonLink> root = cq.from(OkrWorkReportPersonLink.class);
		Predicate p = root.get(OkrWorkReportPersonLink_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByReportId( String reportId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportPersonLink> root = cq.from( OkrWorkReportPersonLink.class);
		Predicate p = cb.equal( root.get(OkrWorkReportPersonLink_.workReportId), reportId );
		cq.select(root.get( OkrWorkReportPersonLink_.id) );
		return em.createQuery(cq.where(p)).setMaxResults(5000).getResultList();
	}
	
	/**
	 * 根据工作信息ID，获取汇报处理链信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据工作信息ID，获取汇报处理链信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportPersonLink> root = cq.from( OkrWorkReportPersonLink.class);
		Predicate p = cb.equal( root.get(OkrWorkReportPersonLink_.workId), workId );
		cq.select(root.get( OkrWorkReportPersonLink_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportPersonLink> root = cq.from(OkrWorkReportPersonLink.class);
		Predicate p = cb.equal( root.get( OkrWorkReportPersonLink_.centerId ), centerId );
		cq.select(root.get( OkrWorkReportPersonLink_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据汇报ID获取该汇报的最高处理过程级别
	 * @param reportId
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据汇报ID获取该汇报的最高处理过程级别" )
	public Integer getMaxProcessLevel(String reportId) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception( " reportId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportPersonLink.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<OkrWorkReportPersonLink> cq = cb.createQuery( OkrWorkReportPersonLink.class );
		Root<OkrWorkReportPersonLink> root = cq.from( OkrWorkReportPersonLink.class);		
		cq.orderBy( cb.desc( root.get( OkrWorkReportPersonLink_.processLevel ) ) );	
		Predicate p = cb.equal( root.get( OkrWorkReportPersonLink_.workReportId ), reportId );
		List<OkrWorkReportPersonLink> resultList = em.createQuery(cq.where(p)).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return 0;
		}else{
			return resultList.get(0).getProcessLevel();
		}
	}
	//@MethodDescribe( "根据汇报ID和处理等级来查询指定等级的处理人" )
	public List<String> getProcessPersonLinkInfoByReportAndLevel(String reportId, Integer processLevel, String processorIdentity, String processStatus, String status ) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception( " reportId is null!" );
		}
		if( processLevel == null || processLevel < 0  ){
			throw new Exception( " processLevel is invalid!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportPersonLink> root = cq.from(OkrWorkReportPersonLink.class);
		Predicate p = cb.equal( root.get( OkrWorkReportPersonLink_.workReportId ), reportId );
		p = cb.and( p, cb.equal( root.get( OkrWorkReportPersonLink_.processLevel ), processLevel ));
		if( processorIdentity != null && !processorIdentity.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportPersonLink_.processorIdentity ), processorIdentity ));
		}
		if( processStatus != null && !processStatus.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportPersonLink_.processStatus ), processStatus ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportPersonLink_.status ), status ));
		}
		cq.select(root.get( OkrWorkReportPersonLink_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public List<OkrWorkReportPersonLink> listNextWithFilter(String id, Integer count, Object sequence, WorkPersonSearchFilter wrapIn) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportPersonLink.class);
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if ( order == null || order.isEmpty() ) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkReportPersonLink.class.getCanonicalName() + " o where 1=1" );
		if ((null != sequence)) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like  ?" + (index) );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportPersonLink.class);
		// 为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults( count ).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<OkrWorkReportPersonLink> listPrevWithFilter( String id, Integer count, Object sequence, WorkPersonSearchFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportPersonLink.class );
		String order = wrapIn.getOrder(); // 排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		if (order == null || order.isEmpty()) {
			order = "DESC";
		}
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkReportPersonLink.class.getCanonicalName() + " o where 1=1" );
		if ((null != sequence)) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField() + " " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? ">" : "<" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like  ?" + (index) );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportPersonLink.class);
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
	public long getCountWithFilter( WorkPersonSearchFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkReportPersonLink.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count( o.id ) FROM " + OkrWorkReportPersonLink.class.getCanonicalName() + " o where 1=1" );
		
		if ( null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like  ?" + (index) );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkReportPersonLink.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

	/**
	 * 查询工作汇报处理者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctProcessorIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkReportPersonLink> root = cq.from(OkrWorkReportPersonLink.class);
		Predicate p = cb.isNotNull( root.get( OkrWorkReportPersonLink_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportPersonLink_.processorIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkReportPersonLink_.processorIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkReportPersonLink_.processorIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从工作汇报处理者信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportPersonLink> listErrorIdentitiesInReportPersonInfo(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportPersonLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportPersonLink> cq = cb.createQuery( OkrWorkReportPersonLink.class );
		Root<OkrWorkReportPersonLink> root = cq.from( OkrWorkReportPersonLink.class );
		Predicate p = cb.isNotNull(root.get( OkrWorkReportPersonLink_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrWorkReportPersonLink_.id ), recordId ) );
		}
		
		Predicate p_processorIdentity = cb.isNotNull(root.get( OkrWorkReportPersonLink_.processorIdentity ));
		p_processorIdentity = cb.and( p_processorIdentity, cb.equal( root.get( OkrWorkReportPersonLink_.processorIdentity ), identity ) );		
		p = cb.and( p, p_processorIdentity );
		return em.createQuery(cq.where(p)).getResultList();
	}

	
}
