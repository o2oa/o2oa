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
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_C_WorkProgDetail_;

/**
 * 系统汇报信息服务类
 * @author O2LEE
 */
public class Report_C_WorkProgDetailFactory extends AbstractFactory {
	
	public Report_C_WorkProgDetailFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_C_WorkProgDetail信息对象")
	public Report_C_WorkProgDetail get(String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_C_WorkProgDetail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_C_WorkProgDetail信息列表")
	@SuppressWarnings("unused")
	public List<Report_C_WorkProgDetail> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_C_WorkProgDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProgDetail> cq = cb.createQuery( Report_C_WorkProgDetail.class );
		Root<Report_C_WorkProgDetail> root = cq.from( Report_C_WorkProgDetail.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_C_WorkProgDetail信息列表")
	public List<Report_C_WorkProgDetail> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProgDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProgDetail> cq = cb.createQuery(Report_C_WorkProgDetail.class);
		Root<Report_C_WorkProgDetail> root = cq.from(Report_C_WorkProgDetail.class);
		Predicate p = root.get(Report_C_WorkProgDetail_.id).in(ids);
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<Report_C_WorkProgDetail> listWorkProgDetail(String reportId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProgDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProgDetail> cq = cb.createQuery(Report_C_WorkProgDetail.class);
		Root<Report_C_WorkProgDetail> root = cq.from(Report_C_WorkProgDetail.class);
		Predicate p = cb.equal( root.get(Report_C_WorkProgDetail_.reportId), reportId);
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<Report_C_WorkProgDetail> listDetailWithProgId(String progId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_C_WorkProgDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_C_WorkProgDetail> cq = cb.createQuery(Report_C_WorkProgDetail.class);
		Root<Report_C_WorkProgDetail> root = cq.from(Report_C_WorkProgDetail.class);
		Predicate p = cb.equal( root.get( Report_C_WorkProgDetail_.progId ), progId);
		return em.createQuery(cq.where(p)).getResultList();
	}

    public List<String> listDetailIdsWithReportId(String reportId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Report_C_WorkProgDetail.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Report_C_WorkProgDetail> root = cq.from(Report_C_WorkProgDetail.class);
        Predicate p = cb.equal( root.get( Report_C_WorkProgDetail_.reportId ), reportId);
        cq.select( root.get( Report_C_WorkProgDetail_.id ) );
        return em.createQuery(cq.where(p)).getResultList();
    }
}