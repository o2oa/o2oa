package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportIdEmptyException() {
		super("id为空，无法继续进行查询操作。");
	}
}
