package com.x.organization.assemble.express.jaxrs.person;

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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

class ActionListFilterPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(business, wi);
			List<Wo> wos = emc.fetchAscPaging(Person.class, Wo.copier, p, page, size, Person.pinyin_FIELDNAME);
			this.hide(effectivePerson, business, wos);
			result.setData(wos);
			result.setCount(emc.count(Person.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(Business business,  Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.conjunction();
		if (StringUtils.isNotBlank(wi.getEmployee())) {
			p = cb.and(p, cb.equal(root.get(Person_.employee), wi.getEmployee()));
		}
		if (StringUtils.isNotBlank(wi.getMobile())) {
			p = cb.and(p, cb.equal(root.get(Person_.mobile), wi.getMobile()));
		}
		if (StringUtils.isNotBlank(wi.getUnique())) {
			p = cb.and(p, cb.equal(root.get(Person_.unique), wi.getUnique()));
		}
		if (StringUtils.isNoneBlank(wi.getName())) {
			String key = StringTools.escapeSqlLikeKey(wi.getName());
			p = cb.and(p, cb.like(root.get(Person_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
		}

		return p;
	}

	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -2991229948693512630L;

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("工号")
		private String employee;

		@FieldDescribe("唯一标识")
		private String unique;

		@FieldDescribe("手机号.")
		private String mobile;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmployee() {
			return employee;
		}

		public void setEmployee(String employee) {
			this.employee = employee;
		}

		public String getUnique() {
			return unique;
		}

		public void setUnique(String unique) {
			this.unique = unique;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
	}

	public static class Wo extends Person {

		private static final long serialVersionUID = 1847108296662273067L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class,
				JpaObject.singularAttributeField(Person.class, true, true), null);

	}
}
