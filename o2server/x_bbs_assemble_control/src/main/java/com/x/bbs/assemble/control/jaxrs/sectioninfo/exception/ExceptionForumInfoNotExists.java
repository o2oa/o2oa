package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumInfoNotExists( String id ) {
		super("指定ID的论坛分区不存在.ID:" + id );
	}
}
