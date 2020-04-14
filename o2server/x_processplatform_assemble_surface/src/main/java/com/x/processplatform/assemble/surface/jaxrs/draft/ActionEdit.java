package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Work;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Draft draft = emc.find(id, Draft.class);
			if (null == draft) {
				throw new ExceptionEntityNotExist(id, Draft.class);
			}
			if (effectivePerson.isNotPerson(draft.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, draft);
			}
			emc.beginTransaction(Draft.class);
			draft.setTitle(wi.getWork().getTitle());
			draft.getProperties().setData(wi.getData());
			emc.check(draft, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(draft.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("数据")
		private Data data;

		@FieldDescribe("工作")
		private Work work;

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public Work getWork() {
			return work;
		}

		public void setWork(Work work) {
			this.work = work;
		}

	}

	public static class Wo extends WoId {

	}

}
