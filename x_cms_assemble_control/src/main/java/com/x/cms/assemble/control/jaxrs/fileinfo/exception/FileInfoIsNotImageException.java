package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.exception.PromptException;

public class FileInfoIsNotImageException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public FileInfoIsNotImageException( String id ) {
		super("文件信息不为图片格式，无法进行图片转换。Id:" + id );
	}
}
