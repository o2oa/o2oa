package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.project.exception.PromptException;

class ExceptionShareNameExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionShareNameExist(String person, String name) {
		super("用户:{},分享的文件:{}已经存在.", person, name);
	}
}
