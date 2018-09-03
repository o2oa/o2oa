package com.x.report.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_P_Permission;
import com.x.report.core.entity.Report_P_Permission_;

/**
 * 系统汇报访问权限信息记录服务类
 * @author O2LEE
 */
public class Report_P_PermissionFactory extends AbstractFactory {
	
	public Report_P_PermissionFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("查询未被更新过的汇报文档权限信息ID列表")
	public List<String> listWithReport(String reportId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Permission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Permission> root = cq.from( Report_P_Permission.class );
		Predicate p = cb.equal( root.get( Report_P_Permission_.reportId ), reportId );
		cq.select( root.get( Report_P_Permission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("根据指定的信息查询汇报文档权限记录ID列表")
	public List<String> listIds( String reportId, String permission, String permissionObjectType, String permissionObjectCode ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Permission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Permission> root = cq.from( Report_P_Permission.class );
		Predicate p = cb.equal( root.get( Report_P_Permission_.reportId ), reportId );
		p = cb.and( p, cb.equal( root.get( Report_P_Permission_.permission ), permission ) );
		p = cb.and( p, cb.equal( root.get( Report_P_Permission_.permissionObjectType ), permissionObjectType ) );
		p = cb.and( p, cb.equal( root.get( Report_P_Permission_.permissionObjectCode ), permissionObjectCode ) );
		cq.select( root.get( Report_P_Permission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> lisViewableReportIdsWithFilter( String title, String reportType, String year, String month,
			String week, String reportDateString, String createDateString, List<String> activityList,
			List<String> unitList, String reportObjType,
			String wfProcessStatus, List<String> permissionObjectCodes, String permission, int maxResultCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Permission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Permission> root = cq.from( Report_P_Permission.class );
		
		//先圈定用户能访问的文档
		Predicate permissionWhere = root.get( Report_P_Permission_.permissionObjectCode ).in( permissionObjectCodes );
		
		Predicate p = cb.isNotNull( root.get( Report_P_Permission_.id ) );
		if( permission != null  && !permission.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.permission ), permission ));
		}	
		if( reportType != null  && !reportType.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.reportType ), reportType));
		}
		if( year != null  && !year.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.year ), year));
		}
		if( month != null  && !month.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.month ), month));
		}
		if( week != null  && !week.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.week ), week));
		}
		if( wfProcessStatus != null  && !wfProcessStatus.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.wfProcessStatus ), wfProcessStatus));
		}
		if( activityList != null && !activityList.isEmpty() ){
			p = cb.and( p, root.get( Report_P_Permission_.activityName ).in( activityList ));
		}
		if( unitList != null && !unitList.isEmpty() ){
			p = cb.and( p, root.get( Report_P_Permission_.targetUnit ).in( unitList ));
		}
		if( reportObjType != null  && !reportObjType.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.reportObjType ), reportObjType));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( Report_P_Permission_.title ), "%" + title + "%" ));
		}
		if( reportDateString != null  && !reportDateString.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.reportDateString ), reportDateString ));
		}
		if( createDateString != null  && !createDateString.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.createDateString ), createDateString ));
		}
		if( maxResultCount == 0 ){
			maxResultCount = 500;
		}
		cq.distinct(true).select( root.get( Report_P_Permission_.reportId ));
		
		return em.createQuery(cq.where( permissionWhere, p )).setMaxResults( maxResultCount ).getResultList();
	}

	public List<String> listIdsWithReportId(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Permission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Permission> root = cq.from( Report_P_Permission.class );
		Predicate p = cb.equal( root.get( Report_P_Permission_.reportId ), id );
		cq.select( root.get( Report_P_Permission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listAllCreateDateString( List<String> permissionCodes, String year, String month ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_P_Permission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Permission> root = cq.from( Report_P_Permission.class );
		Predicate p = root.get( Report_P_Permission_.permissionObjectCode ).in( permissionCodes );
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.year ), year ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and( p, cb.equal(root.get( Report_P_Permission_.month ), month ));
		}
		cq.distinct(true).select( root.get( Report_P_Permission_.createDateString ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsWithProfileId(String profileId) throws Exception {
		if( StringUtil.isEmpty( profileId )) {
			throw new Exception("profileId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( Report_P_Permission.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_P_Permission> root = cq.from( Report_P_Permission.class );
		Predicate p = cb.equal( root.get( Report_P_Permission_.profileId ), profileId );
		cq.select( root.get( Report_P_Permission_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}