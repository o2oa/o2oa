package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import org.apache.commons.lang3.StringUtils;

class ActionSave extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			String identity = this.decideCreatorIdentity(business, effectivePerson, wi.getIdentity());
			String unit = business.organization().unit().getWithIdentity(identity);
			String person = business.organization().person().getWithIdentity(identity);
			List<String> identities = business.organization().identity().listWithPerson(person);
			List<String> units = business.organization().unit().listWithPerson(person);
			List<String> roles = business.organization().role().listWithPerson(person);
			Application application = business.application().pick(wi.getWork().getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(wi.getWork().getApplication(), Application.class);
			}
			Process process = business.process().pick(wi.getWork().getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(wi.getWork().getProcess(), Process.class);
			}
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new ExceptionAccessDenied(effectivePerson, application);
			}
			if (!business.process().startable(effectivePerson, identities, units, process)) {
				throw new ExceptionAccessDenied(effectivePerson, process);
			}
			emc.beginTransaction(Draft.class);
			Draft draft = null;
			if (StringUtils.isEmpty(wi.getWork().getId())) {
				draft = new Draft();
				this.update(draft, wi, application, process, person, identity, unit);
				emc.persist(draft, CheckPersistType.all);
			} else {
				draft = emc.find(wi.getWork().getId(), Draft.class);
				if (null == draft) {
					throw new ExceptionEntityNotExist(wi.getWork().getId(), Draft.class);
				}
				this.update(draft, wi, application, process, person, identity, unit);
				emc.check(draft, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(draft.getId());
			result.setData(wo);
			return result;
		}
	}

	private void update(Draft draft, Wi wi, Application application, Process process, String person, String identity,
			String unit) {
		draft.setApplication(application.getId());
		draft.setApplicationAlias(application.getAlias());
		draft.setApplicationName(application.getName());
		draft.setProcess(process.getId());
		draft.setProcessAlias(process.getAlias());
		draft.setProcessName(process.getName());
		draft.setPerson(person);
		draft.setIdentity(identity);
		draft.setUnit(unit);
		String title = wi.getWork().getTitle();
		if (null != wi.getData()) {
			Object value = wi.getData().getOrDefault("subject", null);
			if (null != value) {
				title = Objects.toString(value);
			} else {
				value = wi.getData().getOrDefault("title", null);
				if (null != value) {
					title = Objects.toString(value);
				}
			}
		}
		draft.setTitle(title);
		draft.getProperties().setData(wi.getData());
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("数据")
		private Data data = new Data();

		@FieldDescribe("工作")
		private Work work;

		@FieldDescribe("身份")
		private String identity;

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

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

	public static class Wo extends WoId {

	}

}
