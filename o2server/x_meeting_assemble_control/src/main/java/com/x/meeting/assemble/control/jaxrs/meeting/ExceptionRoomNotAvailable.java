package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.project.exception.PromptException;

public class ExceptionRoomNotAvailable extends PromptException {

	private static final long serialVersionUID = -6884859406782731288L;

	public ExceptionRoomNotAvailable(String flag) {
		super("会议室 {}, 不可用.", flag);
	}
}
