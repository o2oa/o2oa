package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportNotExists( String id ) {
		super("指定id的工作汇报信息不存在，无法继续进行操作。ID:" + id );
	}
}
