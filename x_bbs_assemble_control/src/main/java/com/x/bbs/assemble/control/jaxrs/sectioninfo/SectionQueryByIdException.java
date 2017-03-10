package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionQueryByIdException( Throwable e, String id ) {
		super("根据指定ID查询版块信息时发生异常.ID:" + id, e );
	}
}
