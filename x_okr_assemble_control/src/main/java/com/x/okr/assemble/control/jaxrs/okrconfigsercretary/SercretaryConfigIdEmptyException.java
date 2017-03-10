package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.exception.PromptException;

class SercretaryConfigIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SercretaryConfigIdEmptyException() {
		super("id为空，无法继续进行查询操作。");
	}
}
