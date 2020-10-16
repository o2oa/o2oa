package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionCapacityOut extends PromptException {

	private static final long serialVersionUID = 5186249956453089454L;

	ExceptionCapacityOut(long curCapacity, long limitCapacity) {
		super("超过文件存储容量，当前将使用: {}M，限制使用：{}M", curCapacity, limitCapacity);
	}

}
