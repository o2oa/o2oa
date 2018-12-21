package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class FileInfoContentReadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public FileInfoContentReadException(Throwable e ) {
		super("系统读取文件输出时发生异常.", e);
	}
}
