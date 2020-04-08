package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionLeaderOpinionSubmit extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionLeaderOpinionSubmit( Throwable e, String id ) {
		super("系统为工作汇报处理领导审批意见时发生异常。ID:" + id, e );
	}
}
