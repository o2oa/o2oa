package com.x.okr.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrStatisticReportContent_;

/**
 * 类   名：OkrStatisticReportContent<br/>
 * 实体类：OkrStatisticReportContent<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrStatisticReportContentFactory extends AbstractFactory {

	public OkrStatisticReportContentFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrStatisticReportContent实体信息对象" )
	public OkrStatisticReportContent get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrStatisticReportContent.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe( "根据中心工作，年份，月份，周数查询指定实体信息ID列表" )
	public List<String> list( String workId, Integer year, Integer month, Integer week, String cycleType ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.equal( root.get(OkrStatisticReportContent_.workId ), workId );
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		cq.select( root.get( OkrStatisticReportContent_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据中心工作，年份，月份，周数查询指定实体信息ID列表" )
	public Long count( String workId, Integer year, Integer month, Integer week, String cycleType ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrStatisticReportContent> root = cq.from( OkrStatisticReportContent.class );
		Predicate p = cb.equal( root.get(OkrStatisticReportContent_.workId ), workId );
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		cq.select( cb.count(root) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据条件获取统计信息列表
	 * @param workTypeName
	 * @param reportCycle
	 * @param year
	 * @param month
	 * @param week
	 * @return
	 * @throws Exception 
	 */
	public List<String> list( String centerId, String centerTitle, String parentId, String workType, String statisticTime, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get(OkrStatisticReportContent_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( centerTitle != null && !centerTitle.trim().isEmpty() ){
			p = cb.and( p, cb.like( root.get(OkrStatisticReportContent_.centerTitle ), "%" + centerTitle.trim() + "%" ));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.parentId ), parentId ));
		}
		if( workType != null && !workType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workType ), workType ));
		}
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.status ), status ));
		}
		if( statisticTime != null && !statisticTime.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticTimeFlag ), statisticTime ));
		}
		cq.select( root.get( OkrStatisticReportContent_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据条件获取统计信息列表
	 * @param workTypeName
	 * @param reportCycle
	 * @param year
	 * @param month
	 * @param week
	 * @return
	 * @throws Exception 
	 */
	public List<String> listFirstLayer( String centerId, String centerTitle, String workId, String workType, String statisticTimeFlag, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get(OkrStatisticReportContent_.id ) );
		p = cb.and( p, cb.isNull( root.get(OkrStatisticReportContent_.parentId )));
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( centerTitle != null && !centerTitle.trim().isEmpty() ){
			p = cb.and( p, cb.like( root.get(OkrStatisticReportContent_.centerTitle ), "%" + centerTitle.trim() + "%" ));
		}
		if( workId != null && !workId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workId ), workId ));
		}
		if( workType != null && !workType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workType ), workType ));
		}
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.status ), status ));
		}
		if( statisticTimeFlag != null && !statisticTimeFlag.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticTimeFlag ), statisticTimeFlag ));
		}
		cq.select( root.get( OkrStatisticReportContent_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据条件获取统计信息列表
	 * @param workTypeName
	 * @param reportCycle
	 * @param year
	 * @param month
	 * @param week
	 * @return
	 * @throws Exception 
	 */
	public Long count( String centerId, String centerTitle, String parentId, String workType, String statisticTime, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( centerTitle != null && !centerTitle.trim().isEmpty() ){
			p = cb.and( p, cb.like( root.get(OkrStatisticReportContent_.centerTitle ), "%" + centerTitle.trim() + "%" ));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.parentId ), parentId ));
		}
		if( workType != null && !workType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workType ), workType ));
		}
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.status ), status ));
		}
		if( statisticTime != null && !statisticTime.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticTimeFlag ), statisticTime ));
		}
		cq.select( cb.count(root) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	public Long countFirstLayer( String centerId, String centerTitle, String workId, String workType, String statisticTime, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		p = cb.and( p, cb.isNull( root.get(OkrStatisticReportContent_.parentId )));
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( centerTitle != null && !centerTitle.trim().isEmpty() ){
			p = cb.and( p, cb.like( root.get(OkrStatisticReportContent_.centerTitle ), "%" + centerTitle.trim() + "%" ));
		}
		if( workType != null && !workType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workType ), workType ));
		}
		if( workId != null && !workId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workId ), workId ));
		}
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.status ), status ));
		}
		if( statisticTime != null && !statisticTime.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticTimeFlag ), statisticTime ));
		}
		cq.select( cb.count(root) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<OkrStatisticReportContent> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportContent> cq = cb.createQuery( OkrStatisticReportContent.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = root.get(OkrStatisticReportContent_.id ).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listDateTimeFlags(String centerId, String centerTitle, String workId, String workType, String cycleType, Integer year, Integer month, Integer week, Date startDate, Date endDate, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( centerTitle != null && !centerTitle.trim().isEmpty() ){
			p = cb.and( p, cb.like( root.get(OkrStatisticReportContent_.centerTitle ), "%" + centerTitle.trim() + "%" ));
		}
		if( workId != null && !workId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workId ), workId ));
		}
		if( workType != null && !workType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.workType ), workType ));
		}
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.cycleType ), cycleType ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticYear ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticMonth ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.statisticWeek ), week ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.status ), status ));
		}
		if( startDate != null ){
			p = cb.and( p, cb.greaterThan( root.get(OkrStatisticReportContent_.statisticTime ), startDate));
		}
		if( endDate != null ){
			p = cb.and( p, cb.lessThan( root.get(OkrStatisticReportContent_.statisticTime ), endDate));
		}
		cq.distinct(true).select( root.get(OkrStatisticReportContent_.statisticTimeFlag ));
		return em.createQuery( cq.where(p) ).getResultList();
	}
	/**
	 * 查询统计数据中工作责任者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctResponsibilityIdentity( List<String> identities_ok, List<String> identities_error ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrStatisticReportContent_.responsibilityIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrStatisticReportContent_.responsibilityIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrStatisticReportContent_.responsibilityIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从工作最新汇报内容统计信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrStatisticReportContent> listErrorIdentitiesInStReportContent(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportContent> cq = cb.createQuery( OkrStatisticReportContent.class );
		Root<OkrStatisticReportContent> root = cq.from( OkrStatisticReportContent.class );
		Predicate p = cb.isNotNull(root.get( OkrStatisticReportContent_.id ));	
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrStatisticReportContent_.id ), recordId ) );
		}
		
		Predicate p_responsibilityIdentity = cb.isNotNull(root.get( OkrStatisticReportContent_.responsibilityIdentity ));
		p_responsibilityIdentity = cb.and( p_responsibilityIdentity, cb.equal( root.get( OkrStatisticReportContent_.responsibilityIdentity ), identity ) );		
		p = cb.and( p, p_responsibilityIdentity );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		cq.select(root.get( OkrStatisticReportContent_.id ));
		return em.createQuery(cq).getResultList();
	}
}
