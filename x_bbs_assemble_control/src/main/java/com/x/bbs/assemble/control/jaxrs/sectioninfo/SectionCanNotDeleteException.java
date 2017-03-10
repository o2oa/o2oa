package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionCanNotDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionCanNotDeleteException( String id ) {
		super("版块中仍存在子版块或者主题，无法继续进行删除操作！ID=" + id);
	}
}
