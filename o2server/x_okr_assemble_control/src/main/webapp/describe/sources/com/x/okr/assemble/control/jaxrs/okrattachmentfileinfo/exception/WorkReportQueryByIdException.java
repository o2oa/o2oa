package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class WorkReportQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的工作汇报信息时发生异常。ID：" + id, e );
	}
}
