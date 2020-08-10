package com.x.meeting.assemble.control.jaxrs.building;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutBuilding;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Building;

class ActionListWithStartCompletedRoom extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String start, String completed,String currentRoom,String meetingId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Date startTime = DateTools.parse(start, DateTools.format_yyyyMMdd + " " + DateTools.format_HHmm);
			Date completedTime = DateTools.parse(completed, DateTools.format_yyyyMMdd + " " + DateTools.format_HHmm);
			List<String> ids = business.building().list();
			List<Wo> wos = Wo.copier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wos);
			for (WrapOutBuilding wo : wos) {
				WrapTools.setFutureMeeting(business, wo.getRoomList(), true);
				for (WrapOutRoom room : wo.getRoomList()) {
					if( currentRoom.equalsIgnoreCase(room.getId())) {
						room.setIdle(business.room().checkIdle(room.getId(), startTime, completedTime, meetingId));
					}else {
					    WrapTools.checkRoomIdle(business, room, startTime, completedTime);
					}
				}
			}
			SortTools.asc(wos, false, Building.name_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}
	
	public static class Wo extends WrapOutBuilding {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Building, Wo> copier = WrapCopierFactory.wo(Building.class, Wo.class, null,
				Wo.Excludes);

	}

}
