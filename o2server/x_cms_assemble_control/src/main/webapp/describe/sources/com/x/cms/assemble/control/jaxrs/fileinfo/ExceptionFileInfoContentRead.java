package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFileInfoContentRead extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionFileInfoContentRead(Throwable e ) {
		super("系统读取文件输出时发生异常.", e);
	}
}
