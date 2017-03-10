package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SubjectCountBySectionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectCountBySectionException( Throwable e, String id ) {
		super("系统在根据版块ID查询主题信息数量时发生异常.ID:" + id, e );
	}
}
