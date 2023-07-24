package com.x.meeting.assemble.control.jaxrs.room;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Room;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Room room = emc.find(id, Room.class);
			if (null == room) {
				throw new ExceptionEntityNotExist(id, Room.class);
			}
			Wo wo = Wo.copier.copy(room);
			WrapTools.setFutureMeeting(business, wo, true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapOutRoom {

		private static final long serialVersionUID = -969148596991975992L;

		static WrapCopier<Room, Wo> copier = WrapCopierFactory.wo(Room.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

}
