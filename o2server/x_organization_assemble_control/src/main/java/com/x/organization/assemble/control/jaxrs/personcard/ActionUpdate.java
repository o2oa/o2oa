package com.x.organization.assemble.control.jaxrs.personcard;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersonCard;

public class ActionUpdate extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String cardid, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, cardid:{}.", effectivePerson::getDistinguishedName, () -> cardid);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            PersonCard personCard = business.personCard().pick(cardid);

            if (null == personCard) {
                throw new ExceptionPersonCardNotExist(cardid);
            }
            Wi.copier.copy(wi, personCard);
            emc.beginTransaction(PersonCard.class);

            PersonCard entityPerson = emc.find(cardid, PersonCard.class);
            if (null == personCard.getId() || StringUtils.isBlank(entityPerson.getId())) {
                entityPerson.setId(cardid);
                throw new ExceptionPersonCardNotExist(cardid);
            }

            personCard.copyTo(entityPerson);
            if (entityPerson.getGroupType().equals("")) {
                entityPerson.setGroupType("默认分组");
            }

            emc.check(entityPerson, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(PersonCard.class);

            Wo wo = new Wo();
            wo.setId(personCard.getId());
            result.setData(wo);
            return result;
        }
    }

    static class Wi extends PersonCard {
        private static final long serialVersionUID = -4714395467753481398L;
        static WrapCopier<Wi, PersonCard> copier = WrapCopierFactory.wi(Wi.class, PersonCard.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial", "distinguishedName", "inputTime",
                        "orderNumber"));
    }

    public static class Wo extends WoPersonCardAbstract {
        private static final long serialVersionUID = 7871578639804765941L;
        static WrapCopier<PersonCard, Wo> copier = WrapCopierFactory.wo(PersonCard.class, Wo.class, null,
                JpaObject.FieldsUnmodify);
    }

    /*
     * public void copyTo(Object a,Object o, boolean ignoreNull) throws Exception {
     * List<String> list = new ArrayList<String>();
     * Collection<String> excludes = list;
     * for (Field fld : FieldUtils.getAllFields(a.getClass())) {
     * if (!excludes.contains(fld.getName())) {
     * if (PropertyUtils.isReadable(a, fld.getName()) &&
     * PropertyUtils.isWriteable(o, fld.getName())) {
     * Object value = PropertyUtils.getProperty(a, fld.getName());
     * if (ignoreNull && (null == value)) {
     * value = PropertyUtils.getProperty(o, fld.getName());
     * }
     * PropertyUtils.setProperty(o, fld.getName(), value);
     * }
     * }
     * }
     * }
     */
}
