package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.tools.DateTools;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import org.apache.commons.io.IOUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.apache.commons.lang3.StringUtils;

class ActionCreate extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();

            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

            Wo wo = new Wo();
            result.setData(wo);
            OnlyOfficeFile record = new OnlyOfficeFile();
            record.setRelevanceId(wi.getRelevanceId());
            record.setCategory(StringUtils.isBlank(wi.getCategory()) ? Business.TEMPLATE_APP : wi.getCategory());
            record.setCreator(effectivePerson.getDistinguishedName());
            Date date = new Date();
            record.setCreateTime(date);
            DocumentManager.Init(request);

            String curExt = FileUtility.getFileExtension(wi.getFileName());
            String fileName = record.getId() + curExt;
            String fileExt = wi.getFileType();
            String templateId = wi.getSampleName();

            if (fileExt != null) {
                try {
                    byte[] byteArray = null;
                    String templateFileName = "new." + fileExt;
                    if (StringUtils.isNotBlank(templateId)) {
                        EntityManager em = emc.get(OnlyOfficeFile.class);
                        OnlyOfficeFile template = em.find(OnlyOfficeFile.class, templateId);
                        if (template != null) {
                            StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
									template.getStorage());
							byteArray = template.readContent(mapping);
                        }
                    } else {
                        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFileName)) {
                            byteArray = IOUtils.toByteArray(stream);
                        }
                    }
                    record.setStatus("normal");

                    StorageMapping gfMapping = ThisApplication.context().storageMappings().random(OnlyOfficeFile.class);
                    record.saveContent(gfMapping, byteArray, fileName);

                    String uid = effectivePerson.getName();
                    String name = effectivePerson.getDistinguishedName();
                    Map<String, String> map = new HashMap<>();
                    map.put("created", DateTools.now());
                    map.put("id", uid);
                    map.put("name", name);
                    String jsonStr = gson.toJson(map);
                    //添加历史记录信息

                } catch (Exception ex) {
                    logger.error(ex);
                    wo.setRequestStatus(false);
                    return result;
                }
            }

            FileModel fileModel = new FileModel(fileName, "zh", effectivePerson.getDistinguishedName(), effectivePerson.getName());
            fileModel.changeType(null, null);

            record.setKey(fileModel.document.key);
            record.setFileVersion("1");
            record.setExtension(fileExt);
            fileModel.document.title = wi.getFileName();
            fileModel.document.url = DocumentManager.getFileUriById(record.getId()) + "/0";

            record.setFileModel(gson.toJson(fileModel));
            record.setFileName(wi.getFileName());
            emc.beginTransaction(OnlyOfficeFile.class);
            emc.persist(record, CheckPersistType.all);
            emc.commit();

            if (DocumentManager.tokenEnabled()) {
                fileModel.BuildToken();
            }

            wo.setFileModel(fileModel);
            wo.setId(record.getId());
            wo.setFileName(record.getFileName());
            wo.setStatus(record.getStatus());
            wo.setCreator(record.getCreator());
            wo.setFileType(record.getExtension());
            wo.setFileSize(record.getLength().toString());
            wo.setRequestStatus(true);

            return result;
        }
    }

    public static class Wo extends GsonPropertyObject {

        public boolean requestStatus;
        public String id;
        public FileModel fileModel;

        private String fileName;
        private String creator;
        private String filePath;
        private String status;
        private String fileType;
        private String fileSize;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(boolean requestStatus) {
            this.requestStatus = requestStatus;
        }

        public FileModel getFileModel() {
            return fileModel;
        }

        public void setFileModel(FileModel fileModel) {
            this.fileModel = fileModel;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

    }


    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("文件名")
        public String fileName;

        @FieldDescribe("模板id")
        public String sampleName;

        @FieldDescribe("文件类型(docx|xlsx|pptx)")
        public String fileType;

        @FieldDescribe("关联文档id")
        public String relevanceId;

        @FieldDescribe("文档分类")
        private String category;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getSampleName() {
            return sampleName;
        }

        public void setSampleName(String sampleName) {
            this.sampleName = sampleName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getRelevanceId() {
            return relevanceId;
        }

        public void setRelevanceId(String relevanceId) {
            this.relevanceId = relevanceId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

}
