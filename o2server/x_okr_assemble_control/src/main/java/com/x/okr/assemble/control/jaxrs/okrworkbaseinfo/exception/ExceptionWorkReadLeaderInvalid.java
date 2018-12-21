package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;


import java.util.List;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReadLeaderInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReadLeaderInvalid( Throwable e,  List<String>  personList ) {
		super("系统根据用户所选择的阅知者身份为工作信息组织阅知者信息时发生异常。Person:" + personList.toString() , e );
	}
}
