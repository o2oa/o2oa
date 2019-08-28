package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.element.Application;

class ActionListCountWithApplication extends BaseAction {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<NameValueCountPair> wraps = this.listApplicationPair(business, effectivePerson);
			for (NameValueCountPair o : wraps) {
				o.setCount(this.countWithApplication(business, effectivePerson, Objects.toString(o.getValue())));
			}
			result.setData(wraps);
			return result;
		}
	}

	private List<NameValueCountPair> listApplicationPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		List<NameValueCountPair> list = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p,
				cb.or(cb.equal(root.get(TaskCompleted_.latest), true), cb.isNull(root.get(TaskCompleted_.latest))));
		cq.select(root.get(TaskCompleted_.application)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		for (String str : os) {
			NameValueCountPair o = new NameValueCountPair();
			Application application = business.application().pick(str);
			if (null != application) {
				o.setValue(application.getId());
				o.setName(application.getName());
			} else {
				o.setValue(str);
				o.setName(str);
			}
			list.add(o);
		}
		SortTools.asc(list, "name");
		return list;
	}
}