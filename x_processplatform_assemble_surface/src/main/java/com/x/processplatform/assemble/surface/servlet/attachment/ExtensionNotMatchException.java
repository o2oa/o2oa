package com.x.processplatform.assemble.surface.servlet.attachment;

import com.x.base.core.exception.PromptException;

@Deprecated
class ExtensionNotMatchException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExtensionNotMatchException(String name, String extension) {
		super("文件: {} 的扩展名不匹配,期望的扩展名: {}.", name);
	}
}
