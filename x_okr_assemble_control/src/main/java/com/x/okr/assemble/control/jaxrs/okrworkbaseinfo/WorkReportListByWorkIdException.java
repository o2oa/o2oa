package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkReportListByWorkIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReportListByWorkIdException( Throwable e, String id ) {
		super("系统根据工作ID查询所有工作汇报ID列表发生异常. ID：" + id, e );
	}
}
