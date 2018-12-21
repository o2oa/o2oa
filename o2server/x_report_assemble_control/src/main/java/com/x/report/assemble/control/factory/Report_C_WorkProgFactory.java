package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_C_WorkProgDetail_;
import com.x.report.core.entity.Report_C_WorkProg_;

/**
 * 系统汇报信息服务类
 * @author O2LEE
 */
public class Report_C_WorkProgFactory extends AbstractFactory {
	
	public Report_C_WorkProgFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的 Report_C_WorkProg 信息对象")
	public Report_C_WorkProg get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Report_C_WorkProg.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe("列示全部的 Report_C_WorkProg 信息列表")
	@SuppressWarnings("unused")
	public List<Report_C_WorkProg> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProg> cq = cb.createQuery( Report_C_WorkProg.class );
		Root<Report_C_WorkProg> root = cq.from( Report_C_WorkProg.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_C_WorkProg信息列表")
	public List<Report_C_WorkProg> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProg> cq = cb.createQuery(Report_C_WorkProg.class);
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = root.get(Report_C_WorkProg_.id).in(ids);
		cq.orderBy( cb.asc( root.get( Report_C_WorkProg_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_C_WorkProg> listWorkProgWithReportId(String reportId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProg> cq = cb.createQuery(Report_C_WorkProg.class);
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.reportId), reportId);
		cq.orderBy( cb.asc( root.get( Report_C_WorkProg_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWorkProgWithReportIds( List<String> reportIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = root.get(Report_C_WorkProg_.reportId).in( reportIds );
		cq.select( root.get(Report_C_WorkProg_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_C_WorkProgDetail> listDetailWithReportId(String reportId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProgDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProgDetail> cq = cb.createQuery(Report_C_WorkProgDetail.class);
		Root<Report_C_WorkProgDetail> root = cq.from(Report_C_WorkProgDetail.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProgDetail_.reportId), reportId);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listReportIdsWithKeyWorkId(String year, String month, String week, String reportDate,
			String reportType, List<String> keyWorkIds, String reportStatus) throws Exception {
		if( keyWorkIds == null || keyWorkIds.isEmpty() ) {
			throw new Exception("keyWorkIds is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = root.get(Report_C_WorkProg_.keyWorkId).in( keyWorkIds );
		if( year != null && !year.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_C_WorkProg_.year), year));
		}
		if( month != null && !month.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_C_WorkProg_.month), month));
		}
		if( week != null && !week.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_C_WorkProg_.week), week));
		}
		cq.select( root.get(Report_C_WorkProg_.reportId) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_C_WorkProg> listWithKeyWorkIds(String reportId, List<String> keyWorkIds) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProg> cq = cb.createQuery( Report_C_WorkProg.class );
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.reportId), reportId);
		if( keyWorkIds != null && !keyWorkIds.isEmpty() ) {
			p = cb.and( p, root.get( Report_C_WorkProg_.keyWorkId).in(keyWorkIds) );
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listWithReportAndWorkInfoId(String reportId, String workInfoId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.reportId), reportId);
		p = cb.and( p, cb.equal( root.get( Report_C_WorkProg_.workInfoId), workInfoId) );
		cq.select( root.get(Report_C_WorkProg_.id) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listWithReportAndWorkId(String reportId, String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.reportId), reportId);
		p = cb.and( p, cb.equal( root.get( Report_C_WorkProg_.keyWorkId), workId) );
		cq.select( root.get(Report_C_WorkProg_.id) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listIdsWithProfileId(String profileId) throws Exception {
		if( StringUtil.isEmpty( profileId )) {
			throw new Exception("profileId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.profileId), profileId);
		cq.select( root.get(Report_C_WorkProg_.id) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<Report_C_WorkProg> listWithKeyWorkIdAndYear(String workId, String year) throws Exception {
		if( StringUtil.isEmpty( workId )) {
			throw new Exception("workId is null!");
		}
		if( StringUtil.isEmpty( year )) {
			throw new Exception("year is null!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProg> cq = cb.createQuery( Report_C_WorkProg.class );
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.keyWorkId), workId);
		p = cb.and( p, cb.equal( root.get(Report_C_WorkProg_.year), year));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listWithYear(String year) throws Exception {
		if( StringUtil.isEmpty( year )) {
			throw new Exception("year is null!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProg.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_C_WorkProg> root = cq.from(Report_C_WorkProg.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProg_.year), year);
		cq.select( root.get(Report_C_WorkProg_.id) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
}