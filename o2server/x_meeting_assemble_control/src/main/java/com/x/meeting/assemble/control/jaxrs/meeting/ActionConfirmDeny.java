package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

class ActionConfirmDeny extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionEntityNotExist(id, Meeting.class);
			}
			Room room = emc.find(meeting.getRoom(), Room.class);
			if (null == room) {
				throw new ExceptionEntityNotExist(meeting.getRoom(), Room.class);
			}
			if (!business.roomEditAvailable(effectivePerson, room)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Meeting.class);
			// if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(),
			// meeting.getCompletedTime(),
			// meeting.getId())) {
			// throw new ExceptionRoomNotAvailable("会议室:" + room.getName() + ", 已经预约.");
			// }
			meeting.setConfirmStatus(ConfirmStatus.deny);
			emc.check(meeting, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
