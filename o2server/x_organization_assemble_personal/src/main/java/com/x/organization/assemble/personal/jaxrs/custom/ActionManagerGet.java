package com.x.organization.assemble.personal.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.StringUtils;

class ActionManagerGet extends BaseAction {

    ActionResult<String> execute(EffectivePerson effectivePerson, String person, String name) throws Exception {

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<String> result = new ActionResult<>();
            Business business = new Business(emc);
            if (effectivePerson.isManager() && StringUtils.isNotEmpty(person)) {
                Person p = business.person().pick(person);
                if (p != null) {
                    person = p.getDistinguishedName();
                }
                Custom o = this.getWithName(emc, person, name);
                if (null != o) {
                    result.setData(o.getData());
                }
            }
            return result;
        }
    }
}
