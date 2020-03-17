package com.x.meeting.assemble.control.jaxrs.building;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutBuilding;
import com.x.meeting.core.entity.Building;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Building building = emc.find(id, Building.class);
			if (null == building) {
				throw new ExceptionEntityNotExist(id, Building.class);
			}
			Wo wo = Wo.copier.copy(building);
			/* 添加Room信息 */
			WrapTools.setRoom(business, wo);
			/* 添加将来的Meeting信息 */
			WrapTools.setFutureMeeting(business, wo.getRoomList(), true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapOutBuilding {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Building, Wo> copier = WrapCopierFactory.wo(Building.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
