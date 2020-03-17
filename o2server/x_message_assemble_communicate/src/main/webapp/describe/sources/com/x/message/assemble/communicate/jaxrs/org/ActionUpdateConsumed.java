package com.x.message.assemble.communicate.jaxrs.org;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapNumber;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.jaxrs.org.ActionDelete.Wo;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Org;

class ActionUpdateConsumed extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateConsumed.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String type, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<Org> orgs = emc.list(Org.class, wi.getIdList());
			
			if (!orgs.isEmpty()) {
				emc.beginTransaction(Org.class);
				for (Org o : orgs) {
//					emc.remove(o);
					o.setConsumed(true);
				}
				emc.commit();
			}
			
			Wo wo = new Wo();
			wo.setValue(orgs.size());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("标识")
		List<String> idList = new ArrayList<>();

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

	}

	public static class Wo extends WrapNumber {

	}

}
