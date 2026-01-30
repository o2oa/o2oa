package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_onlyofficefile_assemble_control;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 流程平台或内容管理创建在线编辑文档
 * @author sword
 */
public class ActionCreateForO2 extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCreateForO2.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        if(effectivePerson.isAnonymous()){
            throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if(!Business.PROCESS_PLATFORM_APP.equals(wi.getAppToken()) && !Business.CMS_APP.equals(wi.getAppToken())){
            throw new ExceptionCustom("无效的应用ID");
        }
        if(StringUtils.isBlank(wi.getWorkId()) || StringUtils.isBlank(wi.getFileName()) || StringUtils.isBlank(wi.getSite())){
            throw new ExceptionCustom("参数错误");
        }

        String ext = FilenameUtils.getExtension(wi.getFileName()).toLowerCase();
        if (StringUtils.isBlank(ext)) {
            throw new ExceptionCustom("附件名称需带扩展名");
        }

        wi.setDocId(wi.getWorkId());
        logger.info("{}在线编辑创建附件：{} 到应用：{} 的文档：{}", effectivePerson.getDistinguishedName(), wi.getFileName(), wi.getAppToken(), wi.getWorkId());

        String appUr = ThisApplication.context().applications().randomWithWeight(
                x_onlyofficefile_assemble_control.class.getName()).getUrlJaxrsRoot();
        String fileUrl = StringUtils.substringBeforeLast(appUr, "jaxrs") + "template/blank." + ext;
        if(StringUtils.isNotBlank(wi.getTempId())){
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                OnlyOfficeFile record = emc.find(wi.getTempId(), OnlyOfficeFile.class);
                if(record != null){
                    fileUrl = appUr + Applications.joinQueryUri("onlyoffice", "file", wi.getTempId(), "0");
                    fileUrl = fileUrl + "?" + Config.person().getTokenName()+"="+ UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset);
                }
            }
        }
        wi.setFileUrl(fileUrl);

        List<NameValuePair> headers = ListTools.toList(new NameValuePair(Config.person().getTokenName(), effectivePerson.getToken()));
        String className = ThisApplication.context().applications().findApplicationName(wi.getAppToken());
        String url = ThisApplication.context().applications().randomWithWeight(className).getUrlJaxrsRoot();
        if(Business.PROCESS_PLATFORM_APP.equals(wi.getAppToken())) {
            url = url + Applications.joinQueryUri("attachment", "upload", "with", "url");
        }else{
            url = url + Applications.joinQueryUri("fileinfo", "upload", "with", "url");
        }

        WoId woId = ConnectionAction.post(url, headers, wi).getData(WoId.class);

        WrapFile woFile = this.getO2File(wi.getAppToken(), woId.getId(), FileModel.MODE_EDIT, effectivePerson);

        if(woFile == null){
            throw new ExceptionCustom("业务系统异常");
        }

        if(RESULT_ERROR_CODE.equals(woFile.getResult()) && StringUtils.isNotBlank(woFile.getMsg())){
            throw new ExceptionCustom(woFile.getMsg());
        }

        wo.setFileHistory(woFile.getFileHistory());
        wo.setFileModel(woFile.getFileModel());
        wo.setFileName(woFile.getFileModel().document.title);
        wo.setFileId(woId.getId());

        result.setData(wo);
        return result;
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("文档信息")
        private FileModel fileModel;

        @FieldDescribe("文档名称")
        private String fileName;

        @FieldDescribe("文档ID")
        private String fileId;

        @FieldDescribe("历史记录")
        public String[] FileHistory;

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

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String[] getFileHistory() {
            return FileHistory;
        }

        public void setFileHistory(String[] fileHistory) {
            FileHistory = fileHistory;
        }
    }


    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("应用ID")
        private String appToken;

        @FieldDescribe("*流程Work、WorkCompleted或内容管理文档的id.")
        private String workId;

        @FieldDescribe("*文件名称,带扩展名的文件名.")
        private String fileName;

        @FieldDescribe("*附件分类.")
        private String site;

        @FieldDescribe("模板文件ID,为空或不存在就取空模板.")
        private String tempId;

        private String fileUrl;

        private String docId;

        public String getWorkId() {
            return workId;
        }

        public void setWorkId(String workId) {
            this.workId = workId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getTempId() {
            return tempId;
        }

        public void setTempId(String tempId) {
            this.tempId = tempId;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getAppToken() {
            return appToken;
        }

        public void setAppToken(String appToken) {
            this.appToken = appToken;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }
    }



}
