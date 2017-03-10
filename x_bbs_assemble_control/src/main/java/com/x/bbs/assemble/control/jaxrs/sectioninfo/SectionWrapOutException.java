package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionWrapOutException( Throwable e ) {
		super("系统在转换所有BBS版块信息为输出对象时发生异常.", e );
	}
}
