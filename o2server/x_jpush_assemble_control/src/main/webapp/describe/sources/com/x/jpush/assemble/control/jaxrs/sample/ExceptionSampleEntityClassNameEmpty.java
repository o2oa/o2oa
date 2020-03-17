package com.x.jpush.assemble.control.jaxrs.sample;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSampleEntityClassNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSampleEntityClassNameEmpty() {
		super("保存操作传入的参数name为空，无法进行保存操作.");
	}
}
