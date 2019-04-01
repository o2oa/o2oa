package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyDraftData extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEmptyDraftData(String flag) {
		super("表: {}, 的设计结构为空.", flag);
	}
}
