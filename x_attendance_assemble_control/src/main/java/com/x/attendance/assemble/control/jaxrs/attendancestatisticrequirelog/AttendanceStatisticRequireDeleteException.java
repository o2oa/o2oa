package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import com.x.base.core.exception.PromptException;

class AttendanceStatisticRequireDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceStatisticRequireDeleteException(Exception e, String id ) {
		super("系统删除统计周期信息对象时发生异常.ID:" + id, e );
	}
}
