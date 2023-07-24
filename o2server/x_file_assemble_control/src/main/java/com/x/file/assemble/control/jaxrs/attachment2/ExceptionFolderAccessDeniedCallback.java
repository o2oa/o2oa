package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.CallbackPromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.core.entity.personal.Folder2;

class ExceptionFolderAccessDeniedCallback extends CallbackPromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderAccessDeniedCallback(EffectivePerson effectivePerson, String callbackName, Folder2 folder) {
		super(callbackName, "person: {} access attachment :{} denied.", effectivePerson.getDistinguishedName(),
				folder.getName());
	}
}
