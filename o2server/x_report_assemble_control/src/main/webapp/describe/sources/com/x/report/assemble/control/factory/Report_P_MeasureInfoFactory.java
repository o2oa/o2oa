package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_P_MeasureInfo;
import com.x.report.core.entity.Report_P_MeasureInfo_;

/**
 * 战略举措信息服务类
 * @author O2LEE
 */
public class Report_P_MeasureInfoFactory extends AbstractFactory {

	public Report_P_MeasureInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_P_MeasureInfo信息对象")
	public Report_P_MeasureInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_P_MeasureInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_P_MeasureInfo信息列表")
	@SuppressWarnings("unused")
	public List<Report_P_MeasureInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_MeasureInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_MeasureInfo> cq = cb.createQuery( Report_P_MeasureInfo.class );
		Root<Report_P_MeasureInfo> root = cq.from( Report_P_MeasureInfo.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_P_MeasureInfo信息列表")
	public List<Report_P_MeasureInfo> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_P_MeasureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_MeasureInfo> cq = cb.createQuery(Report_P_MeasureInfo.class);
		Root<Report_P_MeasureInfo> root = cq.from(Report_P_MeasureInfo.class);
		Predicate p = root.get(Report_P_MeasureInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithYear(String thisYear) throws Exception {
		if( StringUtils.isEmpty( thisYear ) ){
			throw new Exception("year is null");
		}
		EntityManager em = this.entityManagerContainer().get(Report_P_MeasureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_P_MeasureInfo> root = cq.from(Report_P_MeasureInfo.class);
		Predicate p = cb.equal( root.get(Report_P_MeasureInfo_.year ), thisYear );
		cq.select( root.get( Report_P_MeasureInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}