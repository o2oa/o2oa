package com.x.file.assemble.control.jaxrs.recycle;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.Unit;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Share;

abstract class BaseAction extends StandardJaxrsAction {

    protected boolean hasPermission(Business business, EffectivePerson effectivePerson, Share share) throws Exception {
        boolean flag = true;
        if (!StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())
                && (!share.getShareUserList().contains(effectivePerson.getDistinguishedName()))) {
            flag = false;
            List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
            for (String str : identities) {
                List<String> units = business.organization().unit().listWithIdentitySupNested(str);
                for (String unitName : units) {
                    Unit unit = business.organization().unit().getObject(unitName);
                    if (unit != null) {
                        if (share.getShareOrgList().contains(unit.getUnique())) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
        return flag;
    }

}
