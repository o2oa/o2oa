package com.x.attendance.assemble.control.processor.sender;

import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.processor.EntityAnalyseData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 将所有需要分析的数据发送到数据分析队列，由相应的处理器进行数据分析操作
 * 根据员工姓名进行分组，避免多次操作员工排班信息
 * 
 * @author O2LEE
 *
 */
public class SenderForAnalyseData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForAnalyseData.class );
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	
	public void execute(List<String> personNames, String startDate, String endDate, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) {
		
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		List<String> ids = null;
		
		for ( String personName : personNames ) {
			try {
				ids = attendanceDetailServiceAdv.getAllAnalysenessDetails( startDate, endDate, personName );
				if( ids != null && !ids.isEmpty() ) {
					statusSystemImportOpt.setProcessing( true );
					statusSystemImportOpt.setProcessing_analysis( true );
					statusSystemImportOpt.increaseProcess_analysis_total( ids.size() );
					DataProcessThreadFactory.getInstance().submit( new EntityAnalyseData( "analyse", personName, ids, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap ), debugger );
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
	public void executeForce(List<String> personNames, String startDate, String endDate,Boolean forceFlag, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) {
			
			StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
			List<String> ids = null;
			 
			for ( String personName : personNames ) {
				try {
					ids = attendanceDetailServiceAdv.getAllAnalysenessDetailsForce( startDate, endDate, personName ,forceFlag);
					if( ids != null && !ids.isEmpty() ) {
						statusSystemImportOpt.setProcessing( true );
						statusSystemImportOpt.setProcessing_analysis( true );
						statusSystemImportOpt.increaseProcess_analysis_total( ids.size() );
						DataProcessThreadFactory.getInstance().submit( new EntityAnalyseData( "analyse", personName, ids, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap ), debugger );
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		
	}
