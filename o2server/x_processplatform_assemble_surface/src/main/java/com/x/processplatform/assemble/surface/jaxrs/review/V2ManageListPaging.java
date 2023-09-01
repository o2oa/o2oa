package com.x.processplatform.assemble.surface.jaxrs.review;

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
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class V2ManageListPaging extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ManageListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(Review.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Review> root = cq.from(Review.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi, true);
			if(ListTools.isNotEmpty(wi.getPersonList())) {
				List<String> person_ids = business.organization().person().list(wi.getPersonList());
				p = cb.and(p, root.get(Review_.person).in(person_ids));
			}
			if (ListTools.isNotEmpty(wi.getJobList())) {
				p = cb.and(p, root.get(Review_.job).in(wi.getJobList()));
			}
			if (ListTools.isNotEmpty(wi.getIdList())) {
				p = cb.and(p, root.get(Review_.id).in(wi.getIdList()));
			}
			String orderBy = StringUtils.isBlank(wi.getOrderBy()) ? Review.sequence_FIELDNAME : wi.getOrderBy();
			List<Wo> wos;
			if(BooleanUtils.isTrue(wi.getAscOrder())){
				wos = emc.fetchAscPaging(Review.class, Wo.copier, p, page, size, orderBy);
			}else{
				wos = emc.fetchDescPaging(Review.class, Wo.copier, p, page, size, orderBy);
			}
			result.setData(wos);
			result.setCount(emc.count(Review.class, p));
			this.relate(business, result.getData(), wi);
			return result;
		}
	}

	public static class Wi extends RelateFilterWi {

		@FieldDescribe("参阅用户")
		private List<String> personList = new ArrayList<>();

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

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}
	}

	public static class Wo extends AbstractWo {
		private static final long serialVersionUID = 6612518284150311901L;
		static WrapCopier<Review, Wo> copier = WrapCopierFactory.wo(Review.class, Wo.class,
				JpaObject.singularAttributeField(Review.class, true, false), JpaObject.FieldsInvisible);
	}
}
