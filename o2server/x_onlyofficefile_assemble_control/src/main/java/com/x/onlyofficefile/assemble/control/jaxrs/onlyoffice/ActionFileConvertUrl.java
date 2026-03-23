package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;

public class ActionFileConvertUrl extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionFileConvertUrl.class);

    ActionResult<ActionFileConvertUrl.Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

            Wo wo = new Wo();
            result.setData(wo);

            OnlyOfficeFile record = new OnlyOfficeFile();
            record.setFileName(wi.getFileName());
            record.setCreator(effectivePerson.getDistinguishedName());

            DocumentManager.Init(request);
            String curExt = FileUtility.getFileExtension(wi.getFileName());
            String fileName = record.getId() + curExt;

            FileModel fileModel = new FileModel(fileName, "zh", effectivePerson.getName(), effectivePerson.getDistinguishedName());
            fileModel.changeType(FileModel.MODE_VIEW, null);
            fileModel.document.key = ServiceConverter.GenerateRevisionId(StringTools.uniqueToken());
            record.setStatus("normal");
            record.setFileVersion("1");

            fileModel.document.title = wi.getFileName();
            fileModel.document.url = wi.getUrl();

            if (DocumentManager.tokenEnabled()) {
                fileModel.BuildToken();
            }

            wo.setFileModel(fileModel);
            wo.setId(record.getId());
            wo.setFileName(record.getFileName());
            wo.setStatus(record.getStatus());
            wo.setCreator(record.getCreator());
            wo.setFileType(record.getExtension());
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

        @FieldDescribe("文件url")
        public String url;

        @FieldDescribe("用户userId")
        private String userId;

        @FieldDescribe("文件类型(docx|xlsx|pptx)")
        private String fileType;

        @FieldDescribe("文件名")
        private String fileName;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }


    }

}
