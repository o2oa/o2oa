package com.x.okr.assemble.control.factory;

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
import com.x.okr.entity.OkrCenterWorkReportStatistic;
import com.x.okr.entity.OkrCenterWorkReportStatistic_;

/**
 * 类   名：OkrCenterWorkReportStatistic<br/>
 * 实体类：OkrCenterWorkReportStatistic<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrCenterWorkReportStatisticFactory extends AbstractFactory {

	public OkrCenterWorkReportStatisticFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrCenterWorkReportStatistic实体信息对象" )
	public OkrCenterWorkReportStatistic get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrCenterWorkReportStatistic.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "根据中心工作，年份，月份，周数查询指定OkrCenterWorkReportStatistic实体信息ID列表" )
	public List<String> list( String centerId, Integer year, Integer month, Integer week, String statisticCycle ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception("centerId is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkReportStatistic.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrCenterWorkReportStatistic> root = cq.from(OkrCenterWorkReportStatistic.class);
		Predicate p = cb.equal( root.get(OkrCenterWorkReportStatistic_.centerId ), centerId );
		if( statisticCycle != null && !statisticCycle.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.statisticCycle ), statisticCycle ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.year ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.month ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.week ), week ));
		}
		cq.select( root.get( OkrCenterWorkReportStatistic_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe( "根据中心工作，年份，月份，周数查询指定OkrCenterWorkReportStatistic实体信息ID列表" )
	public Long count( String centerId, Integer year, Integer month, Integer week, String statisticCycle ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception("centerId is null.");
		}
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkReportStatistic.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrCenterWorkReportStatistic> root = cq.from(OkrCenterWorkReportStatistic.class);
		Predicate p = cb.equal( root.get(OkrCenterWorkReportStatistic_.centerId ), centerId );
		if( statisticCycle != null && !statisticCycle.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.statisticCycle ), statisticCycle ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.year ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.month ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.week ), week ));
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
	public List<OkrCenterWorkReportStatistic> listBaseInfo( String centerId, String reportCycle, Integer year, Integer month, Integer week ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkReportStatistic.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrCenterWorkReportStatistic> cq = cb.createQuery( OkrCenterWorkReportStatistic.class );
		Root<OkrCenterWorkReportStatistic> root = cq.from(OkrCenterWorkReportStatistic.class);
		Predicate p = cb.isNotNull( root.get(OkrCenterWorkReportStatistic_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.centerId ), centerId ));
		}
		if( reportCycle != null && !reportCycle.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.statisticCycle ), reportCycle ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.year ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.month ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.week ), week ));
		}
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
	public Long countBaseInfo( String centerId, String reportCycle, Integer year, Integer month, Integer week ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkReportStatistic.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<OkrCenterWorkReportStatistic> root = cq.from(OkrCenterWorkReportStatistic.class);
		Predicate p = cb.isNotNull( root.get(OkrCenterWorkReportStatistic_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.centerId ), centerId ));
		}
		if( reportCycle != null && !reportCycle.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.statisticCycle ), reportCycle ));
		}
		if( year != null && year > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.year ), year ));
		}
		if( month != null && month > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.month ), month ));
		}
		if( week != null && week > 0 ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.week ), week ));
		}
		cq.select( cb.count(root) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listByCenterWorkId(String centerId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkReportStatistic.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrCenterWorkReportStatistic> root = cq.from(OkrCenterWorkReportStatistic.class);
		Predicate p = cb.isNotNull( root.get(OkrCenterWorkReportStatistic_.id ) );
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrCenterWorkReportStatistic_.centerId ), centerId ));
		}
		cq.select( root.get(OkrCenterWorkReportStatistic_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
