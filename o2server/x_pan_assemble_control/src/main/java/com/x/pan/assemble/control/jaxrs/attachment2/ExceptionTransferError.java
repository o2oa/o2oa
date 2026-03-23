package com.x.pan.assemble.control.jaxrs.attachment2;


import com.x.base.core.project.exception.PromptException;

class ExceptionTransferError extends PromptException {

	private static final long serialVersionUID = -964550598835274846L;

	ExceptionTransferError() {
		super("文件转换错误.");
	}
}
