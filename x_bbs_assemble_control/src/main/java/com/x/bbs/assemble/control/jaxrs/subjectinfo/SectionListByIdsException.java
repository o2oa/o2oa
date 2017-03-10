package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SectionListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionListByIdsException( Throwable e ) {
		super("根据指定ID列表查询版块信息时发生异常.", e );
	}
}
