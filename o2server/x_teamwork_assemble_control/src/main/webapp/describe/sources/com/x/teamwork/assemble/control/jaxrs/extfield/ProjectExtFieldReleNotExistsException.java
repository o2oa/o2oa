package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class ProjectExtFieldReleNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectExtFieldReleNotExistsException( String id ) {
		super("指定ID的项目扩展属性关联信息不存在。ID:" + id );
	}
}
