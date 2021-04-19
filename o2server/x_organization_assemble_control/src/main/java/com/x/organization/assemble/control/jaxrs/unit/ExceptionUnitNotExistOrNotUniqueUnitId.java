package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUnitNotExistOrNotUniqueUnitId extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionUnitNotExistOrNotUniqueUnitId(String flag) {
		super("组织:{}, 不存在，或者组织uuid不唯一.", flag);
	}
}
