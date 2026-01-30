package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileInfoModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ActionFilePreview extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionFilePreview.class);
    private static Map<String, String> tokenMap = Map.of("x_pan_assemble_control","attachment3/preview/file/info",
            "x_knowledge_assemble_control","attachment3/preview/file/info");
    private static final Integer RESULT_ERROR_CODE = 10001;

    ActionResult<ActionFilePreview.Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if(StringUtils.isBlank(wi.getAppToken()) || !tokenMap.containsKey(wi.getAppToken())){
            throw new ExceptionCustom("无效的认证票据");
        }
        if(StringUtils.isBlank(wi.getFileId())){
            throw new ExceptionCustom("参数错误");
        }
        WoFile woFile = this.getWoFile(wi, effectivePerson);
        if(woFile == null){
            throw new ExceptionCustom("业务系统异常");
        }
        if(RESULT_ERROR_CODE.equals(woFile.getResult()) && StringUtils.isNotBlank(woFile.getMsg())){
            throw new ExceptionCustom(woFile.getMsg());
        }
        if(woFile.getFileInfo() == null || woFile.getUserInfo() == null){
            throw new ExceptionCustom("业务系统参数错误");
        }

        Wo wo = new Wo();
        result.setData(wo);

        DocumentManager.Init(request);

        String curExt = FileUtility.getFileExtension(woFile.getFileInfo().getName());
        String fileName = woFile.getFileInfo().getId() + curExt;
        FileModel fileModel = new FileModel(fileName, FileModel.DEFAULT_LANG, woFile.getUserInfo().getId(), woFile.getUserInfo().getName());
        fileModel.changeType(FileModel.MODE_VIEW, null);
        fileModel.setPermission(woFile.getFileInfo().getUserPermission());
        fileModel.document.title = woFile.getFileInfo().getName();
        fileModel.document.url = woFile.getFileInfo().getDownloadUrl();
        fileModel.document.key = ServiceConverter.GenerateRevisionId(fileName+woFile.getFileInfo().getModifyTime());

        if (DocumentManager.tokenEnabled()) {
            fileModel.BuildToken();
        }

        wo.setFileHistory(fileModel.getHistory(woFile));
        wo.setFileModel(fileModel);
        wo.setFileName(woFile.getFileInfo().getName());
        wo.setFileId(wi.getFileId());
        return result;
    }

    private WoFile getWoFile(final Wi wi, EffectivePerson effectivePerson) {
        try {
            String uri = tokenMap.get(wi.getAppToken());
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
            if(wi.getExtendParam()!=null){
                wi.getExtendParam().keySet().forEach(key -> {
                    stringBuffer.append(key).append("=").append(UrlEncoded.encodeString(wi.getExtendParam().get(key), DefaultCharset.charset));
                });
            }
            String url = stringBuffer.toString();
            logger.info(wi.getAppToken() + "===" +url);
            String json = HttpConnection.getAsString(url, null);

            if(StringUtils.isNotBlank(json)){
                WoFile woFile = gson.fromJson(json, WoFile.class);
                return woFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return extendParam;
        }

        public void setExtendParam(Map<String, String> extendParam) {
            this.extendParam = extendParam;
        }
    }

    public static class WoFile extends FileInfoModel{
        private Integer result;
        private String msg;

        public Integer getResult() {
            return result;
        }

        public void setResult(Integer result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
