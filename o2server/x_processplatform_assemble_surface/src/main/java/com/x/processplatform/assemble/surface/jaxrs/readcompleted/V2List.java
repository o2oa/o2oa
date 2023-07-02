package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;

import io.swagger.v3.oas.annotations.media.Schema;

class V2List extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2List.class);

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if ((!wi.isEmptyFilter()) || ListTools.isNotEmpty(wi.getJobList()) || ListTools.isNotEmpty(wi.getIdList())) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EntityManager em = emc.get(ReadCompleted.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
				Root<ReadCompleted> root = cq.from(ReadCompleted.class);
				Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
				if (ListTools.isNotEmpty(wi.getJobList())) {
					p = cb.and(p, root.get(ReadCompleted_.job).in(wi.getJobList()));
				}
				if (ListTools.isNotEmpty(wi.getIdList())) {
					p = cb.and(p, root.get(ReadCompleted_.id).in(wi.getIdList()));
				}
				wos = emc.fetch(ReadCompleted.class, Wo.copier, p);
				this.relate(business, wos, wi);
			}
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.V2List$Wi")
	public static class Wi extends RelateFilterWi {

		private static final long serialVersionUID = 6156652409095161653L;

		@FieldDescribe("任务标识.")
		@Schema(description = "任务标识.")
		private List<String> jobList = new ArrayList<>();

		@FieldDescribe("已阅标识.")
		@Schema(description = "已阅标识.")
		private List<String> idList = new ArrayList<>();

		public List<String> getJobList() {
			return jobList;
		}

		public void setJobList(List<String> jobList) {
			this.jobList = jobList;
		}

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.V2List$Wo")
	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = -4773789253221941109L;

		static WrapCopier<ReadCompleted, Wo> copier = WrapCopierFactory.wo(ReadCompleted.class, Wo.class,
				JpaObject.singularAttributeField(ReadCompleted.class, true, false), JpaObject.FieldsInvisible);

	}
}
