package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Activity;
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
import com.x.processplatform.core.entity.element.FormVersion;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
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
			if (StringUtils.equalsIgnoreCase(application.getDefaultForm(), form.getId())) {
				throw new ExceptionUsedDefaultForm(form.getName(), form.getId());
			}
			// 校验是否有在使用的节点
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
			this.checkUsedWithParallel(business, form);
			this.checkUsedWithPublish(business, form);
			this.checkUsedWithService(business, form);
			this.checkUsedWithSplit(business, form);
			// 先删除FormField
			List<String> formFieldIds = business.formField().listWithForm(form.getId());
			List<String> formVersionIds = emc.idsEqual(FormVersion.class, FormVersion.form_FIELDNAME, form.getId());
			if (!ListTools.isEmpty(formFieldIds)) {
				emc.beginTransaction(FormField.class);
				this.delete(business, FormField.class, formFieldIds);
			}
			if (!ListTools.isEmpty(formVersionIds)) {
				emc.beginTransaction(FormVersion.class);
				this.delete(business, FormVersion.class, formVersionIds);
			}
			emc.beginTransaction(Form.class);
			this.delete(business, Form.class, form.getId());
			emc.commit();
			CacheManager.notify(Form.class);
			Wo wo = new Wo();
			wo.setId(form.getId());
			result.setData(wo);
			return result;
		}
	}

	private <T extends JpaObject> void delete(Business business, Class<T> clz, List<String> ids) throws Exception {
		EntityManager em = business.entityManagerContainer().get(clz);
		Query query = em.createQuery("DELETE FROM " + clz.getName() + " o WHERE o.id IN :ids");
		query.setParameter("ids", ids);
		query.executeUpdate();
	}

	private <T extends JpaObject> void delete(Business business, Class<T> clz, String... ids) throws Exception {
		this.delete(business, clz, Arrays.asList(ids));
	}

	public static class Wo extends WoId {
	}

	private void checkUsedWithAgent(Business business, Form form) throws Exception {
		List<String> ids = business.agent().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Agent> list = business.entityManagerContainer().fetch(ids, Agent.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithAgent(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithBegin(Business business, Form form) throws Exception {
		List<String> ids = business.begin().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Begin> list = business.entityManagerContainer().fetch(ids, Begin.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithBegin(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithCancel(Business business, Form form) throws Exception {
		List<String> ids = business.cancel().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Cancel> list = business.entityManagerContainer().fetch(ids, Cancel.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithCancel(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithChoice(Business business, Form form) throws Exception {
		List<String> ids = business.choice().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Choice> list = business.entityManagerContainer().fetch(ids, Choice.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithChoice(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithDelay(Business business, Form form) throws Exception {
		List<String> ids = business.delay().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Delay> list = business.entityManagerContainer().fetch(ids, Delay.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithDelay(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithEnd(Business business, Form form) throws Exception {
		List<String> ids = business.end().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<End> list = business.entityManagerContainer().fetch(ids, End.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithEnd(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithEmbed(Business business, Form form) throws Exception {
		List<String> ids = business.embed().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Embed> list = business.entityManagerContainer().fetch(ids, Embed.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithEmbed(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithInvoke(Business business, Form form) throws Exception {
		List<String> ids = business.invoke().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Invoke> list = business.entityManagerContainer().fetch(ids, Invoke.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithInvoke(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithManual(Business business, Form form) throws Exception {
		List<String> ids = business.manual().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Manual> list = business.entityManagerContainer().fetch(ids, Manual.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithManual(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithMerge(Business business, Form form) throws Exception {
		List<String> ids = business.merge().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Merge> list = business.entityManagerContainer().fetch(ids, Merge.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithMerge(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithParallel(Business business, Form form) throws Exception {
		List<String> ids = business.parallel().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Parallel> list = business.entityManagerContainer().fetch(ids, Parallel.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithParallel(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithPublish(Business business, Form form) throws Exception {
		List<String> ids = business.publish().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Publish> list = business.entityManagerContainer().fetch(ids, Publish.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithPublish(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithService(Business business, Form form) throws Exception {
		List<String> ids = business.service().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Service> list = business.entityManagerContainer().fetch(ids, Service.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithService(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

	private void checkUsedWithSplit(Business business, Form form) throws Exception {
		List<String> ids = business.split().listWithForm(form.getId());
		if (ListTools.isNotEmpty(ids)) {
			List<Split> list = business.entityManagerContainer().fetch(ids, Split.class,
					ListTools.toList(Activity.name_FIELDNAME));
			throw new ExceptionUsedWithSplit(form.getName(), form.getId(),
					ListTools.extractProperty(list, Activity.name_FIELDNAME, String.class, true, false));
		}
	}

}
