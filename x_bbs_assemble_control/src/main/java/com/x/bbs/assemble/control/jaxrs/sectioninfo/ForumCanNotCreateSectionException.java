package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class ForumCanNotCreateSectionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumCanNotCreateSectionException( String name ) {
		super("在论坛分区内不允许用户创建版块信息！Forum:" + name );
	}
}
