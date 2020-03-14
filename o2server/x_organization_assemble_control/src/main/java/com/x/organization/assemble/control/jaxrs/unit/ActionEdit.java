package com.x.organization.assemble.control.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.assemble.control.message.OrgBodyMessage;
import com.x.organization.assemble.control.message.OrgMessage;
import com.x.organization.assemble.control.message.OrgMessageFactory;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

class ActionEdit extends BaseAction {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Unit unit = business.unit().pick(flag);
			if (null == unit) {
				throw new ExceptionUnitNotExist(flag);
			}
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionDenyEditUnit(effectivePerson, flag);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}

			/** pick出来的对象需要重新取出 */
			emc.beginTransaction(Unit.class);
			unit = emc.find(unit.getId(), Unit.class);
			
			Gson gsontool = new Gson();
			String strOriginalUnit = gsontool.toJson(unit);
			
			unit.setControllerList(ListTools.extractProperty(business.person().pick(ListTools.trim(unit.getControllerList(), true, true)),
					JpaObject.id_FIELDNAME, String.class, true, true));
			Wi.copier.copy(wi, unit);
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.duplicateUniqueWhenNotEmpty(business, unit)) {
				throw new ExceptionDuplicateUnique(unit.getName(), unit.getUnique());
			}
			/** 判断同一级别下name不重复 */
			if (this.duplicateName(business, unit)) {
				throw new ExceptionDuplicateName(unit.getName());
			}
			business.unit().adjustInherit(unit);
			emc.check(unit, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Unit.class);
			
			this.updateIdentityUnitNameAndUnitLevelName(effectivePerson, flag, jsonElement);

			/**创建 组织变更org消息通信 */
			OrgMessageFactory  orgMessageFactory = new OrgMessageFactory();
			orgMessageFactory.createMessageCommunicate("modfiy", "unit",strOriginalUnit, unit, effectivePerson);
			
			Wo wo = new Wo();
			wo.setId(unit.getId());
			result.setData(wo);
			return result;

		}

	}

	public static class Wo extends WoId {
	}

	public static class Wi extends Unit {

		private static final long serialVersionUID = -7527954993386512109L;

		//		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
		//				ListTools.toList(JpaObject.FieldsUnmodify, Unit.superior_FIELDNAME, Unit.pinyin_FIELDNAME,
		//						Unit.pinyinInitial_FIELDNAME, Unit.level_FIELDNAME, Unit.levelName_FIELDNAME,
		//						Unit.inheritedControllerList_FIELDNAME));
		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Unit.pinyin_FIELDNAME, Unit.pinyinInitial_FIELDNAME, Unit.level_FIELDNAME,
						Unit.levelName_FIELDNAME, Unit.inheritedControllerList_FIELDNAME));
	}

	//根据组织标志列出身份列表
	private List<Identity> listIdentityByUnitFlag(Business business, Unit unit) throws Exception {
		//Unit unit = business.unit().pick(unitFlag);
		if (null == unit.getId() || StringUtils.isEmpty(unit.getId()) || null == unit) {
			throw new ExceptionUnitNotExist(unit.getId());
		}
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.unit), unit.getId());
		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	//列出所有递归下级组织（包含当前组织）
	private List<Unit> listUnit(Business business, String flag) throws Exception {
		//Unit unit = business.unit().pick(flag);

		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.id), flag);
		List<Unit> units = em.createQuery(cq.select(root).where(p)).getResultList();

		Unit unit;
		if (units.size() != 1) {
			unit = null;
			throw new ExceptionUnitNotExistOrNotUniqueUnitId(flag);
		} else {
			unit = units.get(0);
		}

		if (null == unit) {
			throw new ExceptionUnitNotExist(flag);
		}

		//所有下级组织
		List<Unit> os = business.unit().listSubNestedObject(unit);

		//把当前组织加入到os
		List<Unit> _currentUnitSingleArray = new ArrayList<Unit>();
		_currentUnitSingleArray.add(unit);
		os = ListTools.add(_currentUnitSingleArray, true, true, os);
		return os;
	}

	void updateIdentityUnitNameAndUnitLevelName(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		ApplicationCache.notify(Unit.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Unit unit = business.unit().pick(flag);
			if (null == unit) {
				throw new ExceptionUnitNotExist(flag);
			}
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionDenyEditUnit(effectivePerson, flag);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			/*
			 * 同时更新unit下的所有身份的UnitLevelName，UnitName
			 * */
			List<Unit> unitList = this.listUnit(business, flag);

			for (Unit u : unitList) {
				List<Identity> identityList = this.listIdentityByUnitFlag(business, u);
				if (ListTools.isNotEmpty(identityList)) {
					String _unitName = u.getName();
					String _unitLevelName = u.getLevelName();

					for (Identity i : identityList) {
						Identity _identity = emc.find(i.getId(), Identity.class);
						_identity.setUnitName(_unitName);
						_identity.setUnitLevelName(_unitLevelName);
						emc.beginTransaction(Identity.class);
						emc.check(_identity, CheckPersistType.all);
						emc.commit();
						ApplicationCache.notify(Identity.class);
					}
				}

			}

		}
	}
	
	

}
