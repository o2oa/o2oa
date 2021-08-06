package com.x.organization.assemble.control.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;

class ActionOrder extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String followFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Identity identity = business.identity().pick(flag);
			if (null == identity) {
				throw new ExceptionIdentityNotExist(flag);
			}
			/** 重新加载对象,之前是从缓存中取得 */
			identity = emc.find(identity.getId(), Identity.class);
			Unit unit = business.unit().pick(identity.getUnit());
			if (null == unit) {
				throw new ExceptionUnitNotExist(identity.getUnit());
			}
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionAccessDenied(effectivePerson, unit);
			}
			Identity followIdentity = null;
			if (!StringUtils.equals(followFlag, EMPTY_SYMBOL)) {
				followIdentity = business.identity().pick(followFlag);
				if (null == followIdentity) {
					throw new ExceptionIdentityNotExist(followFlag);
				}
				/** 重新加载对象,之前是从缓存中取得 */
				followIdentity = emc.find(followIdentity.getId(), Identity.class);
				if (followIdentity.equals(identity)) {
					throw new ExceptionSameNotOrder(flag, followFlag);
				}
			}
			List<Identity> os = this.list(business, identity.getUnit());
			os = ListUtils.subtract(os, ListTools.toList(identity));
			/** 至少有2条数据才需要排序 */
			if (!os.isEmpty()) {
				os = business.identity().sort(os);
				if (null == followIdentity) {
					os.add(identity);
				} else {
					List<Identity> _sort = new ArrayList<>();
					for (Identity o : os) {
						if (o.equals(followIdentity)) {
							_sort.add(identity);
						}
						_sort.add(o);
					}
					os = _sort;
				}
			}
			emc.beginTransaction(Identity.class);
			int order = 1;
			for (Identity o : os) {
				o.setOrderNumber(order++);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			CacheManager.notify(Identity.class);
			CacheManager.notify(Unit.class);
			return result;
		}
	}

	private List<Identity> list(Business business, String unitId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.unit), unitId);
		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	public static class Wo extends WrapBoolean {

	}

}
