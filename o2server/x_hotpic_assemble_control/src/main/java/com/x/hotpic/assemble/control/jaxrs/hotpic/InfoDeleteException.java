package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoDeleteException( Throwable e, String id ) {
		super("根据信息Id删除信息对象时发生异常。ID:" + id, e );
	}
	
	InfoDeleteException( Throwable e, String application, String id ) {
		super("根据信息Id删除信息对象时发生异常。Application:"+application+", ID:" + id, e );
	}
}
