package com.x.query.core.express.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.x.base.core.project.annotation.FieldTypeDescribe;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.core.express.Organization;
import com.x.query.core.express.plan.FilterEntry;

public class Runtime extends GsonPropertyObject {

    private static final long serialVersionUID = 2119629142378875023L;

    public static final String PARAMETER_PERSON = "person";
    public static final String PARAMETER_IDENTITYLIST = "identityList";
    public static final String PARAMETER_UNITLIST = "unitList";
    public static final String PARAMETER_UNITALLLIST = "unitAllList";
    public static final String PARAMETER_GROUPLIST = "groupList";
    public static final String PARAMETER_ROLELIST = "roleList";

    public static final List<String> ALL_BUILT_IN_PARAMETER = ListUtils
            .unmodifiableList(Arrays.asList(PARAMETER_PERSON, PARAMETER_IDENTITYLIST, PARAMETER_UNITLIST,
                    PARAMETER_UNITALLLIST, PARAMETER_GROUPLIST, PARAMETER_ROLELIST));

    public static final String filterList_FIELDNAME = "filterList";
    @FieldDescribe("过滤条件.")
    @FieldTypeDescribe(fieldType = "class", fieldTypeName = "com.x.query.core.express.plan.FilterEntry", fieldValue = "{\"logic\": \"and\", \"path\": \"o.name\", \"comparison\": \"equals\", \"value\": \"name\", \"formatType\": \"textValue\"}", fieldSample = "{\"logic\":\"逻辑运算:and\",\"path\":\"data数据的路径:o.title\",\"comparison\":\"比较运算符:equals|notEquals|like|notLike|greaterThan|greaterThanOrEqualTo|lessThan|lessThanOrEqualTo\","
            + "\"value\":\"7月\",\"formatType\":\"textValue|numberValue|dateTimeValue|booleanValue\"}")
    public List<FilterEntry> filterList = new ArrayList<>();

    public static final String parameter_FIELDNAME = "parameter";
    @FieldDescribe("参数.")
    public Map<String, Object> parameter = new HashMap<>();

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public Integer page = 0;

    public Integer size = 20;

    public boolean hasParameter(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }

        return this.parameter.containsKey(name);
    }

    public void setParameter(String name, Object obj) {
        this.parameter.put(name, obj);
    }

    public Object getParameter(String name) {
        return this.parameter.get(name);
    }

    public List<FilterEntry> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<FilterEntry> filterList) {
        this.filterList = filterList;
    }

    public static Runtime concrete(EffectivePerson effectivePerson, JsonElement jsonElement,
            Organization organization, Integer page, Integer size) throws Exception {
        Runtime runtime = XGsonBuilder.instance().fromJson(jsonElement, Runtime.class);
        runtime.page = page;
        runtime.size = size;
        Set<String> keys = runtime.parameter.keySet();
        if (keys.contains(Runtime.PARAMETER_PERSON)) {
            runtime.parameter.put(Runtime.PARAMETER_PERSON, effectivePerson.getDistinguishedName());
        }
        if (keys.contains(Runtime.PARAMETER_IDENTITYLIST)) {
            runtime.parameter.put(Runtime.PARAMETER_IDENTITYLIST,
                    organization.identity().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_UNITLIST)) {
            runtime.parameter.put(Runtime.PARAMETER_UNITLIST, organization.unit().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_UNITALLLIST)) {
            runtime.parameter.put(Runtime.PARAMETER_UNITALLLIST,
                    organization.unit().listWithPersonSupNested(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_GROUPLIST)) {
            runtime.parameter.put(Runtime.PARAMETER_GROUPLIST, organization.group().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_ROLELIST)) {
            runtime.parameter.put(Runtime.PARAMETER_ROLELIST, organization.role().listWithPerson(effectivePerson));
        }
        return runtime;
    }

}
