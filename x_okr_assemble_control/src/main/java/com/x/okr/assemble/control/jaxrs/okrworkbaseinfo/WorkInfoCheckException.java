package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkInfoCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkInfoCheckException( Throwable e, String id ) {
		super("系统校验需要部署的工作信息合法性时发生异常! WorkId:" + id, e );
	}
}
