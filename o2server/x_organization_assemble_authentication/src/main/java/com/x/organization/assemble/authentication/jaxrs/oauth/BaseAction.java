package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.x.base.core.project.config.Token.Oauth;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

    protected String getScope(Oauth oauth, String scope) throws Exception {
        if (StringUtils.isEmpty(scope)) {
            return StringUtils.join(oauth.getMapping().keySet(), ",");
        } else {
            List<String> os = new ArrayList<>();
            for (String o : StringUtils.split(scope, ",")) {
                if (StringUtils.isNotEmpty(oauth.getMapping().get(o))) {
                    os.add(o);
                } else {
                    throw new ExceptionScopeNotExist(o);
                }
            }
            return StringUtils.join(os, ",");
        }
    }
}
