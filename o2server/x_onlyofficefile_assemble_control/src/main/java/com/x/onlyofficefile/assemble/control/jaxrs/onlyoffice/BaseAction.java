package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileInfoModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.Strings;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

import java.util.Date;
import java.util.List;
import java.util.Map;

class BaseAction extends StandardJaxrsAction {

    protected static Map<String, String> appMap = Map.of("x_pan_assemble_control","attachment3/edit/file/info/only/office",
            "x_archive_assemble_control","attachment3/edit/file/info/only/office",
            "x_knowledge_assemble_control","attachment3/edit/file/info/only/office",
            Business.PROCESS_PLATFORM_APP, "",
            Business.OFFICE_ONLINE_APP, "",
            Business.TEMPLATE_APP, "",
            Business.CMS_APP, "");

    protected static final Integer RESULT_ERROR_CODE = 10001;

    protected WrapFile getO2File(String appId, String fileId, String mode, EffectivePerson effectivePerson) throws Exception{
        WrapFile woFile = new WrapFile();
        List<NameValuePair> headers = ListTools.toList(new NameValuePair(Config.person().getTokenName(), effectivePerson.getToken()));
        String className = ThisApplication.context().applications().findApplicationName(appId);
        String url = ThisApplication.context().applications().randomWithWeight(className).getUrlJaxrsRoot();
        if(Business.PROCESS_PLATFORM_APP.equals(appId)) {
            url = url + Applications.joinQueryUri("attachment", fileId, "online", "info");
        }else{
            url = url + Applications.joinQueryUri("fileinfo", fileId, "online", "info");
        }
        WoO2File woO2File = ConnectionAction.get(url, headers).getData(WoO2File.class);
        if(BooleanUtils.isTrue(woO2File.getCanRead())){
            if(!FileModel.MODE_EDIT.equals(mode) || BooleanUtils.isNotTrue(woO2File.getCanEdit())){
                mode = FileModel.MODE_VIEW;
            }
            String name = woO2File.getName();
            if (Strings.utf8Length(name) > 255) {
                name = Strings.utf8FileNameSubString(name, 255);
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                OnlyOfficeFile record = emc.find(fileId, OnlyOfficeFile.class);
                if(record == null){
                    String downLoadUrl = Applications.joinQueryUri("attachment","download",fileId);
                    if(!Business.PROCESS_PLATFORM_APP.equals(appId)) {
                        downLoadUrl = Applications.joinQueryUri("fileinfo","download", "document",fileId);
                    }
                    byte[] fileByte = ThisApplication.context().applications().getQueryBinary(className, downLoadUrl);
                    record = new OnlyOfficeFile();
                    record.setId(fileId);
                    record.setRelevanceId(fileId);
                    record.setCategory(appId);
                    record.setCreateTime(woO2File.getCreateTime());
                    record.setDocId(woO2File.getJob());
                    record.setCreator(woO2File.getOwnerId());
                    record.setFileVersion("1");
                    record.setStatus("normal");
                    record.setFileName(name);
                    record.setLength(woO2File.getLength());
                    record.setLastUpdateTime(woO2File.getLastUpdateTime());
                    if(FileModel.MODE_EDIT.equals(mode)) {
                        StorageMapping gfMapping = ThisApplication.context().storageMappings().random(OnlyOfficeFile.class);
                        record.saveContent(gfMapping, fileByte, name);
                        record.setLastUpdateTime(woO2File.getLastUpdateTime());
                        emc.beginTransaction(OnlyOfficeFile.class);
                        emc.persist(record, CheckPersistType.all);
                        emc.commit();
                    }
                }else{
                    record.setFileName(name);
                    record.setLastUpdateTime(woO2File.getLastUpdateTime());
                }
                this.joinFile(woFile, mode, record, effectivePerson, emc);
            }
        }else{
            woFile.setResult(RESULT_ERROR_CODE);
            woFile.setMsg("您没有权限");
        }
        return woFile;
    }

