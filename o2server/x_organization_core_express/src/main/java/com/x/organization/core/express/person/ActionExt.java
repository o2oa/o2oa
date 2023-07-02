package com.x.organization.core.express.person;

import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.PersonAttribute;
import com.x.base.core.project.organization.Role;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.organization.UnitDuty;

class ActionExt extends BaseAction {

    public static Wo execute(AbstractContext context, String flag)
            throws Exception {
        return context.applications().getQuery(applicationClass, Applications.joinQueryUri("person", flag))
                .getData(Wo.class);
    }

    public static class Wo extends Person {

        private static final long serialVersionUID = -8456354949288335211L;

        @FieldDescribe("身份对象")
        private List<WoIdentity> woIdentityList;

        @FieldDescribe("角色对象")
        private List<WoRole> woRoleList;

        @FieldDescribe("群组对象")
        private List<WoGroup> woGroupList;

        @FieldDescribe("个人属性对象")
        private List<WoPersonAttribute> woPersonAttributeList;

        public List<WoIdentity> getWoIdentityList() {
            return woIdentityList;
        }

        public void setWoIdentityList(List<WoIdentity> woIdentityList) {
            this.woIdentityList = woIdentityList;
        }

        public List<WoRole> getWoRoleList() {
            return woRoleList;
        }

        public void setWoRoleList(List<WoRole> woRoleList) {
            this.woRoleList = woRoleList;
        }

        public List<WoGroup> getWoGroupList() {
            return woGroupList;
        }

        public void setWoGroupList(List<WoGroup> woGroupList) {
            this.woGroupList = woGroupList;
        }

        public List<WoPersonAttribute> getWoPersonAttributeList() {
            return woPersonAttributeList;
        }

        public void setWoPersonAttributeList(List<WoPersonAttribute> woPersonAttributeList) {
            this.woPersonAttributeList = woPersonAttributeList;
        }

    }

    public static class WoIdentity extends Identity {

        private static final long serialVersionUID = 6193615461099768815L;

        @FieldDescribe("组织对象")
        private WoUnit woUnit;

        @FieldDescribe("组织职务对象")
        private List<WoUnitDuty> woUnitDutyList;

        public WoUnit getWoUnit() {
            return woUnit;
        }

        public void setWoUnit(WoUnit woUnit) {
            this.woUnit = woUnit;
        }

        public List<WoUnitDuty> getWoUnitDutyList() {
            return woUnitDutyList;
        }

        public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
            this.woUnitDutyList = woUnitDutyList;
        }

    }

    public static class WoGroup extends Group {

        private static final long serialVersionUID = 4503618773692247688L;

    }

    public static class WoRole extends Role {

        private static final long serialVersionUID = -3903028273062897622L;

    }

    public static class WoUnit extends Unit {

        private static final long serialVersionUID = 6172047743675016186L;

        private Long subDirectUnitCount;
        private Long subDirectIdentityCount;

        public Long getSubDirectUnitCount() {
            return subDirectUnitCount;
        }

        public void setSubDirectUnitCount(Long subDirectUnitCount) {
            this.subDirectUnitCount = subDirectUnitCount;
        }

        public Long getSubDirectIdentityCount() {
            return subDirectIdentityCount;
        }

        public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
            this.subDirectIdentityCount = subDirectIdentityCount;
        }
    }

    public static class WoUnitDuty extends UnitDuty {

        private static final long serialVersionUID = 3145496265299807549L;

        @FieldDescribe("组织对象")
        private WoUnit woUnit;

        public WoUnit getWoUnit() {
            return woUnit;
        }

        public void setWoUnit(WoUnit woUnit) {
            this.woUnit = woUnit;
        }
    }

    public static class WoPersonAttribute extends PersonAttribute {

        private static final long serialVersionUID = -3155093360276871418L;

    }
}
