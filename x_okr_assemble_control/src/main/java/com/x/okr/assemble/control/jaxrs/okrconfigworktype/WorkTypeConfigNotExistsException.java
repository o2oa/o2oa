package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import com.x.base.core.exception.PromptException;

class WorkTypeConfigNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkTypeConfigNotExistsException( String id ) {
		super("指定ID的工作类别配置不存在。ID:" + id );
	}
}
