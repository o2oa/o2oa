package com.x.ai.assemble.control.jaxrs.file;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.core.entity.FileInfo;
import com.x.processplatform.core.entity.content.Attachment;
import org.apache.commons.lang3.StringUtils;

class ActionCopyFile extends BaseAction {
    public static final String COPY_FROM_CMS = "cms";
    public static final String COPY_FROM_PROCESSPLATFORM = "processPlatform";
    public static final String COPY_FROM_PAN = "x_pan_assemble_control";

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {

        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getId())) {
            throw new ExceptionFieldEmpty("id");
        }
        if (StringUtils.isEmpty(wi.getName())) {
            throw new ExceptionFieldEmpty("name");
        }
        this.verifyConstraint(wi.getName());
        if (StringUtils.isEmpty(wi.getCopyFrom())) {
            throw new ExceptionFieldEmpty("copyFrom");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            byte[] bs = null;
            if (COPY_FROM_CMS.equals(wi.getCopyFrom())) {
                FileInfo fileInfo = emc.find(wi.getId(), FileInfo.class);
                if (fileInfo == null) {
                    throw new ExceptionEntityNotExist(wi.getId(), FileInfo.class);
                }
                StorageMapping fromStorageMapping = ThisApplication.context()
                        .storageMappings()
                        .get(FileInfo.class, fileInfo.getStorage());
                bs = fileInfo.readContent(fromStorageMapping);
            } else if (COPY_FROM_PAN.equals(wi.getCopyFrom())) {
                String className = ThisApplication.context().applications()
                        .findApplicationName(COPY_FROM_PAN);
                String downLoadUrl = Applications.joinQueryUri("attachment3", wi.getId(), "download");
                bs = ThisApplication.context().applications().getQueryBinary(className, downLoadUrl);
            } else if(COPY_FROM_PROCESSPLATFORM.equals(wi.getCopyFrom())){
                Attachment o = emc.find(wi.getId(), Attachment.class);
                if (o == null) {
                    throw new ExceptionEntityNotExist(wi.getId(), Attachment.class);
                }
                StorageMapping fromStorageMapping = ThisApplication.context()
                        .storageMappings()
                        .get(Attachment.class, o.getStorage());
                bs = o.readContent(fromStorageMapping);
            } else {
                throw new ExceptionCustom("没有匹配的拷贝来源");
            }
            StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
            File file = new File(mapping.getName(), wi.getName(), effectivePerson.getDistinguishedName());
            emc.check(file, CheckPersistType.all);
            String fileId = this.uploadToO2Ai(file, bs);
            file.saveContent(mapping, bs, wi.getName());
            file.setFileId(fileId);
            emc.beginTransaction(File.class);
            emc.persist(file);
            emc.commit();
            wo.setId(StringUtils.isBlank(fileId) ? file.getId() : fileId);
        }
        result.setData(wo);
        return result;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 6624639107781167248L;

        @FieldDescribe("附件标识.")
        private String id;

        @FieldDescribe("附件名称.")
        private String name;

        @FieldDescribe("附件来源(cms|内容管理附件、processPlatform|流程平台附件、x_pan_assemble_control|企业网盘附件).")
        private String copyFrom;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCopyFrom() {
            return copyFrom;
        }

        public void setCopyFrom(String copyFrom) {
            this.copyFrom = copyFrom;
        }
    }

    public static class Wo extends WoId {

    }

}
