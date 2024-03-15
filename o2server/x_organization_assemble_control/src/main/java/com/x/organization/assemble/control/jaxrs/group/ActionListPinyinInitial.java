package com.x.organization.assemble.control.jaxrs.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;

class ActionListPinyinInitial extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getKey(), StringUtils.join(wi.getGroupList(), ","),
					StringUtils.join(wi.getRoleList(), ","));
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, wi);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
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

	public static class Wo extends WoGroupAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Group, Wo> copier = WrapCopierFactory.wo(Group.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (StringUtils.isEmpty(wi.getKey())) {
			return wos;
		}
		List<String> groupIds = business.expendGroupRoleToGroup(wi.getGroupList(), wi.getRoleList());
		String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.like(root.get(Group_.pinyinInitial), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);
		if (ListTools.isNotEmpty(groupIds)) {
			p = cb.and(p, root.get(Group_.id).in(groupIds));
		}
		List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
		wos = Wo.copier.copy(os);
		wos = business.group().sort(wos);
		return wos;
	}

}