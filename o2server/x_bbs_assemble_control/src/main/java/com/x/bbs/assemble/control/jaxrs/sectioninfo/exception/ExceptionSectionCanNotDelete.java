package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionCanNotDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionCanNotDelete( String id ) {
		super("版块中仍存在子版块或者主题，无法继续进行删除操作！ID=" + id);
	}
}
