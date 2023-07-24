package com.x.bbs.assemble.control.jaxrs.shutup;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSShutup;
import com.x.bbs.entity.BBSShutup_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionListPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(!business.isManager(effectivePerson)){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(BBSShutup.class, Wo.copier, p, page, size, BBSShutup.createTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(BBSShutup.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(BBSShutup.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<BBSShutup> root = cq.from(BBSShutup.class);
		Predicate p = cb.conjunction();

		if(StringUtils.isNotEmpty(wi.getPerson())){
			String person = business.organization().person().get(wi.getPerson());
			if(StringUtils.isEmpty(person)){
				person = wi.getPerson();
			}
			p = cb.and(p, cb.equal(root.get(BBSShutup_.person), person));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("禁言用户")
		private String person;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends BBSShutup {

		private static final long serialVersionUID = 4928128631701115688L;

		static WrapCopier<BBSShutup, Wo> copier = WrapCopierFactory.wo(BBSShutup.class, Wo.class,
				JpaObject.singularAttributeField(BBSShutup.class, true, true),
				ListTools.toList(BBSShutup.unmuteDateTime_FIELDNAME));


	}

}
