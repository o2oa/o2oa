package com.x.onlyofficefile.assemble.control.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.core.entity.FileInfo;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileSaveInfo;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.Strings;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import com.x.onlyofficefile.core.entity.OnlyOfficeFileVersion;
import com.x.processplatform.core.entity.content.Attachment;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;
import org.primeframework.jwt.domain.JWT;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author sword
 */
@WebServlet(name = "IndexServlet", urlPatterns = { "/IndexServlet" })
@MultipartConfig
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DocumentJwtHeader = null;
	private static Logger logger = LoggerFactory.getLogger(IndexServlet.class);

	private static Map<String, String> appMap;

	static {
		try {
			appMap = Map.of("x_pan_assemble_control","attachment3/3rd/file/save/only/office",
					"x_archive_assemble_control","attachment3/3rd/file/save/only/office",
					"x_knowledge_assemble_control","attachment3/3rd/file/save/only/office",
					"OfficeOnline",Config.url_x_program_center_jaxrs("invoke", "officeOnlineSrv") + "/execute");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private Gson gson = new Gson();

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String action = request.getParameter("type");
		if(logger.isDebugEnabled()) {
			logger.debug("onlyOffice call back action:{}", action);
		}
		if (action == null) {
			return;
		}

		DocumentManager.Init(request, response);
		PrintWriter writer = response.getWriter();

		switch (action.toLowerCase()) {
		case "track":
			track(request, response, writer);
			break;
		}
	}


	private void track(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {

		String appId = request.getParameter("appId");
		String authToken = request.getParameter("authToken");
		String fileName = request.getParameter("fileName");
		if(fileName!=null && fileName.length() > 1024){
			return;
		}
		String id = StringUtils.substringBeforeLast(fileName, ".");
		String body = "";
		try {
			Scanner scanner = new Scanner(request.getInputStream());
			scanner.useDelimiter("\\A");
			body = scanner.hasNext() ? scanner.next() : "";
			scanner.close();
		} catch (Exception ex) {
			logger.error(ex);
			return;
		}
		if (body.isEmpty()) {
			logger.debug("onlyOffice call back empty request.getInputStream");
			return;
		}
		String encoding = System.getProperty("sun.jnu.encoding");
	    if(DefaultCharset.name_gbk.equalsIgnoreCase(encoding)) {
	    	try {
				body = new String(body.getBytes(DefaultCharset.name_gbk), DefaultCharset.name);
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
	    }
	    if(logger.isDebugEnabled()) {
			logger.debug("onlyOffice call back body:{}", body);
		}
		JsonObject jsonObj = gson.fromJson(body, JsonObject.class);
		String userId = "";

		JsonArray actions = jsonObj.getAsJsonArray("actions");
		if(actions!=null && actions.size() > 0) {
			JsonObject jsonObject = actions.get(0).getAsJsonObject();
			userId = jsonObject.get("userid").getAsString();
		}
		int status;
		String downloadUri = "";
		String changesUri = "";
		String key = "";

		if (DocumentManager.tokenEnabled()) {
			String token = jsonObj.has("token") ? jsonObj.get("token").getAsString() : null;
			if (token == null) {
				String header = request.getHeader(
						DocumentJwtHeader == null || DocumentJwtHeader.isEmpty() ? "Authorization" : DocumentJwtHeader);
				if (StringUtils.isNotBlank(header)) {
					token = header.startsWith("Bearer ") ? header.substring(7) : header;
				}
			}

			if (StringUtils.isNotBlank(token)) {
				JWT jwt = DocumentManager.readToken(token);
				if (jwt == null) {
					logger.warn("onlyOffice call back error token");
					return;
				}
				if (jwt.getObject("payload") != null) {
					try {
						@SuppressWarnings("unchecked")
						LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) jwt.getObject("payload");

						jwt.claims = payload;
					} catch (Exception ex) {
						logger.error(ex);
						return;
					}
				}
				status = jwt.getInteger("status");
				downloadUri = jwt.getString("url");
				changesUri = jwt.getString("changesurl");
				key = jwt.getString("key");
			}else {
				status = jsonObj.has("status") ? Math.toIntExact(jsonObj.get("status").getAsLong()) : 0;
				downloadUri = jsonObj.has("url") ? jsonObj.get("url").getAsString() : "";
				changesUri = jsonObj.has("changesurl") ? jsonObj.get("changesurl").getAsString() : "";
				key = jsonObj.get("key").getAsString();
			}
		} else {
			status = jsonObj.has("status") ? Math.toIntExact(jsonObj.get("status").getAsLong()) : 0;
			downloadUri = jsonObj.has("url") ? jsonObj.get("url").getAsString() : "";
			changesUri = jsonObj.has("changesurl") ? jsonObj.get("changesurl").getAsString() : "";
			key = jsonObj.get("key").getAsString();
		}

		int saved = 0;
		boolean isSave = false;
		//status: 1|editing, 2|MustSave, 3|Corrupted, 4|closed, 6|MustForceSave, 7|CorruptedForceSave
		if (status == 2 || status == 6) {
			try {
				String changes = this.getChanges(jsonObj);
				if(StringUtils.isNotBlank(appId) && appMap.containsKey(appId)){
					this.saveToApp(changes, id, appId, downloadUri, changesUri, authToken);
				}else {
					this.saveFile(changes, id, appId, downloadUri, changesUri, key, userId);
				}
				isSave = true;
			} catch (Exception ex) {
				logger.info("onlyoffice call back save file fail:{}", body);
				logger.error(ex);
				saved = 1;
			}
		}
		logger.info("onlyoffice call back Track status={} isSave={}" , status, isSave);
		writer.write("{\"error\":" + saved + "}");
		writer.flush();
		writer.close();
	}

	private String getChanges(JsonObject jsonObj) {
		String changes = jsonObj.has("changeshistory") ? jsonObj.get("changeshistory").getAsString() : null;
		if (changes == null && jsonObj.has("history")) {
			JsonObject historyObj = jsonObj.get("history").getAsJsonObject();
			changes = historyObj.toString();
		}
		return changes;
	}

	/**
	 * 外部应用接入到onlyOffice集成的回调处理
	 * @param changes
	 * @param fileId
	 * @param appId
	 * @param downloadUri
	 * @param diffUrl
	 * @param authToken
	 * @throws Exception
	 */
	private void saveToApp(String changes, String fileId, String appId, String downloadUri, String diffUrl, String authToken) throws Exception{
		FileSaveInfo fileSaveInfo = new FileSaveInfo();
		fileSaveInfo.setAppId(appId);
		fileSaveInfo.setFileId(fileId);
		fileSaveInfo.setChanges(changes);
		fileSaveInfo.setDownLoadUrl(downloadUri);
		fileSaveInfo.setDiffUrl(diffUrl);
		String uri = appMap.get(appId);
		final StringBuilder stringBuilder = new StringBuilder(1024);
		String className = ThisApplication.context().applications().findApplicationName(appId);
		stringBuilder.append(ThisApplication.context().applications().randomWithWeight(className).getUrlJaxrsRoot())
				.append(uri)
				.append("?fileId=")
				.append(fileId)
				.append("&")
				.append(Config.person().getTokenName())
				.append("=")
				.append(UrlEncoded.encodeString(authToken, DefaultCharset.charset));
		String url = stringBuilder.toString();
		logger.info("call back to "+appId + " url:" +url);
		String json = HttpConnection.postAsString(url, null, XGsonBuilder.toJson(fileSaveInfo));
		logger.info("post to {} response:{}", appId, json);
	}

	private void saveFile(String changes, String id, String appId, String downloadUri, String diffUrl, String key, String userId) throws Exception{
		logger.info("download file {} from:{}", id, downloadUri);
		byte[] bytes = CipherConnectionAction.getBinary(false, downloadUri);

		if(bytes != null) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EntityManager em = emc.get(OnlyOfficeFile.class);
				OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
						record.getStorage());
				byte[] hisBytes = record.readContent(mapping);
				String histName = record.getName();
				record.deleteContent(mapping);
				record.saveContent(mapping, bytes, this.getName(record, business));
				record.setLastUpdateTime(new Date());
				if (StringUtils.isNotBlank(changes) && StringUtils.isNotBlank(diffUrl)) {
					byte[] diffBytes = CipherConnectionAction.getBinary(false, diffUrl);
					OnlyOfficeFileVersion diff = new OnlyOfficeFileVersion(record);
					diff.saveContent(mapping, diffBytes, "diff.zip");
					Map<String, String> map = new HashMap<>(2);
					map.put("key", key);
					map.put("changes", changes);
					map.put("changesUrl", diffUrl);
					diff.setFileVersionInfo(XGsonBuilder.toJson(map));
					diff.setFileType(OnlyOfficeFileVersion.FILE_TYPE_DIFF);

					OnlyOfficeFileVersion fileVersion = new OnlyOfficeFileVersion(record);
					fileVersion.saveContent(mapping, hisBytes, histName);
					fileVersion.setFileVersionInfo(XGsonBuilder.toJson(map));
					fileVersion.setFileType(OnlyOfficeFileVersion.FILE_TYPE_FILE);
					String operator = business.organization().person().get(userId);
					fileVersion.setCreator(StringUtils.isBlank(operator) ? userId : operator);
					emc.beginTransaction(OnlyOfficeFileVersion.class);
					emc.persist(diff, CheckPersistType.all);
					emc.persist(fileVersion, CheckPersistType.all);
				}
				emc.beginTransaction(OnlyOfficeFile.class);
				record.setFileVersion(String.valueOf(Integer.parseInt(record.getFileVersion()) + 1));
				emc.commit();
				this.businessCallBack(record, userId, bytes, business);
			}
		}
	}

	private String getName(OnlyOfficeFile record, Business business) throws Exception{
		String name = record.getName();
		if(StringUtils.isNotBlank(record.getCategory())){
			EntityManagerContainer emc = business.entityManagerContainer();
			if(Business.PROCESS_PLATFORM_APP.equals(record.getCategory())) {
				Attachment attachment = emc.find(record.getId(), Attachment.class);
				if (null != attachment) {
					name = attachment.getName();
				}
			} else if(Business.CMS_APP.equals(record.getCategory())) {
				FileInfo attachment = emc.find(record.getId(), FileInfo.class);
				if (null != attachment) {
					name = attachment.getName();
				}
			}
		}
		if (Strings.utf8Length(name) > 255) {
			name = Strings.utf8FileNameSubString(name, 255);
		}
		return name;
	}

	/**
	 * O2平台内接入到onlyOffice集成应用的回调处理
	 * @param record
	 * @param userId
	 * @param bytes
	 * @param business
	 * @throws Exception
	 */
	private void businessCallBack(OnlyOfficeFile record, String userId, byte[] bytes, Business business) throws Exception{
		if(StringUtils.isNotBlank(record.getCategory())){
			EntityManagerContainer emc = business.entityManagerContainer();
			if(Business.PROCESS_PLATFORM_APP.equals(record.getCategory())){
				Attachment attachment = emc.find(record.getId(), Attachment.class);
				if (null != attachment) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							attachment.getStorage());
					emc.beginTransaction(Attachment.class);
					attachment.deleteContent(mapping);
					attachment.saveContent(mapping, bytes, attachment.getName());
					attachment.setLastUpdatePerson(business.organization().person().get(userId));
					attachment.setLastUpdateTime(record.getLastUpdateTime());
					emc.commit();
					logger.info("{}-修改了流程平台附件：{}，对应的文档JOB：{}", userId, record.getId(), record.getDocId());
				}
			} else if(Business.CMS_APP.equals(record.getCategory())){
				FileInfo attachment = emc.find(record.getId(), FileInfo.class);
				if (null != attachment) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class,
							attachment.getStorage());
					emc.beginTransaction(FileInfo.class);
					attachment.deleteContent(mapping);
					attachment.saveContent(mapping, bytes, attachment.getName());
					attachment.setLastUpdateTime(record.getLastUpdateTime());
					emc.commit();
					logger.info("{}-修改了内容管理附件：{}，对应的文档：{}", userId, record.getId(), record.getDocId());
				}
			}else {
				String url = appMap.get(record.getCategory());
				if (StringUtils.isNotBlank(url)) {
					String content = "{\"fun\": \"callback\",\"data\":{\"documentId\":\"" + record.getId() + "\",\"userId\":\"" + userId + "\",\"size\":" + record.getLength() + "}}";
					try {
						String json = HttpConnection.postAsString(url, null, content);
						logger.info("{} change file call back to [{}] url:{} result:{}", userId, record.getCategory(), url, json);
					} catch (Exception e) {
						logger.warn("{} change file call back to [{}] url:{} error:{}", userId, record.getCategory(), url, e.getMessage());
					}
				}
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public String getServletInfo() {
		return "Handler";
	}
}
