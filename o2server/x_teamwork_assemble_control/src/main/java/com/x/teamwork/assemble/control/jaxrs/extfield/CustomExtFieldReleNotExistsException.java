package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.exception.PromptException;

class CustomExtFieldReleNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CustomExtFieldReleNotExistsException( String id ) {
		super("指定ID的扩展属性关联信息不存在。ID:" + id );
	}
}
