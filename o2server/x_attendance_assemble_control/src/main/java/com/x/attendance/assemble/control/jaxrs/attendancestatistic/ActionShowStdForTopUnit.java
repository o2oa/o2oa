package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.StatisticTopUnitForDay;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowStdForTopUnit extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionShowStdForTopUnit.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name, String year, String month ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<StatisticTopUnitForDay> statisticTopUnitForDay_list = null;
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
				ids = attendanceStatisticServiceAdv.listStatisticTopUnitForDay_ByNameYearAndMonth( name, year, month );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess( e, 
					"系统根据顶层组织名称列表，年份和月份查询顶层组织每日统计数据信息ID列表时发生异常.Name:" +name+ ", Year:"+year+", Month:" + month
				);
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticTopUnitForDay_list = attendanceStatisticServiceAdv.listTopUnitForDay( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统根据ID列表查询顶层组织每日统计数据信息列表时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticTopUnitForDay_list != null ){
				try {
					wraps = Wo.copier.copy( statisticTopUnitForDay_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统将所有查询到的顶层组织每日统计信息对象转换为可以输出的信息时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null );
				}
			}
		}
		return result;
	}

	public static class Wo extends StatisticTopUnitForDay  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<StatisticTopUnitForDay, Wo> copier = 
				WrapCopierFactory.wo( StatisticTopUnitForDay.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}