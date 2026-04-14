package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import com.google.gson.JsonObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import org.apache.commons.lang3.StringUtils;

public class ConfigManager {

    public static final String CONFIG_NAME = "onlyofficeFileSettings";

	private static ConfigManager instance = null;

    @FieldDescribe("最大文件大小")
    private String filesizeMax;
    @FieldDescribe("超时时间")
    private String timeout;

    @FieldDescribe("文件存储路径")
    private String storageFolder;
    @FieldDescribe("文件模板存储路径")
    private String storageFolderTemplate;
    @FieldDescribe("公章存储路径")
    private String storageFolderSeal;
    @FieldDescribe("文件索引存储路径")
    private String storageFolderIndex;
    @FieldDescribe("文件转pdf存储路径")
    private String storageFolderMark;
    @FieldDescribe("文件转换程序路径")
    private String docbuilderEXEPath;

    @FieldDescribe("查看文件类型")
    private String docserviceViewedDocs = ".pdf|.docx|.xlsx|.csv|.pptx|.txt";
    @FieldDescribe("编辑文件类型")
    private String docserviceEditedDocs = ".docx|.xlsx|.csv|.pptx|.txt";
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

    @FieldDescribe("附件下载地址(供onlyOffice下载附件,配置到应用上下文即可，能用内网访问则配内网地址)")
    private String downLoadUrl;

    @FieldDescribe("密钥，可为空")
    private String secret = "o2oa@2022";

    @FieldDescribe("文件下载允许ip，多值逗号分隔。可为空，表示全允许")
    private String ipWhiteList;

    @FieldDescribe("回退地址")
    private String gobackUrl;
    @FieldDescribe("回调o2oa服务地址")
    private String callbackUrl;

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

    public String getStorageFolderSeal() {
        return storageFolderSeal;
    }

    public void setStorageFolderSeal(String storageFolderSeal) {
        this.storageFolderSeal = storageFolderSeal;
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

    public String getDocserviceTempstorage() throws Exception {

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

    public String getGobackUrl() {
        return gobackUrl;
    }

    public void setGobackUrl(String gobackUrl) {
        this.gobackUrl = gobackUrl;
    }

    public String getCallbackUrl() throws Exception {
        if (StringUtils.isBlank(callbackUrl)) {
            // 调用默认地址
            String hostAddress = DocumentManager.curUserHostAddress(null);
            callbackUrl = Config.url_x_program_center_jaxrs("invoke", "cloudDocumentSrv") + "/execute";
            if (callbackUrl.indexOf("127.0.0.1") > -1) {
                callbackUrl = callbackUrl.replace("127.0.0.1", hostAddress);
            }
        }
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getIpWhiteList() {
        return ipWhiteList;
    }

    public void setIpWhiteList(String ipWhiteList) {
        this.ipWhiteList = ipWhiteList;
    }

    public String getStorageFolderConfig() {
        return storageFolder;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public static synchronized ConfigManager init(String base) {
		if("0".equals(base)){
			instance = null;
		}
		if(instance!=null){
			return instance;
		}
		try {
			JsonObject jpaObject = Config.customConfig(CONFIG_NAME);
			if(jpaObject!=null) {
				instance = XGsonBuilder.instance().fromJson(jpaObject, ConfigManager.class);
			}
		} catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            if(instance==null){
                instance = new ConfigManager();
            }
        }
		return instance;
	}


}