    protected void joinFile(WrapFile woFile, String mode, OnlyOfficeFile record, EffectivePerson effectivePerson, EntityManagerContainer emc) throws Exception{
        String curExt = FileUtility.getFileExtension(record.getName());
        String fileName = record.getId() + curExt;
        FileModel fileModel = new FileModel(fileName, FileModel.DEFAULT_LANG, effectivePerson.getDistinguishedName(), effectivePerson.getName());
        fileModel.changeType(mode, null);
        fileModel.setCallBackUrl(record.getCategory(), effectivePerson.getToken());
        fileModel.document.title = record.getName();
        String downLoadUrl = StringUtils.substringBeforeLast(DocumentManager.getServerUrl(), "/") + "/";
        if(Business.PROCESS_PLATFORM_APP.equals(record.getCategory())) {
            downLoadUrl = downLoadUrl + Applications.joinQueryUri(record.getCategory(),"jaxrs","attachment","download", record.getId());
        }else{
            downLoadUrl = downLoadUrl + Applications.joinQueryUri(record.getCategory(),"jaxrs","fileinfo","download", "document",record.getId());
        }
        fileModel.document.url = downLoadUrl + "?"+ Config.person().getTokenName()+"="+ UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset);
        fileModel.document.key = ServiceConverter.GenerateRevisionId(fileName+record.getLastUpdateTime().getTime());
        String[] fileHistory = fileModel.getHistory(record, emc, Config.person().getTokenName(), UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset));
        if (DocumentManager.tokenEnabled()) {
            fileModel.BuildToken();
        }

        woFile.setFileModel(fileModel);
        woFile.setFileHistory(fileHistory);
    }

    protected WrapFile getOnlyOfficeFile(String fileId, String mode, EffectivePerson effectivePerson) throws Exception{
        WrapFile woFile = new WrapFile();
        if(!FileModel.MODE_EDIT.equals(mode)){
            mode = FileModel.MODE_VIEW;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            OnlyOfficeFile record = emc.find(fileId, OnlyOfficeFile.class);
            if(record == null){
                throw new ExceptionEntityNotExist(fileId);
            }
            String fileName = record.getId() + "." + record.getExtension();
            FileModel fileModel = new FileModel(fileName, "zh", effectivePerson.getDistinguishedName(), effectivePerson.getName());
            fileModel.changeType(mode, null);
            fileModel.document.url = DocumentManager.getFileUriById(record.getId()) + "/0";
            Long lastModified = record.getLastUpdateTime()!=null ? record.getLastUpdateTime().getTime() : record.getUpdateTime().getTime();
            fileModel.document.key = ServiceConverter.GenerateRevisionId(DocumentManager.curUserHostAddress(null) + "/" + fileName + "/" + lastModified);

            String[] fileHistory = fileModel.getHistory(record, emc, Config.person().getTokenName(), UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset));
            fileModel.document.url = fileModel.document.url + "?"+ Config.person().getTokenName() +"=" +
                    UrlEncoded.encodeString(effectivePerson.getToken(), DefaultCharset.charset);
            fileModel.editorConfig.user.id = effectivePerson.getDistinguishedName();
            fileModel.editorConfig.user.name = effectivePerson.getName();
            fileModel.document.title = record.getFileName();

            if (DocumentManager.tokenEnabled()) {
                fileModel.BuildToken();
            }
            woFile.setFileHistory(fileHistory);
            woFile.setFileModel(fileModel);
        }
        return woFile;
    }

    public static class WrapFile extends FileInfoModel {
        private Integer result;
        private String msg;
        private FileModel fileModel;
        private String[] FileHistory;

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
    }

    public static class WoO2File extends GsonPropertyObject {

        @FieldDescribe("附件ID.")
        private String id;
        @FieldDescribe("任务.")
        private String job;
        @FieldDescribe("附件名称.")
        private String name;
        @FieldDescribe("附件大小.")
        private Long length;
        @FieldDescribe("创建用户ID.")
        private String ownerId;
        @FieldDescribe("创建用户名称.")
        private String ownerName;
        @FieldDescribe("当前用户ID.")
        private String userId;
        @FieldDescribe("当前用户名称.")
        private String userName;
        @FieldDescribe("创建时间.")
        private Date createTime;
        @FieldDescribe("最后更新时间.")
        private Date lastUpdateTime;
        @FieldDescribe("当前用户是否可编辑.")
        private Boolean canEdit;
        @FieldDescribe("当前用户是否可阅读.")
        private Boolean canRead;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getLength() {
            return length;
        }

        public void setLength(Long length) {
            this.length = length;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Boolean getCanEdit() {
            return canEdit;
        }

        public void setCanEdit(Boolean canEdit) {
            this.canEdit = canEdit;
        }

        public Boolean getCanRead() {
            return canRead;
        }

        public void setCanRead(Boolean canRead) {
            this.canRead = canRead;
        }

        public Date getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(Date lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }
    }
}
