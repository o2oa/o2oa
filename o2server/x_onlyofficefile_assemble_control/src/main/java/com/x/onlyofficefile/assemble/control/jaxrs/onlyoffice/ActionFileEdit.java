package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

/**
 * @author sword
 */
public class ActionFileEdit extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionFileEdit.class);
    private static Map<String, String> appViewMap = Map.of("x_pan_assemble_control","attachment3/preview/file/info",
            "x_archive_assemble_control","attachment3/preview/file/info",
            "x_knowledge_assemble_control","attachment3/preview/file/info");


    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if(StringUtils.isBlank(wi.getAppToken())){
            throw new ExceptionCustom("无效的认证票据");
        }
        if(StringUtils.isBlank(wi.getFileId())){
            throw new ExceptionCustom("参数错误");
        }
        DocumentManager.Init(request);
        WrapFile woFile = this.getWoFile(wi, effectivePerson);
        if(woFile == null){
            throw new ExceptionCustom("业务系统异常");
        }
        if(RESULT_ERROR_CODE.equals(woFile.getResult()) && StringUtils.isNotBlank(woFile.getMsg())){
            throw new ExceptionCustom(woFile.getMsg());
        }
        if(woFile.getFileModel() !=null ){
            wo.setFileHistory(woFile.getFileHistory());
            woFile.getFileModel().setPermission(wi.getExtendParam().get(FileModel.PERMISSION_KEY));
            wo.setFileModel(woFile.getFileModel());
            wo.setFileName(woFile.getFileModel().document.title);
        }else {
            if (woFile.getFileInfo() == null || woFile.getUserInfo() == null) {
                throw new ExceptionCustom("业务系统参数错误");
            }

            String curExt = FileUtility.getFileExtension(woFile.getFileInfo().getName());
            String fileName = woFile.getFileInfo().getId() + curExt;
            FileModel fileModel = new FileModel(fileName, FileModel.DEFAULT_LANG, woFile.getUserInfo().getId(), woFile.getUserInfo().getName());
            fileModel.changeType(wi.getMode(), null);
            fileModel.setCallBackUrl(wi.getAppToken(), effectivePerson.getToken());
            fileModel.setPermission(woFile.getFileInfo().getUserPermission());
            fileModel.document.title = woFile.getFileInfo().getName();
            fileModel.document.url = woFile.getFileInfo().getDownloadUrl();
            fileModel.document.key = ServiceConverter.GenerateRevisionId(fileName + woFile.getFileInfo().getVersion());

            if (DocumentManager.tokenEnabled()) {
                fileModel.BuildToken();
            }

            wo.setFileHistory(fileModel.getHistory(woFile));
            wo.setFileModel(fileModel);
            wo.setFileName(woFile.getFileInfo().getName());
        }
        wo.setFileId(wi.getFileId());

        result.setData(wo);
        return result;
    }

    private WrapFile getWoFile(final Wi wi, EffectivePerson effectivePerson) throws Exception{
        if(Business.PROCESS_PLATFORM_APP.equals(wi.getAppToken()) || Business.CMS_APP.equals(wi.getAppToken())){
            return this.getO2File(wi.getAppToken(), wi.getFileId(), wi.getMode(), effectivePerson);
        }
        String uri = appMap.get(wi.getAppToken());
        if(StringUtils.isNotBlank(uri)) {
            try {
                if (FileModel.MODE_VIEW.equals(wi.getMode()) && appViewMap.containsKey(wi.getAppToken())) {
                    uri = appViewMap.get(wi.getAppToken());
                }
                final StringBuffer stringBuffer = new StringBuffer();
                String className = ThisApplication.context().applications().findApplicationName(wi.getAppToken());
                stringBuffer.append(ThisApplication.context().applications().randomWithWeight(className).getUrlJaxrsRoot())
                        .append(uri)
                        .append("?fileId=")
                        .append(wi.getFileId())
                        .append("&")
                        .append(Config.person().getTokenName())
                        .append("=")
                        .append(UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset));
                if (wi.getExtendParam() != null) {
                    wi.getExtendParam().keySet().forEach(key -> {
                        stringBuffer.append(key).append("=").append(UrlEncoded.encodeString(wi.getExtendParam().get(key), DefaultCharset.charset));
                    });
                }
                String url = stringBuffer.toString();
                logger.debug(wi.getAppToken() + "===" + url);
                String json = HttpConnection.getAsString(url, null);

                if (StringUtils.isNotBlank(json)) {
                    WrapFile woFile = gson.fromJson(json, WrapFile.class);
                    return woFile;
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }else{
            if(!Business.OFFICE_ONLINE_APP.equals(wi.getAppToken()) && effectivePerson.isNotManager()){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            return this.getOnlyOfficeFile(wi.getFileId(), wi.getMode(), effectivePerson);
        }
        return null;
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

        @FieldDescribe("临时秘钥")
        private String appToken;

        @FieldDescribe("文件Id")
        private String fileId;

        @FieldDescribe("编辑模式：edit(默认)|view")
        private String mode;

        @FieldDescribe("扩展参数，key:value对象")
        @FieldTypeDescribe(fieldType = "class", fieldTypeName = "Map", fieldValue = "{\"key\": \"value\"}")
        private Map<String, String> extendParam;

        public String getAppToken() {
            return appToken;
        }

        public void setAppToken(String appToken) {
            this.appToken = appToken;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public Map<String, String> getExtendParam() {
            return extendParam == null ? new HashMap<>() : extendParam;
        }

        public void setExtendParam(Map<String, String> extendParam) {
            this.extendParam = extendParam;
        }

        public String getMode() {
            return FileModel.MODE_VIEW.equals(mode) ? mode : FileModel.MODE_EDIT;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }



}
