package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.List;

import javax.persistence.criteria.Predicate;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;

public class ActionListPrev extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			ActionResult<List<Wo>> result = this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME,
					DESC, p);
			return result;
		}
	}

	public class Wi extends FilterWi {
	}

	public static class Wo extends Review {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Review, Wo> copier = WrapCopierFactory.wo(Review.class, Wo.class,
				JpaObject.singularAttributeField(Review.class, true, true), JpaObject.FieldsInvisible);

	}

}
