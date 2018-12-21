package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportPersonLinkIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportPersonLinkIdEmpty() {
		super("工作汇报处理人ID为空。" );
	}
}
