package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionAttendanceDetailProcess;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionCycleMonthEmpty;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionCycleYearEmpty;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.sender.SenderForSupplementData;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionCheckWithPersonByCycle extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionCheckWithPersonByCycle.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String cycleYear, String cycleMonth ) throws Exception {
		logger.debug( effectivePerson, ">>>>>>>>>>系统尝试对统计周期[" + cycleYear + "-" + cycleMonth + "]的打卡数据核对......");
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		Boolean check = true;
		
		if( statusSystemImportOpt.getProcessing() ) {
			check = false;
			Exception exception = new ExceptionAttendanceDetailProcess( "考勤数据处理器正在处理数据中，请稍候再试......" );
			result.error(exception);
		}
		
		if (check) {
			//查询系统是否正在进行数据操作
			if( statusSystemImportOpt.getProcessing() ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( "系统正在进行数据处理，请稍候再进行数据操作." );
				result.error( exception );
			}else {
				statusSystemImportOpt.setProcessing( true );
			}
		}
		
		if (check) {
			if ( cycleYear == null || cycleYear.isEmpty() ) {
				check = false;
				Exception exception = new ExceptionCycleYearEmpty();
				result.error(exception);
			}
		}
		
		if (check) {
			if ( cycleMonth == null || cycleMonth.isEmpty() ) {
				check = false;
				Exception exception = new ExceptionCycleMonthEmpty();
				result.error(exception);
			}
		}
		
		if ( check ) {
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
				if( attendanceEmployeeConfigList == null || attendanceEmployeeConfigList.isEmpty() ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess( "系统未获取到需要考勤的人员配置，尚不需要补录任何信息." );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询需要考勤的人员配置列表时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null );
			}
		}
		
		if (check) {
			try {// 查询所有的统计周期配置，组织成Map
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询并且组织所有的统计周期时发生异常." );
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			new SenderForSupplementData().execute( attendanceEmployeeConfigList, topUnitAttendanceStatisticalCycleMap, cycleYear, cycleMonth, effectivePerson.getDebugger() );
		}
		
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}