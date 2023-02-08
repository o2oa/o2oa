package com.x.query.service.processing.jaxrs.touch;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.core.express.plan.Runtime;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;

abstract class BaseAction extends StandardJaxrsAction {

    /**
     * 根据node节点名获取集群中的x_query_service_processing模块,如果node=(0)
     * AbstractJaxrsAction.EMPTY_SYMBOL 取得所有节点.
     * 
     * @param node
     * @return
     * @throws Exception
     */
    protected List<Application> listApplication(String node) throws Exception {

        List<Application> applications = ThisApplication.context().applications()
                .get(ThisApplication.context().clazz());
        if (!StringUtils.equals(node, EMPTY_SYMBOL)) {
            applications = applications.stream().filter(o -> StringUtils.equals(o.getNode(), node))
                    .collect(Collectors.toList());
        }
        return applications;

    }
}
