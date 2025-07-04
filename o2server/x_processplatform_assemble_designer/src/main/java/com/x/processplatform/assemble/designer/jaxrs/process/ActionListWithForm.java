package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionListWithForm extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithForm.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String formId) throws Exception {
		LOGGER.debug("execute:{}, formId:{}.", effectivePerson::getDistinguishedName, () -> formId);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = new ArrayList<>();
			ids.addAll(listWithForm(business, Agent.class, formId));
			ids.addAll(listWithForm(business, Begin.class, formId));
			ids.addAll(listWithForm(business, Cancel.class, formId));
			ids.addAll(listWithForm(business, Choice.class, formId));
			ids.addAll(listWithForm(business, Delay.class, formId));
			ids.addAll(listWithForm(business, Embed.class, formId));
			ids.addAll(listWithForm(business, End.class, formId));
			ids.addAll(listWithForm(business, Invoke.class, formId));
			ids.addAll(listWithForm(business, Manual.class, formId));
			ids.addAll(listWithForm(business, Merge.class, formId));
			ids.addAll(listWithForm(business, Parallel.class, formId));
			ids.addAll(listWithForm(business, Publish.class, formId));
			ids.addAll(listWithForm(business, Service.class, formId));
			ids.addAll(listWithForm(business, Split.class, formId));
			ids = ids.stream().distinct().collect(Collectors.toList());
			for (String id : ids) {
				Process process = emc.find(id, Process.class);
				if (null != process) {
					Wo wo = new Wo();
					wo.setId(process.getId());
					wo.setName(process.getName());
					wos.add(wo);
				}
			}
		}
		result.setData(wos);
		return result;
	}

	private List<String> listWithForm(Business business, Class<? extends Activity> cls, String formId)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<?> root = cq.from(cls);
		Predicate p = cb.equal(root.get("form"), formId);
		cq.select(root.get("process")).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 3253225373830391713L;

		@FieldDescribe("标识")
		private String id;

		@FieldDescribe("名称")
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
