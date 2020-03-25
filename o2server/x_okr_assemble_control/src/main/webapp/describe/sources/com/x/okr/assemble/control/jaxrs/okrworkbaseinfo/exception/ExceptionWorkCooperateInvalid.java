package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import java.util.List;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkCooperateInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkCooperateInvalid( Throwable e, List<String> personList ) {
		super("系统根据用户所选择的协助者身份为工作信息组织协助者信息时发生异常。Person:" + personList.toString(), e );
	}
}
