package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowStForPersonInUnit extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionShowStForPersonInUnit.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name, String year, String month ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<StatisticPersonForMonth> statisticPersonForMonth_list = null;
		List<String> unitNameList = new ArrayList<String>();
		Boolean check = true;
		if ("(0)".equals(year) ) {
			year = null;
		}
		if ("(0)".equals(month) ) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new ExceptionQueryStatisticUnitNameEmpty();
				result.error( exception );
			}else{
				unitNameList.add( name );
			}
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listPersonForMonthByUnitYearAndMonth( unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e, 
						"系统根据组织名称列表，年份和月份查询个人统计数据信息ID列表时发生异常.Name:"+unitNameList+", Year:"+year+", Month:" + month
				);
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticPersonForMonth_list = attendanceStatisticServiceAdv.listPersonForMonth(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统根据ID列表查询个人每月统计数据信息列表时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticPersonForMonth_list != null ){
				try {
					wraps = Wo.copier.copy( statisticPersonForMonth_list );
					result.setData(wraps);
				} catch ( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "系统将所有查询到的个人每月统计信息对象转换为可以输出的信息时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends StatisticPersonForMonth  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<StatisticPersonForMonth, Wo> copier = 
				WrapCopierFactory.wo( StatisticPersonForMonth.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}