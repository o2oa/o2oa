package com.x.processplatform.service.processing.processor.split;

import com.x.base.core.project.exception.RunningException;

class ExceptionSplitEmptySplitValue extends RunningException {

	private static final long serialVersionUID = 6143035361950594561L;

	ExceptionSplitEmptySplitValue(String name, String title, String id, String job) {
		super("split:{} splitValue is empty, work:{}, id:{}, job:{}.", name, title, id, job);
	}

}
