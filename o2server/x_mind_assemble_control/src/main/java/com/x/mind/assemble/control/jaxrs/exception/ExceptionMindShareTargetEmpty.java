package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindShareTargetEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindShareTargetEmpty() {
		super("脑图信息分享目标为空，无法进行文件分享！");
	}
}
