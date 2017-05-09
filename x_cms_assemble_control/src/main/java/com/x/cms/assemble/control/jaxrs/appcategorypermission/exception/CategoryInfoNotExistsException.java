package com.x.cms.assemble.control.jaxrs.appcategorypermission.exception;

import com.x.base.core.exception.PromptException;

public class CategoryInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CategoryInfoNotExistsException( String id ) {
		super("指定的分类信息不存在。ID:" + id );
	}
}
