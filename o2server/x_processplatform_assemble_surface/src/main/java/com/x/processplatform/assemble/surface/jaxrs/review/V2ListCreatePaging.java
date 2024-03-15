package com.x.processplatform.assemble.surface.jaxrs.review;

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
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class V2ListCreatePaging extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListCreatePaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			EntityManager em = emc.get(Review.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Review> root = cq.from(Review.class);
			Predicate p = cb.equal(root.get(Review_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.and(p, this.toFilterPredicate(effectivePerson, business, wi, null));
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

	}

	public static class Wo extends AbstractWo {
		private static final long serialVersionUID = -4773789253221941109L;
		static WrapCopier<Review, Wo> copier = WrapCopierFactory.wo(Review.class, Wo.class,
				JpaObject.singularAttributeField(Review.class, true, false), JpaObject.FieldsInvisible);
	}
}
