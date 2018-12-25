package com.x.meeting.assemble.control.jaxrs.building;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.wrapout.WrapOutBuilding;
import com.x.meeting.core.entity.Building;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (!business.buildingEditAvailable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Building o = emc.find(id, Building.class);
			if (null == o) {
				throw new ExceptionEntityNotExist(id, Building.class);
			}
			emc.beginTransaction(Building.class);
			Wi.copier.copy(wi, o);
			emc.check(o, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Building {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Wi, Building> copier = WrapCopierFactory.wi(Wi.class, Building.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wo extends WrapOutBuilding {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Building, Wo> copier = WrapCopierFactory.wo(Building.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
