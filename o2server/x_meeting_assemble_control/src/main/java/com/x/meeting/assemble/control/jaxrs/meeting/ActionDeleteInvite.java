package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;

class ActionDeleteInvite extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(id);
			}
			if (!business.meetingEditAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> inviteDelPersonList = this.convertToPerson(business,ListTools.trim(wi.getInvitePersonList(), true, true));
			emc.beginTransaction(Meeting.class);
			List<String> invitePersonList = ListUtils.subtract(meeting.getInvitePersonList(),inviteDelPersonList);
			meeting.setInvitePersonList(invitePersonList);
			
			List<String> originalList = meeting.getInviteDelPersonList();
			originalList.addAll(inviteDelPersonList);
			originalList = ListTools.trim(originalList, true, true);
			meeting.setInviteDelPersonList(originalList);
			
			emc.check(meeting, CheckPersistType.all);
			emc.commit();
			
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				for (String _s : inviteDelPersonList) {
					MessageFactory.meeting_deleteInvitePerson(_s, meeting);
				}
				//this.notifyMeetingInviteMessage(business, meeting);
			}
			
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -4637797853096659198L;
		
		@FieldDescribe("将要删除名单列表")
		public List<String> InvitePersonList;
		

		public List<String> getInvitePersonList() {
			return InvitePersonList;
		}

		public void setInvitePersonList(List<String> invitePersonList) {
			InvitePersonList = invitePersonList;
		}

		
	}

	public static class Wo extends WoId {

	}

}
