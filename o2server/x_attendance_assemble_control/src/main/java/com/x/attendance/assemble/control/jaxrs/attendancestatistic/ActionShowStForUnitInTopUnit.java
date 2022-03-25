package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowStForUnitInTopUnit extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionShowStForUnitInTopUnit.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name, String year, String month ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<StatisticUnitForMonth> statisticUnitForMonth_list = null;
		List<String> unitNames = new ArrayList<String>();
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new ExceptionTopUnitNameEmpty();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				//根据顶层组织递归查询下级顶层组织经及顶层组织的顶级组织
				unitNames = getTopUnitNameList( name, unitNames, effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess( e, 
						"根据顶层组织递归查询下级顶层组织经及顶层组织的顶级组织时发生异常！"
						+ "Name:" + name
						+ ", Units:" + unitNames );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( unitNames == null ){
				unitNames = new ArrayList<>();
			}
			unitNames.add( name );
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listUnitForMonthByUnitYearAndMonth( unitNames, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e, 
					"系统根据组织名称列表，年份和月份查询组织统计数据信息ID列表时发生异常.Name:"+unitNames+", Year:"+year+", Month:" + month
				);
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticUnitForMonth_list = attendanceStatisticServiceAdv.listUnitForMonth( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统根据ID列表查询组织每月统计数据信息列表时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticUnitForMonth_list != null ){
				try {
					wraps = Wo.copier.copy( statisticUnitForMonth_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统将所有查询到的组织每月统计信息对象转换为可以输出的信息时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends StatisticUnitForMonth  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<StatisticUnitForMonth, Wo> copier = 
				WrapCopierFactory.wo( StatisticUnitForMonth.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}