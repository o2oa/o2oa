package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.AttendanceCycles;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseService {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseService.class );
	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();
	private DateOperation dateOperation = new DateOperation();
	
	/**
	 * 根据员工姓名，开始日期和结束日期获取日期范围内所有的打卡记录信息ID列表，从开始日期0点，到结束日期23点59分
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> getAnalyseAttendanceDetailIds( EntityManagerContainer emc, String empName, Date startDate, Date endDate )throws Exception{		
		Business business = null;
		List<String> ids = null;
		String startDateString = null, endDateString = null;
		business = new Business(emc);
		startDateString = dateOperation.getDateStringFromDate( startDate, "yyyy-MM-dd" ) + " 00:00:00";
		endDateString = dateOperation.getDateStringFromDate( endDate, "yyyy-MM-dd" ) + "23:59:59";
		ids = business.getAttendanceDetailFactory().getUserAnalysenessDetails( empName, startDateString, endDateString);
		return ids;
	}
	
	/**
     * 根据员工姓名，开始日期和结束日期重新分析所有的打卡记录
     * 一般在用户补了休假记录的时候使用，补充了休假记录，需要将休假日期范围内的所有打卡记录重新分析，并且触发相关月份的统计
     * @param empName
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
	public Boolean analyseAttendanceDetails( EntityManagerContainer emc, String empName, Date startDate, Date endDate, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		Business business = null;
		List<String> ids = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		List<AttendanceDetail> attendanceDetailList = null;
		
		business = new Business(emc);
		ids = getAnalyseAttendanceDetailIds( emc, empName, startDate, endDate );
		if( ids == null || ids.isEmpty() ){
			logger.debug("attendance detail info ids not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}
		attendanceDetailList = business.getAttendanceDetailFactory().list( ids );
		if( attendanceDetailList == null || attendanceDetailList.isEmpty() ){
			logger.debug("attendance detail info not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}
		attendanceWorkDayConfigList = business.getAttendanceWorkDayConfigFactory().listAll();
		for( AttendanceDetail detail : attendanceDetailList ){
			try{
				analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap );	
			}catch(Exception e){
				logger.info( "employee attenance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]");
				logger.error(e);
			}
		}
		logger.info( "employee attendance details by empname, startdate and end date analyse over！" );;
		return true;
	}
	
	/**
	 * 对一组的打卡信息数据进行分析
	 * @param detailList
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( EntityManagerContainer emc, List<AttendanceDetail> detailList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		if( detailList == null || detailList.isEmpty() ){
			return true;
		}
		Business business = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		business = new Business(emc);
		attendanceWorkDayConfigList = business.getAttendanceWorkDayConfigFactory().listAll();
		for( AttendanceDetail detail : detailList ){
			try{
				analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap );	
			}catch(Exception e){
				logger.info( "employee attendance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]" );
				logger.error(e);
			}
		}
		logger.info( "employee attendance detail analyse over！" );
		return true;
	}
	
	/**
	 * 对单条的打卡数据分析统计周期
	 * 包装成List使用另一个方法进行操作：analyseAttendanceDetails
	 * 
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	public void analyseAttendanceDetailStatisticCycle( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		List<AttendanceStatisticalCycle> departmentCycles = null;
		Map<String, List<AttendanceStatisticalCycle>> departmentAttendanceStatisticalCycleMap = null;
		boolean hasConfig = false;
		String companyName = null, departmentName = null;
		//从Map里查询与公司和部门相应的周期配置信息
		if( companyAttendanceStatisticalCycleMap != null ){
			//如果总体的Map不为空
			departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( detail.getCompanyName() );
			if( departmentAttendanceStatisticalCycleMap != null ){
				companyName = detail.getCompanyName();
				//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
				departmentCycles = departmentAttendanceStatisticalCycleMap.get(detail.getDepartmentName());
				if( departmentCycles != null){
					departmentName = detail.getDepartmentName();
				}else{
					departmentName = "*";
					departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
				}
			}else{
				//找公司为*的Map看看是否存在
				departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( "*" );
				companyName = "*";
				if( departmentAttendanceStatisticalCycleMap != null ){
					//存在当前公司的配置信息，再根据部门查询部门的配置列表是否存在[company - department]
					departmentCycles = departmentAttendanceStatisticalCycleMap.get(detail.getDepartmentName());
					if( departmentCycles != null){
						departmentName = detail.getDepartmentName();
					}else{
						departmentName = "*";
						departmentCycles = departmentAttendanceStatisticalCycleMap.get("*");
					}
				}else{
					departmentName = "*";
				}
			}
		}else{
			companyName = "*";
			departmentName = "*";
		}
		
		Date cycleStartDate = null, cycleEndDate = null;
		if( departmentCycles != null && departmentCycles.size() > 0 ){
			//说明配置信息里有配置，看看配置信息里的数据是否满足打卡信息需要的周期
			for( AttendanceStatisticalCycle attendanceStatisticalCycle : departmentCycles ){
				//如果年份为*
				cycleStartDate = attendanceStatisticalCycle.getCycleStartDate();
				cycleEndDate = attendanceStatisticalCycle.getCycleEndDate();
				if( detail.getRecordDate().getTime() >= cycleStartDate.getTime() && detail.getRecordDate().getTime() <= cycleEndDate.getTime() ){
					hasConfig = true;
					detail.setCycleYear(attendanceStatisticalCycle.getCycleYear());
					detail.setCycleMonth(attendanceStatisticalCycle.getCycleMonth());
					break;
				}
			}
		}
		
		if( !hasConfig ){
			//说明没有找到任何相关的配置，那么新创建一条配置
			detail.setCycleYear(detail.getYearString());
			detail.setCycleMonth(detail.getMonthString());
			DateOperation dateOperation = new DateOperation();
			String cycleMonth =  dateOperation.getMonth( detail.getRecordDate() );
			if( Integer.parseInt( cycleMonth ) < 10 ){
				cycleMonth = "0" + Integer.parseInt( cycleMonth );
			}			
			AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
			attendanceStatisticalCycle.setCompanyName(companyName);
			attendanceStatisticalCycle.setDepartmentName(departmentName);
			attendanceStatisticalCycle.setCycleMonth( dateOperation.getMonth( detail.getRecordDate() ));
			attendanceStatisticalCycle.setCycleYear( dateOperation.getYear(detail.getRecordDate()) );
			attendanceStatisticalCycle.setCycleStartDate( dateOperation.getFirstDateInMonth(detail.getRecordDate()));
			attendanceStatisticalCycle.setCycleStartDateString( dateOperation.getFirstDateStringInMonth(detail.getRecordDate()));
			attendanceStatisticalCycle.setCycleEndDate(dateOperation.getLastDateInMonth(detail.getRecordDate()));
			attendanceStatisticalCycle.setCycleEndDateString(dateOperation.getLastDateStringInMonth(detail.getRecordDate()));
			attendanceStatisticalCycle.setDescription("系统自动创建");
			
			//先把对象放到Map里
			if( companyAttendanceStatisticalCycleMap == null ){
				companyAttendanceStatisticalCycleMap = new HashMap<String, Map<String, List<AttendanceStatisticalCycle>>>();
			}
			departmentAttendanceStatisticalCycleMap = companyAttendanceStatisticalCycleMap.get( companyName );
			if( departmentAttendanceStatisticalCycleMap == null ){
				departmentAttendanceStatisticalCycleMap = new HashMap<String, List<AttendanceStatisticalCycle>>();
				companyAttendanceStatisticalCycleMap.put( companyName, departmentAttendanceStatisticalCycleMap);
			}
			departmentCycles = departmentAttendanceStatisticalCycleMap.get(departmentName);
			if( departmentCycles == null ){
				departmentCycles = new ArrayList<AttendanceStatisticalCycle>();
				departmentAttendanceStatisticalCycleMap.put( departmentName, departmentCycles);
			}
			attendanceStatisticalCycleService.putDistinctCycleInList( attendanceStatisticalCycle, departmentCycles);

			//判断需要保存的数据是否已经存在
			if( attendanceStatisticalCycleService.getAttendanceDetailStatisticCycle(
					attendanceStatisticalCycle.getCompanyName(), 
					attendanceStatisticalCycle.getDepartmentName(), 
					attendanceStatisticalCycle.getCycleYear(), 
					attendanceStatisticalCycle.getCycleMonth(), 
					companyAttendanceStatisticalCycleMap
			) != null ){
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					emc.beginTransaction(AttendanceStatisticalCycle.class);
					emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
					emc.commit();
				}catch(Exception e){
					logger.info("系统在保存新的统计周期信息时发生异常！");
					throw e;
				}
			}else{
				logger.info("需要保存的统计周期数据已经存在。");
			}
		}
	}
	
	/**
	 * 对单条的打卡数据进行分析
	 * 包装成List使用另一个方法进行操作：analyseAttendanceDetails
	 * 
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		if( detail != null ){
			List<AttendanceDetail> detailList = new ArrayList<AttendanceDetail>();
			detailList.add( detail );
			return analyseAttendanceDetails( emc, detailList, companyAttendanceStatisticalCycleMap );
		}else{
			logger.info( "detail为空，无法继续进行分析。" );
		}
		return false;
	}
	
	/**
	 * 根据部门列表，查询适用的排班信息
	 * @param departmentList
	 * @return
	 */
	public AttendanceScheduleSetting getAttendanceScheduleSettingByDepartments( List<WrapDepartment> departmentList ){
	//	logger.debug("[getAttendanceScheduleSettingByDepartments]根据部门列表，查询适用的排班配置记录......");
		List<String> departmentNames = new ArrayList<String>();
		if( departmentList == null || departmentList.size() == 0 ){
			logger.warn( "[getAttendanceScheduleSettingByDepartments]部门列表为空，无法进行查询。" );
			return null;
		}else{
			for( WrapDepartment department : departmentList ){
				departmentNames.add( department.getName());
			}
		}
		//1、根据部门列表查询排班信息，如果没有，则使用上级部门查询
		Business business = null;
		List<String> ids = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//查询员工所在部门的排班信息
			ids = business.getAttendanceScheduleSettingFactory().listByDepartmentNames( departmentNames );
			//如果未查询到，则查询上级部门是否有配置
			if( ids !=null && ids.size() > 0){
				attendanceScheduleSettingList = business.getAttendanceScheduleSettingFactory().list(ids);
				if( attendanceScheduleSettingList != null && attendanceScheduleSettingList.size() > 0 ){
					return attendanceScheduleSettingList.get(0);
				}
			}else{
				//循环所有的部门列表，查询每个部门设置的排班信息，如果当前部门没有设置， 则查询上级部门直到公司的排班信息
				for( WrapDepartment wrapDepartment : departmentList ){
					//没查到，查询该部门的上级部门名称所对应的排班设置是否存在
					attendanceScheduleSetting = getAttendanceScheduleSettingByDepartment( wrapDepartment );
					if( attendanceScheduleSetting != null ){
						//先为当前部门添加一个排班信息设计，再进行返回
						AttendanceScheduleSetting new_attendanceScheduleSetting = new AttendanceScheduleSetting();
						new_attendanceScheduleSetting.setAbsenceStartTime( attendanceScheduleSetting.getAbsenceStartTime());
						new_attendanceScheduleSetting.setLateStartTime(attendanceScheduleSetting.getLateStartTime());
						new_attendanceScheduleSetting.setLeaveEarlyStartTime(attendanceScheduleSetting.getLeaveEarlyStartTime());
						new_attendanceScheduleSetting.setOffDutyTime(attendanceScheduleSetting.getOffDutyTime());
						new_attendanceScheduleSetting.setOnDutyTime(attendanceScheduleSetting.getOnDutyTime());
						new_attendanceScheduleSetting.setOrganizationOu(attendanceScheduleSetting.getOrganizationOu());
						new_attendanceScheduleSetting.setCompanyName( wrapDepartment.getCompany() );
						new_attendanceScheduleSetting.setOrganizationName( wrapDepartment.getName() );
						emc.beginTransaction( AttendanceScheduleSetting.class );
						emc.persist( new_attendanceScheduleSetting, CheckPersistType.all );
						emc.commit();
						return new_attendanceScheduleSetting;
					}else{
						//先为当前部门添加一个排班信息设计，再进行返回
						AttendanceScheduleSetting new_attendanceScheduleSetting = new AttendanceScheduleSetting();
						new_attendanceScheduleSetting.setAbsenceStartTime( null );
						new_attendanceScheduleSetting.setLateStartTime("9:05");
						new_attendanceScheduleSetting.setLeaveEarlyStartTime(null);
						new_attendanceScheduleSetting.setOffDutyTime("17:00");
						new_attendanceScheduleSetting.setOnDutyTime("09:00");
						new_attendanceScheduleSetting.setOrganizationOu("");
						new_attendanceScheduleSetting.setCompanyName( wrapDepartment.getCompany() );
						new_attendanceScheduleSetting.setOrganizationName( wrapDepartment.getName() );
						emc.beginTransaction( AttendanceScheduleSetting.class );
						emc.persist( new_attendanceScheduleSetting, CheckPersistType.all );
						emc.commit();
						return new_attendanceScheduleSetting;
					}
				}
			}
		} catch (Exception e) {
			logger.warn("[analyseAttendanceDetails]系统在查询员工休假记录和部门排班信息时发生异常" );
			logger.error(e);
		}
		return attendanceScheduleSetting;
	}
	
	/**
	 * 递归根据部署信息查询上级部门的排班设置，如果是一级部门，则直接查询所属公司的排班信息
	 * @param wrapDepartment
	 * @return
	 */
	public AttendanceScheduleSetting getAttendanceScheduleSettingByDepartment( WrapDepartment wrapDepartment ){		
		WrapDepartment superDepartment = null;
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			superDepartment = business.organization().department().getSupDirect( wrapDepartment.getName() );
		} catch (Exception e) {
			logger.warn("[getAttendanceScheduleSettingByDepartment]递归查询部门的排班设置发生异常" );
			logger.error(e);
		}		
		
		if( superDepartment != null ){//说明还有上级部门
			if( wrapDepartment.getSuperior() != null ){
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					//查询排班设计
					ids = new Business(emc).getAttendanceScheduleSettingFactory().listByDepartmentName( superDepartment.getName() );
					if( ids !=null && ids.size() > 0){
						attendanceScheduleSettingList = new Business(emc).getAttendanceScheduleSettingFactory().list(ids);
						if( attendanceScheduleSettingList != null && attendanceScheduleSettingList.size() > 0){
							return attendanceScheduleSettingList.get(0);
						}
					}else{
						WrapDepartment _wrapDepartment = new WrapDepartment();
						superDepartment.copyTo( _wrapDepartment );
						return getAttendanceScheduleSettingByDepartment( _wrapDepartment );
					}		
				} catch (Exception e) {
					logger.warn("[getAttendanceScheduleSettingByDepartment]递归查询部门的排班设置发生异常");
					logger.error(e);
				}
			}
		}else{//说明是一级部门，直接使用公司的排班配置
			if( wrapDepartment.getCompany() != null ){
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					WrapCompany _wrapCompany  = new WrapCompany();
					_wrapCompany = business.organization().company().getWithDepartment( wrapDepartment.getName() );
					return getAttendanceScheduleSettingByCompany( _wrapCompany );
				} catch (Exception e) {
					logger.warn("[getAttendanceScheduleSettingByDepartment]递归查询公司的排班设置发生异常");
					logger.error(e);
				}
			}
		}
		return null;
	}
	
	/**
	 * 递归查询公司的排班信息，如果当前公司无排班信息设置，那么查询上级公司的排班设置
	 * @param wrapCompany
	 * @return
	 */
	private AttendanceScheduleSetting getAttendanceScheduleSettingByCompany( WrapCompany wrapCompany ) {
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		List<String> ids = null;
		Business business = null;
		if( wrapCompany != null ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				ids = new Business(emc).getAttendanceScheduleSettingFactory().listByDepartmentName( wrapCompany.getName());				
				if( ids !=null && ids.size() > 0){
					attendanceScheduleSettingList = new Business(emc).getAttendanceScheduleSettingFactory().list(ids);
					if( attendanceScheduleSettingList != null && attendanceScheduleSettingList.size() > 0){
						return attendanceScheduleSettingList.get(0);
					}
				}else{
					//查询上级公司，如果有上级公司那么查询上级公司的排班设置
					WrapCompany _wrapCompany = null;
					business = new Business(emc);
					try{
						_wrapCompany = business.organization().company().getSupDirect( wrapCompany.getName() );
					}catch(Exception e){
						logger.warn( "根据公司["+wrapCompany.getName()+"]获取上级公司的信息发生异常。" );
						logger.error(e);
					}
					if( _wrapCompany != null ){
						return getAttendanceScheduleSettingByCompany( _wrapCompany );
					}else{
						return null;
					}
				}	
			} catch (Exception e) {
				logger.warn("[getAttendanceScheduleSettingByCompany]递归查询公司的排班设置发生异常" );
				logger.error(e);
			}
		}
		return null;
	}

	/**
	 * 对单条的打卡数据进行详细分析
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @param attendanceSelfHolidayList
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail,  List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception{
		if( detail != null ){
			DateOperation dateOperation = new DateOperation();
			logger.debug( "attendance detail analysing, employee name:["+detail.getEmpName()+"]-["+detail.getRecordDateString()+"]......" );		
			List<String> ids = null;
			List<WrapDepartment> departmentList = null;
			List<AttendanceSelfHoliday> attendanceSelfHolidayList = null;
			AttendanceScheduleSetting attendanceScheduleSetting = null;
			Business business = null;
			Boolean check = true;
			business = new Business(emc);
			
			if( check ){//查询员工的休假记录
				try{
					ids = business.getAttendanceSelfHolidayFactory().getByEmployeeName( detail.getEmpName() );
				}catch( Exception e ){
					check = false;
					logger.warn( "system list attendance self holiday info ids with employee name got an exception.empname:" + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据员工姓名查询该员工的所有请假休假信息ID列表时发生异常" );
				}
			}
			if( check && ids != null && !ids.isEmpty() ){
				try{
					attendanceSelfHolidayList = business.getAttendanceSelfHolidayFactory().list( ids );
				}catch( Exception e ){
					check = false;
					logger.warn( "system list attendance self holiday info with ids got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据员工姓名查询该员工的所有请假休假信息列表时发生异常" );
				}
			}
			if( check ){//查询员工所在部门的排班信息
				try{
					departmentList = business.organization().department().listWithPerson( detail.getEmpName() );
					if( departmentList == null || departmentList.isEmpty() ){
						check = false;
						logger.warn( "user department is not exists, please check employee department info." + detail.getEmpName() );
						saveAnalyseResultAndStatus( emc, detail.getId(), -1, "员工未设置部门信息,请检查员工部门设置!" );
					}
				}catch( Exception e ){
					check = false;
					logger.warn( "system list department names with employee name got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据员工姓名查询该员工所在的部门和组织列表时发生异常" );
				}				
			}
			if( check && departmentList!= null && !departmentList.isEmpty() ){
				try{
					attendanceScheduleSetting =  getAttendanceScheduleSettingByDepartments( departmentList );
					if( attendanceScheduleSetting == null ){
						check = false;
						saveAnalyseResultAndStatus( emc, detail.getId(), -1, "未查询到员工[" + detail.getEmpName() + "]所在部门的排班信息" );
					}
				}catch( Exception e ){
					check = false;
					logger.warn( "system get department schedule setting for employee with department names got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据部门列表，获取用户所在的部门的所有排班列表中适合用户的配置信息时发生异常" );
				}
			}else{
				logger.warn( "attendance schedule setting not exists for user:"+detail.getEmpName() );
			}
			
			Date recordDate = null;
			if( check ){
				if( detail.getRecordDateString() != null && detail.getRecordDateString().length() > 0 ){
					try{
						recordDate = dateOperation.getDateFromString( detail.getRecordDateString());
					}catch(Exception e){
						saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统格式化记录的打卡日期发生异常，未能设置日期格式的打卡时间属性recordDate，日期recordDateString：" + detail.getRecordDateString() );
						check = false;
					}
				}else{
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统格式化记录的打卡日期发生异常，未能设置日期格式的打卡时间属性为空" );
					check = false;
				}
			}
			if( check ){
				detail = emc.find( detail.getId(), AttendanceDetail.class );//要重新查询一次，才可能在这个emc里进行管理
				if( detail != null ){
					detail.refresh(); //将打卡数据里分析过的状态全部清空，还原成未分析的数据
				}else{
					check = false;
					logger.warn( "system can not find detail, record may be deleted." );
				}
			}
			if( check ){
				if( attendanceScheduleSetting != null ){
					detail.setCompanyName( attendanceScheduleSetting.getCompanyName() );
					detail.setDepartmentName( attendanceScheduleSetting.getOrganizationName() );
					detail.setOnWorkTime( attendanceScheduleSetting.getOnDutyTime() );
					detail.setOffWorkTime( attendanceScheduleSetting.getOffDutyTime() );
				}
				if( recordDate != null ){
					detail.setRecordDate( recordDate );
				}
			}
			if( check ){
				try{
					setSelfHolidays( detail, attendanceSelfHolidayList, dateOperation );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse employee self holiday for detail got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息，请假信息分析员工请假情况时发生异常" );
				}
			}
			if( check ){
				try{
					detail.setIsWeekend( dateOperation.isWeekend( detail.getRecordDate() ));
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be weekend got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是周末时发生异常, recordDate:" + detail.getRecordDateString() );
				}
			}
			if( check ){
				try{
					detail.setIsHoliday( isHoliday( detail, attendanceWorkDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be holiday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是节假日时发生异常, recordDate:"+ detail.getRecordDateString() );
				}
			}
			if( check ){
				try{
					detail.setIsWorkday( isWorkday( detail, attendanceWorkDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be workday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是工作日时发生异常" );
				}
			}	
			if( check ){
				try{
					analyseAttendanceDetailStatisticCycle( detail, companyAttendanceStatisticalCycleMap );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail statistic cycle got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息以及排班信息进一步分析打卡信息统计周期时发生异常" );
				}
			}
			if( check ){
				try{
					analyseAttendanceDetail( detail, attendanceScheduleSetting, dateOperation );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail by on and off work time for advance analyse got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息排班信息进一步分析员工出勤情况时发生异常" );
				}
			}
			if( check ){
				emc.beginTransaction( AttendanceDetail.class );
				detail.setRecordStatus( 1 );
				detail.setDescription("员工打卡记录分析完成！" );
				emc.check( detail, CheckPersistType.all );
				emc.commit();
			}
			if( check ){
				//分析成功根据打卡数据来记录与这条数据相关的统计需求记录
				recordStatisticRequireLog( detail );
			}
			return true;
		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}
	/**
	 * 根据打卡数据来记录与这条数据相关的统计需求记录
	 * @param detail
	 * @throws Exception 
	 */
	public void recordStatisticRequireLog( AttendanceDetail detail ) throws Exception{
		//数据分析完成，那么需要记录一下需要统计的信息数据
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		Business business = null;
		List<AttendanceStatisticRequireLog> logList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			attendanceStatisticRequireLogFactory = business.getAttendanceStatisticRequireLogFactory();
			//个人统计每月统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("PERSON_PER_MONTH", detail.getEmpName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( logList == null || logList.size() == 0 ){
				//logger.debug("统计数据不存在：PERSON_PER_MONTH，"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("员工每月统计");
				attendanceStatisticRequireLog.setStatisticType("PERSON_PER_MONTH");
				attendanceStatisticRequireLog.setStatisticKey( detail.getEmpName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				logger.debug("统计数据已存在：PERSON_PER_MONTH，"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//部门按月统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("DEPARTMENT_PER_MONTH", detail.getDepartmentName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( logList == null || logList.size() == 0 ){
				//logger.debug("统计数据不存在：DEPARTMENT_PER_MONTH，"+ detail.getDepartmentName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("部门每月统计");
				attendanceStatisticRequireLog.setStatisticType("DEPARTMENT_PER_MONTH");
				attendanceStatisticRequireLog.setStatisticKey( detail.getDepartmentName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				//logger.debug("统计数据已存在：DEPARTMENT_PER_MONTH，"+ detail.getDepartmentName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//公司按月统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("COMPANY_PER_MONTH", detail.getCompanyName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( logList == null || logList.size() == 0 ){
				//logger.debug("统计数据不存在：COMPANY_PER_MONTH，"+ detail.getCompanyName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("公司每月统计");
				attendanceStatisticRequireLog.setStatisticType("COMPANY_PER_MONTH");
				attendanceStatisticRequireLog.setStatisticKey( detail.getCompanyName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				//logger.debug("统计数据已存在：COMPANY_PER_MONTH，"+ detail.getCompanyName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//部门每日统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("DEPARTMENT_PER_DAY", detail.getDepartmentName(), null, null, detail.getRecordDateString(), "WAITING");
			if( logList == null || logList.size() == 0 ){
				//logger.debug("统计数据不存在：DEPARTMENT_PER_DAY，"+ detail.getDepartmentName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("部门每日统计");
				attendanceStatisticRequireLog.setStatisticType("DEPARTMENT_PER_DAY");
				attendanceStatisticRequireLog.setStatisticKey( detail.getDepartmentName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				attendanceStatisticRequireLog.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				//logger.debug("统计数据已存在：DEPARTMENT_PER_DAY，"+ detail.getDepartmentName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
			//公司每日统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("COMPANY_PER_DAY", detail.getCompanyName(), null, null, detail.getRecordDateString(), "WAITING");
			if( logList == null || logList.size() == 0 ){
				//logger.debug("统计数据不存在：COMPANY_PER_DAY，"+ detail.getCompanyName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("公司每日统计");
				attendanceStatisticRequireLog.setStatisticType("COMPANY_PER_DAY");
				attendanceStatisticRequireLog.setStatisticKey( detail.getCompanyName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				attendanceStatisticRequireLog.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				//logger.debug("统计数据已存在：COMPANY_PER_DAY，"+ detail.getCompanyName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
		}catch(Exception e){
			logger.warn("系统在向数据库新增统计需求时发生异常" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据员工休假数据来记录与这条数据相关的统计需求记录
	 * @param detail
	 * @throws Exception 
	 */
	public void recordStatisticRequireLog( AttendanceSelfHoliday holiday ) throws Exception{
		//数据分析完成，那么需要记录一下需要统计的信息数据
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		Business business = null;
		List<AttendanceStatisticRequireLog> logList = null;
		List<AttendanceCycles> cycleList = null;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//查询员工请假期间有几个统计周期
			cycleList = business.getAttendanceDetailFactory().getCyclesFromDetailWithDateSplit( holiday.getEmployeeName(), holiday.getStartTime(), holiday.getEndTime());
		}catch(Exception e){
			logger.warn("系统在查询员工请假期间有几个统计周期时发生异常" );
			logger.error(e);
		}
		
		if( cycleList != null && cycleList.size() > 0 ){
			for( AttendanceCycles attendanceCycles : cycleList){
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					attendanceStatisticRequireLogFactory = business.getAttendanceStatisticRequireLogFactory();
					//个人统计每月统计
					logList = null;
					logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("PERSON_PER_MONTH", holiday.getEmployeeName(), attendanceCycles.getCycleYear(), attendanceCycles.getCycleMonth(), null, "WAITING");
					if( logList == null || logList.size() == 0 ){
						logger.debug("统计数据不存在：PERSON_PER_MONTH，"+ holiday.getEmployeeName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
						attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
						attendanceStatisticRequireLog.setStatisticName("员工每月统计");
						attendanceStatisticRequireLog.setStatisticType("PERSON_PER_MONTH");
						attendanceStatisticRequireLog.setStatisticKey( holiday.getEmployeeName() );
						attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
						attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						//logger.debug("统计数据已存在：PERSON_PER_MONTH，"+ holiday.getEmployeeName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
					}
					//部门按月统计
					logList = null;
					logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("DEPARTMENT_PER_MONTH", holiday.getOrganizationName(), attendanceCycles.getCycleYear(), attendanceCycles.getCycleMonth(), null, "WAITING");
					if( logList == null || logList.size() == 0 ){
						logger.debug("统计数据不存在：DEPARTMENT_PER_MONTH，"+ holiday.getOrganizationName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
						attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
						attendanceStatisticRequireLog.setStatisticName("部门每月统计");
						attendanceStatisticRequireLog.setStatisticType("DEPARTMENT_PER_MONTH");
						attendanceStatisticRequireLog.setStatisticKey( holiday.getOrganizationName() );
						attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
						attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						//logger.debug("统计数据已存在：DEPARTMENT_PER_MONTH，"+ holiday.getOrganizationName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
					}
					//公司按月统计
					logList = null;
					logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("COMPANY_PER_MONTH", holiday.getCompanyName(), attendanceCycles.getCycleYear(), attendanceCycles.getCycleMonth(), null, "WAITING");
					if( logList == null || logList.size() == 0 ){
						logger.debug("统计数据不存在：COMPANY_PER_MONTH，"+ holiday.getCompanyName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
						attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
						attendanceStatisticRequireLog.setStatisticName("公司每月统计");
						attendanceStatisticRequireLog.setStatisticType("COMPANY_PER_MONTH");
						attendanceStatisticRequireLog.setStatisticKey( holiday.getCompanyName() );
						attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
						attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						//logger.debug("统计数据已存在：COMPANY_PER_MONTH，"+ holiday.getCompanyName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
					}
				}catch(Exception e){
					logger.warn("系统在向数据库新增每月统计需求时发生异常" );
					logger.error(e);
				}
			}
		}
		List<String> dateList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//查询员工请假期间有几多少天
			DateOperation dateOperation = new DateOperation();
			dateList = dateOperation.listDateStringBetweenDate( holiday.getStartTime(), holiday.getEndTime() );
		}catch(Exception e){
			logger.warn("系统在查询员工请假期间有几多少天时发生异常" );
			logger.error(e);
		}
		DateOperation dateOperation = new DateOperation();
		if( dateList != null ){
			for( String date : dateList ){
				
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					//查询员工请假期间有几个统计周期
					cycleList = business.getAttendanceDetailFactory().getCyclesFromDetailWithDateSplit( holiday.getEmployeeName(), dateOperation.getDateFromString(date + " 00:00:00"), dateOperation.getDateFromString(date+ " 23:59:59"));
				}catch(Exception e){
					logger.warn("系统在查询员工请假期间有几个统计周期时发生异常" );
					logger.error(e);
				}
				
				if( cycleList !=  null && cycleList.size() > 0 ){
					for( AttendanceCycles attendanceCycles : cycleList ){
						try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
							business = new Business(emc);
							//部门每日统计
							logList = null;
							logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("DEPARTMENT_PER_DAY", holiday.getOrganizationName(), null, null, date, "WAITING");
							if( logList == null || logList.size() == 0 ){
								logger.debug("统计数据不存在：DEPARTMENT_PER_DAY，"+ holiday.getOrganizationName() + ", null, null , "+ date +", WAITING" );
								attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
								attendanceStatisticRequireLog.setStatisticName("部门每日统计");
								attendanceStatisticRequireLog.setStatisticType("DEPARTMENT_PER_DAY");
								attendanceStatisticRequireLog.setStatisticKey( holiday.getOrganizationName() );
								attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
								attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
								attendanceStatisticRequireLog.setStatisticDay( date );
								emc.beginTransaction( AttendanceStatisticRequireLog.class );
								emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
								emc.commit();
							}else{
								//logger.debug("统计数据已存在：DEPARTMENT_PER_DAY，"+ holiday.getOrganizationName() + ", null, null , "+ date +", WAITING" );
							}
							//公司每日统计
							logList = null;
							logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("COMPANY_PER_DAY", holiday.getCompanyName(), null, null, date, "WAITING");
							if( logList == null || logList.size() == 0 ){
								logger.debug("统计数据不存在：COMPANY_PER_DAY，"+ holiday.getCompanyName() + ", null, null , "+ date +", WAITING" );
								attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
								attendanceStatisticRequireLog.setStatisticName("公司每日统计");
								attendanceStatisticRequireLog.setStatisticType("COMPANY_PER_DAY");
								attendanceStatisticRequireLog.setStatisticKey( holiday.getCompanyName() );
								attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
								attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
								attendanceStatisticRequireLog.setStatisticDay( date );
								emc.beginTransaction( AttendanceStatisticRequireLog.class );
								emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
								emc.commit();
							}else{
								//logger.debug("统计数据已存在：COMPANY_PER_DAY，"+ holiday.getCompanyName() + ", null, null , "+ date +", WAITING" );
							}
						}catch(Exception e){
							logger.warn("系统在向数据库新增每日统计需求时发生异常" );
							logger.error(e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 判断当前日期是否为调休工作日
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @return
	 */
	private boolean isWorkday(AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, DateOperation dateOperation) {
		if( attendanceWorkDayConfigList != null ){
			Date configDate = null;
			for( AttendanceWorkDayConfig workDayConfig : attendanceWorkDayConfigList ){
				try {
					configDate = dateOperation.getDateFromString( workDayConfig.getConfigDate() );
				} catch (Exception e) {
					logger.warn( "系统转换"+workDayConfig.getConfigDate()+"格式为日期时发生异常！" );
					logger.error(e);
				}
				//进行日期对比
				Calendar calendar_record = Calendar.getInstance();
				Calendar calendar_config = Calendar.getInstance();
				calendar_record.setTime( detail.getRecordDate() );
				calendar_config.setTime( configDate );				
				if( calendar_record.get( Calendar.YEAR ) == calendar_config.get( Calendar.YEAR )
					&& calendar_record.get( Calendar.MONTH ) == calendar_config.get( Calendar.MONTH )
					&& calendar_record.get( Calendar.DATE ) == calendar_config.get( Calendar.DATE )
					&& "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
				){
					return true;
				}
			}
		}	
		return false;
	}

	/**
	 * 判断当前日期是否为法定节假日
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @return
	 */
	private boolean isHoliday( AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, DateOperation dateOperation) {
		if( attendanceWorkDayConfigList != null ){
			Date configDate = null;
			for( AttendanceWorkDayConfig workDayConfig : attendanceWorkDayConfigList ){
				try {
					configDate = dateOperation.getDateFromString( workDayConfig.getConfigDate() );
				} catch (Exception e) {
					logger.warn( "系统转换"+workDayConfig.getConfigDate()+"格式为日期时发生异常！" );
					logger.error(e);
				}
				//进行日期对比
				Calendar calendar_record = Calendar.getInstance();
				Calendar calendar_config = Calendar.getInstance();
				calendar_record.setTime( detail.getRecordDate() );
				calendar_config.setTime( configDate );				
				if( calendar_record.get( Calendar.YEAR ) == calendar_config.get( Calendar.YEAR )
					&& calendar_record.get( Calendar.MONTH ) == calendar_config.get( Calendar.MONTH )
					&& calendar_record.get( Calendar.DATE ) == calendar_config.get( Calendar.DATE )
					&& "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
				){
					return true;
				}
			}
		}	
		return false;
	}
	
	/**
	 * 判断员工是否正在休假以及休假的时段
	 * @param recordDate
	 * @param attendanceSelfHolidayList 
	 * @return
	 * @throws Exception 
	 */
	private void setSelfHolidays( AttendanceDetail detail, List<AttendanceSelfHoliday> attendanceSelfHolidayList, DateOperation dateOperation) throws Exception {
		//查询当前打卡用户所有的请假申请中，打卡时间是否处于休假时间段中
		Date dayWorkStart = null, dayWorkEnd = null, dayMiddle = null ;
		if( attendanceSelfHolidayList != null ){
			//如果打卡日期全天 在 休假时间的中间，那么是全天请假
			try {
				dayWorkStart = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOnWorkTime());
			} catch (Exception e) {
				logger.warn( "setSelfHolidays获取打卡日期的一天的工作开始时间发生异常。" );
				logger.error(e);
				return;
			}
			try {
				dayWorkEnd = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOffWorkTime() );
			} catch (Exception e) {
				logger.warn( "setSelfHolidays获取打卡日期的一天的工作结束时间发生异常。" );
				logger.error(e);
				return;
			}
			try {
				dayMiddle = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " 12:00:00" );
			} catch (Exception e) {
				logger.warn( "setSelfHolidays获取打卡日期的一天的工作午休时间发生异常。" );
				logger.error(e);
				return;
			}
			//循环比对，看看是否全天请假
			for( AttendanceSelfHoliday selfHoliday : attendanceSelfHolidayList ){
				if( selfHoliday.getEndTime().getTime() > dayWorkStart.getTime() && selfHoliday.getStartTime().getTime() < dayWorkEnd.getTime() ){
					//对比员工姓名，员工姓名是唯一键
					if( detail.getEmpName().equals( selfHoliday.getEmployeeName() ) ){
						if( selfHoliday.getStartTime().getTime() <= dayWorkStart.getTime() && selfHoliday.getEndTime().getTime() >= ( dayMiddle.getTime() + 2*60*60*1000 )){
							//logger.debug("全天请假了");
							//全天休假
							detail.setIsGetSelfHolidays(true);
							detail.setSelfHolidayDayTime("全天");
							detail.setGetSelfHolidayDays(1.0);
						}else if( selfHoliday.getEndTime().getTime() <= dayMiddle.getTime() && selfHoliday.getEndTime().getTime() > dayWorkStart.getTime()
						){
							//上午休假
							//logger.debug("上午休假了");
							detail.setIsGetSelfHolidays(true);
							detail.setSelfHolidayDayTime("上午");
							detail.setGetSelfHolidayDays(0.5);
						}else if( selfHoliday.getStartTime().getTime() >= dayMiddle.getTime() && selfHoliday.getStartTime().getTime() <= dayWorkEnd.getTime()
						){
							//上午休假
							//logger.debug("下午休假了");
							detail.setIsGetSelfHolidays( true );
							detail.setSelfHolidayDayTime("下午");
							detail.setGetSelfHolidayDays(0.5);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 分析一条打卡记录信息<br/>
		<b>迟到</b>：晚于最迟“迟到起算时间”，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>早退</b>：下班打卡时间早于“迟到起算时间”，并且工作当日时间不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>异常</b>：缺少签到退中的1条打卡数据 或者 上下班打卡时间都在最迟起算时间内，不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>缺勤</b>：当天没有打卡数据，并且当天是工作日（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）
	 * @param detail
	 * @param attendanceScheduleSetting
	 * @param dateOperation
	 * @throws Exception 
	 */
	private void analyseAttendanceDetail( AttendanceDetail detail, AttendanceScheduleSetting attendanceScheduleSetting, DateOperation dateOperation ) throws Exception {
		if( dateOperation == null ){
			dateOperation = new DateOperation();
		}
		if( detail == null ){
			throw new Exception("detail is null!" );
		}
		if( attendanceScheduleSetting == null ){
			throw new Exception("attendanceScheduleSetting is null, empName:" + detail.getEmpName() );
		}
		
		Date onDutyTime = null, offDutyTime = null;
		Date onWorkTime = null, offWorkTime = null;
		Date lateStartTime = null, leaveEarlyStartTime = null, absenceStartTime = null;
		Date morningEndTime = null;
		
		//先初始化当前打卡信息中的上下班时间要求，该要求是是根据员工所在部门排班信息获取到的
		try {
			//logger.debug( "格式化[上班签到时间]onWorkTime=" +  detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			onWorkTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnWorkTime() );
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			onWorkTime = null;
			logger.warn( "系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime());
			logger.error(e);
		}
		
		try {
			//logger.debug( "格式化[下班签退时间]offWorkTime=" +  detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			offWorkTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffWorkTime() );
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			offWorkTime = null;
			logger.warn( "系统进行时间转换时发生异常,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			logger.error(e);
		}
		
		if( attendanceScheduleSetting.getLateStartTime() != null && !attendanceScheduleSetting.getLateStartTime().isEmpty() ){
			try {
				lateStartTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + attendanceScheduleSetting.getLateStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,lateStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLateStartTime() );
				lateStartTime = null;
				logger.warn( "系统进行时间转换时发生异常,lateStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLateStartTime());
				logger.error(e);
			}
		}else{
			//logger.warn("迟到时间设置为空！系统将不判断迟到情况");
		}
		
		if( attendanceScheduleSetting.getLeaveEarlyStartTime()  != null && !attendanceScheduleSetting.getLeaveEarlyStartTime().trim().isEmpty() ){
			try {
				//logger.debug( "格式化[早退起算时间]leaveEarlyStartTime=" +  detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
				leaveEarlyStartTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
				leaveEarlyStartTime = null;
				logger.warn( "系统进行时间转换时发生异常,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
				logger.error(e);
			}
		}else{
			//logger.warn("早退时间设置为空！系统将不判断早退情况");
		}
		
		if( attendanceScheduleSetting.getAbsenceStartTime()  != null && !attendanceScheduleSetting.getAbsenceStartTime().trim().isEmpty() ){
			try {
				//logger.debug( "格式化[缺勤起算时间]absenceStartTime=" +  detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
				absenceStartTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,absenceStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
				absenceStartTime = null;
				logger.warn( "系统进行时间转换时发生异常,absenceStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
				logger.error(e);
			}
		}else{
			//logger.warn("上午缺勤时间设置为空！系统将不判上午缺勤情况");
		}
		
		try {
			//logger.debug( "格式化[上午工作结束时间]morningEndTime=" +  detail.getRecordDateString() + " 12:00:00" );
			morningEndTime = dateOperation.getDateFromString( detail.getRecordDateString() + " 12:00:00"  );
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,morningEndTime=" + detail.getRecordDateString() + " 12:00:00" );
			morningEndTime = null;
			logger.warn( "系统进行时间转换时发生异常,morningEndTime=" + detail.getRecordDateString() + " 12:00:00" );
			logger.error(e);
		}
		
		
		if( onWorkTime != null && offWorkTime != null ){
			//logger.debug( "上下班排班信息获取正常：onWorkTime=" +  onWorkTime + "， offWorkTime="+offWorkTime );
			//获取员工签到时间
			try {
				if( detail.getOnDutyTime() == null || detail.getOnDutyTime().isEmpty() ){
					//logger.debug( "onDutyTime 为空 " );
					onDutyTime = null;
				}else{
					//logger.debug( "格式化onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
					onDutyTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnDutyTime() );
				}
			} catch (Exception e) {
				onDutyTime = null;
				logger.warn( "系统进行时间转换时发生异常,onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
				logger.error(e);
			}
			//获取员工签退时间
			try {
				if( detail.getOffDutyTime() == null || detail.getOffDutyTime().isEmpty() ){
					//logger.debug( "offDutyTime 为空" );
					offDutyTime = null;
				}else{
					//logger.debug( "格式化offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
					offDutyTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffDutyTime() );
				}
			} catch (Exception e) {
				offDutyTime = null;
				logger.warn( "系统进行时间转换时发生异常,offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
				logger.error(e);
			}
			
			//=========================================================================================================
			//=====如果员工没有签到并且没有签退，一条打卡时间都没有，那么是算缺勤的========================================
			//=========================================================================================================
			if( onDutyTime == null && offDutyTime == null ){
				//if( detail.getIsGetSelfHolidays()  && "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ) ){
				if( detail.getIsGetSelfHolidays()  ){
					//logger.debug( "请幸运，全天请假不计缺勤。" );
					detail.setAttendance( 0.0 );
					detail.setIsAbsent( false );
					detail.setAbsence( 0.0 );
					detail.setAbsentDayTime("无");
					detail.setWorkTimeDuration( 0L );
				}else{
					if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
						|| detail.getIsHoliday() //或者是节假日
					){
						//logger.debug( "未请假，不是工作日，不计缺勤。" );
						detail.setAttendance( 0.0 );
						detail.setIsAbsent( false );
						detail.setAbsence( 0.0 );
						detail.setAbsentDayTime("无");
						detail.setWorkTimeDuration( 0L );
					}else{
						//logger.debug( "未请假，工作日，计缺勤1天。" );
						detail.setAttendance( 0.0 );
						detail.setIsAbsent( true );
						detail.setAbsence( 1.0 );
						detail.setAbsentDayTime("全天");
						detail.setWorkTimeDuration( 0L );
					}
				}
			}else{
				//=========================================================================================================
				//=====上午  如果员工已经签到================================================================================
				//=========================================================================================================
				if( onDutyTime != null ){
					//logger.debug( "上午打过卡，时间：onDutyTime=" + onDutyTime + ", 上午工作结束时间：morningEndTime=" + morningEndTime );					
					//absenceStartTimes可以不配置，如果不配置，则为null
					//上午签到过了，如果排班设置里已经配置过了缺勤起算时间，那么判断员工是否已经缺勤，如果未休假，则视为缺勤半天			
					if( absenceStartTime != null && onDutyTime.after( absenceStartTime )){
						//logger.debug( "上午打卡时间晚于缺勤计时时间......" );
						if( detail.getIsGetSelfHolidays()  && ("上午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
							//logger.debug( "请幸运，请假不计考勤，出勤只算半天，但请过假了不算缺勤" );
							detail.setIsAbsent( false );
							detail.setAbsence(0.0);
						}else{
							if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
								){
								//logger.debug( "休息天，不算缺勤，出勤最多只能算半天" );
								detail.setIsAbsent( false );
								detail.setAbsence(0.0);
							}else{
//								logger.debug( "呵呵，没请假，缺勤半天，出勤最多只能算半天" );
								detail.setIsAbsent( true );
								detail.setAbsence(0.5);
								detail.setAbsentDayTime("上午");
							}
						}
						//缺勤直接扣半天出勤, 请假不算出勤，所以也只有半天
						detail.setAttendance( 0.5 );
					}else if( lateStartTime != null && onDutyTime.after( lateStartTime )){ 
						//上午签到过了，如果排班设置里已经配置过了迟到起算时间，那么判断员工是否已经迟到，如果未休假
						detail.setAttendance( 1.0 );//迟到没关系，出勤时间不用扣半天
						//logger.debug( "上午打卡时间晚于迟到计时时间......" );
						if( detail.getIsGetSelfHolidays()  && ("上午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))
						){
							//logger.debug( "请幸运，请过假了不算迟到" );
							detail.setLateTimeDuration( 0L ); //请假了不算迟到
							detail.setIsLate(false );//请假了不算迟到
						}else{
							if( ( detail.getIsWeekend()  && !detail.getIsWorkday()) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
								){
								detail.setLateTimeDuration( 0L );
								detail.setIsLate( false );
								//logger.debug( "休息天，不算迟到" );
							}else{
								if( onDutyTime == null || offDutyTime == null ){
									detail.setIsAbnormalDuty( true );
								}else{
									long minutes = dateOperation.getMinutes( onWorkTime, onDutyTime ); //迟到计算从上班时间开始计算，不是迟到起算时间
									detail.setLateTimeDuration( minutes );//没请假算迟到时长 
									detail.setIsLate( true );//没请假算迟到
									//logger.debug( "呵呵，没请假，计迟到一次，迟到时长：minutes=" + minutes );
								}
							}
						}
					}
					long minutes = dateOperation.getMinutes( onDutyTime, morningEndTime );
					//logger.debug( "上午工作时长, 从"+onDutyTime+"到"+morningEndTime+" ：minutes=" + minutes + "分钟。" );
					detail.setWorkTimeDuration( minutes );//记录上午的工作时长
				}else{
				//	logger.debug( "员工上午未打卡，异常状态......" );
					if( detail.getIsGetSelfHolidays() && ("上午".equalsIgnoreCase( detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))
					){
					//	logger.debug( "请幸运，请假不计考勤，不需要打卡，不算异常" );
						detail.setIsAbsent( false );
						//detail.setAttendance( 0.5 );
					}else{
						if( ( detail.getIsWeekend()&& !detail.getIsWorkday() ) //周末，并且未调休为工作日
								|| detail.getIsHoliday() //或者是节假日
							){
//							logger.debug( "休息天，不算打卡异常，本来就不需要打卡" );
							detail.setAbnormalDutyDayTime("无");
							detail.setIsAbnormalDuty( false );
							//detail.setAttendance( 0 )
						}else{
//							logger.debug( "呵呵，没请假，打卡异常。" );
							detail.setAbnormalDutyDayTime("上午");
							detail.setIsAbnormalDuty(true);
							//detail.setAttendance( 0.5 ); //上午打卡异常，不知道算不算出勤时间
						}
					}
					//logger.debug( "上午工作时长, 未打卡：minutes= 0 分钟。" );
					detail.setWorkTimeDuration( 0L );//记录上午的工作时长
				}
				//=========================================================================================================
				//=====下午  如果员工已经签退================================================================================
				//=========================================================================================================
				if( offDutyTime != null ){
					//logger.debug( "早退计时时间：leaveEarlyStartTime=" + leaveEarlyStartTime );
					long minutes = dateOperation.getMinutes( morningEndTime, offDutyTime);
					//logger.debug( "下午工作时长, 从"+morningEndTime+"到"+offDutyTime+" ：minutes=" + minutes + "分钟。" );
					detail.setWorkTimeDuration( detail.getWorkTimeDuration() + minutes );//记录上午的工作时长 + 下午工作时长
					//logger.debug( "全天工作时长, ：minutes=" + detail.getWorkTimeDuration() + "分钟。" );					
					if( leaveEarlyStartTime != null && offDutyTime.before( leaveEarlyStartTime )){
						//logger.debug( "下午打卡时间早于早退计时时间......" );
						if( detail.getIsGetSelfHolidays() && ("下午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
							//logger.debug( "请幸运，请假不计考勤，出勤只算半天，但请过假了不算早退" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
							//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤时间
						}else{
							if( ( detail.getIsWeekend() && !detail.getIsWorkday() ) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
								){
//								logger.debug( "休息天，不算早退" );
								detail.setLeaveEarlierTimeDuration( 0L );
								detail.setIsLeaveEarlier( false );
								//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤
							}else{
								minutes = dateOperation.getMinutes( offDutyTime, leaveEarlyStartTime );
								detail.setLeaveEarlierTimeDuration(minutes); //早退时间
								detail.setIsLeaveEarlier( true );
								//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤
								//logger.debug( "呵呵，没请假，早退计一次，全天工作时长："+detail.getWorkTimeDuration()+"分钟，早退时长：minutes=" + minutes );
							}
						}
					}
				}else{
					//logger.debug( "员工下午未打卡，属于异常状态......" );
					//员工未签退，算缺勤了半天，出勤率: - 0.5
					if( detail.getIsGetSelfHolidays()  && ("下午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
						//logger.debug( "请幸运，请假不计考勤，不需要打卡，不算异常状态" );
						detail.setLeaveEarlierTimeDuration( 0L );
						detail.setIsLeaveEarlier( false );
						//detail.setAttendance( detail.getAttendance() - 0.5 );
					}else{
						if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
								|| detail.getIsHoliday() //或者是节假日
							){
							//logger.debug( "休息天，不算异常" );
							detail.setAbnormalDutyDayTime("无");
							detail.setIsAbnormalDuty(false);
						}else{
							detail.setAbnormalDutyDayTime("下午");
							detail.setIsAbnormalDuty(true);
							//logger.debug( "呵呵，没请假，未打卡，算异常状态。" );
						}
					}
					//logger.debug( "全天工作时长,[下午未打卡，只算上午的工作时长] ：minutes=" + detail.getWorkTimeDuration() + "分钟。" );
				}
			}
			
			
			//=========================================================================================================
			//=====如果上下班都打卡了，但是工作时间没有9小时，那么属于工时不足(如果迟到了，就算迟到，不算工时不足)========================================
			//=========================================================================================================
			if( onDutyTime != null && offDutyTime != null){
				if( ( detail.getIsWeekend() && !detail.getIsWorkday() ) //周末，并且未调休为工作日
						|| detail.getIsHoliday() //或者是节假日
					){
					//没事
				}else{
					if( !detail.getIsGetSelfHolidays() ){ // 没请假
						//计算工时
						long minutes = dateOperation.getMinutes( onDutyTime, offDutyTime);
						if( minutes < (60*9)){
							//logger.debug( "签到时间["+onDutyTime+"]，签退时间["+offDutyTime+"]，工作时间:"+minutes+"分钟！工时不足！" );
							if( !detail.getIsLate() ){
								detail.setIsLackOfTime(true);//工时不足
							}
						}
					}
				}
			}
			detail.setRecordStatus( 1 );
		}
	}
	
	private void saveAnalyseResultAndStatus( EntityManagerContainer emc, String id, int status, String description) throws Exception {
		AttendanceDetail detail = emc.find( id, AttendanceDetail.class );
		emc.beginTransaction( AttendanceDetail.class );
		detail.setRecordStatus( status );
		detail.setDescription( description );
		if( detail != null && detail.getEmpName() != null ){
			detail.setEmpName( detail.getEmpName().trim() );
		}
		emc.check( detail, CheckPersistType.all );
		emc.commit();
	}

}
