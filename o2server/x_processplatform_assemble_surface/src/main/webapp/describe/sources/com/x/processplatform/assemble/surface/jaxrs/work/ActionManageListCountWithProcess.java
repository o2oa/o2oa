package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
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
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageListCountWithProcess extends BaseAction {

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
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.application), application.getId());
			cq.select(root.get(Work_.process)).where(p).distinct(true);
			List<String> list = em.createQuery(cq).getResultList();
			for (String str : list) {
				Wo wo = new Wo();
				Process process = business.process().pick(str);
				if (null != process) {
					if (business.canManageApplicationOrProcess(effectivePerson, application, process)) {
						wo.setValue(process.getId());
						wo.setName(process.getName());
						wo.setCount(this.countWithProcess(business, process));
						wos.add(wo);
					}
				}
			}
			SortTools.asc(wos, "name");
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
	//
	// private String getProcessName(Business business, String id) throws
	// Exception {
	// Process o = business.process().pick(id);
	// if (null != o) {
	// return o.getName();
	// }
	// EntityManagerContainer emc = business.entityManagerContainer();
	// EntityManager em = emc.get(Work.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<String> cq = cb.createQuery(String.class);
	// Root<Work> root = cq.from(Work.class);
	// Predicate p = cb.equal(root.get(Work_.process), id);
	// cq.select(root.get(Work_.processName)).where(p);
	// List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
	// if (!list.isEmpty()) {
	// return list.get(0);
	// }
	// return null;
	// }

	private Long countWithProcess(Business business, Process process) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.process), process.getId());
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}