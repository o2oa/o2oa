package com.x.meeting.assemble.control.jaxrs.room;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Room;

class ActionListPinyinInitial extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.room().listPinyinInitial(key);
			List<Wo> wos = Wo.copier.copy(emc.list(Room.class, ids));
			WrapTools.setFutureMeeting(business, wos, true);
			SortTools.asc(wos, Room.orderNumber_FIELDNAME, Room.name_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WrapOutRoom {

		private static final long serialVersionUID = -969148596991975992L;

		static WrapCopier<Room, Wo> copier = WrapCopierFactory.wo(Room.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

}
