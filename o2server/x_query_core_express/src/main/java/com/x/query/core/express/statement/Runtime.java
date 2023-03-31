package com.x.query.core.express.statement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Runtime extends GsonPropertyObject {

    public static final String PARAMETER_PERSON = "person";
    public static final String PARAMETER_IDENTITYLIST = "identityList";
    public static final String PARAMETER_UNITLIST = "unitList";
    public static final String PARAMETER_UNITALLLIST = "unitAllList";
    public static final String PARAMETER_GROUPLIST = "groupList";
    public static final String PARAMETER_ROLELIST = "roleList";

    public static final List<String> ALL_BUILT_IN_PARAMETER = ListUtils
            .unmodifiableList(Arrays.asList(PARAMETER_PERSON,
                    PARAMETER_IDENTITYLIST, PARAMETER_UNITLIST, PARAMETER_UNITALLLIST, PARAMETER_GROUPLIST,
                    PARAMETER_ROLELIST));

    @FieldDescribe("参数")
    public Map<String, Object> parameters = new HashMap<>();

    public Map<String, Object> getParameters() {
        return parameters;
    }

    @FieldDescribe("页码")
    public Integer page = 0;

    @FieldDescribe("每页大小")
    public Integer size = 20;

    public boolean hasParameter(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }

        return this.parameters.containsKey(name);
    }

    public void setParameter(String name, Object obj) {
        this.parameters.put(name, obj);
    }

    public Object getParameter(String name) {
        return this.parameters.get(name);
    }
}
