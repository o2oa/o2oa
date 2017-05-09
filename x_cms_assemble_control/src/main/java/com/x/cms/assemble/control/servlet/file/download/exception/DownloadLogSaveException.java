package com.x.cms.assemble.control.servlet.file.download.exception;

import com.x.base.core.exception.PromptException;

public class DownloadLogSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public DownloadLogSaveException(Throwable e ) {
		super("系统记录下载日志时发生异常.", e);
	}
}
