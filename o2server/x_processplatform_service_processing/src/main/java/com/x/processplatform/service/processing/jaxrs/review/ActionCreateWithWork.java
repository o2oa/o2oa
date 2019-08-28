package com.x.processplatform.service.processing.jaxrs.review;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;

import java.util.ArrayList;
import java.util.List;

class ActionCreateWithWork extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Work work = emc.find(wi.getWorkId(), Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(wi.getWorkId(), Work.class);
			}
			List<String> people = business.organization().person().list(wi.getPersonList());
			if (ListTools.isEmpty(people)) {
				throw new ExceptionPersonEmpty();
			}
			if (ListTools.isNotEmpty(people)) {
				emc.beginTransaction(Review.class);
				for (String person : people) {
					Review review = new Review(work, person);
					emc.persist(review, CheckPersistType.all);
				}
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {
		private String workId;

		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}
