package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoQueryByIdException( Throwable e, String id ) {
		super("根据信息Id查询信息对象时发生异常。ID:" + id, e );
	}
}
