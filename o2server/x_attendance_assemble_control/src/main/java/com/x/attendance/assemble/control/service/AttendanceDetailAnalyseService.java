package com.x.attendance.assemble.control.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseService {

	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseService.class );
	private AttendanceSelfHolidayService attendanceSelfHolidayService = new AttendanceSelfHolidayService();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();
	private AttendanceStatisticalCycleService statisticalCycleService = new AttendanceStatisticalCycleService();
	private AttendanceWorkDayConfigService workDayConfigService = new AttendanceWorkDayConfigService();
	private AttendanceDetailAnalyseCoreService attendanceDetailAnalyseCoreService = new AttendanceDetailAnalyseCoreService();
	private DateOperation dateOperation = new DateOperation();
	private UserManagerService userManagerService = new UserManagerService();
	private  AttendanceSettingServiceAdv attendanceSettingServiceAdv = new AttendanceSettingServiceAdv();

	/**
	 * 根据员工姓名\开始日期\结束日期查询日期范围内所有的打卡记录信息ID列表<br/>
	 * 时间从开始日期0点，到结束日期23点59分
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> listAnalyseAttendanceDetailIds( EntityManagerContainer emc, String empName, Date startDate, Date endDate )throws Exception{
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
	 * 对一组的打卡信息数据进行分析
	 * @param emc
	 * @param detailIds
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetailWithIds(EntityManagerContainer emc, List<String> detailIds, Boolean debugger ) throws Exception{
		if( ListTools.isEmpty( detailIds ) ){
			return true;
		}
		analyseAttendanceDetails( emc, emc.list( AttendanceDetail.class, detailIds ), debugger );
		logger.debug( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 对一组的打卡信息数据进行分析
	 * @param emc
	 * @param detailList
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( EntityManagerContainer emc, List<AttendanceDetail> detailList, Boolean debugger ) throws Exception{
		if( ListTools.isEmpty( detailList ) ){
			return true;
		}
		List<AttendanceWorkDayConfig> workDayConfigList = workDayConfigService.getAllWorkDayConfigWithCache(debugger);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger);

		for( AttendanceDetail detail : detailList ){
			try{
				analyseAttendanceDetail( emc, detail, workDayConfigList, statisticalCycleMap, debugger );
			}catch(Exception e){
				logger.debug( "employee attendance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]" );
				logger.error(e);
			}
		}

		logger.debug( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 对一组的打卡信息数据进行分析
	 * @param emc
	 * @param detailList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( EntityManagerContainer emc, List<AttendanceDetail> detailList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		if( detailList == null || detailList.isEmpty() ){
			return true;
		}
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = workDayConfigService.getAllWorkDayConfigWithCache(debugger);

		for( AttendanceDetail detail : detailList ){
			try{
				analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, statisticalCycleMap, debugger );
			}catch(Exception e){
				logger.debug( "employee attendance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]" );
				logger.error(e);
			}
		}

		logger.debug( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 根据员工姓名，开始日期和结束日期重新分析所有的打卡记录，一般在用户补了休假记录的时候使用，补充了休假记录，需要将休假日期范围内的所有打卡记录重新分析，并且触发相关月份的统计
	 * @param emc
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetails( EntityManagerContainer emc, String empName, Date startDate, Date endDate, Boolean debugger ) throws Exception{
		Business business = null;
		List<String> ids = null;
		List<AttendanceWorkDayConfig> workDayConfigList = workDayConfigService.getAllWorkDayConfigWithCache(debugger);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger);
		List<AttendanceDetail> attendanceDetailList = null;

		ids = listAnalyseAttendanceDetailIds( emc, empName, startDate, endDate );
		if(ListTools.isEmpty( ids )){
			logger.debug( debugger, "attendance detail info ids not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}
		attendanceDetailList = business.getAttendanceDetailFactory().list( ids );
		if( ListTools.isEmpty( attendanceDetailList ) ){
			logger.debug( debugger, "attendance detail info not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}

		for( AttendanceDetail detail : attendanceDetailList ){
			try{
				analyseAttendanceDetail( emc, detail, workDayConfigList, statisticalCycleMap, debugger );
			}catch(Exception e){
				logger.info( "employee attenance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]");
				logger.error(e);
			}
		}
		logger.info( "employee attendance details by empname, startdate and end date analyse over！" );;
		return true;
	}

	/**
	 * 对单条的打卡数据进行分析
	 * @param emc
	 * @param detailId
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( EntityManagerContainer emc, String detailId, Boolean debugger ) throws Exception{
		if( StringUtils.isNotEmpty( detailId )){
			return analyseAttendanceDetail( emc, emc.find( detailId, AttendanceDetail.class ),
					workDayConfigService.getAllWorkDayConfigWithCache(debugger),
					statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger),
					debugger );
		}else{
			logger.info( "detailId为空，无法继续进行分析。" );
		}
		return false;
	}

	/**
	 * 对单条的打卡数据进行分析
	 * @param emc
	 * @param detail
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, Boolean debugger ) throws Exception{
		if( detail != null ){
			return analyseAttendanceDetail( emc, detail,
					workDayConfigService.getAllWorkDayConfigWithCache(debugger),
					statisticalCycleService.getAllStatisticalCycleMapWithCache(debugger),
					debugger );
		}else{
			logger.info( "detail为空，无法继续进行分析。" );
		}
		return false;
	}

	/**
	 * 对单条的打卡数据进行详细分析
	 * @param emc
	 * @param detail
	 * @param workDayConfigList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, Boolean debugger ) throws Exception{
		List<String> ids_temp = null;
		AttendanceScheduleSetting scheduleSetting = null;
		List<AttendanceSelfHoliday> selfHolidayList = null;
		if( detail != null ){
			selfHolidayList = attendanceSelfHolidayService.listWithPersonFromCache(emc, detail.getEmpName(), false );
			//查询用户匹配的排班配置
			if(StringUtils.isNotEmpty(detail.getUnitName())){
				scheduleSetting = attendanceScheduleSettingService.getAttendanceScheduleSettingWithDetail( detail, debugger );
			}else{
				scheduleSetting = attendanceScheduleSettingService.getAttendanceScheduleSettingWithPerson( detail.getEmpName(), debugger );
			}
			return analyseAttendanceDetail( emc, detail, scheduleSetting, selfHolidayList, workDayConfigList, statisticalCycleMap, debugger );

		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}

	/**
	 * 对单条的打卡数据进行详细分析
	 * @param emc
	 * @param detail
	 * @param scheduleSetting
	 * @param selfHolidayList
	 * @param workDayConfigList
	 * @param statisticalCycleMap
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	private Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail,
											AttendanceScheduleSetting scheduleSetting,
											List<AttendanceSelfHoliday> selfHolidayList,
											List<AttendanceWorkDayConfig> workDayConfigList,
											Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap,
			Boolean debugger ) throws Exception{

		if( detail != null ){
			logger.debug( debugger, "attendance detail analysing, employee name:["+detail.getEmpName()+"]-["+detail.getRecordDateString()+"]......" );
			Boolean check = true;


			if( scheduleSetting == null ){
				check = false;
				saveAnalyseResultAndStatus( emc, detail.getId(), -1, "未查询到员工[" + detail.getEmpName() + "]所在组织的排班信息" );
			}

			//初始化考勤打卡记录
			if( check ){
				//要重新查询，才可能在这个emc里进行事务管理
				detail = emc.find( detail.getId(), AttendanceDetail.class );
				if( detail != null ){
					//将打卡数据里分析过的状态全部清空，还原成未分析的数据
					detail.refresh();
				}else{
					check = false;
					logger.warn( "system can not find detail, record may be deleted." );
				}
			}

			Date recordDate = null;
			if( check ){
				if( StringUtils.isNotEmpty(detail.getRecordDateString()) ){
					try{
						//把日期字符串转换为日期对象，便于比较计算
						recordDate = dateOperation.getDateFromString( detail.getRecordDateString());
					}catch(Exception e){
						saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统格式化记录的打卡日期发生异常，未能设置日期格式的打卡时间属性recordDate，日期recordDateString：" + detail.getRecordDateString() );
						check = false;
					}
					if( recordDate != null ){
						detail.setRecordDate( recordDate );
						detail.setYearString( dateOperation.getYear( detail.getRecordDate() ) );
						detail.setMonthString( dateOperation.getMonth( detail.getRecordDate() ) );
					}
				}else{
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统格式化记录的打卡日期发生异常，未能设置日期格式的打卡时间属性为空" );
					check = false;
				}
			}

			if( check ){
				if( scheduleSetting != null ){
					if(StringUtils.isEmpty(detail.getTopUnitName())){
						detail.setTopUnitName(scheduleSetting.getTopUnitName());
					}
					//if(StringUtils.isEmpty(detail.getTopUnitName())){
					if(StringUtils.isEmpty(detail.getUnitName())){
						detail.setUnitName( userManagerService.getUnitNameWithPersonName( detail.getEmpName() ) );
					}
					detail.setOnWorkTime( scheduleSetting.getOnDutyTime() );
					detail.setMiddayRestStartTime( scheduleSetting.getMiddayRestStartTime() );
					detail.setMiddayRestEndTime( scheduleSetting.getMiddayRestEndTime() );
					detail.setOffWorkTime( scheduleSetting.getOffDutyTime() );
				}else{
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "考勤排班信息为空，无法执行考勤记录分析。" );
					check = false;
					logger.warn( "system can not find scheduleSetting for detail. UNIT:" + detail.getUnitName() );
				}
			}

			if( check ){
				try{
					composeSelfHolidaysForAttendanceDetail( detail, selfHolidayList, dateOperation, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse employee self holiday for detail got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息，请假信息分析员工请假情况时发生异常" );
				}
			}

			if( check ){
				try{
					//System.out.println("isWeekend="+attendanceSettingServiceAdv.isWeekend( detail.getRecordDate()));
					detail.setIsWeekend( attendanceSettingServiceAdv.isWeekend( detail.getRecordDate() ));
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be weekend got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是周末时发生异常, recordDate:" + detail.getRecordDateString() );
				}
			}

			if( check ){
				try{
					detail.setIsHoliday( isHoliday( detail, workDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be holiday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是节假日时发生异常, recordDate:"+ detail.getRecordDateString() );
				}
			}

			if( check ){
				try{
					detail.setIsWorkday( isWorkday( detail, workDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be workday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是工作日时发生异常" );
				}
			}

			if( check ){
				try{
					composeStatisticCycleForAttendanceDetail( detail, statisticalCycleMap, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail statistic cycle got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息以及排班信息进一步分析打卡信息统计周期时发生异常" );
				}
			}
			if( check ){
				try{
					//使用分析核心算法进行考勤状态分析
					detail = attendanceDetailAnalyseCoreService.analyseCore( detail, scheduleSetting, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail by on and off work time for advance analyse got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息排班信息进一步分析员工出勤情况时发生异常" );
				}
			}
			if( check ){
				//保存打卡数据分析结果
				emc.beginTransaction( AttendanceDetail.class );
				detail.setRecordStatus( 1 );
				detail.setDescription("员工打卡记录分析完成！" );
				emc.check( detail, CheckPersistType.all );
				emc.commit();
			}

			if( check ){
				//分析成功根据打卡数据来记录与这条数据相关的统计需求记录
				recordStatisticRequireLog( detail, debugger );
			}
			return true;
		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}

	/**
	 * 分析打卡信息，为打卡数据计算考勤周期信息
	 *
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	private AttendanceDetail composeStatisticCycleForAttendanceDetail( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		//根据打卡信息所属个人的组织获取一个合适的统计周期对象
		attendanceStatisticalCycle = statisticalCycleService.getStatisticCycleWithStartAndEnd(
				detail.getTopUnitName(),
				detail.getUnitName(),
				detail.getRecordDate(),
				topUnitAttendanceStatisticalCycleMap,
				debugger
		);
		if( attendanceStatisticalCycle != null ) {
			logger.debug( debugger, "日期" + detail.getRecordDateString() + ", 周期" + attendanceStatisticalCycle.getCycleStartDateString() + "-" + attendanceStatisticalCycle.getCycleEndDateString() );
			detail.setCycleYear( attendanceStatisticalCycle.getCycleYear() );
			detail.setCycleMonth( attendanceStatisticalCycle.getCycleMonth() );
		}
		return detail;
	}

	/**
	 * 分析打卡信息，判断员工是否正在休假以及休假的时段
	 * @param attendanceSelfHolidayList
	 * @return
	 * @throws Exception
	 */
	private AttendanceDetail composeSelfHolidaysForAttendanceDetail( AttendanceDetail detail, List<AttendanceSelfHoliday> attendanceSelfHolidayList, DateOperation dateOperation, Boolean debugger ) throws Exception {
		//查询当前打卡用户所有的请假申请中，打卡时间是否处于休假时间段中
		Date dayWorkStart = null, dayWorkEnd = null, dayMiddle = null ;
		if( attendanceSelfHolidayList != null ){
			//如果打卡日期全天 在 休假时间的中间，那么是全天请假
			try {
				dayWorkStart = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOnWorkTime());
			} catch (Exception e) {
				logger.warn( "获取打卡日期的一天的工作开始时间发生异常。" );
				logger.error(e);
				return detail;
			}
			try {
				dayWorkEnd = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOffWorkTime() );
			} catch (Exception e) {
				logger.warn( "获取打卡日期的一天的工作结束时间发生异常。" );
				logger.error(e);
				return detail;
			}
			try {
				dayMiddle = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " 12:00:00" );
			} catch (Exception e) {
				logger.warn( "获取打卡日期的一天的工作午休时间发生异常。" );
				logger.error(e);
				return detail;
			}
			//循环比对，看看是否全天请假
			for( AttendanceSelfHoliday selfHoliday : attendanceSelfHolidayList ){
				if( selfHoliday.getEndTime().getTime() > dayWorkStart.getTime() && selfHoliday.getStartTime().getTime() < dayWorkEnd.getTime() ){
					//对比员工姓名，员工姓名是唯一键
					if( detail.getEmpName().equals( selfHoliday.getEmployeeName() ) ){
						if( selfHoliday.getStartTime().getTime() <= dayWorkStart.getTime() && selfHoliday.getEndTime().getTime() >= ( dayMiddle.getTime() + 2*60*60*1000 )){
							logger.debug( debugger, detail.getEmpName()+"全天请假了");
							//全天休假
							detail.setIsGetSelfHolidays(true);
							detail.setLeaveType(selfHoliday.getLeaveType());
							detail.setSelfHolidayDayTime("全天");
							detail.setGetSelfHolidayDays(1.0);
						}else if( selfHoliday.getEndTime().getTime() <= dayMiddle.getTime() && selfHoliday.getEndTime().getTime() > dayWorkStart.getTime()
						){
							//上午休假
							logger.debug( debugger, detail.getEmpName()+"上午休假了");
							detail.setIsGetSelfHolidays(true);
							detail.setLeaveType(selfHoliday.getLeaveType());
							detail.setSelfHolidayDayTime("上午");
							detail.setGetSelfHolidayDays(0.5);
						}else if( selfHoliday.getStartTime().getTime() >= dayMiddle.getTime() && selfHoliday.getStartTime().getTime() <= dayWorkEnd.getTime()
						){
							//上午休假
							logger.debug( debugger, detail.getEmpName()+"下午休假了");
							detail.setIsGetSelfHolidays( true );
							detail.setLeaveType(selfHoliday.getLeaveType());
							detail.setSelfHolidayDayTime("下午");
							detail.setGetSelfHolidayDays(0.5);
						}
					}
				}
			}
		}
		return detail;
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
	 * 修改打卡记录的分析状态，0-未分析，1-已分析
	 * @param emc
	 * @param id
	 * @param status
	 * @param description
	 * @throws Exception
	 */
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

	/**
	 * 根据打卡数据来记录与这条数据相关的统计需求记录
	 * @param detail
	 * @param debugger
	 * @throws Exception
	 */
	public void recordStatisticRequireLog( AttendanceDetail detail, Boolean debugger ) throws Exception{
		//数据分析完成，那么需要记录一下需要统计的信息数据
		AttendanceStatisticRequireLog log = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		Business business = null;
		List<AttendanceStatisticRequireLog> logList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			attendanceStatisticRequireLogFactory = business.getAttendanceStatisticRequireLogFactory();
			//个人统计每月统计
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("PERSON_PER_MONTH", detail.getEmpName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "统计数据不存在：PERSON_PER_MONTH，"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("员工每月统计");
				log.setStatisticType("PERSON_PER_MONTH");
				log.setStatisticKey( detail.getEmpName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();

				//统计考勤数据
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "统计数据已存在：PERSON_PER_MONTH，"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//组织按月统计
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_MONTH", detail.getUnitName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "统计数据不存在：UNIT_PER_MONTH，"+ detail.getUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("组织每月统计");
				log.setStatisticType("UNIT_PER_MONTH");
				log.setStatisticKey( detail.getUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//统计考勤数据
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "统计数据已存在：UNIT_PER_MONTH，"+ detail.getUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//顶层组织按月统计
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_MONTH", detail.getTopUnitName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "统计数据不存在：TOPUNIT_PER_MONTH，"+ detail.getTopUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("顶层组织每月统计");
				log.setStatisticType("TOPUNIT_PER_MONTH");
				log.setStatisticKey( detail.getTopUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//统计考勤数据
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "统计数据已存在：TOPUNIT_PER_MONTH，"+ detail.getTopUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//组织每日统计
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_DAY", detail.getUnitName(), null, null, detail.getRecordDateString(), "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "统计数据不存在：UNIT_PER_DAY，"+ detail.getUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("组织每日统计");
				log.setStatisticType("UNIT_PER_DAY");
				log.setStatisticKey( detail.getUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				log.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//统计考勤数据
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "统计数据已存在：UNIT_PER_DAY，"+ detail.getUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
			//顶层组织每日统计
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_DAY", detail.getTopUnitName(), null, null, detail.getRecordDateString(), "WAITING");
			if( ListTools.isEmpty(logList) ){
				logger.debug( debugger, "统计数据不存在：TOPUNIT_PER_DAY，"+ detail.getTopUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				log = new AttendanceStatisticRequireLog();
				log.setStatisticName("顶层组织每日统计");
				log.setStatisticType("TOPUNIT_PER_DAY");
				log.setStatisticKey( detail.getTopUnitName() );
				log.setStatisticYear( detail.getCycleYear() );
				log.setStatisticMonth( detail.getCycleMonth() );
				log.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( log, CheckPersistType.all);
				emc.commit();
				//统计考勤数据
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}else{
				logger.debug( debugger, "统计数据已存在：TOPUNIT_PER_DAY，"+ detail.getTopUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
		}catch(Exception e){
			logger.warn("系统在向数据库新增统计需求时发生异常" );
			logger.error(e);
		}
	}
}
