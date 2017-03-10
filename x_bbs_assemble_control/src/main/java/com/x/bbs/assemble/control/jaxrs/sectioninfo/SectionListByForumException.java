package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionListByForumException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionListByForumException( Throwable e, String id ) {
		super("根据指定论坛分区ID查询所有主版块信息时发生异常.Forum:" + id, e );
	}
}
