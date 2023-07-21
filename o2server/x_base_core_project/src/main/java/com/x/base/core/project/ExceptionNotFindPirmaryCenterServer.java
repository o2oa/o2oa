package com.x.base.core.project;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNotFindPirmaryCenterServer extends PromptException {

	private static final long serialVersionUID = -4834776351837354462L;

	public ExceptionNotFindPirmaryCenterServer(String node) {
		super("can not find pirmary center server node: {}.", node);
	}

}
