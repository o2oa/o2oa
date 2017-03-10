package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class PublicSectionFilterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PublicSectionFilterException( Throwable e ) {
		super("根据指定条件查询所有公开的分区信息时发生异常.", e );
	}
}
