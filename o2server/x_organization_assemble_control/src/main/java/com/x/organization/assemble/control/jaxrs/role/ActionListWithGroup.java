package com.x.organization.assemble.control.jaxrs.role;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

import net.sf.ehcache.Element;

class ActionListWithGroup extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String groupFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), groupFlag);
			Element element =  business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, groupFlag);
				 business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wo extends WoRoleAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Role, Wo> copier = WrapCopierFactory.wo(Role.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Wo> list(Business business, String groupFlag) throws Exception {
		/** 检查Group是否存在 */
		Group group = business.group().pick(groupFlag);
		if (null == group) {
			throw new ExceptionGroupNotExist(groupFlag);
		}
		List<String> groups = new ArrayList<>();
		groups.addAll(business.group().listSupNested(group.getId()));
		/* 将自己加入到查询中 */
		groups.add(group.getId());
		EntityManager em = business.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> cq = cb.createQuery(Role.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = root.get(Role_.groupList).in(groups);
		List<Role> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		List<Wo> wos = Wo.copier.copy(os);
		wos = business.role().sort(wos);
		return wos;
	}

}