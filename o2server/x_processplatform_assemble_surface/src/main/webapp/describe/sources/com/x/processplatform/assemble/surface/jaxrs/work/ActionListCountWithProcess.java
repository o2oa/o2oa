package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;

class ActionListCountWithProcess extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			EntityManager em = business.entityManagerContainer().get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.and(p, cb.equal(root.get(Work_.application), application.getId()));
			javax.persistence.criteria.Path<String> processPath = root.get(Work_.process);
			javax.persistence.criteria.Path<String> processNamePath = root.get(Work_.processName);
			/* count group by 的值只会返回首行，看上去是个BUG 只能分成多个语句执行 */
			cq.multiselect(processPath, processNamePath).where(p).groupBy(processPath, processNamePath);
			for (Tuple o : em.createQuery(cq).getResultList()) {
				Wo wo = new Wo();
				String process = o.get(processPath);
				String processName = o.get(processNamePath);
				wo.setName(processName);
				wo.setValue(process);
				wo.setCount(this.countWithProcess(business, effectivePerson, process, processName));
				wos.add(wo);
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("流程名称")
		private String value;

		@FieldDescribe("流程标识")
		private String name;

		@FieldDescribe("数量")
		private Long count;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}

	/* 由于上面的BUG只能分行取 */
	private Long countWithProcess(Business business, EffectivePerson effectivePerson, String process,
			String processName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Work_.process), process));
		p = cb.and(p, cb.equal(root.get(Work_.processName), processName));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}