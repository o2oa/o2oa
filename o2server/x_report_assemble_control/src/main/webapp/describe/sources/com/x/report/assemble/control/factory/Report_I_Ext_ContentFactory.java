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
import com.x.report.common.tools.LogUtil;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_Content_;

/**
 * 汇报扩展信息服务数据操作
 * @author O2LEE
 */
public class Report_I_Ext_ContentFactory extends AbstractFactory {
	
	public Report_I_Ext_ContentFactory( Business business) throws Exception {
		super(business);
	}

	public Report_I_Ext_Content get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_Ext_Content.class, ExceptionWhen.none);
	}
	
	public List<Report_I_Ext_Content> listWithProfile( String profileId ) throws Exception {
		if(StringUtils.isEmpty(profileId)) {
			throw new Exception("reportId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_Content.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_Content> cq = cb.createQuery( Report_I_Ext_Content.class );
		Root<Report_I_Ext_Content> root = cq.from(Report_I_Ext_Content.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_Content_.profileId), profileId);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listIdsWithProfile( String profileId ) throws Exception {
		if(StringUtils.isEmpty(profileId)) {
			throw new Exception("reportId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_Content.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_Ext_Content> root = cq.from(Report_I_Ext_Content.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_Content_.profileId), profileId);
		cq.select( root.get(Report_I_Ext_Content_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithReports( List<String> reportIds, String infoLevel, String targetPerson ) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_Content.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_Ext_Content> root = cq.from(Report_I_Ext_Content.class);
		Predicate p = root.get(Report_I_Ext_Content_.reportId).in( reportIds );
		if(StringUtils.isNotEmpty( infoLevel )) {
			p = cb.and( p, cb.equal( root.get(Report_I_Ext_Content_.infoLevel), infoLevel) );
		}
		if(StringUtils.isNotEmpty( targetPerson )) {
			p = cb.and( p, cb.equal( root.get(Report_I_Ext_Content_.targetPerson), targetPerson) );
		}
		cq.select( root.get(Report_I_Ext_Content_.id) );
		LogUtil.INFO( "SQL", em.createQuery(cq.where(p)).toString() );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_I_Ext_Content> listWithReport( String reportId, String infoLevel, String targetPerson ) throws Exception {
		if ( StringUtils.isEmpty( reportId )) {
			throw new Exception("reportId is empty.");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_Content.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_Content> cq = cb.createQuery( Report_I_Ext_Content.class );
		Root<Report_I_Ext_Content> root = cq.from(Report_I_Ext_Content.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_Content_.reportId) , reportId);
		if(StringUtils.isNotEmpty( infoLevel )) {
			p = cb.and( p, cb.equal( root.get(Report_I_Ext_Content_.infoLevel), infoLevel) );
		}
		if(StringUtils.isNotEmpty( targetPerson )) {
			p = cb.and( p, cb.equal( root.get(Report_I_Ext_Content_.targetPerson), targetPerson) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_I_Ext_Content> listWithInfoLevel( String infoLevel ) throws Exception {
		if(StringUtils.isEmpty(infoLevel)) {
			throw new Exception("infoLevel is empty!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Ext_Content.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_Content> cq = cb.createQuery( Report_I_Ext_Content.class );
		Root<Report_I_Ext_Content> root = cq.from(Report_I_Ext_Content.class);
		Predicate p = cb.equal( root.get(Report_I_Ext_Content_.infoLevel), infoLevel);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<Report_I_Ext_Content> list(List<String> ids) throws Exception {
		if(ListTools.isEmpty(ids)) {
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_Ext_Content.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Ext_Content> cq = cb.createQuery(Report_I_Ext_Content.class);
		Root<Report_I_Ext_Content> root = cq.from(Report_I_Ext_Content.class);
		Predicate p = root.get(Report_I_Ext_Content_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
}