package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class WorkCooperateInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkCooperateInvalidException( Throwable e, String person ) {
		super("系统根据用户所选择的协助者身份为工作信息组织协助者信息时发生异常。Person:" + person, e );
	}
}
