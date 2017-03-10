package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionListByParentException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionListByParentException( Throwable e, String id ) {
		super("根据指定主版ID查询子版块信息时发生异常.MainId:" + id, e );
	}
}
