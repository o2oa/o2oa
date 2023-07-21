package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionArchiveAttendanceDetail extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionArchiveAttendanceDetail.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if ( StringUtils.isNotEmpty( id )) {
				try {
					attendanceDetailServiceAdv.archive(id);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "系统归档员工打卡信息时发生异常！ID:" + id);
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			} else {
				try {
					attendanceDetailServiceAdv.archiveAll();
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "系统归档员工打卡信息时发生异常！ID:" + id);
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
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