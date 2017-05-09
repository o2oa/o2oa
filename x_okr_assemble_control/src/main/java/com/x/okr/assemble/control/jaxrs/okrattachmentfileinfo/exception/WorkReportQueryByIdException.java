package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportQueryByIdException( Throwable e, String id ) {
		super("系统根据id查询工作汇报信息时发生异常。ID:" + id, e );
	}
}
