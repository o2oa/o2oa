package com.x.report.assemble.control.jaxrs.workinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSaveWorkInfo extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSaveWorkInfo(Throwable e ) {
		super("系统在保存汇报工作信息内容时发生异常。", e );
	}
}
