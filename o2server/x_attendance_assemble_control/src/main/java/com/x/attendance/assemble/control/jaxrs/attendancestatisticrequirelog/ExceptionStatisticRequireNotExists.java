package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import com.x.base.core.project.exception.PromptException;

class ExceptionStatisticRequireNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionStatisticRequireNotExists( String id ) {
		super("指定ID的统计需求记录信息对象不存在.ID:" + id );
	}
}
