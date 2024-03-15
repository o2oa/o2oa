package com.x.organization.assemble.control.jaxrs.person;

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
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Custom_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

class ActionListDeletePaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (!this.editable(business, effectivePerson, "")) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Predicate p = toFilterPredicate(business, wi);
			List<WoCustom> woCustoms = emc.fetchDescPaging(Custom.class, WoCustom.copier, p, page, size, JpaObject.sequence_FIELDNAME);
			List<Wo> wos = new ArrayList<>();
			for (WoCustom woCustom : woCustoms){
				Wo wo = gson.fromJson(woCustom.getData(), Wo.class);
				wo.setOperateTime(woCustom.getCreateTime());
				wos.add(wo);
			}
			result.setData(wos);
			result.setCount(emc.count(Custom.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(Business business,  Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Custom.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Custom> root = cq.from(Custom.class);
		Predicate p = cb.equal(root.get(Custom_.name), PERSON_DELETE_CUSTOM_NAME);
		if(StringUtils.isNotBlank(wi.getPerson())){
			p = cb.and(p, cb.like(root.get(Custom_.person), "%" + wi.getPerson() + "%", StringTools.SQL_ESCAPE_CHAR));
		}
		return p;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户")
		private String person;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends CustomPersonInfo {

		private static final long serialVersionUID = 8462618330733553654L;

	}

	public static class WoCustom extends Custom {

		private static final long serialVersionUID = 8501479874360244059L;
		static WrapCopier<Custom, WoCustom> copier = WrapCopierFactory.wo(Custom.class, WoCustom.class,
				JpaObject.singularAttributeField(Custom.class, true, false), null);

	}
}
