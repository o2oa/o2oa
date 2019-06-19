package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

public class ExceptionResponseHeaderSet extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionResponseHeaderSet(Throwable e ) {
		super("系统下载文件时设置responseHeader时发生异常.", e);
	}
}
