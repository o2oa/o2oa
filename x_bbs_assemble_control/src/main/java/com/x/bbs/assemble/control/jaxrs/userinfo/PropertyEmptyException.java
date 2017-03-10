package com.x.bbs.assemble.control.jaxrs.userinfo;

import com.x.base.core.exception.PromptException;

class PropertyEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PropertyEmptyException( String name ) {
		super("传入的数据不完整，无法保存主题信息.属性:" + name );
	}
}
