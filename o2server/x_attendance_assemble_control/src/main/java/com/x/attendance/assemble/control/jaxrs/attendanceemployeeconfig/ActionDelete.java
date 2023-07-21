package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionConfigIdEmpty();
				result.error( exception );
			}
		}		
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get( id );
				if( attendanceEmployeeConfig == null ){
					check = false;
					Exception exception = new ExceptionConfigNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess( e, "系统根据ID查询指定的人员考勤配置信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceEmployeeConfigServiceAdv.delete( id );
				result.setData( new Wo( id ) );
				logger.info( "人员考勤配置数据保存成功！" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess( e, "系统根据ID删除人员考勤配置对象信息时发生异常.ID:" + id );
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