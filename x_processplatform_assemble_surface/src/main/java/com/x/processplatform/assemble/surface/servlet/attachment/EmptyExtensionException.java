package com.x.processplatform.assemble.surface.servlet.attachment;

import com.x.base.core.exception.PromptException;

@Deprecated
class EmptyExtensionException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	EmptyExtensionException(String name) {
		super("不能上传文件扩展名为空的文件: {}.", name);
	}
}
