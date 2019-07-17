package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.attendance.entity.AttendanceWorkDayConfig_;
import com.x.base.core.project.exception.ExceptionWhen;

/**
 * 系统配置信息表基础功能服务类
 * @author liyi
 */
public class AttendanceWorkDayConfigFactory extends AbstractFactory {

	private DateOperation dateOperation = new DateOperation();
	
	public AttendanceWorkDayConfigFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceWorkDayConfig信息对象")
	public AttendanceWorkDayConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceWorkDayConfig.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe("列示全部的AttendanceWorkDayConfig信息列表")
	@SuppressWarnings("unused")
	public List<AttendanceWorkDayConfig> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkDayConfig> cq = cb.createQuery(AttendanceWorkDayConfig.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceWorkDayConfig信息列表")
	public List<AttendanceWorkDayConfig> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceWorkDayConfig>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkDayConfig> cq = cb.createQuery(AttendanceWorkDayConfig.class);
		Root<AttendanceWorkDayConfig> root = cq.from(AttendanceWorkDayConfig.class);
		Predicate p = root.get(AttendanceWorkDayConfig_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("根据年份月份列示全部的AttendanceWorkDayConfig信息列表")
	public List<String> listByYearAndMonth( String year, String month ) throws Exception {
		if( year == null ){
			return null;
		}
		if( "0".equals(month) || "00".equals(month) || "(0)".equals(month)){
			month = null;
		}
		
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		cq.select(root.get(AttendanceWorkDayConfig_.id));
		
		Predicate p = cb.equal( root.get(AttendanceWorkDayConfig_.configYear), year);
		if( month != null ){
			p = cb.and( p, cb.equal( root.get(AttendanceWorkDayConfig_.configMonth), month));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("根据年份和节假日名称列示全部的AttendanceWorkDayConfig信息列表")
	public List<String> listByYearAndName( String year, String configName ) throws Exception {
		if( year == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		cq.select(root.get(AttendanceWorkDayConfig_.id));
		
		Predicate p = cb.equal( root.get(AttendanceWorkDayConfig_.configYear), year);
		if( configName != null ){
			p = cb.and( p, cb.equal( root.get(AttendanceWorkDayConfig_.configName), configName));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("根据节假日名称列示全部的AttendanceWorkDayConfig信息列表")
	public List<String> listByName( String configName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		cq.select(root.get(AttendanceWorkDayConfig_.id));
		
		Predicate p = cb.equal( root.get(AttendanceWorkDayConfig_.configName), configName);
		
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("根据节假日配置计算一个月的应出勤天数")
	public int getWorkDaysCountForMonth( String s_year, String _month, List<AttendanceWorkDayConfig> workDayConfigList ) throws Exception {
		/**
		 * 1、计算当月的总天数
		 * 2、遍历每天的日期
		 *    1）判断是否周末
		 *    2）判断是否调工作日
		 *    3）判断是否节日
		 */
		int workDaysCountForMonth = 0;
		//1、计算当月的总天数
		int daysCountForMonth = dateOperation.getDaysForMonth( s_year + "-" + _month + "-01" );
		workDaysCountForMonth = daysCountForMonth; //先假设每天都是工作日
		//2、遍历每天的日期
		String dateString = null;
		boolean isHoliday = true;
		for( int i = 1; i<= daysCountForMonth ; i++ ){
			isHoliday = true;
			dateString = s_year + "-" + _month + "-" + (i<10?"0"+i:i);
			//判断当天是否周末
			if( !dateOperation.isWeekend( dateOperation.getDateFromString( dateString )) ){
				//如果不是周末
				if( workDayConfigList != null && workDayConfigList.size() > 0 ){
					//遍历所有的节假日配置进行判断，是否法定节假日
					for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
						if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
						){
							workDaysCountForMonth--;//当天不是工作日，当月出勤日减一天。
							break;
						}
					}
				}
			}else{
				//如果是周末
				if( workDayConfigList != null && workDayConfigList.size() > 0 ){
					//遍历所有的节假日配置进行判断，是否调休工作日
					for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
						if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
						){
							isHoliday = false; //是配置的调休工作日
							break;
						}
					}
				}
				if( isHoliday ){
					//如果不是配置的调休工作日，则当天不是工作日，当月出勤日减一天。
					workDaysCountForMonth--;
				}
			}
		}
		return workDaysCountForMonth;
	}
	
	//@MethodDescribe("根据节假日配置计算一个周期内的应出勤天数")
	public Integer getWorkDaysCountForMonth( Date startDate, Date endDate, List<AttendanceWorkDayConfig> workDayConfigList ) throws Exception {
		/**
		 * 1、计算当月的总天数
		 * 2、遍历每天的日期
		 *    1）判断是否周末
		 *    2）判断是否调工作日
		 *    3）判断是否节日
		 */
		boolean isHoliday = true;
		int workDaysCountForMonth = 0;
		if( endDate.getTime() > new Date().getTime()){
			endDate = new Date();
		}
		List<String> dateStringList = dateOperation.listDateStringBetweenDate(startDate, endDate);
		if( dateStringList != null && dateStringList.size() > 0 ){
			workDaysCountForMonth = dateStringList.size();
			for( String dateString : dateStringList){
				//判断当天是否周末
				if( !dateOperation.isWeekend( dateOperation.getDateFromString( dateString )) ){
					//如果不是周末
					if( workDayConfigList != null && workDayConfigList.size() > 0 ){
						//遍历所有的节假日配置进行判断，是否法定节假日
						for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
							if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
							){
								workDaysCountForMonth--;//当天不是工作日，当月出勤日减一天。
								break;
							}
						}
					}
				}else{
					//如果是周末
					if( workDayConfigList != null && workDayConfigList.size() > 0 ){
						//遍历所有的节假日配置进行判断，是否调休工作日
						for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
							if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
							){
								isHoliday = false; //是配置的调休工作日
								break;
							}
						}
					}
					if( isHoliday ){
						//如果不是配置的调休工作日，则当天不是工作日，当月出勤日减一天。
						workDaysCountForMonth--;
					}
				}
			}
		}
		return workDaysCountForMonth;
	}
}