package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.meeting.assemble.control.jaxrs.meeting.BaseAction;
import com.x.meeting.core.entity.Meeting;

/**
 * 会议签到
 *
 */
class ActionCheckIn extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			
			if( meeting != null ) {
				emc.beginTransaction( Meeting.class );
				meeting.addCheckinPerson( effectivePerson.getDistinguishedName() );
				emc.check( meeting, CheckPersistType.all );
				emc.commit();
			}			
			
			Wo wo = new Wo();
			wo.setCheckinPersonList( meeting.getCheckinPersonList());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo {
		
		@FieldDescribe("已签到的人员列表")
		private List<String> checkinPersonList;

		public List<String> getCheckinPersonList() {
			return checkinPersonList;
		}

		public void setCheckinPersonList(List<String> checkinPersonList) {
			this.checkinPersonList = checkinPersonList;
		}
	}

}
