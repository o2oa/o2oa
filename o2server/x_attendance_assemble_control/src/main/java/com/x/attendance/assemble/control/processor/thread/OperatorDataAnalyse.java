package com.x.attendance.assemble.control.processor.thread;

import java.util.List;

import com.x.attendance.assemble.control.processor.EntityAnalyseData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatorDataAnalyse implements Runnable{

	private static  Logger logger = LoggerFactory.getLogger( OperatorDataAnalyse.class );
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = null;
	private AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = null;
	private StatusSystemImportOpt statusSystemImportOpt = null;
	private EntityAnalyseData entityAnalyseData = null;
	
	private Boolean debugger = false;
	
	public OperatorDataAnalyse( EntityAnalyseData entityAnalyseData, Boolean debugger ) {
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
		List<String> detail_ids = entityAnalyseData.getDetailIds();
		AttendanceDetail detail = null;
		
		statusSystemImportOpt.setProcessing( true );
		statusSystemImportOpt.setProcessing_analysis( true );
		
		if ( detail_ids != null && !detail_ids.isEmpty() ) {

			for ( String id: detail_ids ) {
				try {
					detail = attendanceDetailServiceAdv.get( id );
					if ( detail != null ) {
						attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, debugger );
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
