package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment2;

class ExceptionAttachmentAccessDenied extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentAccessDenied(EffectivePerson effectivePerson, Attachment attachment) {
		super("person: {} access attachment :{} denied.", effectivePerson.getDistinguishedName(), attachment.getName());
	}

	ExceptionAttachmentAccessDenied(EffectivePerson effectivePerson, Attachment2 attachment) {
		super("person: {} access attachment :{} denied.", effectivePerson.getDistinguishedName(), attachment.getName());
	}
}
