package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.exception.PromptException;

public class CenterWorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkIdEmptyException() {
		super("id为空，无法继续进行查询操作。");
	}
}
