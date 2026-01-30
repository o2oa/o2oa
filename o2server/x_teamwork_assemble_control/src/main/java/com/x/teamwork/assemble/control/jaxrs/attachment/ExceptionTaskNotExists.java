package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTaskNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTaskNotExists( String id ) {
		super("指定ID的工作任务信息记录不存在。ID：" + id );
	}
}
