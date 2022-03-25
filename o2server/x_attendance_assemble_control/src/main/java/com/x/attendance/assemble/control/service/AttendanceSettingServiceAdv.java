package com.x.attendance.assemble.control.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

public class AttendanceSettingServiceAdv {

	private AttendanceSettingService attendanceSettingService = new AttendanceSettingService();
	private DateOperation dateOperation = new DateOperation();

	public List<AttendanceSetting> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSettingService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceSetting get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceSettingService.get( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceSetting save( AttendanceSetting attendanceSetting ) throws Exception {
		AttendanceSetting attendanceSetting_old = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting_old = emc.find( attendanceSetting.getId(), AttendanceSetting.class );
			if( attendanceSetting_old != null ){
				return attendanceSettingService.update( emc, attendanceSetting );	
			}else{
				return attendanceSettingService.create( emc, attendanceSetting );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSettingService.delete( emc, id );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public AttendanceSetting getByCode( String code ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return attendanceSettingService.getByCode( emc, code );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public String getValueByCode( String code ) throws Exception {
		AttendanceSetting attendanceSetting = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceSetting = attendanceSettingService.getByCode( emc, code );
			if( attendanceSetting != null ) {
				return attendanceSetting.getConfigValue();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}

	/**
	 * 根据提交的内容以及打卡信息组织一个新的申诉信息对象
	 * @param attendanceDetail
	 * @param reason
	 * @param appealReason
	 * @param selfHolidayType
	 * @param address
	 * @param startTime
	 * @param endTime
	 * @param description
	 * @return
	 */
	public AttendanceAppealInfo composeAppealInfoWithDetailInfo( AttendanceDetail attendanceDetail, 
			String reason, String appealReason, String selfHolidayType, String address, String startTime, String endTime, String description ) {
		DateOperation dateOperation = new DateOperation();
		// 申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过!
		AttendanceAppealInfo attendanceAppealInfo = new AttendanceAppealInfo();
		attendanceAppealInfo.setId( attendanceDetail.getId() );
		attendanceAppealInfo.setDetailId( attendanceDetail.getId() );
		attendanceAppealInfo.setRecordDate(attendanceDetail.getRecordDate());
		attendanceAppealInfo.setRecordDateString(attendanceDetail.getRecordDateString());
		attendanceAppealInfo.setYearString(attendanceDetail.getCycleYear());
		attendanceAppealInfo.setMonthString(attendanceDetail.getCycleMonth());
		attendanceAppealInfo.setEmpName( attendanceDetail.getEmpName() );
		attendanceAppealInfo.setUnitName( attendanceDetail.getUnitName() );
		attendanceAppealInfo.setTopUnitName( attendanceDetail.getTopUnitName() );
		attendanceAppealInfo.setAppealDescription( description );
		attendanceAppealInfo.setAppealReason( appealReason );
		attendanceAppealInfo.setReason( reason );
		attendanceAppealInfo.setAddress( address );
		attendanceAppealInfo.setSelfHolidayType( selfHolidayType );
		attendanceAppealInfo.setAppealDateString( dateOperation.getNowDateTime() );
		attendanceAppealInfo.setStartTime( startTime );
		attendanceAppealInfo.setEndTime( endTime );
		return attendanceAppealInfo;
	}
	/**
	 * 判断是否周末
	 * @param recordDate
	 * @return
	 */
	public boolean isWeekend( Date recordDate ) throws Exception {
		boolean iflag = false;
		Calendar cal = Calendar.getInstance();
		cal.setTime( recordDate );


		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			AttendanceSetting attendanceSetting = this.getByCode("ATTENDANCE_WEEKEND");
			String configValue =  attendanceSetting.getConfigValue();
			if(attendanceSetting != null){
				if( (configValue.indexOf("周六")>-1 && cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY) || (configValue.indexOf("周日")>-1 && cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)){
					iflag = true;
				}
				if(configValue.indexOf("无")>-1 && (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) ){
					iflag = true;
				}
			}

		}
		return iflag;
	}
}
