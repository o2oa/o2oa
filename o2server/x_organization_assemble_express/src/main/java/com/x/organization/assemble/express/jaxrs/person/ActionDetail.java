package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Octuple;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
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
            CacheKey cacheKey = new CacheKey(this.getClass(), flag);
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                result.setData((Wo) optional.get());
            } else {
                Wo wo = new Wo();
                if (Config.token().isInitialManager(flag)) {
                    // 如果是xadmin单独处理
                    Config.token().initialManagerInstance().copyTo(wo, "password");
                } else {
                    Person person = business.person().pick(flag);
                    if (null != person) {
                        wo.setDistinguishedName(person.getDistinguishedName());
                        detail(business, person, wi);
                    }
                    CacheManager.put(cacheCategory, cacheKey, wo);
                }
                result.setData(wo);
            }
            return result;
        }
    }

    public Wo detail(Business business, Person person, Wi wi) {
        // identity,unit,unitDuty,group,role,personAttribute
        boolean fetchIdentity = BooleanUtils.isNotFalse(wi.getFetchIdentity());
        boolean fetchUnit = BooleanUtils.isNotFalse(wi.getFetchUnit());
        boolean fetchUnitDuty = BooleanUtils.isNotFalse(wi.getFetchUnitDuty());
        boolean fetchGroup = BooleanUtils.isNotFalse(wi.getFetchGroup());
        boolean fetchRole = BooleanUtils.isNotFalse(wi.getFetchRole());
        boolean fetchPersonAttribute = BooleanUtils.isNotFalse(wi.getFetchPersonAttribute());
        Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param = Octuple
                .of(business, person, new ListOrderedSet<String>(), new ListOrderedSet<String>(),
                        new ListOrderedSet<String>(),
                        new ListOrderedSet<String>(), new ListOrderedSet<String>(), new ListOrderedSet<String>());

        List<UnaryOperator<Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>>>> functions = new ArrayList<>();

        if (fetchIdentity || fetchUnit || fetchUnitDuty || fetchGroup || fetchRole) {
            functions.add(this::functionIdentity);
        }

        if (fetchUnit || fetchUnitDuty || fetchGroup || fetchRole) {
            functions.add(this::functionUnit);
        }

        if (fetchUnitDuty || fetchGroup || fetchRole) {
            functions.add(this::funcUnitDuty);
        }

        if (fetchGroup || fetchRole) {
            functions.add(this::functionGroup);
        }

        if (fetchRole) {
            functions.add(this::functionRole);
        }

        if (fetchPersonAttribute) {
            functions.add(this::functionPersonAttribute);
        }

        functions.stream().forEach(o -> o.apply(param));

        return convert(param);

    }

    private Wo convert(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        Wo wo = new Wo();
        wo.setDistinguishedName(param.second().getDistinguishedName());
        param.third().stream().forEach(o -> {
            Identity obj;
            try {
                obj = param.first().identity().pick(o);
                if (null != obj) {
                    wo.getIdentityList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        param.fourth().stream().forEach(o -> {
            Unit obj;
            try {
                obj = param.first().unit().pick(o);
                if (null != obj) {
                    wo.getUnitList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        param.fifth().stream().forEach(o -> {
            UnitDuty obj;
            try {
                obj = param.first().unitDuty().pick(o);
                if (null != obj) {
                    wo.getUnitDutyList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        param.sixth().stream().forEach(o -> {
            Group obj;
            try {
                obj = param.first().group().pick(o);
                if (null != obj) {
                    wo.getGroupList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        param.seventh().stream().forEach(o -> {
            Role obj;
            try {
                obj = param.first().role().pick(o);
                if (null != obj) {
                    wo.getRoleList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        param.eighth().stream().forEach(o -> {
            PersonAttribute obj;
            try {
                obj = param.first().personAttribute().pick(o);
                if (null != obj) {
                    wo.getPersonAttributeList().add(obj.getDistinguishedName());
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
        return wo;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> functionIdentity(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsEqual(Identity.class,
                    Identity.person_FIELDNAME,
                    param.second().getId());
            param.third().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> functionUnit(
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

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> funcUnitDuty(
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

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> functionGroup(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsInOrInOrIsMember(Group.class,
                    Group.identityList_FIELDNAME,
                    param.third(), Group.unitList_FIELDNAME, param.fourth(), Group.personList_FIELDNAME,
                    param.second().getId());
            for (String s : ids) {
                param.sixth().add(s);
                param.sixth().addAll(param.first().group().listSupNested(s));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> functionRole(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsInOrIsMember(Role.class,
                    Role.groupList_FIELDNAME,
                    param.sixth(), Role.personList_FIELDNAME,
                    param.second().getId());
            param.sixth().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    private Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> functionPersonAttribute(
            Octuple<Business, Person, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>, ListOrderedSet<String>> param) {
        try {
            List<String> ids = param.first().entityManagerContainer().idsEqual(PersonAttribute.class,
                    PersonAttribute.person_FIELDNAME,
                    param.second().getId());
            param.eighth().addAll(ids);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return param;
    }

    public static class Wi extends GsonPropertyObject {

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

        public Boolean getFetchIdentity() {
            return fetchIdentity;
        }

        public void setFetchIdentity(Boolean fetchIdentity) {
            this.fetchIdentity = fetchIdentity;
        }

        public Boolean getFetchUnit() {
            return fetchUnit;
        }

        public void setFetchUnit(Boolean fetchUnit) {
            this.fetchUnit = fetchUnit;
        }

        public Boolean getFetchUnitDuty() {
            return fetchUnitDuty;
        }

        public void setFetchUnitDuty(Boolean fetchUnitDuty) {
            this.fetchUnitDuty = fetchUnitDuty;
        }

        public Boolean getFetchGroup() {
            return fetchGroup;
        }

        public void setFetchGroup(Boolean fetchGroup) {
            this.fetchGroup = fetchGroup;
        }

        public Boolean getFetchRole() {
            return fetchRole;
        }

        public void setFetchRole(Boolean fetchRole) {
            this.fetchRole = fetchRole;
        }

        public Boolean getFetchPersonAttribute() {
            return fetchPersonAttribute;
        }

        public void setFetchPersonAttribute(Boolean fetchPersonAttribute) {
            this.fetchPersonAttribute = fetchPersonAttribute;
        }

    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -8456354949288335211L;

        @FieldDescribe("用户")
        private String distinguishedName = "";

        @FieldDescribe("组织")
        private List<String> unitList = new ArrayList<>();

        @FieldDescribe("群组")
        private List<String> groupList = new ArrayList<>();

        @FieldDescribe("角色")
        private List<String> roleList = new ArrayList<>();

        @FieldDescribe("身份")
        private List<String> identityList = new ArrayList<>();

        @FieldDescribe("人员属性")
        private List<String> personAttributeList = new ArrayList<>();

        @FieldDescribe("组织职务")
        private List<String> unitDutyList = new ArrayList<>();

        public String getDistinguishedName() {
            return distinguishedName;
        }

        public void setDistinguishedName(String distinguishedName) {
            this.distinguishedName = distinguishedName;
        }

        public List<String> getUnitList() {
            return unitList;
        }

        public void setUnitList(List<String> unitList) {
            this.unitList = unitList;
        }

        public List<String> getGroupList() {
            return groupList;
        }

        public void setGroupList(List<String> groupList) {
            this.groupList = groupList;
        }

        public List<String> getRoleList() {
            return roleList;
        }

        public void setRoleList(List<String> roleList) {
            this.roleList = roleList;
        }

        public List<String> getPersonAttributeList() {
            return personAttributeList;
        }

        public void setPersonAttributeList(List<String> personAttributeList) {
            this.personAttributeList = personAttributeList;
        }

        public List<String> getUnitDutyList() {
            return unitDutyList;
        }

        public void setUnitDutyList(List<String> unitDutyList) {
            this.unitDutyList = unitDutyList;
        }

        public List<String> getIdentityList() {
            return identityList;
        }

        public void setIdentityList(List<String> identityList) {
            this.identityList = identityList;
        }

    }

}
