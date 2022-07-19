package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionCountWithFilterWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionCountWithFilterWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCountWithFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCountWithFilter.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			Business business = new Business(emc);
			List<String> personIds = new ArrayList<>();
			List<String> applicationIds = new ArrayList<>();
			List<String> processIds = new ArrayList<>();
			if (ListTools.isNotEmpty(wi.getCredentialList())) {
				personIds = business.organization().person().list(wi.getCredentialList());
			}
			if (ListTools.isNotEmpty(wi.getApplicationList())) {
				applicationIds = business.organization().person().list(wi.getApplicationList());
			}
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				processIds = business.organization().person().list(wi.getProcessList());
			}
			if (!ListTools.isEmpty(personIds, applicationIds, processIds)) {
				wo.setCount(this.count(business, personIds, applicationIds, processIds));
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionCountWithFilter$Wi")
	public static class Wi extends ActionCountWithFilterWi {
		private static final long serialVersionUID = 2830916660268536267L;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionCountWithFilter$Wo")
	public static class Wo extends ActionCountWithFilterWo {
		private static final long serialVersionUID = 7385617103294433776L;
	}

	/**
	 * 如果同时输入application和process那么和application和process取合集(or)
	 * 
	 * @param business
	 * @param personIds
	 * @param applicationIds
	 * @param processIds
	 * @return
	 * @throws Exception
	 */
	private Long count(Business business, List<String> personIds, List<String> applicationIds, List<String> processIds)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(personIds)) {
			p = cb.and(p, root.get(Task_.person).in(personIds));
		}
		if (ListTools.isNotEmpty(applicationIds) && ListTools.isNotEmpty(processIds)) {
			p = cb.and(p,
					cb.or(root.get(Task_.application).in(applicationIds), root.get(Task_.process).in(processIds)));
		} else {
			if (ListTools.isNotEmpty(applicationIds)) {
				p = cb.and(p, root.get(Task_.application).in(applicationIds));
			}
			if (ListTools.isNotEmpty(processIds)) {
				p = cb.and(p, root.get(Task_.process).in(processIds));
			}
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}