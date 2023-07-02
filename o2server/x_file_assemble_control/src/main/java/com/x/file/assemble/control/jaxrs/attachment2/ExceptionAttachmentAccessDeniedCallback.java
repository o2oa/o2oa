package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment2;

class ExceptionAttachmentAccessDeniedCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionAttachmentAccessDeniedCallback(EffectivePerson effectivePerson, String callbackName,
			Attachment attachment) {
		super(callbackName, "person: {} access attachment :{} denied.", effectivePerson.getDistinguishedName(),
				attachment.getName());
	}

	ExceptionAttachmentAccessDeniedCallback(EffectivePerson effectivePerson, String callbackName,
											Attachment2 attachment) {
		super(callbackName, "person: {} access attachment :{} denied.", effectivePerson.getDistinguishedName(),
				attachment.getName());
	}
}
