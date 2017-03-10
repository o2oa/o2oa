package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.exception.PromptException;

class GetCycleMapFromAllCyclesException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetCycleMapFromAllCyclesException( Throwable e ) {
		super("系统在查询并且组织所有的统计周期时发生异常.", e );
	}
}
