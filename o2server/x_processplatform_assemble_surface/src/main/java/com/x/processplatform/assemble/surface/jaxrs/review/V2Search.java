package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SqlTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;

import io.swagger.v3.oas.annotations.media.Schema;

class V2Search extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Search.class);

	private static final int DEFAULT_PAGESIZE = 20;

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String person = this.getPerson(effectivePerson, business, wi.getPerson());
			String query = SqlTools.removeLikePatternEscapeCharacter(wi.getQuery());
			if (StringUtils.isEmpty(query)) {
				throw new ExceptionEmptyQuery();
			}
			Predicate predicate = this.toPredicate(business, person, query);
			int max = (wi.getSize() == null || wi.getSize() < 1) ? DEFAULT_PAGESIZE : wi.getSize();
			int startPosition = (wi.getPage() == null || wi.getPage() < 1) ? 0 : (wi.getPage() - 1) * max;
			List<Review> os = this.list(business, predicate, max, startPosition);
			Long count = this.count(business, predicate);
			result.setData(Wo.copier.copy(os));
			result.setCount(count);
		}
		return result;
	}

	private Predicate toPredicate(Business business, String person, String query) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.conjunction();
		if (StringUtils.isNotEmpty(person)) {
			p = cb.equal(root.get(Review_.person), person);
		}
		query = "%" + query + "%";
		return cb.and(p, cb.or(cb.like(root.get(Review_.title), query), cb.like(root.get(Review_.serial), query)));
	}

	private List<Review> list(Business business, Predicate predicate, Integer max, Integer startPosition)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		return em.createQuery(cq.select(root).where(predicate).orderBy(cb.desc(root.get(JpaObject_.sequence))))
				.setMaxResults(max).setFirstResult(startPosition).getResultList();
	}

	private Long count(Business business, Predicate predicate) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		return em.createQuery(cq.select(cb.count(root)).where(predicate)).getSingleResult();
	}

	private String getPerson(EffectivePerson effectivePerson, Business business, String person) throws Exception {
		if ((!effectivePerson.isManager()) && (!effectivePerson.isCipher())) {
			return effectivePerson.getDistinguishedName();
		} else {
			if (StringUtils.isNotBlank(person)) {
				String p = business.organization().person().get(person);
				if (StringUtils.isBlank(p)) {
					throw new ExceptionEntityNotExist(p);
				} else {
					return p;
				}
			}
			return null;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.review.ActionSearch$Wi")
	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 3627613954808043653L;

		@FieldDescribe("搜索内容.")
		@Schema(description = "搜索内容.")
		private String query;

		@FieldDescribe("分页.")
		@Schema(description = "分页.")
		private Integer page;

		@FieldDescribe("数量.")
		@Schema(description = "数量.")
		private Integer size;

		@FieldDescribe("用户.")
		@Schema(description = "用户.")
		private String person;

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public Integer getPage() {
			return page;
		}

		public void setPage(Integer page) {
			this.page = page;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.review.ActionSearch$Wo")
	public static class Wo extends Review {

		private static final long serialVersionUID = -4773789253221941109L;

		static WrapCopier<Review, Wo> copier = WrapCopierFactory.wo(Review.class, Wo.class,
				JpaObject.singularAttributeField(Review.class, true, false), JpaObject.FieldsInvisible);

	}

}
