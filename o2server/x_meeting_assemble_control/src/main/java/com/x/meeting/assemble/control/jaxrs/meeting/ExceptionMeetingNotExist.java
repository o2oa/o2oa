package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.project.exception.PromptException;

class ExceptionMeetingNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMeetingNotExist(String flag) {
		super("会议 {}, 不存在.", flag);
	}
}
