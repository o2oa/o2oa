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
import com.x.report.core.entity.Report_P_ProfileDetail;
import com.x.report.core.entity.Report_P_ProfileDetail_;

/**
 * 系统汇报生成依据记录详细信息服务类
 * @author O2LEE
 */
public class Report_P_ProfileDetailFactory extends AbstractFactory {
	
	public Report_P_ProfileDetailFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_P_ProfileDetail信息对象")
	public Report_P_ProfileDetail get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_P_ProfileDetail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_P_ProfileDetail信息列表")
	@SuppressWarnings("unused")
	public List<Report_P_ProfileDetail> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_ProfileDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_ProfileDetail> cq = cb.createQuery( Report_P_ProfileDetail.class );
		Root<Report_P_ProfileDetail> root = cq.from( Report_P_ProfileDetail.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_P_ProfileDetail信息列表")
	public List<Report_P_ProfileDetail> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_P_ProfileDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_ProfileDetail> cq = cb.createQuery(Report_P_ProfileDetail.class);
		Root<Report_P_ProfileDetail> root = cq.from(Report_P_ProfileDetail.class);
		Predicate p = root.get(Report_P_ProfileDetail_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据依据信息Id列示Report_P_ProfileDetail信息列表
	 * @param recordId
	 * @param reportModule
	 * @param snapType
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("根据依据信息Id,reportModule,snapType列示Report_P_ProfileDetail信息列表")
	public List<Report_P_ProfileDetail> listWithRecordId( String recordId, String reportModule, String snapType ) throws Exception {
		if( recordId == null || recordId.isEmpty() ) {
			throw new Exception("recordId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_P_ProfileDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_ProfileDetail> cq = cb.createQuery(Report_P_ProfileDetail.class);
		Root<Report_P_ProfileDetail> root = cq.from(Report_P_ProfileDetail.class);
		Predicate p = cb.equal( root.get( Report_P_ProfileDetail_.profileId), recordId );
		if( reportModule != null && !reportModule.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_ProfileDetail_.reportModule ), reportModule ) );
		}
		if( snapType != null && !snapType.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get( Report_P_ProfileDetail_.snapType ), snapType ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_P_ProfileDetail> getDetailValue(String profileId, String reportModule, String snapType) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_ProfileDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_P_ProfileDetail> cq = cb.createQuery( Report_P_ProfileDetail.class );
		Root<Report_P_ProfileDetail> root = cq.from( Report_P_ProfileDetail.class );
		Predicate p = cb.equal( root.get( Report_P_ProfileDetail_.profileId), profileId );
		p = cb.and( p, cb.equal( root.get( Report_P_ProfileDetail_.reportModule ), reportModule ) );
		p = cb.and( p, cb.equal( root.get( Report_P_ProfileDetail_.snapType ), snapType ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listDetailValueListWithCondition(List<String> profileIds, String snapType) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_ProfileDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_ProfileDetail> root = cq.from( Report_P_ProfileDetail.class );
		Predicate p = root.get( Report_P_ProfileDetail_.profileId).in( profileIds );
		p = cb.and( p, cb.equal( root.get( Report_P_ProfileDetail_.snapType ), snapType ) );
		cq.select( root.get( Report_P_ProfileDetail_.snapContent) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithProfileId(String profileId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_ProfileDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_ProfileDetail> root = cq.from( Report_P_ProfileDetail.class );
		Predicate p = cb.equal( root.get( Report_P_ProfileDetail_.profileId ), profileId );
		cq.select( root.get( Report_P_ProfileDetail_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}