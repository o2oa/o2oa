package com.x.processplatform.service.processing.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotExistItem extends PromptException {
	private static final long serialVersionUID = 4865231321642600303L;

	ExceptionNotExistItem(String itemName) {
		super("path路径无法找到,itemName：{}.",  itemName);
	}
}
