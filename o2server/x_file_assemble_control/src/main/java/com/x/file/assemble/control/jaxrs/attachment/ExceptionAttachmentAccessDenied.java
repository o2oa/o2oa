package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.personal.Attachment;

class ExceptionAttachmentAccessDenied extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentAccessDenied(EffectivePerson effectivePerson, Attachment attachment) {
		super("person: {} access attachment :{} denied.", effectivePerson.getDistinguishedName(), attachment.getName());
	}
}
