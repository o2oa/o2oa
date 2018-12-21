package com.x.meeting.assemble.control.jaxrs.room;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Room;

abstract class BaseAction extends StandardJaxrsAction {

	protected void checkAuditor(Business business, Room room) throws Exception {
		if (StringUtils.isNotEmpty(room.getAuditor())) {
			String person = business.organization().person().get(room.getAuditor());
			if (StringUtils.isNotEmpty(person)) {
				room.setAuditor(person);
			} else {
				room.setAuditor("");
			}
		}
	}
}