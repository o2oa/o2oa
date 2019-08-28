package com.x.processplatform.assemble.surface.jaxrs.review;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

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
				throw new ExceptionEntityNotExist(wi.getWorkId(), WorkCompleted.class);
			}
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (!control.getAllowVisit()) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
			List<String> people = business.organization().person().list(wi.getPersonList());
			if (ListTools.isEmpty(people)) {
				throw new ExceptionPersonEmpty();
			}
			Wo wo = ThisApplication.context().applications()
					.postQuery(x_processplatform_service_processing.class, "review/create/work", wi)
					.getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("流转中工作")
		private String workId;

		@FieldDescribe("可阅读人员")
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

	public static class WoControl extends WorkControl {
	}

}
