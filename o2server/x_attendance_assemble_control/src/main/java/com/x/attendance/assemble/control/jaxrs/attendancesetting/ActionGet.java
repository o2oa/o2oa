package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGet extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSettingIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "系统根据ID查询指定考勤系统配置信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = Wo.copier.copy( attendanceSetting );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "将所有查询到的考勤配置信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceSetting  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		
		public static WrapCopier<AttendanceSetting, Wo> copier = 
				WrapCopierFactory.wo( AttendanceSetting.class, Wo.class, null,JpaObject.FieldsInvisible);
	}

}