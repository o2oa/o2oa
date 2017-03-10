package com.x.okr.assemble.control.servlet.workimport;

import com.x.base.core.exception.PromptException;

class NoDataException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NoDataException() {
		super("未获取到任何需要保存的数据.");
	}
}
