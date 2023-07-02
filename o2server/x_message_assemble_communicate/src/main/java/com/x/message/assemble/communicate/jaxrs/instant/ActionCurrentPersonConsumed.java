package com.x.message.assemble.communicate.jaxrs.instant;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.message.core.entity.Instant;

class ActionCurrentPersonConsumed extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCurrentPersonConsumed.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (ListTools.isNotEmpty(wi.getIdList())) {
				List<Instant> os = emc.listEqualAndIn(Instant.class, Instant.person_FIELDNAME,
						effectivePerson.getDistinguishedName(), JpaObject.id_FIELDNAME, wi.getIdList());
				if (!os.isEmpty()) {
					emc.beginTransaction(Instant.class);
					for (Instant o : os) {
						o.setConsumed(true);
					}
					emc.commit();
				}
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -8841713058732134969L;

		@FieldDescribe("标识")
		private List<String> idList = new ArrayList<>();

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 1495388301034226530L;

	}

}