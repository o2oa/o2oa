package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDownloadLogSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionDownloadLogSave(Throwable e ) {
		super("系统记录下载日志时发生异常.", e);
	}
}
