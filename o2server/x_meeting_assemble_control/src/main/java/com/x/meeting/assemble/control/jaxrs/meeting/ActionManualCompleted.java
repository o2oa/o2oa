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
import com.x.meeting.core.entity.Meeting;

class ActionManualCompleted extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionEntityNotExist(id, Meeting.class);
			}
			if (!business.meetingEditAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Meeting.class);
			meeting.setManualCompleted(true);
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
