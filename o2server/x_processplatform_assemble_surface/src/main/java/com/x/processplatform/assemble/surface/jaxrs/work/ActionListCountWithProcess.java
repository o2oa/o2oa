package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionListCountWithProcessWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListCountWithProcess extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListCountWithProcess.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		LOGGER.debug("execute:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName, () -> applicationFlag);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			EntityManager em = business.entityManagerContainer().get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Wo> cq = cb.createQuery(Wo.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.and(p, cb.equal(root.get(Work_.application), application.getId()));
			Path<String> processPath = root.get(Work_.process);
			Path<String> processNamePath = root.get(Work_.processName);
			cq.multiselect(processPath, processNamePath, cb.count(root).as(Long.class)).where(p).groupBy(processPath,
					processNamePath);
			List<Wo> wos = em.createQuery(cq).getResultList().stream()
					.sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionListCountWithProcess$Wo")
	public static class Wo extends ActionListCountWithProcessWo {

		public Wo(String value, String name, Long count) {
			super(value, name, count);
		}

	}

}