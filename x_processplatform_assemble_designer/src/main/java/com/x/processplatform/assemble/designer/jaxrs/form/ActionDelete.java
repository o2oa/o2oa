package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.utils.ListTools;
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

class ActionDelete extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Form form = emc.find(id, Form.class);
			if (null == form) {
				throw new FormNotExistedException(id);
			}
			Application application = emc.find(form.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(form.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
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
			WrapOutId wrap = new WrapOutId(form.getId());
			result.setData(wrap);
			return result;
		}
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
			List<Agent> list = business.entityManagerContainer().fetchAttribute(ids, Agent.class, "name");
			throw new UsedWithAgentException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithBegin(Business business, Form form) throws Exception {
		List<String> ids = business.begin().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Begin> list = business.entityManagerContainer().fetchAttribute(ids, Begin.class, "name");
			throw new UsedWithBeginException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithCancel(Business business, Form form) throws Exception {
		List<String> ids = business.cancel().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Cancel> list = business.entityManagerContainer().fetchAttribute(ids, Cancel.class, "name");
			throw new UsedWithCancelException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithChoice(Business business, Form form) throws Exception {
		List<String> ids = business.choice().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Choice> list = business.entityManagerContainer().fetchAttribute(ids, Choice.class, "name");
			throw new UsedWithChoiceException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithDelay(Business business, Form form) throws Exception {
		List<String> ids = business.delay().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Delay> list = business.entityManagerContainer().fetchAttribute(ids, Delay.class, "name");
			throw new UsedWithDelayException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithEmbed(Business business, Form form) throws Exception {
		List<String> ids = business.embed().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Embed> list = business.entityManagerContainer().fetchAttribute(ids, Embed.class, "name");
			throw new UsedWithEmbedException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithEnd(Business business, Form form) throws Exception {
		List<String> ids = business.end().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<End> list = business.entityManagerContainer().fetchAttribute(ids, End.class, "name");
			throw new UsedWithEndException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithInvoke(Business business, Form form) throws Exception {
		List<String> ids = business.invoke().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Invoke> list = business.entityManagerContainer().fetchAttribute(ids, Invoke.class, "name");
			throw new UsedWithInvokeException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithManual(Business business, Form form) throws Exception {
		List<String> ids = business.manual().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Manual> list = business.entityManagerContainer().fetchAttribute(ids, Manual.class, "name");
			throw new UsedWithManualException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithMerge(Business business, Form form) throws Exception {
		List<String> ids = business.merge().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Merge> list = business.entityManagerContainer().fetchAttribute(ids, Merge.class, "name");
			throw new UsedWithMergeException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithMessage(Business business, Form form) throws Exception {
		List<String> ids = business.message().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Message> list = business.entityManagerContainer().fetchAttribute(ids, Message.class, "name");
			throw new UsedWithMessageException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithParallel(Business business, Form form) throws Exception {
		List<String> ids = business.parallel().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Parallel> list = business.entityManagerContainer().fetchAttribute(ids, Parallel.class, "name");
			throw new UsedWithParallelException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithService(Business business, Form form) throws Exception {
		List<String> ids = business.service().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Service> list = business.entityManagerContainer().fetchAttribute(ids, Service.class, "name");
			throw new UsedWithServiceException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

	private void checkUsedWithSplit(Business business, Form form) throws Exception {
		List<String> ids = business.split().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Split> list = business.entityManagerContainer().fetchAttribute(ids, Split.class, "name");
			throw new UsedWithSplitException(form.getName(), form.getId(),
					ListTools.extractProperty(list, "name", String.class, true, false));
		}
	}

}
