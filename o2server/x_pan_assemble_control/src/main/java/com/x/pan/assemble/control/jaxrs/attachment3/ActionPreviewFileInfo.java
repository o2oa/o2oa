package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.entities.FileInfo;
import com.x.pan.assemble.control.entities.FileInfoModel;
import com.x.pan.assemble.control.entities.UserInfo;
import com.x.pan.assemble.control.entities.UserPermission;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.AttachmentVersion;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActionPreviewFileInfo extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionPreviewFileInfo.class);

    public String execute(EffectivePerson effectivePerson, String id) throws Exception {
        Map<String, Object> hashMap = new HashMap<>(2);
        if (effectivePerson.isAnonymous()) {
            hashMap.put("result", Business.ONLY_OFFICE_ERROR_CODE);
            hashMap.put("msg", "用户未登录或");
            return gson.toJson(hashMap);
        }
        WoAttachment attachment;
        FileConfig3 config;
        List<AttachmentVersion> attachmentVersionList;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            config = business.getSystemConfig();
            Attachment3 attachment3 = emc.find(id, Attachment3.class);
            if(attachment3 != null){
                String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment3.getFolder() : attachment3.getZoneId();
                if (!business.zoneViewable(effectivePerson, zoneId)) {
                    hashMap.put("result", Business.ONLY_OFFICE_ERROR_CODE);
                    hashMap.put("msg", "用户无权限访问");
                    return gson.toJson(hashMap);
                }
                attachment = new WoAttachment(attachment3);
            }else{
                Attachment2 attachment2 = emc.find(id, Attachment2.class);
                if (null == attachment2) {
                    hashMap.put("result", Business.ONLY_OFFICE_ERROR_CODE);
                    hashMap.put("msg", "文件不存在");
                    return gson.toJson(hashMap);
                }
                if (!effectivePerson.isManager() && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
                    hashMap.put("result", Business.ONLY_OFFICE_ERROR_CODE);
                    hashMap.put("msg", "用户无权限访问");
                    return gson.toJson(hashMap);
                }
                attachment = new WoAttachment(attachment2);
            }
            attachmentVersionList = emc.listEqual(AttachmentVersion.class, AttachmentVersion.attachmentId_FIELDNAME, id);
        }

        FileInfoModel fileInfoModel = this.assembleFileInfo(attachment, effectivePerson, config, attachmentVersionList);

        return gson.toJson(fileInfoModel);
    }

    private FileInfoModel assembleFileInfo(WoAttachment attachment, EffectivePerson effectivePerson, FileConfig3 config, List<AttachmentVersion> attachmentVersionList){
        FileInfoModel fileInfoModel = new FileInfoModel();

        FileInfo fileInfo = new FileInfo();
        fileInfo.setSize(attachment.getLength());
        String person = attachment.getPerson();
        fileInfo.setCreatorId(person);
        fileInfo.setCreatorName(OrganizationDefinition.name(person));
        fileInfo.setCreateTime(attachment.getCreateTime().getTime());
        fileInfo.setDownloadUrl(this.getFileDownloadUrl(config, attachment.getId(), attachment.isAttachment3(), effectivePerson, 0, false));
        fileInfo.setId(attachment.getId());
        person = StringUtils.isBlank(attachment.getLastUpdatePerson()) ? attachment.getPerson() : attachment.getLastUpdatePerson();
        fileInfo.setModifierId(person);
        fileInfo.setModifierName(OrganizationDefinition.name(person));
        fileInfo.setModifyTime(attachment.getUpdateTime().getTime());
        fileInfo.setName(attachment.getName());
        fileInfo.setVersion(attachment.getFileVersion());

        UserPermission userPermission = new UserPermission();
        fileInfo.setUserPermission(userPermission);

        UserInfo userInfo = new UserInfo();
        person = effectivePerson.getDistinguishedName();
        userInfo.setId(person);
        userInfo.setName(effectivePerson.getName());

        fileInfoModel.setFileInfo(fileInfo);
        fileInfoModel.setUserInfo(userInfo);
        fileInfoModel.setFileHistoryList(this.assembleFileHistory(attachmentVersionList, attachment.isAttachment3(), config, effectivePerson));

        return fileInfoModel;
    }

}
