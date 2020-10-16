package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment2;

class ExceptionAttachmentUploadDenied extends PromptException {

	private static final long serialVersionUID = -7797553180316608418L;

	ExceptionAttachmentUploadDenied(String name) {
		super("【{}】文件类型不符合上传要求", name);
	}
}
