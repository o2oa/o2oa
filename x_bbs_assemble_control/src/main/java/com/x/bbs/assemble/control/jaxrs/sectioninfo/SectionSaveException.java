package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionSaveException( Throwable e ) {
		super("保存版块信息时发生异常.", e );
	}
}
