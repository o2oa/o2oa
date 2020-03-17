package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionMeetingAccessDenied extends PromptException {

	private static final long serialVersionUID = 7237855733312562652L;

	ExceptionMeetingAccessDenied(EffectivePerson effectivePerson, String name) {
		super("用户: {} 访问会议:{} 权限不足..", effectivePerson.getName(), name);

	}
}
