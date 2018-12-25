package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class ReportCycleInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReportCycleInvalidException( String cycle ) {
		super("汇报周期选择不正确："+ cycle +"，无法继续保存工作信息!" );
	}
}
