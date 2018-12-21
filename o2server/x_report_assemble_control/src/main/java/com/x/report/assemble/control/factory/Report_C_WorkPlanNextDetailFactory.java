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
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail_;


/**
 * 系统汇报信息服务类
 * @author O2LEE
 */
public class Report_C_WorkPlanNextDetailFactory extends AbstractFactory {
	
	public Report_C_WorkPlanNextDetailFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_C_WorkPlanNextDetail信息对象")
	public Report_C_WorkPlanNextDetail get(String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_C_WorkPlanNextDetail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_C_WorkPlanNextDetail信息列表")
	@SuppressWarnings("unused")
	public List<Report_C_WorkPlanNextDetail> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkPlanNextDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkPlanNextDetail> cq = cb.createQuery( Report_C_WorkPlanNextDetail.class );
		Root<Report_C_WorkPlanNextDetail> root = cq.from( Report_C_WorkPlanNextDetail.class );
		return em.createQuery( cq ).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_C_WorkPlanNextDetail信息列表")
	public List<Report_C_WorkPlanNextDetail> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkPlanNextDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkPlanNextDetail> cq = cb.createQuery(Report_C_WorkPlanNextDetail.class);
		Root<Report_C_WorkPlanNextDetail> root = cq.from(Report_C_WorkPlanNextDetail.class);
		Predicate p = root.get(Report_C_WorkPlanNextDetail_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_C_WorkPlanNextDetail> listWorkPlanDetailNext( String reportId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkPlanNextDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkPlanNextDetail> cq = cb.createQuery( Report_C_WorkPlanNextDetail.class );
		Root<Report_C_WorkPlanNextDetail> root = cq.from( Report_C_WorkPlanNextDetail.class );
		Predicate p = cb.equal( root.get( Report_C_WorkPlanNextDetail_.reportId ), reportId );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<Report_C_WorkPlanNextDetail> listWorkPlanDetailWithPlanId( String planId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkPlanNextDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkPlanNextDetail> cq = cb.createQuery(Report_C_WorkPlanNextDetail.class);
		Root<Report_C_WorkPlanNextDetail> root = cq.from(Report_C_WorkPlanNextDetail.class);
		Predicate p = cb.equal( root.get( Report_C_WorkPlanNextDetail_.planId ), planId);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}