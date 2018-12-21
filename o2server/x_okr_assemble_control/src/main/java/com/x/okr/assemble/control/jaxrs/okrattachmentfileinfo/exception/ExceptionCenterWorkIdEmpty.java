package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCenterWorkIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCenterWorkIdEmpty() {
		super("id为空，无法继续进行查询操作。");
	}
}
