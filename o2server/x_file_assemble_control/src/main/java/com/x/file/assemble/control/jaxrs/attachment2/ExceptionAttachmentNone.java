package com.x.file.assemble.control.jaxrs.attachment2;


import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentNone extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAttachmentNone(String name) {
		super("未上传附件: {}.", name);
	}
}
