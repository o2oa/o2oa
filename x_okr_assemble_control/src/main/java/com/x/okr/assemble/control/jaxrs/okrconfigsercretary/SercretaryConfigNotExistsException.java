package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.exception.PromptException;

class SercretaryConfigNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SercretaryConfigNotExistsException( String id ) {
		super("指定ID的领导秘书配置信息不存在。ID:"+id );
	}
}
