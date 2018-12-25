package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_R_CreateTime;

/**
 * 记录所有类别的汇报上一次和下一次生成时间的信息
 * @author O2LEE
 */
public class Report_R_CreateTimeFactory extends AbstractFactory {
	
	public Report_R_CreateTimeFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_R_CreateTime信息对象")
	public Report_R_CreateTime get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_R_CreateTime.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_R_CreateTime信息列表")
	@SuppressWarnings("unused")
	public List<Report_R_CreateTime> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_R_CreateTime.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_R_CreateTime> cq = cb.createQuery( Report_R_CreateTime.class );
		Root<Report_R_CreateTime> root = cq.from( Report_R_CreateTime.class );
		return em.createQuery(cq).getResultList();
	}
}