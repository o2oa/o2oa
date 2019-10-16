package com.x.organization.assemble.control.jaxrs.identity;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

public class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Identity identity = business.identity().pick(flag);
			if (null == identity) {
				throw new ExceptionEntityNotExist(flag,Identity.class);
			}
			if (StringUtils.isNotEmpty(identity.getUnit())) {
				Unit unit = business.unit().pick(identity.getUnit());
				if (null == unit) {
					throw new ExceptionUnitNotExist(identity.getUnit());
				}
				if (!business.editable(effectivePerson, unit)) {
					throw new ExceptionAccessDenied(effectivePerson, unit);
				}
				/** 由于有关联所以要分段提交，提交UnitDuty的成员删除。 */
				emc.beginTransaction(UnitDuty.class);
				this.removeMemberOfUnitDuty(business, identity);
				emc.commit();
			}
			/** 由于前面pick出来的需要重新取出 */
			identity = emc.find(identity.getId(), Identity.class);
			// /** 删除下属身份 */
			// emc.beginTransaction(Identity.class);
			// this.removeMemberOfJunior(business, identity);
			// emc.commit();
			/** 最后进行身份的删除 */
			/* 设置主身份 */
			final String id = identity.getId();
			List<Identity> others = emc.listEqual(Identity.class, Identity.person_FIELDNAME, identity.getPerson())
					.stream().filter(o -> !StringUtils.equals(id, o.getId()))
					.sorted(Comparator.comparing(Identity::getCreateTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
			for (int i = 0; i < others.size(); i++) {
				if (i == 0) {
					others.get(i).setMajor(true);
				} else {
					others.get(i).setMajor(false);
				}
			}
			emc.beginTransaction(Identity.class);
			emc.remove(identity, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Identity.class);
			Wo wo = new Wo();
			wo.setId(identity.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	// private void removeMemberOfJunior(Business business, Identity identity)
	// throws Exception {
	// EntityManager em = business.entityManagerContainer().get(Identity.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
	// Root<Identity> root = cq.from(Identity.class);
	// Predicate p = cb.isMember(identity.getId(),
	// root.get(Identity_.juniorList));
	// List<Identity> os =
	// em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
	// for (Identity o : os) {
	// o.getJuniorList().remove(identity.getId());
	// }
	// }

	private void removeMemberOfUnitDuty(Business business, Identity identity) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(identity.getId(), root.get(UnitDuty_.identityList));
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		for (UnitDuty o : os) {
			o.getIdentityList().remove(identity.getId());
		}
	}
}