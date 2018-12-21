package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_I_WorkInfoDetail_;
import com.x.report.core.entity.Report_I_WorkInfo_;

/**
 * 战略举措信息服务类
 * @author O2LEE
 */
public class Report_I_WorkInfoFactory extends AbstractFactory {

	public Report_I_WorkInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_P_MeasureInfo信息对象")
	public Report_I_WorkInfo get(String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_WorkInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_P_MeasureInfo信息列表")
	@SuppressWarnings("unused")
	public List<Report_I_WorkInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_WorkInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_WorkInfo> cq = cb.createQuery( Report_I_WorkInfo.class );
		Root<Report_I_WorkInfo> root = cq.from( Report_I_WorkInfo.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_P_MeasureInfo信息列表")
	public List<Report_I_WorkInfo> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_WorkInfo> cq = cb.createQuery(Report_I_WorkInfo.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = root.get(Report_I_WorkInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

    public List<String> listIdsWithReport(String reportId, String workMonthFlag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = cb.equal( root.get(Report_I_WorkInfo_.reportId), reportId );
		if( StringUtils.isNotEmpty( workMonthFlag )) {
			p = cb.and( p, cb.equal( root.get(Report_I_WorkInfo_.workMonthFlag), workMonthFlag ));
		}
		cq.select(root.get(Report_I_WorkInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
    }
    
    public List<String> listIdsWithReports( List<String> reportIds, String workMonthFlag) throws Exception {
    	if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = root.get(Report_I_WorkInfo_.reportId).in( reportIds );
		if( StringUtils.isNotEmpty( workMonthFlag )) {
			p = cb.and( p, cb.equal( root.get(Report_I_WorkInfo_.workMonthFlag), workMonthFlag ));
		}
		cq.select(root.get(Report_I_WorkInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
    }

    public Report_I_WorkInfo getWithReportAndWorkId(String reportId, String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_WorkInfo> cq = cb.createQuery(Report_I_WorkInfo.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = cb.equal( root.get(Report_I_WorkInfo_.reportId), reportId );
		p = cb.and( p,  cb.equal( root.get(Report_I_WorkInfo_.id), workId ) );
		List<Report_I_WorkInfo> workList = em.createQuery(cq.where(p)).getResultList();
		if(ListTools.isNotEmpty( workList )){
			return workList.get(0);
		}
		return null;
    }

	public List<Report_I_WorkInfo> listWithKeyWorkId(String keyWorkId, String reportId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_WorkInfo> cq = cb.createQuery(Report_I_WorkInfo.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = cb.equal( root.get(Report_I_WorkInfo_.reportId), reportId );
		p = cb.and( p, cb.equal( root.get(Report_I_WorkInfo_.keyWorkId), keyWorkId ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> getDetailIdsWithWorkInfoId( String reportId, String workInfoId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfoDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_WorkInfoDetail> root = cq.from(Report_I_WorkInfoDetail.class);
		Predicate p = cb.equal( root.get(Report_I_WorkInfoDetail_.reportId), reportId );
		p = cb.and( p, cb.equal( root.get(Report_I_WorkInfoDetail_.id), workInfoId ) );
		cq.select(root.get(Report_I_WorkInfoDetail_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listKeyWorkInfoIdsWithUnitAndMeasure(String unitName, String measureId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = cb.equal( root.get(Report_I_WorkInfo_.workUnit), unitName );
		p = cb.and( p, cb.isMember( measureId, root.get(Report_I_WorkInfo_.measuresList )));
		cq.distinct(true).select(root.get(Report_I_WorkInfo_.keyWorkId));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsWithProfileId(String profileId) throws Exception {
		if( StringUtil.isEmpty( profileId )) {
			throw new Exception("profileId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_WorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_WorkInfo> root = cq.from(Report_I_WorkInfo.class);
		Predicate p = cb.equal( root.get( Report_I_WorkInfo_.profileId), profileId );
		cq.select(root.get(Report_I_WorkInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}