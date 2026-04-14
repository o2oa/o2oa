package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.exception.PromptException;

class ExceptionAttachmentVersionNotExist extends PromptException {

	private static final long serialVersionUID = 1220882826501927878L;

	ExceptionAttachmentVersionNotExist(String id, Integer version) {
		super("指定版本：{} 的文件：{}不存在.", version, id);
	}
}
