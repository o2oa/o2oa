package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionDeleteException( Throwable e, String id ) {
		super("根据指定ID删除版块信息时发生异常.ID:" + id, e );
	}
}
