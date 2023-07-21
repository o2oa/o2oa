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
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;

class ActionEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Unit unit = business.unit().pick(flag);
            Unit oldUnit = unit;
            boolean checkFlag = false;

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

            unit.setControllerList(ListTools.extractProperty(
                    business.person().pick(ListTools.trim(unit.getControllerList(), true, true)),
                    JpaObject.id_FIELDNAME, String.class, true, true));
            Wi.copier.copy(wi, unit);
            /** 如果唯一标识不为空,要检查唯一标识是否唯一 */
            if (this.duplicateUniqueWhenNotEmpty(business, unit)) {
                throw new ExceptionDuplicateUnique(unit.getName(), unit.getUnique());
            }
            if (this.checkNameInvalid(business, unit)) {
                throw new ExceptionNameInvalid(unit.getName());
            }
            /** 判断同一级别下name不重复 */
            if (this.duplicateName(business, unit)) {
                throw new ExceptionDuplicateName(unit.getName());
            }
            // 现在由于可能修改了上级组织排序,所以都需要重新计算.
            business.unit().adjustInherit(unit);
            emc.check(unit, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(Unit.class);

            /** 判断是否修改了组织级别或组织名称,如果修改了，需要重新计算当前组织及下属组织成员的身份（组织名称，组织级别名称） */
            checkFlag = this.checkUnitTypeName(oldUnit, unit);
            if (checkFlag) {
                this.updateIdentityUnitNameAndUnitLevelName(unit, business);
            }

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

        static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify, Unit.pinyin_FIELDNAME, Unit.pinyinInitial_FIELDNAME,
                        Unit.level_FIELDNAME, Unit.levelName_FIELDNAME));
    }

    /**
     * 根据组织标志列出身份列表
     * 
     * @param business
     * @param unit
     * @return
     * @throws Exception
     */
    private List<Identity> listIdentityByUnitFlag(Business business, Unit unit) throws Exception {
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

    void updateIdentityUnitNameAndUnitLevelName(Unit unit, Business business) throws Exception {
        /*
         * 同时更新unit下的所有身份的UnitLevelName，UnitName
         */
        List<Unit> unitList = new ArrayList<>();
        unitList.add(unit);
        unitList.addAll(business.unit().listSubNestedObject(unit));
        EntityManagerContainer emc = business.entityManagerContainer();
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
                    CacheManager.notify(Identity.class);
                }
            }

        }
    }

    private boolean checkUnitTypeName(Unit oldUnit, Unit unit) throws Exception {
        List<String> oldUnitType = oldUnit.getTypeList();
        List<String> unitType = unit.getTypeList();
        // 判断两个list是否相同
        if (oldUnitType.retainAll(unitType) || (!StringUtils.equals(oldUnit.getName(), unit.getName()))
                || (!StringUtils.equals(oldUnit.getSuperior(), unit.getSuperior()))) {
            return true;
        }
        return false;
    }

}
