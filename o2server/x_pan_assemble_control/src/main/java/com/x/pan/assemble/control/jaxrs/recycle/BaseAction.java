package com.x.pan.assemble.control.jaxrs.recycle;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.Unit;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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

    protected void deleteFile(Business business, String origin) throws Exception{
        EntityManagerContainer emc = business.entityManagerContainer();
        Long count = emc.countEqual(Attachment2.class, Attachment2.originFile_FIELDNAME, origin);
        count = count + emc.countEqual(Attachment3.class, Attachment3.originFile_FIELDNAME, origin);
        if(count.equals(1L)){
            OriginFile originFile = emc.find(origin, OriginFile.class);
            if(originFile!=null){
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
                        originFile.getStorage());
                if(mapping!=null){
                    originFile.deleteContent(mapping);
                }
                emc.beginTransaction(OriginFile.class);
                emc.remove(originFile);
            }
        }
    }

}
