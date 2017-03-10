package com.x.okr.assemble.control.factory;

import java.util.Date;
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
	
	@MethodDescribe( "获取指定Id的OkrStatisticReportContent实体信息对象" )
	public OkrStatisticReportContent get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrStatisticReportContent.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "根据中心工作，年份，月份，周数查询指定实体信息ID列表" )
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
	
	@MethodDescribe( "根据中心工作，年份，月份，周数查询指定实体信息ID列表" )
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
	public List<String> list( String centerId, String parentId, String statisticTime, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get(OkrStatisticReportContent_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.parentId ), parentId ));
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
	public List<String> listFirstLayer( String centerId, String workId, String statisticTimeFlag, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get(OkrStatisticReportContent_.id ) );
		p = cb.and( p, cb.isNull( root.get(OkrStatisticReportContent_.parentId )));
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
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
	public Long count( String centerId, String parentId, String statisticTime, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.parentId ), parentId ));
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
	
	public Long countFirstLayer( String centerId, String workId, String statisticTime, String cycleType, Integer year, Integer month, Integer week, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		p = cb.and( p, cb.isNull( root.get(OkrStatisticReportContent_.parentId )));
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
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

	public List<String> listDateTimeFlags(String centerId, String workId, String cycleType, Integer year, Integer month, Integer week, Date startDate, Date endDate, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportContent> root = cq.from(OkrStatisticReportContent.class);
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportContent_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportContent_.centerId ), centerId ));
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
		if( startDate != null ){
			p = cb.and( p, cb.greaterThan( root.get(OkrStatisticReportContent_.statisticTime ), startDate));
		}
		if( endDate != null ){
			p = cb.and( p, cb.lessThan( root.get(OkrStatisticReportContent_.statisticTime ), endDate));
		}
		cq.distinct(true).select( root.get(OkrStatisticReportContent_.statisticTimeFlag ));

		return em.createQuery( cq.where(p) ).getResultList();
	}
}
