package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Person person = business.person().pick(wi.getPerson());
			if (null == person) {
				throw new ExceptionPersonNotExist(wi.getPerson());
			}
			person = emc.find(person.getId(), Person.class);
			Unit unit = business.unit().pick(wi.getUnit());
			if (null == unit) {
				throw new ExceptionUnitNotExist(wi.getUnit());
			}
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionAccessDenied(effectivePerson, unit);
			}
			if (this.existedWithPersonWithUnit(business, person, unit)) {
				throw new ExceptionExistInUnit(person, unit);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			Identity identity = new Identity();
			Wi.copier.copy(wi, identity);
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.uniqueDuplicateWhenNotEmpty(business, identity)) {
				throw new ExceptionDuplicateUnique(identity.getName(), identity.getUnique());
			}
			identity.setUnit(unit.getId());
			identity.setUnitLevel(unit.getLevel());
			identity.setUnitLevelName(unit.getLevelName());
			identity.setUnitName(unit.getName());
			identity.setPerson(person.getId());
			/* 设置主身份 */
			List<Identity> others = emc.listEqual(Identity.class, Identity.person_FIELDNAME, identity.getPerson());
			if (others.isEmpty()) {
				identity.setMajor(true);
			} else {
				if (BooleanUtils.isTrue(identity.getMajor())) {
					for (Identity o : others) {
						if (!StringUtils.equals(identity.getId(), o.getId())) {
							o.setMajor(false);
						}
					}
				}
			}

			emc.beginTransaction(Identity.class);
			emc.beginTransaction(Person.class);

			emc.persist(identity, CheckPersistType.all);
			List<Unit> topUnits = business.unit()
					.pick(ListTools.trim(person.getTopUnitList(), true, true, this.topUnit(business, unit).getId()));
			person.setTopUnitList(ListTools.extractField(topUnits, Unit.id_FIELDNAME, String.class, true, true));
			emc.persist(person, CheckPersistType.all);

			emc.commit();

			ApplicationCache.notify(Identity.class);
			ApplicationCache.notify(Person.class);
			Wo wo = new Wo();
			wo.setId(identity.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Identity {

		private static final long serialVersionUID = -6314932919066148113L;

		static WrapCopier<Wi, Identity> copier = WrapCopierFactory.wi(Wi.class, Identity.class, null, ListTools
				.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial", "unitName", "unitLevel", "unitLevelName"));
	}

	private boolean existedWithPersonWithUnit(Business business, Person person, Unit unit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.unit), unit.getId());
		p = cb.and(p, cb.equal(root.get(Identity_.person), person.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

}
