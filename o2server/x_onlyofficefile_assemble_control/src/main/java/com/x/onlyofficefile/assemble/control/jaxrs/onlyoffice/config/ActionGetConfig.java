package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.config;


import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_onlyofficefile_assemble_control;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ConfigManager;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.http.HttpHeaders;

public class ActionGetConfig extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionGetConfig.class);

	ActionResult<Wo> execute(HttpServletRequest request,EffectivePerson effectivePerson) throws Exception {
		logger.debug("{} execute", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		result.setData(wo);
		ConfigManager configManager = ConfigManager.init(Config.base());
		if(configManager!=null && StringUtils.isNotBlank(configManager.getDownLoadUrl())) {
			wo.setFilesizeMax(configManager.getFilesizeMax());
			wo.setTimeout(configManager.getTimeout());
			wo.setStorageFolder(configManager.getStorageFolder());
			wo.setStorageFolderTemplate(configManager.getStorageFolderTemplate());
			wo.setStorageFolderIndex(configManager.getStorageFolderIndex());
			wo.setStorageFolderMark(configManager.getStorageFolderMark());
			wo.setDocbuilderEXEPath(configManager.getDocbuilderEXEPath());

			wo.setDocserviceViewedDocs(configManager.getDocserviceViewedDocs());
			wo.setDocserviceEditedDocs(configManager.getDocserviceEditedDocs());
			wo.setDocserviceConvertDocs(configManager.getDocserviceConvertDocs());

			wo.setDocserviceConverter(configManager.getDocserviceConverter());
			wo.setDocserviceTempstorage(configManager.getDocserviceTempstorage());
			wo.setDocserviceApi(configManager.getDocserviceApi());
			wo.setDocservicePreloader(configManager.getDocservicePreloader());
			wo.setIpWhiteList(configManager.getIpWhiteList());
			wo.setSecret(configManager.getSecret());
			wo.setGobackUrl(configManager.getGobackUrl());
			wo.setDownLoadUrl(configManager.getDownLoadUrl());
		}else{
			String referer = request.getHeader(HttpHeaders.REFERER);
			if(StringUtils.isNotBlank(referer)) {
				URL url = new URL(referer);
				int port = url.getPort();
				String downloadUrl = url.getProtocol() + "://" + url.getHost() + ((port < 0 || port == 80 || port == 443) ? "" : ":" + port)
						+ "/" + x_onlyofficefile_assemble_control.class.getSimpleName();
				wo.setDownLoadUrl(downloadUrl);
				String protocol = url.getProtocol() + "://";
				wo.setDocserviceConverter(protocol + wo.getDocserviceConverter());
				wo.setDocserviceTempstorage(protocol + wo.getDocserviceTempstorage());
				wo.setDocserviceApi(protocol + wo.getDocserviceApi());
				wo.setDocservicePreloader(protocol + wo.getDocservicePreloader());
			}
		}
		return result;
	}


	public static class Wo  extends GsonPropertyObject {

		@FieldDescribe("最大文件大小")
		private String filesizeMax = "5242880";
		@FieldDescribe("超时时间")
		private String timeout = "120000";

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
		private String docserviceViewedDocs = ".pdf|.docx|.xlsx|.csv|.pptx|.txt";
		@FieldDescribe("编辑文件类型")
		private String docserviceEditedDocs = ".docx|.xlsx|.csv|.pptx|.txt";
		@FieldDescribe("转换文件类型")
		private String docserviceConvertDocs = ".docm|.dotx|.dotm|.dot|.doc|.odt|.fodt|.ott|.xlsm|.xltx|.xltm|.xlt|.xls" +
				"|.ods|.fods|.ots|.pptm|.ppt|.ppsx|.ppsm|.pps|.potx|.potm|.pot|.odp|.fodp|.otp|.rtf|.mht|.html|.htm|.epub";

		@FieldDescribe("onlyoffice转换地址")
		private String docserviceConverter = "onlyoffice.o2oa.net/ConvertService.ashx";
		@FieldDescribe("onlyoffice临时存储路径")
		private String docserviceTempstorage = "onlyoffice.o2oa.net/ResourceService.ashx";
		@FieldDescribe("onlyoffice前端api地址")
		private String docserviceApi = "onlyoffice.o2oa.net/web-apps/apps/api/documents/api.js";
		@FieldDescribe("onlyoffice前端刷新地址")
		private String docservicePreloader = "onlyoffice.o2oa.net/web-apps/apps/api/documents/cache-scripts.html";

		@FieldDescribe("密钥，可为空")
		private String secret = "o2oa@2022";

		@FieldDescribe("附件下载地址(供onlyOffice下载附件,配置到应用上下文即可，能用内网访问则配内网地址)")
		private String downLoadUrl;
		@FieldDescribe("文件下载允许ip，多值逗号分隔。可为空，表示全允许")
		private String ipWhiteList = "{}";

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

	public static class Wi extends GsonPropertyObject {

	}
}
