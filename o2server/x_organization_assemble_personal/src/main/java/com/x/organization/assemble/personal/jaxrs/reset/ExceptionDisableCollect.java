package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionDisableCollect extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	public ExceptionDisableCollect() {
		super("系统没有启用节点连接.");
	}
}
