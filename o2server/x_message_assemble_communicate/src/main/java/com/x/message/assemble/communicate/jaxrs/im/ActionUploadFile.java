package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsgFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.util.Date;

/**
 * Created by fancyLou on 2020-06-15.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionUploadFile extends BaseAction {

    public ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId, String type, String fileName, byte[] bytes,
                                    FormDataContentDisposition disposition) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            StorageMapping mapping = ThisApplication.context().storageMappings().random(IMMsgFile.class);
            if (null == mapping) {
                throw new ExceptionAllocateStorageMaaping();
            }

            /** 文件名编码转换 */
            if (StringUtils.isEmpty(fileName)) {
                try {
                    fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
                            DefaultCharset.charset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fileName = FilenameUtils.getName(fileName);
            if (StringUtils.isEmpty(fileName)) {
                throw new ExceptionFileNameEmpty();
            }
            /** 禁止不带扩展名的文件上传 */
            if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
                throw new ExceptionEmptyExtension(fileName);
            }
            if(bytes==null){
                throw new ExceptionAttachmentNone(fileName);
            }
            IMMsgFile file = new IMMsgFile();
            file.setName(fileName);
            file.setStorage(mapping.getName());
            file.setPerson(effectivePerson.getDistinguishedName());
            Date now = new Date();
            file.setCreateTime(now);
            file.setLastUpdateTime(now);
            file.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)));
            file.setConversationId(conversationId);
            file.setType(type);

            emc.check(file, CheckPersistType.all);
            file.saveContent(mapping, bytes, fileName);
            emc.beginTransaction(IMMsgFile.class);
            emc.persist(file);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(file.getId());
            wo.setFileExtension(file.getExtension());
            wo.setFileName(fileName);
            result.setData(wo);
            return result;
        }
    }




    public static class Wo extends WoId {

        @FieldDescribe( "文件扩展名" )
        private String fileExtension;

        @FieldDescribe( "文件名" )
        private String fileName;

        public String getFileExtension() {
            return fileExtension;
        }

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
