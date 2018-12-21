package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoSaveException( Throwable e  ) {
		super("系统在保存热图信息时发生异常。" , e );
	}
}
