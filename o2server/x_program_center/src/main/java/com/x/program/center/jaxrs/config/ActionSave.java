package com.x.program.center.jaxrs.config;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * 系统配置文件保存
 * @author sword
 */
public class ActionSave extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionSave.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fileName = wi.getFileName();

		if (fileName == null) {
			throw new ExceptionNameEmpty();
		}

		String data = wi.getFileContent();
		Gson gson = new Gson();
		JsonElement je = null;
		try {
			je = gson.fromJson(data, JsonElement.class);
		} catch (Exception e) {
			throw new ExceptionJsonError();
		}
		if ((null == je) || !je.isJsonObject()) {
			throw new ExceptionJsonError();
		}

		if (BooleanUtils.isNotTrue(Config.nodes().centerServers().first().getValue().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}

		byte[] bytes = wi.getFileContent().getBytes(StandardCharsets.UTF_8);

		Nodes nodes = Config.nodes();
		// 同步config文件
		for (String node : nodes.keySet()) {
			executeSyncFile(Config.DIR_CONFIG + "/" + fileName, node, nodes.get(node).nodeAgentPort(), bytes);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		this.configFlush(effectivePerson);

		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);

		return result;
	}

	/**
	 * 文件同步
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
				logger.info("同步文件{}到节点{}完成.......", syncFilePath, nodeName);
			}
			syncFileFlag = true;
		} catch (Exception ex) {
			logger.warn("同步文件{}到节点{}异常：{}", syncFilePath, nodeName, ex.getMessage());
			syncFileFlag = false;
		}
		return syncFileFlag;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("服务器地址(*代表多台应用服务器)")
		private String nodeName;

		@FieldDescribe("服务端口")
		private String nodePort;

		@FieldDescribe("文件名")
		private String fileName;

		@FieldDescribe("config文件内容")
		private String fileContent;

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public String getNodePort() {
			return nodePort;
		}

		public void setNodePort(String nodePort) {
			this.nodePort = nodePort;
		}

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

		@FieldDescribe("执行时间")
		private String time;

		@FieldDescribe("执行结果")
		private String status;

		@FieldDescribe("执行消息")
		private String message;

		@FieldDescribe("config文件内容")
		private String fileContent;

		@FieldDescribe("是否Sample")
		private boolean isSample;

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

		public String getFileContent() {
			return fileContent;
		}

		public void setFileContent(String fileContent) {
			this.fileContent = fileContent;
		}

		public boolean isSample() {
			return isSample;
		}

		public void setSample(boolean isSample) {
			this.isSample = isSample;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

}
