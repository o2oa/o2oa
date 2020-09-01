package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.lang3.BooleanUtils;

class ActionListCountWithProcess extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
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
			Path<String> value = root.get(Work_.process);
			cq.multiselect(value, cb.count(root).as(Long.class)).where(p).groupBy(value);
			List<Wo> list = em.createQuery(cq).getResultList();
			List<Wo> wos = new ArrayList<>();
			for (Wo wo : list) {
				Process process = business.process().pick(wo.getValue());
				if (process != null){
					String name = process.getName();
					if (process.getEditionNumber() != null) {
						name = name + "V" + process.getEditionNumber();
					}
					wo.setName(name);
					wos.add(wo);
				}
			}
			SortTools.asc(wos, "name");
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("流程标志")
		private String value;

		@FieldDescribe("流程名称")
		private String name;

		@FieldDescribe("数量")
		private Long count;

		public Wo(String value, Long count){
			this.value = value;
			this.count = count;
		}

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