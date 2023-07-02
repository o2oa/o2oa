package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionStatisticCycleNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionStatisticCycleNotExists( String id ) {
		super("指定ID的统计周期信息对象不存在.ID:" + id );
	}
}
