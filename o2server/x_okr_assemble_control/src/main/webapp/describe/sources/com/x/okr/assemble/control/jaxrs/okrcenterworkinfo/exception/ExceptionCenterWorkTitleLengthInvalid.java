package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCenterWorkTitleLengthInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCenterWorkTitleLengthInvalid() {
		super("中心工作标题为空，无法继续进行保存操作。");
	}
}
