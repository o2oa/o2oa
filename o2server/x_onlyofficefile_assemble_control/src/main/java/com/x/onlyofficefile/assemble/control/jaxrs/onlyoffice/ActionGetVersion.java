package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.eclipse.jetty.util.UrlEncoded;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

public class ActionGetVersion extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, String version) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            result.setData(wo);
            EntityManager em = emc.get(OnlyOfficeFile.class);
            OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);
            FileModel fileModel = null;
            String[] fileHistory = null;
            DocumentManager.Init(request);

            //断判url参数xtoken
            if (DocumentManager.tokenEnabled()) {
                String xtoken = request.getParameter("xtoken");
                if (!record.getFileToken().equalsIgnoreCase(xtoken)) {
                    throw new Exception("不允许访问,xtoken值不对");
                }
            }

            if (record != null) {
                fileModel = gson.fromJson(record.getFileModel(), FileModel.class);
                String fileName = record.getId() + "." + record.getExtension();

                //当前版本
                if ("0".equals(version) || record.getFileVersion().equals(version)) {
                    Long lastModified = record.getLastUpdateTime()!=null ? record.getLastUpdateTime().getTime() : record.getUpdateTime().getTime();
                    fileModel.document.key = ServiceConverter.GenerateRevisionId(DocumentManager.curUserHostAddress(null) + "/" + fileName + "/" + Long.toString(lastModified));

                    if (record.getCreator().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
                        fileModel.editorConfig.mode = "edit";
                        fileModel.mode = "edit";
                    } else {
                        fileModel.mode = "edit";
                        fileModel.editorConfig.mode = "edit";
                    }

                } else {
                    //历史版本
                    String url = fileModel.document.url;
                    url = url.substring(0, url.lastIndexOf("/"));
                    url = url + "/" + version;
                    fileModel.document.url = url;

                    Long lastModified = record.getLastUpdateTime()!=null ? record.getLastUpdateTime().getTime() : record.getUpdateTime().getTime();
                    fileModel.document.key = ServiceConverter.GenerateRevisionId(DocumentManager.curUserHostAddress(null) + "/" + fileName + "/" + Long.toString(lastModified));
                    fileModel.mode = "view";
                    fileModel.editorConfig.mode = "view";
                }

                String xtoken = null;
                if (DocumentManager.tokenEnabled()) {
                    String tokenSecret = DocumentManager.getTokenSecret();
                    xtoken = Crypto.encrypt(String.valueOf(record.getLength()), tokenSecret);
                    record.setFileToken(xtoken);
                    emc.beginTransaction(OnlyOfficeFile.class);
                    emc.persist(record, CheckPersistType.all);
                    emc.commit();
                }

                fileHistory = fileModel.getHistory(record, emc, Config.person().getTokenName(), UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset));
                fileModel.document.url = fileModel.document.url + "?"+ Config.person().getTokenName() +"=" +
                        UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset);

                fileModel.editorConfig.user.id = effectivePerson.getName();
                fileModel.editorConfig.user.name = effectivePerson.getDistinguishedName();
                fileModel.document.title = record.getFileName();
            } else {
                wo.setRequestStatus(false);
                wo.setMessage("没有找到id=" + id);
                logger.info("没有找到id=" + id);
                return result;
            }

            if (DocumentManager.tokenEnabled()) {

                fileModel.BuildToken();
            }

            wo.setFileHistory(fileHistory);
            wo.setFileModel(fileModel);

            wo.setFileName(record.getFileName());
            wo.setStatus(record.getStatus());
            wo.setCreator(record.getCreator());
            wo.setFileType(record.getExtension());
            wo.setFileSize(String.valueOf(record.getLength()));
            wo.setRequestStatus(true);
            return result;
        }
    }


    public static class Wo extends GsonPropertyObject {

        public boolean requestStatus;

        private String fileName;
        private String creator;
        private String filePath;
        private String status;
        private String fileType;
        private String fileSize;
        public FileModel fileModel;
        public String[] FileHistory;
        public String message;


        public boolean isRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(boolean requestStatus) {
            this.requestStatus = requestStatus;
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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public FileModel getFileModel() {
            return fileModel;
        }

        public void setFileModel(FileModel fileModel) {
            this.fileModel = fileModel;
        }

        public String[] getFileHistory() {
            return FileHistory;
        }

        public void setFileHistory(String[] fileHistory) {
            FileHistory = fileHistory;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

    }

    public static class Wi extends GsonPropertyObject {

    }
}
