package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionProfileNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionProfileNotExists( String id ) {
		super("指定的汇报概要文件信息不存在.profileId:" + id );
	}
}
