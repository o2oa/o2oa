package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( StringUtils.isNotEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionSettingIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.get( id );
				if( attendanceSetting == null ){			
					check = false;
					Exception exception = new ExceptionSettingNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "系统根据ID查询指定考勤系统配置信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceSettingServiceAdv.delete( id );
				result.setData( new Wo( id ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess( e, "根据ID删除考勤系统配置信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}