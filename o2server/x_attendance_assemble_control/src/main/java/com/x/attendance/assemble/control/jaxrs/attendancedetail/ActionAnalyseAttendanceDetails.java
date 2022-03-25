package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.sender.SenderForAnalyseData;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAnalyseAttendanceDetails extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAnalyseAttendanceDetails.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startDate, String endDate ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<String> personNames = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		StatusSystemImportOpt statusSystemImportOpt = StatusSystemImportOpt.getInstance();
		Boolean check = true;

		if( statusSystemImportOpt.getProcessing() ) {
			check = false;
			Exception exception = new ExceptionAttendanceDetailProcess( "考勤数据处理器正在处理数据中，请稍候再试......" );
			result.error(exception);
		}
		
		if (check) {
			try {
				personNames = attendanceDetailServiceAdv.getAllAnalysenessPersonNames( startDate, endDate );
				if( personNames == null || personNames.isEmpty() ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess( "暂时未查询到需要分析的打卡数据." + "开始日期:" + startDate + ", 结束日期:" + endDate );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统根据开始时间和结束时间查询需要分析的员工姓名列表时发生异常." + "开始日期:" + startDate + ", 结束日期:" + endDate );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if ( check ) {
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在根据ID列表查询工作节假日配置信息列表时发生异常！" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if ( check ) {
			try {// 查询所有的周期配置，组织成Map
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询并且组织所有的统计周期时发生异常." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if ( check ) {
			new SenderForAnalyseData().execute( personNames, startDate, endDate, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, effectivePerson.getDebugger() );
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}