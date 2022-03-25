package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.CriteriaQueryTools;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.AttendanceCycles;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.WrapInFilter;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetail_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
/**
 * 系统配置信息表基础功能服务类
 */
public class AttendanceDetailFactory extends AbstractFactory {
	
	public AttendanceDetailFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceDetail信息对象")
	public AttendanceDetail get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceDetail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的AttendanceDetail信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示全部的AttendanceDetail信息列表")
	public String getMaxRecordDate() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery( AttendanceDetail.class );
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);		
		cq.orderBy( cb.desc( root.get( AttendanceDetail_.recordDateString) ) );	
		List<AttendanceDetail> resultList = em.createQuery(cq).setMaxResults(1).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0).getRecordDateString();
		}
	}
	
	//@MethodDescribe("根据员工姓名和打卡日期列示AttendanceDetail信息列表")
	public List<String> listByEmployeeNameAndDate( String employeeName, String recordDateString ) throws Exception {
		
		if( employeeName == null || recordDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get(AttendanceDetail_.empName),  employeeName );
		p = cb.and( p, cb.equal( root.get(AttendanceDetail_.recordDateString ),  recordDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("根据员工姓名和打卡日期列示AttendanceDetail信息列表")
	public List<AttendanceDetail> listDetailByEmployeeNameAndDate( String employeeName, String recordDateString ) throws Exception {
		
		if( employeeName == null || recordDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.equal( root.get(AttendanceDetail_.empName),  employeeName );
		p = cb.and( p, cb.equal( root.get(AttendanceDetail_.recordDateString ),  recordDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceDetail信息列表")
	public List<AttendanceDetail> list(List<String> ids) throws Exception {
		List<AttendanceDetail> resultList = null;
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceDetail>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);
		Predicate p = root.get(AttendanceDetail_.id).in(ids);
		resultList = em.createQuery( cq.where(p) ).getResultList();
		if( resultList == null ){
			resultList = new ArrayList<AttendanceDetail>();
		}
		return resultList;
	}

	/**
	 * 分析时间范围内的所有打卡记录
	 * 1、如果未传入时间，或者时间有错，那么分析所有未分析过的打卡记录
	 * 2、只分析未归档的，已经归档的将不再分析了
	 * @param startDateString
	 * @param endDateString
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("按指定的开始时间，结束时间列示未被分析的AttendanceDetail信息列表")
	public List<String> getAllAnalysenessDetails(String startDateString, String endDateString, String personName ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//一般始终为true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //要未归档的，才再次进行分析
		if( StringUtils.isNotEmpty( personName ) ) {
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.empName ), personName)); //匹配员工姓名
		}
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//如果开始时间和结束时间有值，那么分析一个时间区间内的所有打卡记录，已经分析过了的，也需要重新分析一次
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString和endDateString都为空，只分析所有未分析过的
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //未分析的
				statusArray.add( -1 ); //有错误的
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		cq.select( root.get( AttendanceDetail_.id ) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	/**
	 * 分析时间范围内的所有打卡记录
	 * 1、如果未传入时间，或者时间有错，那么分析所有未分析过的打卡记录
	 * 2、只分析未归档的，已经归档的将不再分析了
	 * 3、如果forceFlag为true则分析（未分析的，已分析的，错误的）
	 * @param startDateString
	 * @param endDateString
	 * @param personName
	 * @param forceFlag
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("按指定的开始时间，结束时间列示未被分析的AttendanceDetail信息列表")
	public List<String> getAllAnalysenessDetailsForce(String startDateString, String endDateString, String personName ,Boolean forceFlag) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//一般始终为true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //要未归档的，才再次进行分析
		if( StringUtils.isNotEmpty( personName ) ) {
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.empName ), personName)); //匹配员工姓名
		}
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//如果开始时间和结束时间有值，那么分析一个时间区间内的所有打卡记录，已经分析过了的，也需要重新分析一次
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString和endDateString都为空，只分析所有未分析过的
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //未分析的
				if(forceFlag){
					statusArray.add( 1 ); //已分析的
				}
				statusArray.add( -1 ); //有错误的
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		cq.select( root.get( AttendanceDetail_.id ) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	/**
	 * 分析时间范围内的所有打卡记录
	 * 1、如果未传入时间，或者时间有错，那么分析所有未分析过的打卡记录
	 * 2、只分析未归档的，已经归档的将不再分析了
	 * @param startDateString
	 * @param endDateString
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("按指定的开始时间，结束时间列示未被分析的员工姓名列表")
	public List<String> getAllAnalysenessPersonNames(String startDateString, String endDateString ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//一般始终为true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //要未归档的，才再次进行分析
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//如果开始时间和结束时间有值，那么分析一个时间区间内的所有打卡记录，已经分析过了的，也需要重新分析一次
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString和endDateString都为空，只分析所有未分析过的
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //未分析的
				statusArray.add( -1 ); //有错误的
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		/*cq.distinct(true).select( root.get( AttendanceDetail_.empName ) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();*/
		cq.select( root.get( AttendanceDetail_.empName ) ).where(p);
		return em.createQuery(cq).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * 分析时间范围内的所有打卡记录(可选择已分析/未分析)
	 * 1、如果未传入时间，或者时间有错， 那么分析所有未分析过的打卡记录
	 * 2、只分析未归档的，已经归档的将不再分析了
	 * 3、如果forceFlag为true则分析（未分析的，已分析的，错误的）
	 * @param startDateString
	 * @param endDateString
	 * @param forceFlag
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("按指定的开始时间，结束时间列示员工姓名列表")
	public List<String> getAllAnalysenessPersonNamesForce(String startDateString, String endDateString ,Boolean forceFlag) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//一般始终为true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //要未归档的，才再次进行分析
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//如果开始时间和结束时间有值，那么分析一个时间区间内的所有打卡记录，已经分析过了的，也需要重新分析一次
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString和endDateString都为空，只分析所有未分析过的
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //未分析的
				if(forceFlag){
					statusArray.add( 1 ); //已分析的
				}
				statusArray.add( -1 ); //有错误的
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		cq.select( root.get( AttendanceDetail_.empName ) ).where(p);
		return em.createQuery(cq).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("按指定的开始时间，结束时间列示未被分析的AttendanceDetail信息列表")
	public List<String> getUserAnalysenessDetails(String empName, String startDateString, String endDateString) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));

		Predicate p = cb.equal( root.get(AttendanceDetail_.empName),  empName );
		
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//如果开始时间和结束时间有值，那么分析一个时间区间内的所有打卡记录，已经分析过了的，也需要重新分析一次
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString和endDateString都为空，只分析所有未分析过的
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //未分析的
				statusArray.add( -1 ); //有错误的
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("按指定的年份，月份列示AttendanceDetail信息列表")
	public List<AttendanceDetail> getDetailsByYearAndMonth(String year, String month) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息列表")
	public List<AttendanceDetail> getDetailsByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("按指定的年份，月份列示已分析的AttendanceDetail信息中涉及的组织列表")
	public List<String> getDetailsUnitsByYearAndMonth(String year, String month) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * 迟到、缺勤、早退、工时不足、异常打卡，但未申诉通过的
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("获取所有需要导出所有异常数据（未申诉的、申诉未通过的）")
	public List<String> getDetailsWithAllAbnormalCase( String year, String month ) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		
		Predicate p = cb.lessThan( root.get(AttendanceDetail_.appealStatus), 9); //如果等于9就是申诉通过
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), month ));
		
		Predicate orCase = cb.isTrue(root.get(AttendanceDetail_.isLate)); //迟到
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isLeaveEarlier)) ); //或者早退
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isAbnormalDuty) )); //或者异常打卡
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isAbsent) )); //或者缺勤
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isLackOfTime) )); //或者工时不足
		
		Predicate where = cb.and( p, orCase );
		
		return em.createQuery(cq.where(where)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息中涉及的组织列表")
	public List<String> getDetailsUnitsByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息中涉及的顶层组织名称列表")
	public List<String> distinctDetailsTopUnitNamesByYearAndMonth(String year, String month) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.topUnitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息中涉及的员工姓名列表")
	public List<String> distinctDetailsEmployeeNamesByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.empName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息中涉及的顶层组织名称列表")
	public List<String> distinctDetailsTopUnitNamesByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.topUnitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息中涉及的组织名称列表")
	public List<String> distinctDetailsUnitNamesByCycleYearAndMonth( String cycleYear, String cycleMonth ) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("按指定的统计周期年份，月份列示AttendanceDetail信息中涉及的组织名称列表")
	public List<String> distinctDetailsUnitNamesByCycleYearAndMonth( String cycleYear, String cycleMonth, String employeeName ) throws Exception {
		if( cycleYear == null || cycleMonth == null || employeeName == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), employeeName ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}


	//@MethodDescribe("按batchName查询一次导入的所有数据记录列表")
	public List<String> listByBatchName( String file_id ) throws Exception{
		if( file_id == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.batchName), file_id );
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	//@MethodDescribe("按年份月份查询某用户的打卡数据记录列表")
	public List<String> listUserAttendanceDetailByYearAndMonth(String user, String year, String month)  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( StringUtils.isNotEmpty( user ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("按周期年份月份查询某用户的打卡数据记录列表")
	public List<String> listUserAttendanceDetailByCycleYearAndMonth(String user, String year, String month)  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( StringUtils.isNotEmpty( user ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listDetailByCycleYearAndMonthWithOutStatus( String user, String year, String month )  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.isNotNull( root.get(AttendanceDetail_.id) );
		if( StringUtils.isNotEmpty( user ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), month ));
		}

		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//根据人员和打卡日期查找打卡记录
		public List<String> listDetailByNameAndDate( String user, String dateStr )  throws Exception {
			if( user == null || user.isEmpty() ||dateStr == null ){
				return null;
			}
			EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
			cq.select( root.get(AttendanceDetail_.id ));
			//一般始终为true, id is not null
			Predicate p = cb.isNotNull( root.get(AttendanceDetail_.id) );
			if( StringUtils.isNotEmpty( user ) ){
				p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
			}
			if( StringUtils.isNotEmpty( dateStr ) ){
				p = cb.and(p, cb.equal( root.get(AttendanceDetail_.recordDateString), dateStr ));
			}
			
	
			return em.createQuery(cq.where(p)).getResultList();
		}
	
	//@MethodDescribe("按年份月份查询某组织的打卡数据记录列表")
	public List<String> listUnitAttendanceDetailByYearAndMonth( List<String> unitNames, String year, String month)  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( unitNames != null && unitNames.size() > 0 ){
			p = cb.and(p, root.get(AttendanceDetail_.unitName).in(unitNames));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("按年份月份查询某顶层组织的打卡数据记录列表")
	public List<String> listTopUnitAttendanceDetailByYearAndMonth(List<String> topUnitNames, String year, String month)  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( ListTools.isNotEmpty(  topUnitNames ) ){
			p = cb.and(p, root.get(AttendanceDetail_.topUnitName).in( topUnitNames ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	
	/**
	 * 查询下一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceDetail> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

		String order = wrapIn.getOrder();//排序方式
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 查询下一页的信息数据(排除不参加考勤的人员)
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceDetail> listIdsNextWithFilterUn( String id, Integer count, Object sequence, WrapInFilter wrapIn ,List<String> unUnitNameList,List<String> personNameList) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

		String order = wrapIn.getOrder();//排序方式
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.unitName).in(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.empName).in(personNameList)));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 根据条件查询考勤信息ids(排除不参加考勤的人员)
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	//根据人员和打卡日期查找打卡记录
	public List<AttendanceDetail> listIdsWithFilterUn( WrapInFilter wrapIn ,List<String> unUnitNameList,List<String> personNameList )  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

		String order = wrapIn.getOrder();//排序方式
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.unitName).in(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.empName).in(personNameList)));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.getResultList();
	}
	
	/**
	 * 查询上一页的文档信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceDetail> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

		String order = wrapIn.getOrder();//排序方式
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}
	
	/**
	 * 查询符合的文档信息总数
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));

		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}

		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 查询符合的文档信息总数(排除不参加考勤的人员)
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilterUn( WrapInFilter wrapIn ,List<String> unUnitNameList,List<String> personNameList) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));

		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.unitName).in(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.empName).in(personNameList)));
		}

		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getByUserAndRecordDate(String employeeName, String recordDateStringFormated)  throws Exception{		
		if( employeeName == null || employeeName.isEmpty() || recordDateStringFormated == null || recordDateStringFormated.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.equal( root.get(AttendanceDetail_.empName), employeeName );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.recordDateString), recordDateStringFormated ));	
		cq.select( root.get(AttendanceDetail_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	
	public List<AttendanceCycles> getCyclesFromDetailWithDateSplit( Date startDate, Date endDate )  throws Exception{
		if( startDate == null || startDate == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceCycles> cq = cb.createQuery(AttendanceCycles.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.between( root.get(AttendanceDetail_.recordDate), startDate, endDate);
		
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
		selectionList.add(root.get(AttendanceDetail_.cycleYear ));
		selectionList.add(root.get(AttendanceDetail_.cycleMonth ));
		cq.multiselect(selectionList);
		
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	public List<AttendanceCycles> getCyclesFromDetailWithDateSplit( String empName, Date startDate, Date endDate )  throws Exception{
		if( startDate == null || startDate == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceCycles> cq = cb.createQuery(AttendanceCycles.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.between( root.get(AttendanceDetail_.recordDate), startDate, endDate);
		p = cb.and( p, cb.equal( root.get(AttendanceDetail_.empName), empName));
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
		selectionList.add(root.get(AttendanceDetail_.cycleYear ));
		selectionList.add(root.get(AttendanceDetail_.cycleMonth ));
		cq.multiselect(selectionList);
		
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("查询未归档的条卡记录列表，最大2000条")
	public List<String> listNonArchiveDetailInfoIds()  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.isNotNull( root.get(AttendanceDetail_.archiveTime) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}

	public List<String> listAnalysenessDetailsByEmployee( String empName ) throws Exception {
		List<Integer> statusArray = new ArrayList<Integer>();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get( AttendanceDetail_.empName ), empName );
		statusArray.add( 0 ); //未分析的
		statusArray.add( -1 ); //有错误的
		p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}

	public List<String> listRecordWithDateAndNoOffDuty( String recordDate ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate );
		Predicate offDutyTime_1 = cb.isNull(root.get( AttendanceDetail_.offDutyTime ));
		Predicate offDutyTime_2 = cb.equal( root.get( AttendanceDetail_.offDutyTime ), "" );
		p = cb.and( p, cb.or( offDutyTime_1, offDutyTime_2 ));
		return em.createQuery(cq.where(p)).setMaxResults(100000).getResultList();
	}

	/**
	 * 查询在指定截止日期前已经打过卡的人员
	 * @param deadline
	 * @param type:all#onDuty#offDuty#morningOffDuty#afternoonOnDuty
	 * @return
	 * @throws Exception
	 */
    public List<String> listSignedPersonsWithDeadLine( String deadline, String type ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		if( StringUtils.isEmpty( deadline )){
			deadline = dateOperation.getNowDateTime();
		}
		Date deadlineDate = dateOperation.getDateFromString( deadline, "yyyy-MM-dd HH:mm:ss");
		String recordDate = dateOperation.getDate( deadlineDate, "yyyy-MM-dd");
		String deadlineTime = dateOperation.getDate( deadlineDate, "HH:mm:ss");

		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate );
		if( StringUtils.equalsAnyIgnoreCase("onDuty", type)){
			Predicate p_type = cb.lessThan( root.get( AttendanceDetail_.onDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else if(StringUtils.equalsAnyIgnoreCase("offDuty", type)){
			Predicate p_type = cb.lessThan(root.get( AttendanceDetail_.offDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else if(StringUtils.equalsAnyIgnoreCase("morningOffDuty", type)){
			Predicate p_type = cb.lessThan(root.get( AttendanceDetail_.morningOffDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else if(StringUtils.equalsAnyIgnoreCase("afternoonOnDuty", type)){
			Predicate p_type = cb.lessThan(root.get( AttendanceDetail_.afternoonOnDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else{
			Predicate p_or = cb.or(
					cb.lessThan( root.get( AttendanceDetail_.onDutyTime ), deadlineTime),
					cb.lessThan(root.get( AttendanceDetail_.morningOffDutyTime ), deadlineTime),
					cb.lessThan(root.get( AttendanceDetail_.afternoonOnDutyTime ), deadlineTime),
					cb.lessThan(root.get( AttendanceDetail_.offDutyTime ), deadlineTime)
			);
			p = cb.and( p, p_or );
		}
		cq.select(root.get(AttendanceDetail_.empName));
		return em.createQuery(cq.where(p)).setMaxResults(100000).getResultList().stream().distinct().collect(Collectors.toList());
	}
}