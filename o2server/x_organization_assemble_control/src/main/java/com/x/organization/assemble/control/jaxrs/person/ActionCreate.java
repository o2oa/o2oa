package com.x.organization.assemble.control.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ActionCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Person person = new Person();
            Wi.copier.copy(wi, person);
            // 防止创建的时候加了空格
            person.setName(StringUtils.trim(person.getName()));
            if ((!business.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager,
                    OrganizationDefinition.PersonManager, OrganizationDefinition.Manager))
                    && (!effectivePerson.isManager()) && (!effectivePerson.isCipher())) {
                throw new ExceptionAccessDenied(effectivePerson);
            }

            if (!effectivePerson.isManager()
                    && business.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager,
                            OrganizationDefinition.Manager)) {
                Person current = business.person().pick(effectivePerson.getDistinguishedName());
                List<Unit> topUnits = business.unit().pick(current.getTopUnitList());
                person.setTopUnitList(
                        ListTools.extractField(topUnits, JpaObject.id_FIELDNAME, String.class, true, true));
            } else {
                person.setTopUnitList(new ArrayList<>());
            }
            this.checkName(business, person.getName(), person.getId());
            this.checkMobile(business, person.getMobile(), person.getId());
            this.checkEmployee(business, person.getEmployee(), person.getId());
            if (StringUtils.isNotEmpty(person.getUnique())) {
                this.checkUnique(business, person.getUnique(), person.getId());
            }
            this.checkMail(business, person.getMail(), person.getId());
            if (StringUtils.isNotEmpty(wi.getSuperior())) {
                Person superior = business.person().pick(wi.getSuperior());
                if (null == superior) {
                    throw new ExceptionSuperiorNotExist(wi.getSuperior());
                }
                person.setSuperior(superior.getId());
            }

            // 不设置默认头像,可以通过为空直接显示默认头像
            if (StringUtils.isNotEmpty(wi.getPassword())) {
                business.person().setPassword(person, wi.getPassword(), true);
            } else {
                business.person().setPassword(person, this.initPassword(business, person), true);
            }
            // 设置默认管理员
            this.convertControllerList(effectivePerson, business, person);
            emc.beginTransaction(Person.class);
            emc.persist(person, CheckPersistType.all);
            emc.commit();
            // 刷新缓存
            CacheManager.notify(Person.class);
            // 通知x_collect_service_transmit同步数据到collect
            business.instrument().collect().person();

            Wo wo = new Wo();
            wo.setId(person.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {

    }

    public static class Wi extends Person {

        private static final long serialVersionUID = 1571810726944802231L;

        static WrapCopier<Wi, Person> copier = WrapCopierFactory.wi(Wi.class, Person.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify, Person.icon_FIELDNAME, Person.pinyin_FIELDNAME,
                        Person.pinyinInitial_FIELDNAME, Person.password_FIELDNAME, Person.passwordExpiredTime_FIELDNAME,
                        Person.changePasswordTime_FIELDNAME, Person.lastLoginTime_FIELDNAME,
                        Person.lastLoginAddress_FIELDNAME, Person.lastLoginClient_FIELDNAME));

    }

    private void convertControllerList(EffectivePerson effectivePerson, Business business, Person person)
            throws Exception {
        List<String> list = new ArrayList<>();
        if (effectivePerson.isManager()) {
            list.add(effectivePerson.getDistinguishedName());
        }
        if (ListTools.isNotEmpty(person.getControllerList())) {
            list.addAll(person.getControllerList());
        }
        if (ListTools.isNotEmpty(list)) {
            List<Person> os = business.person().pick(list);
            List<String> ids = ListTools.extractProperty(os, JpaObject.id_FIELDNAME, String.class, true, true);
            ids.remove(person.getId());
            person.setControllerList(ids);
        }
    }

}
