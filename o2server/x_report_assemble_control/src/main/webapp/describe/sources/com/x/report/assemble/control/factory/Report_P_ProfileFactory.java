package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_Profile_;

/**
 * 系统汇报生成依据记录信息服务类
 * @author O2LEE
 */
public class Report_P_ProfileFactory extends AbstractFactory {
	
	public Report_P_ProfileFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_P_Profile信息对象")
	public Report_P_Profile get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_P_Profile.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_P_Profile信息列表")
	@SuppressWarnings("unused")
	public List<Report_P_Profile> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Profile.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_Profile> cq = cb.createQuery( Report_P_Profile.class );
		Root<Report_P_Profile> root = cq.from( Report_P_Profile.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_P_Profile信息列表")
	public List<Report_P_Profile> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_P_Profile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_Profile> cq = cb.createQuery(Report_P_Profile.class);
		Root<Report_P_Profile> root = cq.from(Report_P_Profile.class);
		Predicate p = root.get(Report_P_Profile_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	//@MethodDescribe("根据条件列示指定Id的Report_P_Profile信息列表")
	public List<Report_P_Profile> getWithCycleAndType( String modules, String reportType, String reportYear,
			String reportMonth, String reportWeek, String reportDateString ) throws Exception {
		if( modules == null || modules.isEmpty() ) {
			throw new Exception("modules is null!");
		}
		if( reportType == null || reportType.isEmpty() ) {
			throw new Exception("reportType is null!");
		}
		if( reportYear == null || reportYear.isEmpty() ) {
			throw new Exception("reportYear is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_P_Profile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_Profile> cq = cb.createQuery(Report_P_Profile.class);
		Root<Report_P_Profile> root = cq.from(Report_P_Profile.class);
		Predicate p = cb.equal( root.get( Report_P_Profile_.modules), modules );
		p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportType ), reportType ) );
		p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportYear ), reportYear ) );
		if( reportMonth != null && !reportMonth.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportMonth ), reportMonth ) );
		}
		if( reportWeek != null && !reportWeek.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportWeek ), reportMonth ) );
		}
		if( reportDateString != null && !reportDateString.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportDateString ), reportMonth ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_P_Profile> listErrorCreateRecord( String reportType, String reportYear, String reportMonth, String reportWeek ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Profile.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_Profile> cq = cb.createQuery( Report_P_Profile.class );
		Root<Report_P_Profile> root = cq.from( Report_P_Profile.class );
		Predicate p = cb.equal( root.get( Report_P_Profile_.reportType), reportType );
		
		if( reportYear != null && !reportYear.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportYear ), reportYear ) );
		}
		if( reportMonth != null && !reportMonth.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportMonth ), reportMonth ) );
		}
		if( reportWeek != null && !reportWeek.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportWeek ), reportWeek ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_P_Profile> listErrorCreateRecord(String reportType, String reportYear, String reportMonth, String reportWeek, String date) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Profile.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_Profile> cq = cb.createQuery( Report_P_Profile.class );
		Root<Report_P_Profile> root = cq.from( Report_P_Profile.class );
		Predicate p = cb.equal( root.get( Report_P_Profile_.reportType), reportType );
		
		if( reportYear != null && !reportYear.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportYear ), reportYear ) );
		}
		if( reportMonth != null && !reportMonth.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportMonth ), reportMonth ) );
		}
		if( reportWeek != null && !reportWeek.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportWeek ), reportWeek ) );
		}
		if( date != null && !date.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportDateString ), date ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsWithCondition(String reportType, String year, String month, String week, String reportDate) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Profile.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Profile> root = cq.from( Report_P_Profile.class );
		Predicate p = cb.equal( root.get( Report_P_Profile_.reportType), reportType );
		
		if( year != null && !year.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportYear ), year ) );
		}
		if( month != null && !month.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportMonth ), month ) );
		}
		if( week != null && !week.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportWeek ), week ) );
		}
		if( reportDate != null && !reportDate.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_Profile_.reportDateString ), reportDate ) );
		}
		cq.select( root.get( Report_P_Profile_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithYear(String year) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_P_Profile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_P_Profile> root = cq.from(Report_P_Profile.class);
		Predicate p = cb.equal( root.get(Report_P_Profile_.reportYear), year);
		cq.select( root.get( Report_P_Profile_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_P_Profile> listCreateTimes( String enumReportType ) throws Exception {
		if( enumReportType == null || enumReportType.isEmpty() ) {
			throw new Exception("enumReportType is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_P_Profile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_Profile> cq = cb.createQuery(Report_P_Profile.class);
		Root<Report_P_Profile> root = cq.from(Report_P_Profile.class);
		Predicate p = cb.equal( root.get(Report_P_Profile_.reportType), enumReportType);
		cq.orderBy( cb.desc( root.get( Report_P_Profile_.createTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}