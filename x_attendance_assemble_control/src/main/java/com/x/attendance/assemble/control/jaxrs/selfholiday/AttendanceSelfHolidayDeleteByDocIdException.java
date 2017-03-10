package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.exception.PromptException;

class AttendanceSelfHolidayDeleteByDocIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSelfHolidayDeleteByDocIdException( Throwable e, String docId ) {
		super("系统在根据流程ID删除同步的员工请假记录信息时发生异常.DocId：" + docId, e );
	}
}
