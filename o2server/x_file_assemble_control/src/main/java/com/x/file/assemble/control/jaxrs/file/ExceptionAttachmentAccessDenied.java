package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentAccessDenied extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentAccessDenied(String person, String name, String id) {
		super("用户: {} 访问Attachment: {}, id:{}, 由于权限不足被拒绝.", person, name, id);
	}
}
