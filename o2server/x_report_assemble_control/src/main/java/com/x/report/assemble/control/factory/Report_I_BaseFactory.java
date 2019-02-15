package com.x.report.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Base_;
import com.x.report.core.entity.Report_I_Detail;

/**
 * 系统汇报信息服务类
 * @author O2LEE
 */
public class Report_I_BaseFactory extends AbstractFactory {
	
	public Report_I_BaseFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_I_Base信息对象")
	public Report_I_Base get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_Base.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("获取指定Id的Report_I_Detail信息对象")
	public Report_I_Detail getDetail( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_I_Detail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的Report_I_Base信息列表")
	@SuppressWarnings("unused")
	public List<Report_I_Base> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_Base.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Base> cq = cb.createQuery( Report_I_Base.class );
		Root<Report_I_Base> root = cq.from( Report_I_Base.class );
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_I_Base信息列表")
	public List<Report_I_Base> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Base> cq = cb.createQuery(Report_I_Base.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = root.get(Report_I_Base_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<Report_I_Base> list(String reportType, String reportObjType, String targetUnit,
			String year, String month, String week, String reportDateString, String createDateString, Boolean listHiddenReport ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Base> cq = cb.createQuery(Report_I_Base.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = cb.isNotNull( root.get(Report_I_Base_.id ) );
		if( reportType != null && !reportType.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.reportType), reportType) );
		}
		if( reportObjType != null && !reportObjType.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.reportObjType), reportObjType) );
		}
		if( targetUnit != null && !targetUnit.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.targetUnit), targetUnit) );
		}
		if( year != null && !year.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.year), year) );
		}
		if( month != null && !month.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.month), month) );
		}
		if( week != null && !week.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.week), week) );
		}
		if( reportDateString != null && !reportDateString.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.reportDateString ), reportDateString) );
		}
		if( createDateString != null && !createDateString.isEmpty() ) {
			p = cb.and( p, cb.equal( root.get(Report_I_Base_.createDateString ), createDateString) );
		}
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		return em.createQuery(cq.where(p)).setMaxResults( 5000 ).getResultList();
	}

	public List<String> lisViewableIdsWithFilter(String title, String reportType, String reportObjType, String year, String month,
			String week, List<String> activityList, List<String> unitList, String wfProcessStatus, int maxResultCount, Boolean listHiddenReport ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( Report_I_Base.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<Report_I_Base> root = cq.from( Report_I_Base.class );

		Predicate p = cb.isNotNull( root.get( Report_I_Base_.id ) );
		
		if( reportType != null && !reportType.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_I_Base_.reportType ), reportType));
		}
		if( reportObjType != null && !reportObjType.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_I_Base_.reportObjType ), reportObjType));
		}
		if( year != null  && !year.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_I_Base_.year ), year));
		}
		if( month != null  && !month.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_I_Base_.month ), month));
		}
		if( week != null  && !week.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_I_Base_.week ), week));
		}
		if( wfProcessStatus != null  && !wfProcessStatus.isEmpty() ){
			p = cb.and( p, cb.equal(root.get( Report_I_Base_.wfProcessStatus ), wfProcessStatus));
		}
		if( activityList != null && !activityList.isEmpty() ){
			p = cb.and( p, root.get( Report_I_Base_.activityName ).in( activityList ));
		}
		if( unitList != null && !unitList.isEmpty() ){
			p = cb.and( p, root.get( Report_I_Base_.targetUnit ).in( unitList ));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( Report_I_Base_.title ), "%" + title + "%" ));
		}		
		if( maxResultCount == 0 ){
			maxResultCount = 500;
		}
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		cq.distinct(true).select( root.get( Report_I_Base_.id ));
		return em.createQuery(cq.where( p )).setMaxResults( maxResultCount ).getResultList();
	}

	public Long countWithIds(List<String> viewAbleReportIds) throws Exception {
		if( viewAbleReportIds == null || viewAbleReportIds.isEmpty() ){
			return 0L;
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Base.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<Report_I_Base> root = cq.from( Report_I_Base.class );
		
		Predicate p = root.get( Report_I_Base_.id ).in( viewAbleReportIds );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<Report_I_Base> listNextWithDocIds( Integer count, List<String> viewAbleReportIds, Object sequenceFieldValue, 
			String orderField, String orderType, Boolean listHiddenReport ) throws Exception {
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		EntityManager em = this.entityManagerContainer().get( Report_I_Base.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_I_Base> cq = cb.createQuery( Report_I_Base.class );
		Root<Report_I_Base> root = cq.from( Report_I_Base.class );

		Predicate p = root.get( Report_I_Base_.id ).in( viewAbleReportIds );
		if( sequenceFieldValue != null ){
			if( "title".equals( orderField  )){//标题
				p = cb.and( p, cb.isNotNull( root.get( Report_I_Base_.title ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( Report_I_Base_.title ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Report_I_Base_.title ), sequenceFieldValue.toString() ));
				}
			}else if( "createTime".equals( orderField  )){//创建时间
				p = cb.and( p, cb.isNotNull( root.get( Report_I_Base_.createTime ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( Report_I_Base_.createTime ), (Date)sequenceFieldValue ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Report_I_Base_.createTime ), (Date)sequenceFieldValue ));
				}
			}else if( "reportDate".equals( orderField  )){//创建时间
				p = cb.and( p, cb.isNotNull( root.get( Report_I_Base_.reportDate ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( Report_I_Base_.reportDate ), (Date)sequenceFieldValue ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Report_I_Base_.reportDate ), (Date)sequenceFieldValue ));
				}
			}else if( "targetUnit".equals( orderField  )){//创建组织
				p = cb.and( p, cb.isNotNull( root.get( Report_I_Base_.targetUnit_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( Report_I_Base_.targetUnit_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Report_I_Base_.targetUnit_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "targetPerson".equals( orderField  )){//targetPerson
				p = cb.and( p, cb.isNotNull( root.get( Report_I_Base_.targetPerson_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( Report_I_Base_.targetPerson_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Report_I_Base_.targetPerson_sequence ), sequenceFieldValue.toString() ));
				}
			}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
				p = cb.and( p, cb.isNotNull( root.get( Report_I_Base_.sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( Report_I_Base_.sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( Report_I_Base_.sequence ), sequenceFieldValue.toString() ));
				}
			}
		}
		
		if( "title".equals( orderField  )){//标题
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( Report_I_Base_.title ) ));
			}else{
				cq.orderBy( cb.asc( root.get( Report_I_Base_.title ) ) );
			}
		}else if( "createTime".equals( orderField  )){//创建时间
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( Report_I_Base_.createTime ) ));
			}else{
				cq.orderBy( cb.asc( root.get( Report_I_Base_.createTime ) ));
			}
		}else if( "reportDate".equals( orderField  )){//创建时间
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( Report_I_Base_.reportDate ) ));
			}else{
				cq.orderBy( cb.asc( root.get( Report_I_Base_.reportDate ) ));
			}
		}else if( "targetUnit".equals( orderField  )){//创建组织
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( Report_I_Base_.targetUnit_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( Report_I_Base_.targetUnit_sequence ) ));
			}
		}else if( "targetPerson".equals( orderField  )){//targetPerson
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( Report_I_Base_.targetPerson_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( Report_I_Base_.targetPerson_sequence ) ));
			}
		}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( Report_I_Base_.sequence ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( Report_I_Base_.sequence ) ) );
			}
		}
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe("列示指定概要文件中需要启动流程的所有汇报文档ID列表")
	public List<String> listIdsForStartWfInProfile( String profileId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = cb.equal(root.get( Report_I_Base_.profileId ), profileId );
		p = cb.and( p, cb.equal(root.get( Report_I_Base_.activityName ), "待启动" ) );
		cq.select( root.get( Report_I_Base_.id ));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listAllProcessingReportIds( Boolean listHiddenReport ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = cb.notEqual( root.get( Report_I_Base_.wfProcessStatus ), "已完成" );
		p = cb.or( p, cb.notEqual(root.get( Report_I_Base_.reportStatus ), "已完成" ) );
		cq.select( root.get( Report_I_Base_.id ));
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listIdsWithYear(String year, Boolean listHiddenReport ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = cb.equal( root.get( Report_I_Base_.year ),year );
		cq.select( root.get( Report_I_Base_.id ));
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listIdsWithProfileId(String profileId) throws Exception {
		if( StringUtil.isEmpty( profileId )) {
			throw new Exception("profileId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = cb.equal( root.get( Report_I_Base_.profileId ),profileId );
		cq.select( root.get( Report_I_Base_.id ));
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	public List<String> listIdsWithProfileIdAndUnitName(String profileId, String unitName ) throws Exception {
		if( StringUtil.isEmpty( profileId )) {
			throw new Exception("profileId is null!");
		}
		if( StringUtil.isEmpty( unitName )) {
			throw new Exception("unitName is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = cb.equal( root.get( Report_I_Base_.profileId ),profileId );
		p = cb.and( p, cb.equal( root.get( Report_I_Base_.targetUnit ),unitName ) );
		cq.select( root.get( Report_I_Base_.id ));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listWithConditions(String year, String month, List<String> unitList,
			List<String> wfProcessStatus, List<String> wfActivityNames, Boolean listHiddenReport ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = root.get( Report_I_Base_.id ).isNotNull();
		if( StringUtils.isNotEmpty( year ) ) {
			p = cb.and( p, cb.equal( root.get( Report_I_Base_.year ), year));
		}
		if( StringUtils.isNotEmpty( month ) ) {
			p = cb.and( p, cb.equal( root.get( Report_I_Base_.month ), month));
		}
		if( ListTools.isNotEmpty( unitList ) ) {
			p = cb.and( p, root.get( Report_I_Base_.targetUnit ).in( unitList ));
		}
		if( ListTools.isNotEmpty( wfProcessStatus ) ) {
			p = cb.and( p, root.get( Report_I_Base_.wfProcessStatus ).in( wfProcessStatus ));
		}
		if( ListTools.isNotEmpty( wfActivityNames ) ) {
			p = cb.and( p, root.get( Report_I_Base_.activityName ).in( wfActivityNames ));
		}
		cq.select( root.get( Report_I_Base_.id ));
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listUnitNamesWithConditions(String year, String month, List<String> wfProcessStatus, 
			List<String> wfActivityNames, Boolean listHiddenReport ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_I_Base.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_I_Base> root = cq.from(Report_I_Base.class);
		Predicate p = root.get( Report_I_Base_.id ).isNotNull();
		if( StringUtils.isNotEmpty( year ) ) {
			p = cb.and( p, cb.equal( root.get( Report_I_Base_.year ), year));
		}
		if( StringUtils.isNotEmpty( month ) ) {
			p = cb.and( p, cb.equal( root.get( Report_I_Base_.month ), month));
		}
		if( ListTools.isNotEmpty( wfProcessStatus ) ) {
			p = cb.and( p, root.get( Report_I_Base_.wfProcessStatus ).in( wfProcessStatus ));
		}
		if( ListTools.isNotEmpty( wfActivityNames ) ) {
			p = cb.and( p, root.get( Report_I_Base_.activityName ).in( wfActivityNames ));
		}
		cq.select( root.get( Report_I_Base_.targetUnit ));
		if( listHiddenReport ) {
			p = cb.and( p, cb.notEqual(root.get( Report_I_Base_.wfProcessStatus ), "已隐藏" ));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
}