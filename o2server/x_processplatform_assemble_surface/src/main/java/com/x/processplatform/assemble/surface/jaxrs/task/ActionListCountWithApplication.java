package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.element.Application;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListCountWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListCountWithApplication.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> list = list(business, effectivePerson);
			result.setData(list);
			return result;
		}
	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> from = cq.from(Task.class);
		Path<String> app = from.get(Task_.application);
		Predicate p = cb.equal(from.get(Task_.person), effectivePerson.getDistinguishedName());
		List<Tuple> os = em.createQuery(cq.groupBy(app).multiselect(app, cb.count(app)).where(p)).getResultList();
		List<Wo> list = new ArrayList<>();
		for (Tuple o : os) {
			Wo wo = new Wo();
			Application application = business.application().pick(o.get(app));
			wo.setName(application.getName());
			wo.setValue(application.getId());
			wo.setCount(o.get(1, Long.class));
			list.add(wo);
		}
		return list.stream().sorted(Comparator.comparing(o -> Objects.toString(o.getName(), "")))
				.collect(Collectors.toList());
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListCountWithApplication$Wo")
	public static class Wo extends NameValueCountPair {
	}

}
