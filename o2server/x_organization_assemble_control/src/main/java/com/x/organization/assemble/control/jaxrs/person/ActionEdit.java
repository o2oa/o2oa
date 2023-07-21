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
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

class ActionEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Person person = business.person().pick(flag);
            if (null == person) {
                throw new ExceptionPersonNotExist(flag);
            }
            if (!this.editable(business, effectivePerson, person)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            boolean isNameUpdate = false;
            if (!person.getName().equals(wi.getName())) {
                isNameUpdate = true;
            }
            Wi.copier.copy(wi, person);
            // 防止创建的时候加了空格
            person.setName(StringUtils.trim(person.getName()));
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
            this.convertControllerList(effectivePerson, business, person);

            // 重新计算顶层组织
            person.setTopUnitList(ListTools.extractField(business.listTopUnitWithPerson(person.getId()),
                    JpaObject.id_FIELDNAME, String.class, true, true));

            List<Identity> identityList = business.entityManagerContainer().listEqual(Identity.class,
                    Identity.person_FIELDNAME, person.getId());
            emc.beginTransaction(Person.class);
            emc.beginTransaction(Identity.class);
            /*
             * 从内存中pick出来的无法作为实体保存,不能在前面执行,以为后面的convertControllerList也有一个pick,
             * 会导致一当前这个对象再次被detech
             */
            Person entityPerson = emc.find(person.getId(), Person.class);
            person.copyTo(entityPerson);
            emc.check(entityPerson, CheckPersistType.all);
            if (isNameUpdate && !identityList.isEmpty()) {
                for (Identity identity : identityList) {
                    identity.setName(person.getName());
                    emc.check(identity, CheckPersistType.all);
                }
            }
            emc.commit();
            // 刷新缓存
            if (isNameUpdate) {
                CacheManager.notify(Identity.class);
            }
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
                        Person.lastLoginTime_FIELDNAME, Person.lastLoginAddress_FIELDNAME,
                        Person.lastLoginClient_FIELDNAME, Person.topUnitList_FIELDNAME));
    }

    private void convertControllerList(EffectivePerson effectivePerson, Business business, Person person)
            throws Exception {
        List<String> list = new ArrayList<>();
        if (!Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
            list.add(effectivePerson.getDistinguishedName());
        }
        if (ListTools.isNotEmpty(person.getControllerList())) {
            list.addAll(person.getControllerList());
        }
        List<Person> os = business.person().pick(list);
        List<String> ids = ListTools.extractProperty(os, JpaObject.id_FIELDNAME, String.class, true, true);
        ids.remove(person.getId());
        person.setControllerList(ids);
    }

}
