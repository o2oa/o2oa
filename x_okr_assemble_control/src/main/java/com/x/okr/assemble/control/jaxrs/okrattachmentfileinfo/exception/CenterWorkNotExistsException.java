package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.exception.PromptException;

public class CenterWorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkNotExistsException( String id ) {
		super("指定id的中心工作信息不存在，无法继续进行操作。ID:" + id );
	}
}
