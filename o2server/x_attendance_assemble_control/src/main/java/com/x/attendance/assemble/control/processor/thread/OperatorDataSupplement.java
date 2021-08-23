package com.x.attendance.assemble.control.processor.thread;

import com.x.attendance.assemble.control.processor.EntitySupplementData;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatorDataSupplement implements Runnable{

	private static  Logger logger = LoggerFactory.getLogger( OperatorDataSupplement.class );
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = null;
	private StatusSystemImportOpt statusSystemImportOpt = null;
	private AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = null;
	private AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = null;
	
	private EntitySupplementData entitySupplementData = null;
	private Boolean debugger = false;
	
	public OperatorDataSupplement( EntitySupplementData entitySupplementData, Boolean debugger ) {
		attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
		attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
		statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
		this.entitySupplementData = entitySupplementData ;
		this.debugger = debugger;
	}
	
	@Override
	public void run() {
		execute( entitySupplementData );
	}
	
	private void execute( EntitySupplementData entitySupplementData ) {
		
		statusSystemImportOpt.setProcessing( true );
		statusSystemImportOpt.setProcessing_supplement( true );
		
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		Boolean check = true;
		attendanceEmployeeConfig = entitySupplementData.getAttendanceEmployeeConfig();

		/*if( check ) {
			//补充和检验一下配置文件中的人员的所属组织和顶层组织信息是否正常
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.checkAttendanceEmployeeConfig( entitySupplementData.getAttendanceEmployeeConfig() );
			} catch (Exception e) {
				check = false;
				logger.warn( "系统检查需要考勤员工配置信息时发生异常." );
				logger.error(e);
			}
		}
		logger.debug( debugger, ">>>>>>>>>>系统正在核对并补充员工的考勤打卡信息：" + attendanceEmployeeConfig.getEmployeeName() );
		*/
		if( check ) {
			//根据组织信息、统计周期年、月从所有的顶层组织统计周期信息MAP中查询一个适合的统计周期，如果没有则为该组织新建一个新的配置
			try {
				logger.debug( debugger, ">>>>>>>>>>查询指定的统计周期："+attendanceEmployeeConfig.getTopUnitName() + ", " +
						attendanceEmployeeConfig.getUnitName() + ",  " +
						entitySupplementData.getCycleYear() + "-" +
						entitySupplementData.getCycleMonth() + "."
				);
				attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycle( 
						attendanceEmployeeConfig.getTopUnitName(),
						attendanceEmployeeConfig.getUnitName(), 
						entitySupplementData.getCycleYear(), 
						entitySupplementData.getCycleMonth(),
						entitySupplementData.getTopUnitAttendanceStatisticalCycleMap(),
						debugger
				);
			} catch (Exception e) {
				check = false;
				logger.warn( "系统在根据员工的顶层组织和组织名称查询指定的统计周期时发生异常."
						+ "TopUnit:" + attendanceEmployeeConfig.getTopUnitName()
						+ ", Unit:" + attendanceEmployeeConfig.getUnitName()
						+ ", CycleYear:" + entitySupplementData.getCycleYear()
						+ ", CycleMonth:" + entitySupplementData.getCycleMonth() );
				logger.error(e);
			}
		}
		
		if( check ) {
			if ( attendanceStatisticalCycle != null ) {
				//System.out.println( ">>>>>>>>>>获取到统计周期：cycle="+attendanceStatisticalCycle.getTopUnitName()+", unitName="+attendanceStatisticalCycle.getUnitName()+", CycleStartDate="+attendanceStatisticalCycle.getCycleStartDateString()+", cycleEndDate="+ attendanceStatisticalCycle.getCycleEndDateString() );
				try {
					logger.warn( "系统尝试核对和补充人员考勤数据，"
							+ "StartDate:" + attendanceStatisticalCycle.getCycleStartDate()
							+ ", EndDate:" + attendanceStatisticalCycle.getCycleEndDate() );
					attendanceDetailServiceAdv.dataSupplement( 
							attendanceStatisticalCycle.getCycleStartDate(),
							attendanceStatisticalCycle.getCycleEndDate(), 
							attendanceEmployeeConfig
					);
				} catch (Exception e) {
					logger.warn( "系统根据时间列表核对和补充员工打卡信息时发生异常.CycleStartDate:" + attendanceStatisticalCycle.getCycleStartDate() + ", CycleEndDate:" + attendanceStatisticalCycle.getCycleEndDate());
				}
			}else {
				logger.info( "未查询到考勤统计周期信息.......");
			}
		}
		
		statusSystemImportOpt.increaseProcess_supplement_count( 1 );
	}
}
