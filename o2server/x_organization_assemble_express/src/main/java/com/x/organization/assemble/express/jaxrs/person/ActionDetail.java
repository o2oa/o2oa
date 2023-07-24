package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Nonuple;
import com.x.base.core.project.bean.tuple.Octuple;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.PersonDetail;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

class ActionDetail extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDetail.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> flag,
                () -> jsonElement);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            CacheKey cacheKey = new CacheKey(this.getClass(), flag, wi.getFetchIdentity(), wi.getFetchUnit(),
                    wi.getFetchUnitDuty(), wi.getFetchGroup(), wi.getFetchRole(), wi.getFetchPersonAttribute());
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                result.setData((Wo) optional.get());
            } else {
                Wo wo = new Wo();
                if (Config.token().isInitialManager(flag)) {
                    // 如果是xadmin单独处理
                    wo.setDistinguishedName(Config.token().initialManagerInstance().getDistinguishedName());
                } else {
                    Person person = business.person().pick(flag);
                    if (null != person) {
                        wo = detail(business, person, wi);
                    }
                    CacheManager.put(cacheCategory, cacheKey, wo);
                }
                result.setData(wo);
            }
            return result;
        }
    }

    private Wo detail(Business business, Person person, Wi wi) {
        boolean fetchIdentity = BooleanUtils.isNotFalse(wi.getFetchIdentity());
        boolean fetchUnit = BooleanUtils.isNotFalse(wi.getFetchUnit());
        boolean fetchUnitDuty = BooleanUtils.isNotFalse(wi.getFetchUnitDuty());
        boolean fetchGroup = BooleanUtils.isNotFalse(wi.getFetchGroup());
        boolean fetchRole = BooleanUtils.isNotFalse(wi.getFetchRole());
        boolean fetchPersonAttribute = BooleanUtils.isNotFalse(wi.getFetchPersonAttribute());
        // first business, second person, third identity, fourth unit, fifth unitDuty,
        // sixth group, seventh role, eighth personAttribute
        Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param = Octuple
                .of(business, person, new ListOrderedSet<String>(), new ListOrderedSet<String>(),
                        new ListOrderedSet<String>(), new ListOrderedSet<String>(), new ListOrderedSet<String>(),
                        new ListOrderedSet<String>());

        List<UnaryOperator<Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>>>> functions = new ArrayList<>();

        if (fetchIdentity || fetchUnit || fetchUnitDuty || fetchGroup || fetchRole) {
            functions.add(this::funcDetailIdentity);
        }

        if (fetchUnit || fetchUnitDuty || fetchGroup || fetchRole) {
            functions.add(this::funcDetailUnit);
        }

        if (fetchUnitDuty || fetchGroup || fetchRole) {
            functions.add(this::funcDetailUnitDuty);
        }

        if (fetchGroup || fetchRole) {
            functions.add(this::funcDetailGroup);
        }

        if (fetchRole) {
            functions.add(this::funcDetailRole);
        }

        if (fetchPersonAttribute) {
            functions.add(this::funcDetailPersonAttribute);
        }

        functions.stream().forEach(o -> o.apply(param));

        Wo wo = convert(param);
        collate(fetchIdentity, fetchUnit, fetchUnitDuty, fetchGroup, fetchRole, fetchPersonAttribute, wo);
        return wo;
    }

    private void collate(boolean fetchIdentity, boolean fetchUnit, boolean fetchUnitDuty, boolean fetchGroup,
            boolean fetchRole, boolean fetchPersonAttribute, Wo wo) {
        if (!fetchIdentity) {
            wo.getIdentityList().clear();
        }
        if (!fetchUnit) {
            wo.getUnitList().clear();
        }
        if (!fetchUnitDuty) {
            wo.getUnitDutyList().clear();
        }
        if (!fetchGroup) {
            wo.getGroupList().clear();
        }
        if (!fetchRole) {
            wo.getRoleList().clear();
        }
        if (!fetchPersonAttribute) {
            wo.getPersonAttributeList().clear();
        }
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcDetailIdentity(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsEqual(Identity.class,
                    Identity.person_FIELDNAME, param.second().getId());
            param.third().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcDetailUnit(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            for (String s : param.third()) {
                Identity obj = param.first().identity().pick(s);
                if (null != obj) {
                    param.fourth().add(obj.getUnit());
                    param.fourth().addAll(param.first().unit().listSupNested(obj.getUnit()));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcDetailUnitDuty(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsIn(UnitDuty.class,
                    UnitDuty.identityList_FIELDNAME, param.third());
            param.fifth().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcDetailGroup(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsInOrInOrIsMember(Group.class,
                    Group.identityList_FIELDNAME, param.third(), Group.unitList_FIELDNAME, param.fourth(),
                    Group.personList_FIELDNAME, param.second().getId());
            for (String s : ids) {
                param.sixth().add(s);
                param.sixth().addAll(param.first().group().listSupNested(s));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcDetailRole(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsInOrIsMember(Role.class,
                    Role.groupList_FIELDNAME, param.sixth(), Role.personList_FIELDNAME, param.second().getId());
            param.seventh().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcDetailPersonAttribute(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsEqual(PersonAttribute.class,
                    PersonAttribute.person_FIELDNAME, param.second().getId());
            param.eighth().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Wo convert(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        Wo wo = new Wo();
        wo.setDistinguishedName(param.second().getDistinguishedName());
        Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> target = Nonuple
                .of(param, wo);
        Stream.<UnaryOperator<Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo>>>of(
                this::funcConvertIdentity, this::funcConvertUnit, this::funcConvertUnitDuty, this::funcConvertGroup,
                this::funcConvertRole, this::funcConvertPersonAttribute).forEach(o -> o.apply(target));
        return wo;
    }

    private Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> funcConvertIdentity(
            Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> param) {
        param.third().stream().forEach(o -> {
            Identity obj;
            try {
                obj = param.first().identity().pick(o);
                if (null != obj) {
                    param.ninth().getIdentityList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return param;
    }

    private Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> funcConvertUnit(
            Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> param) {
        param.fourth().stream().forEach(o -> {
            Unit obj;
            try {
                obj = param.first().unit().pick(o);
                if (null != obj) {
                    param.ninth().getUnitList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return param;
    }

    private Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> funcConvertUnitDuty(
            Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> param) {
        param.fifth().stream().forEach(o -> {
            UnitDuty obj;
            try {
                obj = param.first().unitDuty().pick(o);
                if (null != obj) {
                    param.ninth().getUnitDutyList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return param;
    }

    private Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> funcConvertGroup(
            Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> param) {
        param.sixth().stream().forEach(o -> {
            Group obj;
            try {
                obj = param.first().group().pick(o);
                if (null != obj) {
                    param.ninth().getGroupList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return param;
    }

    private Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> funcConvertRole(
            Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> param) {
        param.seventh().stream().forEach(o -> {
            Role obj;
            try {
                obj = param.first().role().pick(o);
                if (null != obj) {
                    param.ninth().getRoleList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return param;
    }

    private Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> funcConvertPersonAttribute(
            Nonuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, Wo> param) {
        param.eighth().stream().forEach(o -> {
            PersonAttribute obj;
            try {
                obj = param.first().personAttribute().pick(o);
                if (null != obj) {
                    param.ninth().getPersonAttributeList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return param;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -1730556542374559293L;

        public Boolean getFetchIdentity() {
            return BooleanUtils.isNotFalse(fetchIdentity);
        }

        public Boolean getFetchUnit() {
            return BooleanUtils.isNotFalse(fetchUnit);
        }

        public Boolean getFetchUnitDuty() {
            return BooleanUtils.isNotFalse(fetchUnitDuty);
        }

        public Boolean getFetchGroup() {
            return BooleanUtils.isNotFalse(fetchGroup);
        }

        public Boolean getFetchRole() {
            return BooleanUtils.isNotFalse(fetchRole);
        }

        public Boolean getFetchPersonAttribute() {
            return BooleanUtils.isNotFalse(fetchPersonAttribute);
        }

        @FieldDescribe("是否获取身份.")
        private Boolean fetchIdentity;
        @FieldDescribe("是否获取组织.")
        private Boolean fetchUnit;
        @FieldDescribe("是否获取组织职务.")
        private Boolean fetchUnitDuty;
        @FieldDescribe("是否获取群组.")
        private Boolean fetchGroup;
        @FieldDescribe("是否获取角色.")
        private Boolean fetchRole;
        @FieldDescribe("是否获取个人属性.")
        private Boolean fetchPersonAttribute;

        public void setFetchIdentity(Boolean fetchIdentity) {
            this.fetchIdentity = fetchIdentity;
        }

        public void setFetchUnit(Boolean fetchUnit) {
            this.fetchUnit = fetchUnit;
        }

        public void setFetchUnitDuty(Boolean fetchUnitDuty) {
            this.fetchUnitDuty = fetchUnitDuty;
        }

        public void setFetchGroup(Boolean fetchGroup) {
            this.fetchGroup = fetchGroup;
        }

        public void setFetchRole(Boolean fetchRole) {
            this.fetchRole = fetchRole;
        }

        public void setFetchPersonAttribute(Boolean fetchPersonAttribute) {
            this.fetchPersonAttribute = fetchPersonAttribute;
        }

    }

    public static class Wo extends PersonDetail {

        private static final long serialVersionUID = -8456354949288335211L;

    }

}
