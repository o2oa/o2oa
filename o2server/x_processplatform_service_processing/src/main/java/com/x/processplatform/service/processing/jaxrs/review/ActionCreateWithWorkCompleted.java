package com.x.processplatform.service.processing.jaxrs.review;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapIdList;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;

class ActionCreateWithWorkCompleted extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			WorkCompleted workCompleted = emc.find(wi.getWorkCompleted(), WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(wi.getWorkCompleted(), WorkCompleted.class);
			}
			List<String> people = business.organization().person().list(wi.getPersonList());
			if (ListTools.isEmpty(people)) {
				throw new ExceptionPersonEmpty();
			}
			List<String> idList = new ArrayList<>();
			if (ListTools.isNotEmpty(people)) {
				emc.beginTransaction(Review.class);
				for (String person : people) {
					Review review = new Review(workCompleted, person);
					emc.persist(review, CheckPersistType.all);
					idList.add(review.getId());
				}
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setIdList(idList);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private String workCompleted;

		private List<String> personList = new ArrayList<>();

		public String getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(String workCompleted) {
			this.workCompleted = workCompleted;
		}

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	public static class Wo extends WrapIdList {
	}

}
