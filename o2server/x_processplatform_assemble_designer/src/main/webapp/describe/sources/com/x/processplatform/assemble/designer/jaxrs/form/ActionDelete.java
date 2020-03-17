package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Form form = emc.find(id, Form.class);
			if (null == form) {
				throw new ExceptionFormNotExist(id);
			}
			Application application = emc.find(form.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(form.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			/** 校验是否有在使用的节点 */
			this.checkUsedWithAgent(business, form);
			this.checkUsedWithBegin(business, form);
			this.checkUsedWithCancel(business, form);
			this.checkUsedWithChoice(business, form);
			this.checkUsedWithDelay(business, form);
			this.checkUsedWithEmbed(business, form);
			this.checkUsedWithEnd(business, form);
			this.checkUsedWithInvoke(business, form);
			this.checkUsedWithManual(business, form);
			this.checkUsedWithMerge(business, form);
			this.checkUsedWithMessage(business, form);
			this.checkUsedWithParallel(business, form);
			this.checkUsedWithService(business, form);
			this.checkUsedWithSplit(business, form);
			/* 先删除FormField */
			List<String> formFieldIds = business.formField().listWithForm(form.getId());
			emc.beginTransaction(FormField.class);
			emc.beginTransaction(Form.class);
			for (FormField o : emc.list(FormField.class, formFieldIds)) {
				emc.remove(o);
			}
			emc.remove(form, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Form.class);
			Wo wo = new Wo();
			wo.setId(form.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	// emc.beginTransaction(Agent.class);
	// emc.beginTransaction(Begin.class);
	// emc.beginTransaction(Cancel.class);
	// emc.beginTransaction(Choice.class);
	// emc.beginTransaction(Delay.class);
	// emc.beginTransaction(Embed.class);
	// emc.beginTransaction(End.class);
	// emc.beginTransaction(Invoke.class);
	// emc.beginTransaction(Manual.class);
	// emc.beginTransaction(Merge.class);
	// emc.beginTransaction(Message.class);
	// emc.beginTransaction(Parallel.class);
	// emc.beginTransaction(Service.class);
	// emc.beginTransaction(Split.class);

	private void checkUsedWithAgent(Business business, Form form) throws Exception {
		List<String> ids = business.agent().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Agent> list = business.entityManagerContainer().fetch(ids, Agent.class,
					ListTools.toList(Agent.name_FIELDNAME));
			throw new ExceptionUsedWithAgent(form.getName(), form.getId(),
					ListTools.extractProperty(list, Agent.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithBegin(Business business, Form form) throws Exception {
		List<String> ids = business.begin().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Begin> list = business.entityManagerContainer().fetch(ids, Begin.class,
					ListTools.toList(Begin.name_FIELDNAME));
			throw new ExceptionUsedWithBegin(form.getName(), form.getId(),
					ListTools.extractProperty(list, Begin.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithCancel(Business business, Form form) throws Exception {
		List<String> ids = business.cancel().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Cancel> list = business.entityManagerContainer().fetch(ids, Cancel.class,
					ListTools.toList(Cancel.name_FIELDNAME));
			throw new ExceptionUsedWithCancel(form.getName(), form.getId(),
					ListTools.extractProperty(list, Cancel.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithChoice(Business business, Form form) throws Exception {
		List<String> ids = business.choice().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Choice> list = business.entityManagerContainer().fetch(ids, Choice.class,
					ListTools.toList(Choice.name_FIELDNAME));
			throw new ExceptionUsedWithChoice(form.getName(), form.getId(),
					ListTools.extractProperty(list, Choice.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithDelay(Business business, Form form) throws Exception {
		List<String> ids = business.delay().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Delay> list = business.entityManagerContainer().fetch(ids, Delay.class,
					ListTools.toList(Delay.name_FIELDNAME));
			throw new ExceptionUsedWithDelay(form.getName(), form.getId(),
					ListTools.extractProperty(list, Delay.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithEmbed(Business business, Form form) throws Exception {
		List<String> ids = business.embed().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Embed> list = business.entityManagerContainer().fetch(ids, Embed.class,
					ListTools.toList(Embed.name_FIELDNAME));
			throw new ExceptionUsedWithEmbed(form.getName(), form.getId(),
					ListTools.extractProperty(list, Embed.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithEnd(Business business, Form form) throws Exception {
		List<String> ids = business.end().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<End> list = business.entityManagerContainer().fetch(ids, End.class,
					ListTools.toList(End.name_FIELDNAME));
			throw new ExceptionUsedWithEnd(form.getName(), form.getId(),
					ListTools.extractProperty(list, End.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithInvoke(Business business, Form form) throws Exception {
		List<String> ids = business.invoke().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Invoke> list = business.entityManagerContainer().fetch(ids, Invoke.class,
					ListTools.toList(Invoke.name_FIELDNAME));
			throw new ExceptionUsedWithInvoke(form.getName(), form.getId(),
					ListTools.extractProperty(list, Invoke.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithManual(Business business, Form form) throws Exception {
		List<String> ids = business.manual().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Manual> list = business.entityManagerContainer().fetch(ids, Manual.class,
					ListTools.toList(Manual.name_FIELDNAME));
			throw new ExceptionUsedWithManual(form.getName(), form.getId(),
					ListTools.extractProperty(list, Manual.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithMerge(Business business, Form form) throws Exception {
		List<String> ids = business.merge().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Merge> list = business.entityManagerContainer().fetch(ids, Merge.class,
					ListTools.toList(Merge.name_FIELDNAME));
			throw new ExceptionUsedWithMerge(form.getName(), form.getId(),
					ListTools.extractProperty(list, Merge.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithMessage(Business business, Form form) throws Exception {
		List<String> ids = business.message().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Message> list = business.entityManagerContainer().fetch(ids, Message.class,
					ListTools.toList(Message.name_FIELDNAME));
			throw new ExceptionUsedWithMessage(form.getName(), form.getId(),
					ListTools.extractProperty(list, Message.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithParallel(Business business, Form form) throws Exception {
		List<String> ids = business.parallel().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Parallel> list = business.entityManagerContainer().fetch(ids, Parallel.class,
					ListTools.toList(Parallel.name_FIELDNAME));
			throw new ExceptionUsedWithParallel(form.getName(), form.getId(),
					ListTools.extractProperty(list, Parallel.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithService(Business business, Form form) throws Exception {
		List<String> ids = business.service().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Service> list = business.entityManagerContainer().fetch(ids, Service.class,
					ListTools.toList(Service.name_FIELDNAME));
			throw new ExceptionUsedWithService(form.getName(), form.getId(),
					ListTools.extractProperty(list, Service.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithSplit(Business business, Form form) throws Exception {
		List<String> ids = business.split().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Split> list = business.entityManagerContainer().fetch(ids, Split.class,
					ListTools.toList(Split.name_FIELDNAME));
			throw new ExceptionUsedWithSplit(form.getName(), form.getId(),
					ListTools.extractProperty(list, Split.name_FIELDNAME, String.class, true, false));
		}
	}

}
