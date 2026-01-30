package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.config;

import com.google.gson.JsonElement;
import com.x.base.core.project.Application;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_onlyofficefile_assemble_control;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ConfigManager;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 修改配置文件
 * @author sword
 */
public class ActionUpdateConfig extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(ActionUpdateConfig.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        if(effectivePerson.isNotManager()){
            throw new ExceptionAccessDenied(effectivePerson);
        }
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        result.setData(wo);
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        ConfigWi configWi = new ConfigWi();
        configWi.setFileName(ConfigManager.CONFIG_NAME + ".json");
        configWi.setFileContent(gson.toJson(wi));
        CipherConnectionAction.post(false,
                Config.url_x_program_center_jaxrs("config", "save"), configWi);

        //刷新配置文件
        List<Application> apps = ThisApplication.context().applications().get(
                x_onlyofficefile_assemble_control.class);
        if (ListTools.isNotEmpty(apps)) {
            apps.stream().forEach(o -> {
                String url = o.getUrlJaxrsRoot() + "onlyofficeconfig/refresh?timestamp=" + System.currentTimeMillis();
                try {
                    CipherConnectionAction.get(effectivePerson.getDebugger(), url);
                } catch (Exception e) {
                    logger.warn("refresh onlyOffice config error:{}", e.getMessage());
                }
            });
        }

        wo.setStatus("sucess");
        return result;
    }


    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("最大文件大小")
        private String filesizeMax;
        @FieldDescribe("超时时间")
        private String timeout;

        @FieldDescribe("文件存储路径")
        private String storageFolder;
        @FieldDescribe("文件模板存储路径")
        private String storageFolderTemplate;
        @FieldDescribe("文件索引存储路径")
        private String storageFolderIndex;
        @FieldDescribe("文件转pdf存储路径")
        private String storageFolderMark;
        @FieldDescribe("文件转换程序路径")
        private String docbuilderEXEPath;

        @FieldDescribe("查看文件类型")
        private String docserviceViewedDocs;
        @FieldDescribe("编辑文件类型")
        private String docserviceEditedDocs;
        @FieldDescribe("转换文件类型")
        private String docserviceConvertDocs;

        @FieldDescribe("onlyoffice转换地址")
        private String docserviceConverter;
        @FieldDescribe("onlyoffice临时存储路径")
        private String docserviceTempstorage;
        @FieldDescribe("onlyoffice前端api地址")
        private String docserviceApi;
        @FieldDescribe("onlyoffice前端刷新地址")
        private String docservicePreloader;
        @FieldDescribe("密钥，可为空")
        private String secret;

        @FieldDescribe("附件下载地址(供onlyOffice下载附件,配置到应用上下文即可，能用内网访问则配内网地址)")
        private String downLoadUrl;
        @FieldDescribe("文件下载允许ip，多值逗号分隔。可为空，表示全允许")
        private String ipWhiteList;

        @FieldDescribe("回退地址")
        private String gobackUrl;


        public String getFilesizeMax() {
            return filesizeMax;
        }

        public void setFilesizeMax(String filesizeMax) {
            this.filesizeMax = filesizeMax;
        }

        public String getTimeout() {
            return timeout;
        }

        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }

        public String getDocserviceViewedDocs() {
            return docserviceViewedDocs;
        }

        public void setDocserviceViewedDocs(String docserviceViewedDocs) {
            this.docserviceViewedDocs = docserviceViewedDocs;
        }

        public String getDocserviceEditedDocs() {
            return docserviceEditedDocs;
        }

        public void setDocserviceEditedDocs(String docserviceEditedDocs) {
            this.docserviceEditedDocs = docserviceEditedDocs;
        }

        public String getDocserviceConvertDocs() {
            return docserviceConvertDocs;
        }

        public void setDocserviceConvertDocs(String docserviceConvertDocs) {
            this.docserviceConvertDocs = docserviceConvertDocs;
        }

        public String getStorageFolder() {
            return storageFolder;
        }

        public void setStorageFolder(String storageFolder) {
            this.storageFolder = storageFolder;
        }

        public String getStorageFolderTemplate() {
            return storageFolderTemplate;
        }

        public void setStorageFolderTemplate(String storageFolderTemplate) {
            this.storageFolderTemplate = storageFolderTemplate;
        }

        public String getStorageFolderIndex() {
            return storageFolderIndex;
        }

        public void setStorageFolderIndex(String storageFolderIndex) {
            this.storageFolderIndex = storageFolderIndex;
        }

        public String getStorageFolderMark() {
            return storageFolderMark;
        }

        public void setStorageFolderMark(String storageFolderMark) {
            this.storageFolderMark = storageFolderMark;
        }

        public String getDocbuilderEXEPath() {
            return docbuilderEXEPath;
        }

        public void setDocbuilderEXEPath(String docbuilderEXEPath) {
            this.docbuilderEXEPath = docbuilderEXEPath;
        }

        public String getDocserviceConverter() {
            return docserviceConverter;
        }

        public void setDocserviceConverter(String docserviceConverter) {
            this.docserviceConverter = docserviceConverter;
        }

        public String getDocserviceTempstorage() {
            return docserviceTempstorage;
        }

        public void setDocserviceTempstorage(String docserviceTempstorage) {
            this.docserviceTempstorage = docserviceTempstorage;
        }

        public String getDocserviceApi() {
            return docserviceApi;
        }

        public void setDocserviceApi(String docserviceApi) {
            this.docserviceApi = docserviceApi;
        }

        public String getDocservicePreloader() {
            return docservicePreloader;
        }

        public void setDocservicePreloader(String docservicePreloader) {
            this.docservicePreloader = docservicePreloader;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getIpWhiteList() {
            return ipWhiteList;
        }

        public void setIpWhiteList(String ipWhiteList) {
            this.ipWhiteList = ipWhiteList;
        }

        public String getGobackUrl() {
            return gobackUrl;
        }

        public void setGobackUrl(String gobackUrl) {
            this.gobackUrl = gobackUrl;
        }

        public String getDownLoadUrl() {
            return downLoadUrl;
        }

        public void setDownLoadUrl(String downLoadUrl) {
            this.downLoadUrl = downLoadUrl;
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("执行结束")
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ConfigWi extends GsonPropertyObject {

        @FieldDescribe("文件名")
        private String fileName;

        @FieldDescribe("config文件内容")
        private String fileContent;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }

    }

}



