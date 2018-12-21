package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumCanNotCreateSection extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumCanNotCreateSection( String name ) {
		super("在论坛分区内不允许用户创建版块信息！Forum:" + name );
	}
}
