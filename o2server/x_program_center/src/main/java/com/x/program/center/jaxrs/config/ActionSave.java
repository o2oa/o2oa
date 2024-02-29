package com.x.program.center.jaxrs.config;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.StringTools;
import com.x.message.core.entity.Message;

/**
 * 系统配置文件保存
 *
 * @author sword
 */
public class ActionSave extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSave.class);
	private static final String messageConfig = "messages.json";
	private static final String FILE_NAME_TYPE = ".json";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fileName = wi.getFileName();

		if (StringUtils.isBlank(fileName)) {
			throw new ExceptionNameEmpty();
		}

		if(!StringTools.isFileName(fileName) || !fileName.toLowerCase().endsWith(FILE_NAME_TYPE)){
			throw new ExceptionIllegalFileName(fileName);
		}

		String data = wi.getFileContent();
		Gson gson = new Gson();
		JsonElement je = null;
		try {
			je = gson.fromJson(data, JsonElement.class);
		} catch (Exception e) {
			throw new ExceptionJsonError();
		}
		boolean flag = (null == je) || (!je.isJsonObject() && !je.isJsonArray());
		if (flag) {
			throw new ExceptionJsonError();
		}

		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		LOGGER.info("{}修改配置文件：{}", effectivePerson.getDistinguishedName(), fileName);
		byte[] bytes = wi.getFileContent().getBytes(StandardCharsets.UTF_8);

		Nodes nodes = Config.nodes();
		// 同步config文件
		for (String node : nodes.keySet()) {
			executeSyncFile(Config.DIR_CONFIG + "/" + fileName, node, nodes.get(node).nodeAgentPort(), bytes);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
		this.configFlush(effectivePerson);
		if(messageConfig.equals(fileName)){
			CacheManager.notify(Message.class);
		}
		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);

		return result;
	}

	/**
	 * 文件同步
	 *
	 * @param syncFilePath
	 * @param nodeName
	 * @param nodePort
	 * @param byteArray
	 * @return
	 */
	private boolean executeSyncFile(String syncFilePath, String nodeName, int nodePort, byte[] byteArray) {
		boolean syncFileFlag = false;

		try (Socket socket = new Socket(nodeName, nodePort)) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					DataInputStream dis = new DataInputStream(socket.getInputStream());
					InputStream fileInputStream = new ByteArrayInputStream(byteArray)) {
				Map<String, Object> commandObject = new HashMap<>();
				commandObject.put("command", "syncFile:" + syncFilePath);
				commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
				dos.writeUTF(XGsonBuilder.toJson(commandObject));
				dos.flush();

				dos.writeUTF(syncFilePath);
				dos.flush();

				byte[] bytes = new byte[1024];
				int length = 0;
				while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
				}
			}
			syncFileFlag = true;
		} catch (Exception ex) {
			LOGGER.warn("同步文件{}到节点{}异常：{}.", syncFilePath, nodeName, ex.getMessage());
			syncFileFlag = false;
		}
		return syncFileFlag;
	}

	public static class Wi extends GsonPropertyObject {

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

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 8468331052732208961L;

		@FieldDescribe("执行时间")
		private String time;

		@FieldDescribe("执行结果")
		private String status;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	}

}
