package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ConfigManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.core.entity.OnlyOfficeFileVersion;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class FileModel {
	private static Logger logger = LoggerFactory.getLogger(FileModel.class);
	public static final String MODE_EDIT = "edit";
	public static final String MODE_VIEW = "view";
	public static final String DEFAULT_LANG = "zh";
	public static final String PERMISSION_KEY = "permissions";

	public String type = "desktop";
	public String mode = MODE_EDIT;
	public String documentType;
	public Document document;
	public EditorConfig editorConfig;
	public String token;

	public FileModel(String fileName, String lang, String uid, String uName) throws Exception {
		if (fileName == null) {
			fileName = "";
		}
		fileName = fileName.trim();
		documentType = FileUtility.GetFileType(fileName).toString().toLowerCase();
		document = new Document();
		document.title = fileName;
		document.url = "";
		document.fileType = FileUtility.getFileExtension(fileName).replace(".", "");
		document.key = "";
		editorConfig = new EditorConfig();
		editorConfig.callbackUrl = DocumentManager.getCallback(fileName);
		if (lang != null) {
			editorConfig.lang = lang;
		}
		if (uid != null) {
			editorConfig.user.id = uid;
		}
		if (uName != null) {
			editorConfig.user.name = uName;
		}

		try {
			editorConfig.customization.goback.url = ConfigManager.init(Config.base()).getGobackUrl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		changeType(mode, type);
	}

	public void setCallBackUrl(String appId, String authToken){
		editorConfig.callbackUrl = DocumentManager.getCallBack(document.title, appId, authToken);
	}

	public void changeType(String _mode, String _type) {
		if (_mode != null) {
			mode = _mode;
		}
		if (_type != null) {
			type = _type;
		}

		Boolean canEdit = DocumentManager.getEditedExts().contains(FileUtility.getFileExtension(document.title));

		editorConfig.mode = canEdit && !mode.equals(MODE_VIEW) ? MODE_EDIT : MODE_VIEW;

		document.permissions = new Permissions(mode, type, canEdit);

		if (type.equals("embedded")) {
			initDesktop();
		}
	}



	public void setPermission(UserPermission userPermission){
		document.permissions.print = userPermission.getPrint() > 0;
		document.permissions.copy = userPermission.getCopy() > 0;
		document.permissions.download = userPermission.getExport() > 0;
	}

	public void setPermission(String permission){
		if(StringUtils.isNotBlank(permission)){
			Gson gson = new Gson();
			Permissions permissions = gson.fromJson(permission, Permissions.class);
			Boolean canEdit = DocumentManager.getEditedExts().contains(FileUtility.getFileExtension(document.title));
			permissions.init(mode, canEdit);
			document.permissions = permissions;
			if (DocumentManager.tokenEnabled()) {
				this.BuildToken();
			}
		}
	}

	public void initDesktop() {
		editorConfig.InitDesktop(document.url);
	}

	public void BuildToken() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("documentType", documentType);
		map.put("document", document);
		map.put("editorConfig", editorConfig);
		token = DocumentManager.createToken(map);
	}

	public String[] getHistory(FileInfoModel fileInfoModel) {
		List<FileHistory> list = fileInfoModel.getFileHistoryList();
		if(ListTools.isNotEmpty(list)){
			list.add(fileInfoModel.getFileInfo());
			Map<Integer, FileHistory> fileMap = list.stream().collect(Collectors.toMap(FileHistory::getVersion, o -> o));
			List<Integer> versionList = new ArrayList<>(fileMap.keySet());
			Collections.sort(versionList);
			Set<Object> hist = new HashSet<>();
			Map<String, Object> histData = new HashMap<>();
			Integer preVersion = versionList.get(0);
			for(Integer version : versionList){
				FileHistory fileHistory = fileMap.get(version);
				Map<String, Object> obj = new HashMap<>();
				Map<String, Object> dataObj = new HashMap<>();
				String key = ServiceConverter.GenerateRevisionId(document.title + fileHistory.getVersion());
				obj.put("key", key);
				obj.put("version", version);
				obj.put("created", DateTools.format(new Date(fileHistory.getCreateTime())));
				Map<String, Object> user = new HashMap<>(2);
				user.put("id", fileHistory.getCreatorId());
				user.put("name", fileHistory.getCreatorName());
				obj.put("user", user);

				dataObj.put("key", key);
				dataObj.put("url", fileHistory.getDownloadUrl());
				dataObj.put("version", version);

				if(version > preVersion){
					FileHistory preFile = fileMap.get(preVersion);
					JsonObject changes = XGsonBuilder.instance().fromJson(preFile.getChanges(), JsonObject.class);
					obj.put("changes", changes.get("changes"));
					obj.put("serverVersion", changes.get("serverVersion"));

					Map<String, Object> prev = (Map<String, Object>) histData.get(Integer.toString(preVersion));
					Map<String, Object> prevInfo = new HashMap<>();
					prevInfo.put("key", prev.get("key"));
					prevInfo.put("url", prev.get("url"));
					dataObj.put("previous", prevInfo);
					dataObj.put("changesUrl", preFile.getDiffUrl());
				}

				if (DocumentManager.tokenEnabled()) {
					String tokenHistory = DocumentManager.createToken(dataObj);
					dataObj.put("token", tokenHistory);
				}

				hist.add(obj);
				histData.put(Integer.toString(version), dataObj);
				preVersion = version;
			}

			Map<String, Object> histObj = new HashMap<>(2);
			histObj.put("currentVersion", fileInfoModel.getFileInfo().getVersion());
			histObj.put("history", hist);
			return new String[] { XGsonBuilder.toJson(histObj), XGsonBuilder.toJson(histData) };
		}
		return new String[] { "", "" };
	}

	public String[] getHistory(OnlyOfficeFile record, EntityManagerContainer emc, String param, String token) throws Exception {
		List<OnlyOfficeFileVersion> list = emc.listEqualAndEqual(OnlyOfficeFileVersion.class, OnlyOfficeFileVersion.fileId_FIELDNAME, record.getId(),
				OnlyOfficeFileVersion.fileType_FIELDNAME, OnlyOfficeFileVersion.FILE_TYPE_DIFF);
		if(!list.isEmpty()){
			Map<Integer, OnlyOfficeFileVersion> diffMap = list.stream().collect(Collectors.toMap(OnlyOfficeFileVersion::getFileVersion, o -> o));
			List<Integer> versionList = new ArrayList<>(diffMap.keySet());
			Collections.sort(versionList);
			Integer curVersion = Integer.valueOf(record.getFileVersion());
			versionList.add(curVersion);
			List<Object> historyList = new ArrayList<>();
			Map<Integer, Object> historyDataMap = new HashMap<>();
			Integer preVersion = versionList.get(0);
			for(Integer version : versionList){
				OnlyOfficeFileVersion diff = diffMap.get(version);
				String key = document.key;
				if(diff!=null) {
					Map<String, String> infoMap = XGsonBuilder.instance().fromJson(diff.getFileVersionInfo(), Map.class);
					key = infoMap.get("key");
				}

				Map<String, Object> history = new HashMap<>();
				history.put("key", key);
				history.put("version", version);
				history.put("created", DateTools.format(record.getCreateTime()));
				Map<String, Object> user = new HashMap<>(2);
				user.put("id", record.getCreator());
				user.put("name", record.getCreator().split("@")[0]);
				history.put("user", user);

				Map<String, Object> historyData = new HashMap<>();
				historyData.put("key", key);
				String url = DocumentManager.getFileUriById(record.getId());
				if(version.equals(curVersion)) {
					url = url + "/0";
				}else{
					url = url + "/" + version;
				}
				url = StringUtils.isNoneBlank(token) ? url + "?" + param + "=" + token : url;
				historyData.put("url", url);
				historyData.put("version", version);
				historyData.put("fileType", record.getExtension().toLowerCase());

				if(version > preVersion){
					diff = diffMap.get(preVersion);
					Map<String, String> infoMap = XGsonBuilder.instance().fromJson(diff.getFileVersionInfo(), Map.class);
					JsonObject changes = XGsonBuilder.instance().fromJson(infoMap.get("changes"), JsonObject.class);

					history.put("changes", changes==null ? null : changes.get("changes"));
					history.put("serverVersion", changes==null ? null : changes.get("serverVersion"));
					history.put("created", DateTools.format(diff.getCreateTime()));
					user.put("id", diff.getCreator());
					user.put("name", diff.getCreator().split("@")[0]);
					history.put("user", user);

					/**
					 * onlyOffice获取差异信息url的最后一段path路径必须为changes.zip
					 */
					String urlDiff = DocumentManager.getFileDiffUriById(record.getId());
					urlDiff = urlDiff + "/" + preVersion + "/changes.zip";
					urlDiff = StringUtils.isNoneBlank(token) ? urlDiff + "?" + param + "=" + token + "&filename=changes.zip" : urlDiff + "?filename=changes.zip";
					historyData.put("changesUrl", urlDiff);
					Map<String, Object> prev = (Map<String, Object>) historyDataMap.get(preVersion);
					Map<String, Object> prevInfo = new HashMap<>(3);
					prevInfo.put("fileType", record.getExtension().toLowerCase());
					prevInfo.put("key", prev.get("key"));
					prevInfo.put("url", prev.get("url"));
					historyData.put("previous", prevInfo);
				}

				if (DocumentManager.tokenEnabled()) {
					String tokenHistory = DocumentManager.createToken(historyData);
					historyData.put("token", tokenHistory);
				}

				historyList.add(history);
				historyDataMap.put(version, historyData);
				preVersion = version;
			}

			Map<String, Object> histObj = new HashMap<>();
			histObj.put("currentVersion", curVersion);
			histObj.put("history", historyList);
			return new String[] { XGsonBuilder.toJson(histObj), XGsonBuilder.toJson(historyDataMap) };
		}
		return new String[] { "", "" };
	}


	private String readFileToEnd(File file) {
		String output = "";
		try {
			try (FileInputStream is = new FileInputStream(file)) {
				Scanner scanner = new Scanner(is);
				scanner.useDelimiter("\\A");
				while (scanner.hasNext()) {
					output += scanner.next();
				}
				scanner.close();
			}
		} catch (Exception e) {
		}
		return output;
	}

	public class Document {
		public String title;
		public String url;
		public String fileType;
		public String key;
		public Permissions permissions;
	}

	public class Permissions {
		public Boolean comment;
		public Boolean download;
		public Boolean edit;
		public Boolean copy;
		public Boolean print;
		public Boolean fillForms;
		public Boolean modifyFilter;
		public Boolean modifyContentControl;
		public Boolean review;

		public Permissions(String mode, String type, Boolean canEdit) {
			comment = !mode.equals("view") && !mode.equals("fillForms") && !mode.equals("embedded")
					&& !mode.equals("blockcontent");
			download = true;
			edit = canEdit && (mode.equals("edit") || mode.equals("filter") || mode.equals("blockcontent"));
			fillForms = !mode.equals("view") && !mode.equals("comment") && !mode.equals("embedded")
					&& !mode.equals("blockcontent");
			modifyFilter = false;
			modifyContentControl = !mode.equals("blockcontent");
			review = mode.equals("edit") || mode.equals("review");
			print = true;
			copy = true;
		}

		public void init(String mode, Boolean canEdit){
			if(comment == null) {
				comment = !mode.equals("view") && !mode.equals("fillForms") && !mode.equals("embedded")
						&& !mode.equals("blockcontent");
			}
			if(download == null) {
				download = true;
			}
			if(edit == null) {
				edit = canEdit && (mode.equals("edit") || mode.equals("filter") || mode.equals("blockcontent"));
			}
			if(fillForms == null) {
				fillForms = !mode.equals("view") && !mode.equals("comment") && !mode.equals("embedded")
						&& !mode.equals("blockcontent");
			}
			if(modifyFilter == null) {
				modifyFilter = false;
			}
			if(modifyContentControl == null) {
				modifyContentControl = !mode.equals("blockcontent");
			}
			if(review == null) {
				review = mode.equals("edit") || mode.equals("review");
			}
			if(print == null) {
				print = true;
			}
			if(copy == null) {
				copy = true;
			}
		}
	}

	public class EditorConfig {
		public String mode = "edit";
		public String callbackUrl;
		public String lang = "en";
		public User user;
		public Customization customization;
		public Embedded embedded;

		public EditorConfig() {
			user = new User();
			customization = new Customization();
		}

		public void InitDesktop(String url) {
			embedded = new Embedded();
			embedded.saveUrl = url;
			embedded.embedUrl = url;
			embedded.shareUrl = url;
			embedded.toolbarDocked = "top";
		}

		public class User {
			public String id = "uid-1";
			public String name = "John Smith";
		}

		public class Customization {
			public Goback goback;

			public Customization() {
				goback = new Goback();
			}

			public class Goback {
				public String url;
			}
		}

		public class Embedded {
			public String saveUrl;
			public String embedUrl;
			public String shareUrl;
			public String toolbarDocked;
		}
	}

	public static String Serialize(FileModel model) {
		Gson gson = new Gson();
		return gson.toJson(model);
	}
}
