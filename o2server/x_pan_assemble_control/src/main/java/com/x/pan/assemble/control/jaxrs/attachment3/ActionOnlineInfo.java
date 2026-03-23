package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.entities.OnlineInfo;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionOnlineInfo extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionOnlineInfo.class);

    public ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
        LOGGER.debug("execute:{}, id:{}", effectivePerson::getDistinguishedName, () -> id, () -> id);
        WoAttachment attachment;
        FileConfig3 config;
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setId(id);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            config = business.getSystemConfig();
            Attachment3 attachment3 = emc.find(id, Attachment3.class);
            if(attachment3 != null){
                String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment3.getFolder() : attachment3.getZoneId();
                if (business.zoneEditable(effectivePerson, attachment3.getFolder(), "")) {
                    wo.setCanEdit(true);
                    wo.setCanRead(true);
                }else if (business.zoneViewable(effectivePerson, zoneId)) {
                    wo.setCanRead(true);
                }
                attachment = new WoAttachment(attachment3);
            }else{
                Attachment2 attachment2 = emc.find(id, Attachment2.class);
                if (null == attachment2) {
                    throw new ExceptionEntityNotExist(id);
                }
                if (business.controlAble(effectivePerson) || StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
                    wo.setCanEdit(true);
                    wo.setCanRead(true);
                }
                attachment = new WoAttachment(attachment2);
            }
        }
        if(BooleanUtils.isTrue(wo.getCanRead())) {
            this.assembleFileInfo(attachment, effectivePerson, config, wo);
        }
        result.setData(wo);
        return result;
    }

    private void assembleFileInfo(WoAttachment attachment, EffectivePerson effectivePerson,
                                           FileConfig3 config, Wo wo){
        wo.setId(attachment.getId());
        wo.setName(attachment.getName());
        wo.setExtension(attachment.getExtension());
        wo.setLength(attachment.getLength());
        wo.setOwnerId(attachment.getPerson());
        wo.setOwnerName(OrganizationDefinition.name(attachment.getPerson()));
        wo.setUserId(effectivePerson.getDistinguishedName());
        wo.setUserName(effectivePerson.getName());
        wo.setCreateTime(attachment.getCreateTime());
        wo.setLastUpdateTime(attachment.getUpdateTime());
        wo.setJob(attachment.getId());
        wo.setDownloadUrl(this.getFileDownloadUrl(config, attachment.getId(), attachment.isAttachment3(), effectivePerson, 0, false));
    }

    public static class Wo extends OnlineInfo{
        private static final long serialVersionUID = 1L;
    }


}
