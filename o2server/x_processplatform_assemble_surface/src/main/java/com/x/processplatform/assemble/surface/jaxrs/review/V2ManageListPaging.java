package com.x.processplatform.assemble.surface.jaxrs.review;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;

import javax.persistence.criteria.Predicate;
import java.util.List;

class V2ManageListPaging extends V2Base {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi, wi.getPersonList());
			List<Wo> wos = emc.fetchDescPaging(Review.class, Wo.copier, p, page, size, Review.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Review.class, p));
			this.relate(business, result.getData(), wi);
			return result;
		}
	}

	public static class Wi extends RelateFilterWi {

		@FieldDescribe("参阅用户")
		private List<String> personList;

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
