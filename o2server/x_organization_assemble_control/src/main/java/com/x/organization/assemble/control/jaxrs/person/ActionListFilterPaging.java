package com.x.organization.assemble.control.jaxrs.person;

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
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.enums.PersonStatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionListFilterPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Predicate p = toFilterPredicate(business, wi, effectivePerson);
			List<Wo> wos = emc.fetchDescPaging(Person.class, Wo.copier, p, page, size, JpaObject.sequence_FIELDNAME);
			this.updateControl(effectivePerson, business, wos);
			result.setData(wos);
			result.setCount(emc.count(Person.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(Business business,  Wi wi, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Person> root = cq.from(Person.class);
		Predicate predicate = business.personPredicateWithTopUnit(effectivePerson, true);
		if (StringUtils.isNotBlank(wi.getKey())) {
			String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
			Predicate p = cb.like(cb.lower(root.get(Person_.name)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.unique)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyin)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyinInitial)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.mobile)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.distinguishedName)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			predicate = cb.and(predicate, p);
		}
		if(StringUtils.isNotBlank(wi.getStatus())){
			if(PersonStatusEnum.NORMAL.getValue().equals(wi.getStatus())){
				predicate = cb.and(predicate, cb.or(cb.isNull(root.get(Person_.status)), cb.equal(root.get(Person_.status), PersonStatusEnum.NORMAL.getValue())));
			}else{
				predicate = cb.and(predicate, cb.equal(root.get(Person_.status), wi.getStatus()));
			}
		}
		return predicate;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("搜索关键字")
		private String key;
		@FieldDescribe("状态：0|正常、1|锁定、2|禁用.")
		private String status;


		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class Wo extends WoPersonAbstract {

		private static final long serialVersionUID = 1847108296662273067L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class,
				JpaObject.singularAttributeField(Person.class, true, true), null);

	}
}
