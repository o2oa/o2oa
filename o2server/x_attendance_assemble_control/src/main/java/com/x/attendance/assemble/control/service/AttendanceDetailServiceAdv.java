package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class AttendanceDetailServiceAdv {
	private DateOperation dateOperation = new DateOperation();
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailServiceAdv.class );
	private AttendanceDetailService attendanceDetailService = new AttendanceDetailService();
	private AttendanceDetailMobileService attendanceDetailMobileService = new AttendanceDetailMobileService();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();
	private AttendanceSettingService attendanceSettingService = new AttendanceSettingService();
//	protected AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
//	protected AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
//	protected AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	
	public AttendanceDetail get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceDetail> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByBatchName( String file_id ) throws Exception {
		if( file_id == null || file_id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listByBatchName( emc, file_id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据开始和结束日期以及员工信息补充缺失的打卡信息
	 * @param startDate
	 * @param endDate
	 * @param attendanceEmployeeConfig
	 * @throws Exception
	 */
	public void dataSupplement( Date startDate, Date endDate, AttendanceEmployeeConfig attendanceEmployeeConfig) throws Exception {
		DateOperation dateOperation = new DateOperation();
		List<String> dayList = dateOperation.listDateStringBetweenDate( startDate, endDate );
		if( dayList == null ){
			return ;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceDetailService.checkAndReplenish( emc, dayList, attendanceEmployeeConfig );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public String getMaxRecordDate() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getMaxRecordDate( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUserAttendanceDetailByYearAndMonth( String q_empName, String q_year, String q_month ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUserAttendanceDetailByYearAndMonth( emc, q_empName, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listDetailByCycleYearAndMonthWithOutStatus( String q_empName, String q_year, String q_month )  throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listDetailByCycleYearAndMonthWithOutStatus( emc, q_empName, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	//根据人员和打卡日期查找打卡记录  
	public List<String> listDetailByNameAndDate( String q_empName, String datestr )  throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listDetailByNameAndDate( emc, q_empName, datestr);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUserAttendanceDetailByCycleYearAndMonth( String q_empName, String cycleYear, String cycleMonth ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUserAttendanceDetailByCycleYearAndMonth( emc, q_empName, cycleYear, cycleMonth );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listTopUnitAttendanceDetailByYearAndMonth( List<String> topUnitNames, String q_year, String q_month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listTopUnitAttendanceDetailByYearAndMonth( emc, topUnitNames, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUnitAttendanceDetailByYearAndMonth( List<String> unitNames, String q_year, String q_month ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUnitAttendanceDetailByYearAndMonth( emc, unitNames, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> getAllAnalysenessDetails( String startDate, String endDate, String personName ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessDetails( emc, startDate, endDate, personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> getAllAnalysenessDetailsForce( String startDate, String endDate, String personName ,Boolean forceFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessDetailsForce( emc, startDate, endDate, personName ,forceFlag);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> getAllAnalysenessPersonNames( String startDate, String endDate ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessPersonNames( emc, startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> getAllAnalysenessPersonNamesForce( String startDate, String endDate ,Boolean forceFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessPersonNamesForce( emc, startDate, endDate , forceFlag);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void archive( String id ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceDetailService.archive( emc, id, datetime );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void archiveAll() throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		List<String> ids = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			ids = business.getAttendanceDetailFactory().listNonArchiveDetailInfoIds();
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					try{
						attendanceDetailService.archive( emc, id, datetime );
					}catch( Exception e ){
						logger.warn( "system archive attendance appeal info got an exception." );
						logger.error(e);
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public synchronized AttendanceDetail save(AttendanceDetail attendanceDetail) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.save( emc, attendanceDetail );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public synchronized AttendanceDetail saveSingle(AttendanceDetail attendanceDetail) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.saveSingle( emc, attendanceDetail );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetailMobile save(AttendanceDetailMobile attendanceDetailMobile) throws Exception {
		if( attendanceDetailMobile == null ){
			throw new Exception("attendanceDetailMobile is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return attendanceDetailService.save( emc, attendanceDetailMobile );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countAttendanceDetailMobileForPage( String empNo, String empName, String signDescription,
			String startDate, String endDate ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.countAttendanceDetailMobileForPage( emc, empNo, empName, signDescription,
					startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceDetailMobile> listAttendanceDetailMobileForPage(String empNo, String empName,
			String signDescription, String startDate, String endDate, Integer selectTotal) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.listAttendanceDetailMobileForPage( emc, empNo, empName, signDescription,
					startDate, endDate, selectTotal );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceDetailMobile> listAttendanceDetailMobile(String distinguishedName, String signDate) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.listAttendanceDetailMobile( emc, distinguishedName, signDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetailMobile getMobile(String id) throws Exception {
		if( StringUtils.isEmpty( id )){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据所有的移动打卡记录来组织一个完整的打卡记录
	 * @param distinguishedName
	 * @param recordDateString
	 * @throws Exception
	 */
	public void pushToDetail(String distinguishedName, String recordDateString, Boolean debugger ) throws Exception {
		//查询外勤打卡配置
		Boolean ATTENDANCE_FIELD = false;
		try (EntityManagerContainer em = EntityManagerContainerFactory.instance().create()) {
			AttendanceSetting attendanceSettingField = attendanceSettingService.getByCode(em,"ATTENDANCE_FIELD");
			if(attendanceSettingField !=null &&  StringUtils.equalsIgnoreCase(attendanceSettingField.getConfigValue(),"true")){
				ATTENDANCE_FIELD = true;
			}
		}catch ( Exception e0 ) {
			throw e0;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<AttendanceDetailMobile> mobileDetails = attendanceDetailMobileService.listAttendanceDetailMobileWithEmployee( emc, distinguishedName, recordDateString );
			if( ListTools.isNotEmpty( mobileDetails )) {
				AttendanceDetail detail_old = attendanceDetailService.listDetailWithEmployee( emc, distinguishedName, recordDateString );
				AttendanceDetail detail = null;

				AttendanceScheduleSetting scheduleSetting = attendanceScheduleSettingService.getAttendanceScheduleSettingWithPerson( distinguishedName, debugger );
				if( scheduleSetting == null ){
					throw new Exception("scheduleSetting is null, empName:" + distinguishedName );
				}
				for( AttendanceDetailMobile detailMobile : mobileDetails ) {
					if(detailMobile.getIsExternal()){
						ATTENDANCE_FIELD = true;
					}
				}
				//获取打卡策略：两次，三次还是四次
				//根据考勤打卡规则来判断启用何种规则来进行考勤结果分析
				if( 2 == scheduleSetting.getSignProxy() ){
					//2、一天三次打卡：打上班，下班两次卡外，中午休息时间也需要打一次卡，以确保员工在公司活动
					detail = new ComposeDetailWithMobileInSignProxy2().compose( mobileDetails, scheduleSetting, debugger);
				}else if( 3 == scheduleSetting.getSignProxy() ){
					//3、一天四次打卡：打上午上班，上午下班，下午上班，下午下班四次卡
					detail = new ComposeDetailWithMobileInSignProxy3().compose( mobileDetails, scheduleSetting, debugger);
				}else{
					//1、一天只打上下班两次卡
					detail = new ComposeDetailWithMobileInSignProxy1().compose( mobileDetails, scheduleSetting, debugger);
				}
				detail.setIsExternal(ATTENDANCE_FIELD);
				if( detail_old == null ) {
					detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
					detail.setRecordStatus(1);
					emc.beginTransaction( AttendanceDetail.class );
					emc.persist( detail , CheckPersistType.all );
					emc.commit();
					detail_old = detail;
				}else {
					emc.beginTransaction( AttendanceDetail.class );
					detail.copyTo( detail_old, JpaObject.FieldsUnmodify);
					detail_old.setRecordStatus(1);
					emc.check( detail_old , CheckPersistType.all );
					emc.commit();
				}
				/*可能引起多人同时打卡时commit error
				emc.beginTransaction( AttendanceDetailMobile.class );
				for( AttendanceDetailMobile detailMobile : mobileDetails ) {
					detailMobile.setRecordStatus(1);
					emc.check( detailMobile , CheckPersistType.all );
				}
				emc.commit();*/

				//分析保存好的考勤数据
				try {
					ThisApplication.detailAnalyseQueue.send( detail_old.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}


	/**
	 * 根据日期查询未签退的所有打卡记录ID
	 * @param dateString yyyy-mm-dd
	 * @return
	 */
	public List<String> listRecordWithDateAndNoOffDuty( String dateString ) throws Exception {
		if( StringUtils.isEmpty( dateString )){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listRecordWithDateAndNoOffDuty( emc, dateString );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 查询在指定截止日期前已经打过卡的人员
	 * @param deadline
	 * @param type:all#onDuty#offDuty#morningOffDuty#afternoonOnDuty
	 * @return
	 * @throws Exception
	 */
    public List<String> listSignedPersonsWithDeadLine( String deadline, String type ) throws Exception {
		if( StringUtils.isEmpty( deadline )){
			deadline = dateOperation.getNowDateTime();
		}
		if( StringUtils.isEmpty( type )){
			type = "all";
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listSignedPersonsWithDeadLine( emc, deadline, type );
		} catch ( Exception e ) {
			throw e;
		}
    }
}
