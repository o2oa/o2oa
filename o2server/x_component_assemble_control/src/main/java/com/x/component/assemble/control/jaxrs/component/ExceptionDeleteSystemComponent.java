package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeleteSystemComponent extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDeleteSystemComponent() {
		super("不能删除系统组件.");
	}
}
