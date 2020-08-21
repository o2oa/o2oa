package com.x.program.center.jaxrs.edit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;

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
import com.x.program.center.jaxrs.command.ActionCommand.Wo;

public class ActionList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionList.class);
	
	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		File manifestFile = new File(Config.base(),"configSample/manifest.cfg");
		Wo wo = new Wo();
		if(manifestFile.exists()) {
			if(manifestFile.isFile()) {
				String json = FileUtils.readFileToString(manifestFile, DefaultCharset.charset);
				wo.setConfig(json);
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);
		return result;
	}
	
	synchronized private Wo executeCommand(String ctl , String nodeName ,int nodePort) throws Exception{
		Wo wo = new Wo();
		//wo.setNode(nodeName);
		wo.setStatus("success");
		try (Socket socket = new Socket(nodeName, nodePort)) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				 DataInputStream dis = new DataInputStream(socket.getInputStream())){
				Map<String, Object> commandObject = new HashMap<>();
				commandObject.put("command", "command:"+ ctl);
				commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
				dos.writeUTF(XGsonBuilder.toJson(commandObject));
				dos.flush();
				
				if (ctl.indexOf("create encrypt")>-1) {
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
   
	public static class Wi  extends GsonPropertyObject{
		@FieldDescribe("命令名称")
		private String ctl;
		@FieldDescribe("服务器地址(*代表多台应用服务器)")
		private String nodeName;
		@FieldDescribe("服务端口")
		private String nodePort;
		
		public String getCtl() {
			return ctl;
		}
		public void setCtl(String ctl) {
			this.ctl = ctl;
		}
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
	}
	
	public static class Wo extends GsonPropertyObject {
		
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
