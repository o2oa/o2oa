package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumCanNotCreateSectionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumCanNotCreateSectionException( String name ) {
		super("在论坛分区内不允许用户创建版块信息！Forum:" + name );
	}
}
