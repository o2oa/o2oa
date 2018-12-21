package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;
import com.x.report.core.entity.Report_I_Ext_ContentDetail_;

/**
 * 汇报扩展信息内容服务数据操作
 * @author O2LEE
 */
public class Report_I_Ext_ContentDetailFactory extends AbstractFactory {
	
	public Report_I_Ext_ContentDetailFactory( Business business) throws Exception {
		super(business);
	}

	public Report_I_Ext_ContentDetail get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_Ext_ContentDetail.class, ExceptionWhen.none);
	}
	
	public List<Report_I_Ext_ContentDetail> listWithProfile( String profileId ) throws Exception {
		if(StringUtils.isEmpty(profileId)) {
			throw new Exception("reportId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_ContentDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_ContentDetail> cq = cb.createQuery( Report_I_Ext_ContentDetail.class );
		Root<Report_I_Ext_ContentDetail> root = cq.from(Report_I_Ext_ContentDetail.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_ContentDetail_.profileId), profileId);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_I_Ext_ContentDetail> listWithReport( String reportId ) throws Exception {
		if(StringUtils.isEmpty(reportId)) {
			throw new Exception("reportId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_ContentDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_ContentDetail> cq = cb.createQuery( Report_I_Ext_ContentDetail.class );
		Root<Report_I_Ext_ContentDetail> root = cq.from(Report_I_Ext_ContentDetail.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_ContentDetail_.reportId), reportId);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_I_Ext_ContentDetail> listWithContentId( String contentId ) throws Exception {
		if(StringUtils.isEmpty(contentId)) {
			throw new Exception("contentId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_ContentDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_ContentDetail> cq = cb.createQuery( Report_I_Ext_ContentDetail.class );
		Root<Report_I_Ext_ContentDetail> root = cq.from(Report_I_Ext_ContentDetail.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_ContentDetail_.contentId), contentId);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_I_Ext_ContentDetail> list(List<String> ids) throws Exception {
		if(ListTools.isEmpty(ids)) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_Ext_ContentDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_ContentDetail> cq = cb.createQuery(Report_I_Ext_ContentDetail.class);
		Root<Report_I_Ext_ContentDetail> root = cq.from(Report_I_Ext_ContentDetail.class);
		Predicate p = root.get(Report_I_Ext_ContentDetail_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_I_Ext_ContentDetail> getWithContentAndType(String contentId, String contentType) throws Exception {
		if(StringUtils.isEmpty(contentId)) {
			throw new Exception("contentId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_Ext_ContentDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_ContentDetail> cq = cb.createQuery(Report_I_Ext_ContentDetail.class);
		Root<Report_I_Ext_ContentDetail> root = cq.from(Report_I_Ext_ContentDetail.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_ContentDetail_.contentId), contentId);
		if( StringUtils.isNotEmpty( contentType )) {
			p = cb.and( p, cb.equal( root.get(Report_I_Ext_ContentDetail_.contentType), contentType) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
}