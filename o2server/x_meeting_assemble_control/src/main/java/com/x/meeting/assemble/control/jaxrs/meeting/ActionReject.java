package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.core.entity.Meeting;

class ActionReject extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			/* 在被邀请参加的人员之内 */
			if (meeting.getInvitePersonList().contains(effectivePerson.getDistinguishedName())) {
				emc.beginTransaction(Meeting.class);
				ListTools.addWithProperty(meeting, "rejectPersonList", true, effectivePerson.getDistinguishedName());
				ListTools.subtractWithProperty(meeting, "acceptPersonList", effectivePerson.getDistinguishedName());
				emc.check(meeting, CheckPersistType.all);
				emc.commit();
				MessageFactory.meeting_reject(effectivePerson.getDistinguishedName(), meeting);
			}
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
