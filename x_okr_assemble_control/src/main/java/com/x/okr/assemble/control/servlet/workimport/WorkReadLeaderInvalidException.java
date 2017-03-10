package com.x.okr.assemble.control.servlet.workimport;

import com.x.base.core.exception.PromptException;

class WorkReadLeaderInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkReadLeaderInvalidException( Throwable e, String person ) {
		super("系统根据用户所选择的阅知者身份为工作信息组织阅知者信息时发生异常。Person:" + person, e );
	}
}
