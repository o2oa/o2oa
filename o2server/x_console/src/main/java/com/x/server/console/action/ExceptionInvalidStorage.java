package com.x.server.console.action;

import com.x.base.core.entity.StorageObject;
import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidStorage extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionInvalidStorage(StorageObject storageObject) {
		super("can not find storageMapping class: " + storageObject.getClass().getName() + ", storage: "
				+ storageObject.getStorage() + ", id: " + storageObject.getId() + ", name: " + storageObject.getName()
				+ ", set exceptionInvalidStorage to false will ignore item.");
	}

}
