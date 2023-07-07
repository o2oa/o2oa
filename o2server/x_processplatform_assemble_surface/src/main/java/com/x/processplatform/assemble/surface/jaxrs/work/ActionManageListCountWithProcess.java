package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageListCountWithProcess extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListCountWithProcess.class);

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
			Predicate p = cb.equal(root.get(Work_.application),  application.getId());
			Path<String> value = root.get(Work_.process);
			cq.multiselect(value, cb.count(root).as(Long.class)).where(p).groupBy(value);
			List<Wo> list = em.createQuery(cq).getResultList();
			List<Wo> wos = new ArrayList<>();
			for (Wo wo : list) {
				Process process = business.process().pick(wo.getValue());
				if (process != null && business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)){
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

		@FieldDescribe("流程标识")
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

}