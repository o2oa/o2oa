package com.x.organization.core.express.person;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.jaxrs.WrapStringList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class ActionGetAuthInfo extends BaseAction {

    public static List<String> execute(AbstractContext context, String flag) throws Exception {
        if (StringUtils.isBlank(flag)) {
            return Collections.emptyList();
        }
        Wo wo = context.applications().getQuery(applicationClass,
                Applications.joinQueryUri("person", "auth", "info", flag)).getData(Wo.class);
        return wo.getValueList();
    }

    public static class Wo extends WrapStringList {

    }
}
