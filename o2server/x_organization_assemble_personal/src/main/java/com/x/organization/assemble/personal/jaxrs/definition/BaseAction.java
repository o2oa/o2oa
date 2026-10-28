package com.x.organization.assemble.personal.jaxrs.definition;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.assemble.personal.Business;

abstract class BaseAction extends StandardJaxrsAction {
    public Boolean isManager(Business business, EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager()) {
            return true;
        }
        return business.organization().person()
                .hasRole(effectivePerson, OrganizationDefinition.MeetingManager,
                        OrganizationDefinition.AttendanceManager);
    }
}
