package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;

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
				EntityManager em = emc.get(Read.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
				Root<Read> root = cq.from(Read.class);
				Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
				if (ListTools.isNotEmpty(wi.getJobList())) {
					p = cb.and(p, root.get(Read_.job).in(wi.getJobList()));
				}
				if (ListTools.isNotEmpty(wi.getIdList())) {
					p = cb.and(p, root.get(Read_.id).in(wi.getIdList()));
				}
				wos = emc.fetch(Read.class, Wo.copier, p);
				this.relate(business, wos, wi);
			}
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.V2List$Wi")
	public static class Wi extends RelateFilterWi {

		private static final long serialVersionUID = -5931296443875372172L;

		@FieldDescribe("job标识")
		private List<String> jobList = new ArrayList<>();

		@FieldDescribe("标识")
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.V2List$Wo")
	public static class Wo extends AbstractWo {
		private static final long serialVersionUID = -4773789253221941109L;
		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class,
				JpaObject.singularAttributeField(Read.class, true, false), JpaObject.FieldsInvisible);
	}
}
