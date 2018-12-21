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
import com.x.report.core.entity.Report_I_Detail;
import com.x.report.core.entity.Report_I_Detail_;

/**
 * 系统汇报信息服务类
 * @author O2LEE
 */
public class Report_I_DetailFactory extends AbstractFactory {
	
	public Report_I_DetailFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_I_Detail信息对象")
	public Report_I_Detail get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_Detail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("获取指定Id的Report_I_Detail信息对象")
	public Report_I_Detail getDetail( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_Detail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_I_Detail信息列表")
	@SuppressWarnings("unused")
	public List<Report_I_Detail> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_Detail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Detail> cq = cb.createQuery( Report_I_Detail.class );
		Root<Report_I_Detail> root = cq.from( Report_I_Detail.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_I_Detail信息列表")
	public List<Report_I_Detail> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Detail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Detail> cq = cb.createQuery(Report_I_Detail.class);
		Root<Report_I_Detail> root = cq.from(Report_I_Detail.class);
		Predicate p = root.get(Report_I_Detail_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_I_Detail> listWithReportId( String reportId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Detail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Detail> cq = cb.createQuery(Report_I_Detail.class);
		Root<Report_I_Detail> root = cq.from(Report_I_Detail.class);
		Predicate p = cb.equal( root.get(Report_I_Detail_.reportId), reportId);
		return em.createQuery(cq.where(p)).getResultList();
	}
}