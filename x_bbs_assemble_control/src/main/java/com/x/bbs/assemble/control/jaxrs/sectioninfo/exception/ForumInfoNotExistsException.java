package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumInfoNotExistsException( String id ) {
		super("指定ID的论坛分区不存在.ID:" + id );
	}
}
