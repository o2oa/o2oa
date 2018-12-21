package com.x.attendance.assemble.control.processor.thread;

import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.processor.EntityAnalyseData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceSelfHolidayServiceAdv;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatorDataAnalyse implements Runnable{

	private static  Logger logger = LoggerFactory.getLogger( OperatorDataAnalyse.class );
	private AttendanceSelfHolidayServiceAdv attendanceSelfHolidayServiceAdv = null;
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = null;
	private AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = null;
	private AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = null;
	private StatusSystemImportOpt statusSystemImportOpt = null;
	private EntityAnalyseData entityAnalyseData = null;
	
	private Boolean debugger = false;
	
	public OperatorDataAnalyse( EntityAnalyseData entityAnalyseData, Boolean debugger ) {
		attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
		attendanceSelfHolidayServiceAdv = new AttendanceSelfHolidayServiceAdv();
		attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
		statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
		this.entityAnalyseData = entityAnalyseData ;
		this.debugger = debugger;
	}

	@Override
	public void run() {
		execute( entityAnalyseData );
	}
	
	private void execute( EntityAnalyseData entityAnalyseData ) {
		List<String> ids_temp = null;
		List<String> detail_ids = entityAnalyseData.getDetailIds();
		List<AttendanceSelfHoliday> selfHolidays = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = entityAnalyseData.getAttendanceWorkDayConfigList();
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = entityAnalyseData.getTopUnitAttendanceStatisticalCycleMap();
		AttendanceDetail detail = null;
		
		statusSystemImportOpt.setProcessing( true );
		statusSystemImportOpt.setProcessing_analysis( true );
		
		if ( detail_ids != null && !detail_ids.isEmpty() ) {
			try{
				ids_temp = attendanceSelfHolidayServiceAdv.getByPersonName( entityAnalyseData.getPersonName() );
				if( ids_temp != null && !ids_temp.isEmpty() ) {
					selfHolidays = attendanceSelfHolidayServiceAdv.list( ids_temp );
				}
			}catch( Exception e ){
				logger.warn( "system list attendance self holiday info ids with employee name got an exception.empname:" + entityAnalyseData.getPersonName() );
				logger.error(e);
			}
			
			try{
				attendanceScheduleSetting =  attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( entityAnalyseData.getPersonName(), debugger );
			}catch( Exception e ){
				logger.warn( "system get unit schedule setting for employee with unit names got an exception." + entityAnalyseData.getPersonName() );
				logger.error(e);
			}
			
			for ( String id: detail_ids ) {
				try {
					detail = attendanceDetailServiceAdv.get( id );
					if ( detail != null ) {
						attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceScheduleSetting, selfHolidays, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );
						statusSystemImportOpt.increaseProcess_analysis_count(1);
					} else {
						statusSystemImportOpt.increaseProcess_analysis_error(1);
						logger.warn( "attendance detail not exists.id:" + id );
					}
				} catch (Exception e) {
					statusSystemImportOpt.increaseProcess_analysis_error(1);
					logger.warn( "attendance detail analyse got an exception.id:" + id );
					logger.error( e );
				}
			}
		} else {
			logger.info("no attendance detail need to analyse.personName:" + entityAnalyseData.getPersonName() );
		}
		logger.info("attendance detail analyse completed.person:" + detail.getEmpName() + ", count:" + detail_ids.size() );
	}
}
