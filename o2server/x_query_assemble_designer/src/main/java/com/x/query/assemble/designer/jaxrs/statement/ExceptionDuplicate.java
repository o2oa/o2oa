package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionDuplicate(String queryName, String fieldName, Object value) {
		super("与 {} 的查询配置标识冲突, 冲突字段名{}, 冲突值:{}.", queryName, fieldName, value);
	}
}
