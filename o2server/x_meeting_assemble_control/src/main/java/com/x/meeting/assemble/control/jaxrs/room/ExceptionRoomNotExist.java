package com.x.meeting.assemble.control.jaxrs.room;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoomNotExist extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionRoomNotExist(String id) {
		super("找不到指定的会议室: {}.", id);
	}
}
