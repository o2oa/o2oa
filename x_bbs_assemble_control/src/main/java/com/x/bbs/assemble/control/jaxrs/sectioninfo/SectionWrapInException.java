package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionWrapInException( Throwable e ) {
		super("将用户传入的信息转换为一个版块信息对象时发生异常。", e );
	}
}
