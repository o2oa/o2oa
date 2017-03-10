package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionListAllException( Throwable e ) {
		super("查询所有的版块信息时发生异常.", e );
	}
}
