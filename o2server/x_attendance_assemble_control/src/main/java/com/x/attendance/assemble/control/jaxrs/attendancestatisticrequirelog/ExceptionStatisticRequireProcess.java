package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import com.x.base.core.project.exception.PromptException;

class ExceptionStatisticRequireProcess extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionStatisticRequireProcess( Throwable e, String message ) {
		super("用户在进行考勤统计处理时发生异常！message:" + message, e );
	}
}
