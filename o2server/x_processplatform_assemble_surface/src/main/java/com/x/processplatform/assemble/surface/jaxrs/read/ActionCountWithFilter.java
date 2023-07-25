package com.x.processplatform.assemble.surface.jaxrs.read;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.express.assemble.surface.jaxrs.read.ActionCountWithFilterWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.read.ActionCountWithFilterWo;

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
			List<String> person_ids = business.organization().person().list(wi.getCredentialList());
			List<String> application_ids = ListTools.extractProperty(
					business.application().pick(wi.getAppliationList()), JpaObject.id_FIELDNAME, String.class, true,
					true);
			List<String> process_ids = ListTools.extractProperty(business.process().pick(wi.getProcessList()),
					JpaObject.id_FIELDNAME, String.class, true, true);
			if (ListTools.isEmpty(person_ids, application_ids, process_ids)) {
				throw new ExceptionEmptyCountFilter();
			}
			Long count = this.count(business, person_ids, application_ids, process_ids);
			wo.setCount(count);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionCountWithFilter$Wi")
	public static class Wi extends ActionCountWithFilterWi {

		private static final long serialVersionUID = 7738430126109288421L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionCountWithFilter$Wo")
	public static class Wo extends ActionCountWithFilterWo {

		private static final long serialVersionUID = 2846120437833623927L;

	}

	private Long count(Business business, List<String> person_ids, List<String> application_ids,
			List<String> process_ids) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(person_ids)) {
			p = cb.and(p, root.get(Read_.person).in(person_ids));
		}
		if (ListTools.isNotEmpty(application_ids) && ListTools.isNotEmpty(process_ids)) {
			p = cb.and(p,
					cb.or(root.get(Read_.application).in(application_ids), root.get(Read_.process).in(process_ids)));
		} else {
			if (ListTools.isNotEmpty(application_ids)) {
				p = cb.and(p, root.get(Read_.application).in(application_ids));
			}
			if (ListTools.isNotEmpty(process_ids)) {
				p = cb.and(p, root.get(Read_.process).in(process_ids));
			}
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}