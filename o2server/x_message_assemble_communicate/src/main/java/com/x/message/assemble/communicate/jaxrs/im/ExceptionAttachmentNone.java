package com.x.message.assemble.communicate.jaxrs.im;


import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNone extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAttachmentNone(String name) {
		super("未上传附件: {}.", name);
	}
}
