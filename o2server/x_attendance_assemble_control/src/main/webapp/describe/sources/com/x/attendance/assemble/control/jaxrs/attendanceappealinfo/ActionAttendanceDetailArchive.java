package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.exception.ExceptionAttendanceAppealProcess;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAttendanceDetailArchive extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAttendanceDetailArchive.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		if ( StringUtils.isNotEmpty( id )) { // 归档指定的考勤申诉记录
			try {
				attendanceAppealInfoServiceAdv.archive(id);
				result.setData(new Wo(id));
			} catch (Exception e) {
				result.error(e);
				Exception exception = new ExceptionAttendanceAppealProcess(e, "归档考勤打卡申诉信息时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		} else { // 归档所有的考勤申诉记录
			try {
				attendanceAppealInfoServiceAdv.archiveAll();
			} catch (Exception e) {
				Exception exception = new ExceptionAttendanceAppealProcess(e, "归档全部考勤申诉信息数据时发生异常");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
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