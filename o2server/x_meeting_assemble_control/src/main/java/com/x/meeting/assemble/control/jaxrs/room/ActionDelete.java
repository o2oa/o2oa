package com.x.meeting.assemble.control.jaxrs.room;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new 	ActionResult<>();
			Business business = new Business(emc);
			Room room = emc.find(id, Room.class);
			if (null == room) {
				throw new ExceptionEntityNotExist(id, Room.class);
			}
			if(!business.roomEditAvailable(effectivePerson, room)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Room.class);
			emc.beginTransaction(Meeting.class);
			emc.beginTransaction(Attachment.class);
			for (Meeting meeting : emc.list(Meeting.class, business.meeting().listWithRoom(room.getId()))) {
				for (Attachment attachment : emc.list(Attachment.class,
						business.attachment().listWithMeeting(meeting.getId()))) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							attachment.getStorage());
					attachment.deleteContent(mapping);
					emc.remove(attachment);
				}
				emc.remove(meeting);
			}
			emc.remove(room);
			emc.commit();
			Wo wo= new Wo();
			wo.setId(room.getId());
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
