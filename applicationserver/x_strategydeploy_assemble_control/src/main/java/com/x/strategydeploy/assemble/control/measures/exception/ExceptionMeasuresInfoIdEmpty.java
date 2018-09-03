package com.x.strategydeploy.assemble.control.measures.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMeasuresInfoIdEmpty extends PromptException {

	private static final long serialVersionUID = 916967440869203518L;

	public ExceptionMeasuresInfoIdEmpty() {
		super("举措 ID 为空。");
	}
}
