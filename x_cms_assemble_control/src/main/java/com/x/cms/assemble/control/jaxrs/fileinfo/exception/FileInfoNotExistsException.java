package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.exception.PromptException;

public class FileInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public FileInfoNotExistsException( String id ) {
		super("文件信息不存在，无法继续进行操作。Id:" + id );
	}
}
