package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListAll.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceSetting> attendanceSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceSettingList = attendanceSettingServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "系统查询所有考勤系统设置信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wraps = Wo.copier.copy( attendanceSettingList );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends AttendanceSetting  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<AttendanceSetting, Wo> copier = 
				WrapCopierFactory.wo( AttendanceSetting.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}