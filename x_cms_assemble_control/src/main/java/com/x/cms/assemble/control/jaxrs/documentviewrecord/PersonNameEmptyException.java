package com.x.cms.assemble.control.jaxrs.documentviewrecord;

import com.x.base.core.exception.PromptException;

class PersonNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonNameEmptyException() {
		super( "访问人员姓名为空，无法查询文档访问记录信息列表。" );
	}
}
