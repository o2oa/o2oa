package com.x.organization.assemble.control.jaxrs.person;

import java.util.ArrayList;
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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

import net.sf.ehcache.Element;

class ActionListPinyinInitial extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), effectivePerson.getDistinguishedName(),
					wi.getKey(), StringUtils.join(wi.getGroupList(), ","), StringUtils.join(wi.getRoleList(), ","));
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, effectivePerson, wi);
				business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			this.hide(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("搜索关键字")
		private String key;
		@FieldDescribe("搜索群组范围,为空则不限定")
		private List<String> groupList = new ArrayList<>();
		@FieldDescribe("搜索角色范围,为空则不限定")
		private List<String> roleList = new ArrayList<>();

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

	}

	public static class Wo extends WoPersonAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null,
				person_fieldsInvisible);

	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (StringUtils.isEmpty(wi.getKey())) {
			return wos;
		}
		List<String> personIds = business.expendGroupRoleToPerson(wi.getGroupList(), wi.getRoleList());
		String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.like(root.get(Person_.pinyinInitial), str + "%", '\\');
		if (ListTools.isNotEmpty(personIds)) {
			p = cb.and(p, root.get(Person_.id).in(personIds));
		}
		p = cb.and(p, business.personPredicateWithTopUnit(effectivePerson));
		List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
		wos = Wo.copier.copy(os);
		wos = business.person().sort(wos);
		return wos;
	}
}