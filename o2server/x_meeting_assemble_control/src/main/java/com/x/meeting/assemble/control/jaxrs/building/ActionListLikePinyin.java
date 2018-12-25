package com.x.meeting.assemble.control.jaxrs.building;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutBuilding;
import com.x.meeting.core.entity.Building;

class ActionListLikePinyin extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.building().listLikePinyin(key);
			List<Wo> wos = Wo.copier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wos);
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
