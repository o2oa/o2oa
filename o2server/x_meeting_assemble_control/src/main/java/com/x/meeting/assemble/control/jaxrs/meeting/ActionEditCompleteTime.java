package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

class ActionEditCompleteTime extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(id);
			}
			
			if (!business.meetingEditAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			
			Room room = emc.find(wi.getRoom(), Room.class);
			if (null == room) {
				throw new ExceptionRoomNotExist(wi.getRoom());
			}
			
			emc.beginTransaction(Meeting.class);
			
			
			//Wi.copier.copy(wi, meeting);
			meeting.setCompletedTime(wi.getCompletedTime());
			
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(),
					meeting.getId())) {
				throw new ExceptionRoomNotAvailable(room.getName());
			}
			
			emc.persist(meeting, CheckPersistType.all);
			emc.commit();
		
			
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi  {
		
		@FieldDescribe("所属楼层.")
		private String room;
		
		@FieldDescribe("结束时间.")
		private Date completedTime;

		public String getRoom() {
			return room;
		}

		public void setRoom(String room) {
			this.room = room;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}
	   
		
	}

	public static class Wo extends WoId {

	}

}
