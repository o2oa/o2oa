package com.x.strategydeploy.assemble.control.strategy.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionStrategyDeployIdEmpty extends PromptException {

	private static final long serialVersionUID = 682260726729856957L;

	public ExceptionStrategyDeployIdEmpty() {
		super("战略部署 ID 为空。");
	}
}
