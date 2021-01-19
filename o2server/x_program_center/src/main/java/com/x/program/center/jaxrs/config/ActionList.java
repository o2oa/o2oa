package com.x.program.center.jaxrs.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActionList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		File manifestFile = new File(Config.base(), "configSample/manifest.cfg");
		Wo wo = new Wo();
		if (manifestFile.exists()) {
			if (manifestFile.isFile()) {
				String json = FileUtils.readFileToString(manifestFile, DefaultCharset.charset);

				FileFilter fileFilter = new WildcardFileFilter("node_*.json");
				File[] files = Config.dir_config().listFiles(fileFilter);
				if (null != files && files.length > 0) {
					String strNode = "";
					JsonParser parser = new JsonParser();
					JsonObject jsonObj = parser.parse(json).getAsJsonObject();
					jsonObj.remove("node_127.0.0.1.json");

					for (File o : files) {
						String name = StringUtils.substringBetween(o.getName(), "node_", ".json");
						jsonObj.addProperty(o.getName().toString(), name + "应用节点配置");
					}
					wo.setConfig(jsonObj.toString());
				} else {
					wo.setConfig(json);
				}

			}
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);
		return result;
	}

	synchronized private Wo executeCommand(String ctl, String nodeName, int nodePort) throws Exception {
		Wo wo = new Wo();
		// wo.setNode(nodeName);
		wo.setStatus("success");
		try (Socket socket = new Socket(nodeName, nodePort)) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
					DataInputStream dis = new DataInputStream(socket.getInputStream())) {
				Map<String, Object> commandObject = new HashMap<>();
				commandObject.put("command", "command:" + ctl);
				commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
				dos.writeUTF(XGsonBuilder.toJson(commandObject));
				dos.flush();

				if (ctl.indexOf("create encrypt") > -1) {
					String createEncrypt = dis.readUTF();
					logger.info(createEncrypt);
				}
			}
		} catch (Exception ex) {
			wo.setStatus("fail");
			logger.warn("socket dispatch executeCommand to {}:{} error={}", nodeName, nodePort, ex.getMessage());
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		return wo;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -1525143709803057966L;

		@FieldDescribe("执行时间")
		private String time;

		@FieldDescribe("执行结果")
		private String status;

		@FieldDescribe("config文件列表")
		private String config;

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

		public String getConfig() {
			return config;
		}

		public void setConfig(String config) {
			this.config = config;
		}
	}

}
