package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;

abstract class BaseAction extends StandardJaxrsAction {
    protected String decideCreatorIdentity(Business business, EffectivePerson effectivePerson, String identityValue)
            throws Exception {
        if (TokenType.cipher.equals(effectivePerson.getTokenType())) {
            String identity = business.organization().identity().get(identityValue);
            if (StringUtils.isEmpty(identity)) {
                throw new ExceptionDecideCreatorIdentity();
            } else {
                return identity;
            }
        }
        if (StringUtils.isEmpty(identityValue)) {
            List<String> list = business.organization().identity()
                    .listWithPerson(effectivePerson.getDistinguishedName());
            if (list.isEmpty()) {
                throw new ExceptionDecideCreatorIdentity();
            } else {
                return list.get(0);
            }
        }
        List<String> identities = business.organization().identity()
                .listWithPerson(effectivePerson.getDistinguishedName());
        if (ListTools.isEmpty(identities)) {
            throw new ExceptionNoneIdentity(effectivePerson.getDistinguishedName());
        }
        if (identities.size() == 1) {
            return identities.get(0);
        }
        /* 有多个身份需要逐一判断是否包含. */
        for (String o : identities) {
            if (StringUtils.equals(o, identityValue)) {
                return o;
            }
        }
        throw new ExceptionDecideCreatorIdentity();
    }

    protected Work mockWork(Application application, Process process, String person, String identity, String unit,
            String title) {
        Work work = new Work();
        work.setApplication(application.getId());
        work.setApplicationAlias(application.getAlias());
        work.setApplicationName(application.getName());
        work.setProcess(process.getId());
        work.setProcessAlias(process.getAlias());
        work.setProcessName(process.getName());
        work.setCreatorIdentity(identity);
        work.setCreatorPerson(person);
        work.setCreatorUnit(unit);
        work.setTitle(title);
        return work;
    }

    protected String findForm(Business business, Process process) throws Exception {
		String id = null;
		Begin begin = business.begin().getWithProcess(process);
		id = begin.getForm();
		if (StringUtils.isEmpty(id)) {
			Route route = business.route().pick(begin.getRoute());
			Activity activity = business.getActivity(route.getActivity(), route.getActivityType());
			id = activity.getForm();
		}
		if (StringUtils.isEmpty(id)) {
			throw new ExceptionNoneForm();
		}
		return id;
	}

}
