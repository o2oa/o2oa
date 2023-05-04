package com.x.base.core.project.organization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class PersonDetail extends GsonPropertyObject {

    private static final long serialVersionUID = -1577868963290077559L;

    @FieldDescribe("用户")
    private String distinguishedName = "";

    @FieldDescribe("身份")
    private List<String> identityList = new ArrayList<>();

    @FieldDescribe("组织")
    private List<String> unitList = new ArrayList<>();

    @FieldDescribe("组织职务")
    private List<String> unitDutyList = new ArrayList<>();

    @FieldDescribe("群组")
    private List<String> groupList = new ArrayList<>();

    @FieldDescribe("角色")
    private List<String> roleList = new ArrayList<>();

    @FieldDescribe("人员属性")
    private List<String> personAttributeList = new ArrayList<>();

    public boolean containsAny(Object... values) {
        if (null == values) {
            return false;
        }
        for (Object obj : values) {
            Collection<String> o = paramToStringCollection(obj);
            if (containsAnyPerson(o)) {
                return true;
            }
            if (containsAnyIdentity(o)) {
                return true;
            }
            if (containsAnyUnit(o)) {
                return true;
            }
            if (containsAnyUnitDuty(o)) {
                return true;
            }
            if (containsAnyGroup(o)) {
                return true;
            }
            if (containsAnyRole(o)) {
                return true;
            }
        }
        return false;
    }
    
    private List<String> paramToStringCollection(Object obj) {
        List<String> list = new ArrayList<>();
        if (obj != null) {
            if (CharSequence.class.isAssignableFrom(obj.getClass())) {
                list.add(obj.toString());
            } else if (Collection.class.isAssignableFrom(obj.getClass())) {
                for (Object o : (Collection<?>) obj) {
                    if (null != o) {
                        list.add(o.toString());
                    }
                }
            }
        }
        return list;
    }

    public boolean containsAnyPerson(String... people) {
        return containsAnyPerson(Arrays.asList(people));
    }

    public boolean containsAnyPerson(Collection<String> people) {
        for (String p : people) {
            if (StringUtils.equalsIgnoreCase(this.distinguishedName, p)) {
                return true;
            }
        }
        return false;
    }

 

    public boolean containsAnyIdentity(String... identities) {
        return containsAnyIdentity(Arrays.asList(identities));
    }

    public boolean containsAnyIdentity(Collection<String> identities) {
        if ((null != identities) && (!identities.isEmpty())) {
            for (String identity : identities) {
                if (identityList.contains(identity)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAnyUnit(String... units) {
        return containsAnyUnit(Arrays.asList(units));
    }

    public boolean containsAnyUnit(Collection<String> units) {
        if ((null != units) && (!units.isEmpty())) {
            for (String unit : units) {
                if (unitList.contains(unit)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAnyUnitDuty(String... unitDuties) {
        return containsAnyUnitDuty(Arrays.asList(unitDuties));
    }

    public boolean containsAnyUnitDuty(Collection<String> unitDuties) {
        if ((null != unitDuties) && (!unitDuties.isEmpty())) {
            for (String unitDuty : unitDuties) {
                if (unitDutyList.contains(unitDuty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAnyGroup(String... groups) {
        return containsAnyGroup(Arrays.asList(groups));
    }

    public boolean containsAnyGroup(Collection<String> groups) {
        if ((null != groups) && (!groups.isEmpty())) {
            for (String group : groups) {
                if (groupList.contains(group)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAnyRole(String... roles) {
        return containsAnyRole(Arrays.asList(roles));
    }

    public boolean containsAnyRole(Collection<String> roles) {
        if ((null != roles) && (!roles.isEmpty())) {
            for (String role : roles) {
                if (roleList.contains(role)) {
                    return true;
                }
                if (roleList.contains(OrganizationDefinition.toDistinguishedName(role))) {
                    return true;
                }
            }
        }
        return false;
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

    public List<String> getIdentityList() {
        return identityList;
    }

    public void setIdentityList(List<String> identityList) {
        this.identityList = identityList;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public List<String> getUnitDutyList() {
        return unitDutyList;
    }

    public void setUnitDutyList(List<String> unitDutyList) {
        this.unitDutyList = unitDutyList;
    }

    public List<String> getPersonAttributeList() {
        return personAttributeList;
    }

    public void setPersonAttributeList(List<String> personAttributeList) {
        this.personAttributeList = personAttributeList;
    }

}
