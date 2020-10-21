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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

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
import com.x.program.center.jaxrs.command.ActionCommand.Wi;
import com.x.program.center.jaxrs.command.ActionCommand.Wo;

public class ActionSave extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionSave.class);
	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		String curServer = request.getLocalAddr();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fileName = wi.getFileName();
		
		if(fileName == null) {
			wo.setTime(df.format(new Date()));
			wo.setStatus("failure");
			wo.setMessage("文件名为null");
			result.setData(wo);
			return result;
		}
		
		String data = wi.getFileContent();
		
		if(fileName.equalsIgnoreCase("node_127.0.0.1.json")) {
			fileName = "node_"+ curServer +".json";
		}

		if(!Config.nodes().centerServers().first().getValue().getConfigApiEnable()) {
			wo.setTime(df.format(new Date()));
			wo.setStatus("failure");
			wo.setMessage("禁止编辑");
			result.setData(wo);
			return result;
		}

		File configFold = new File(Config.base(),Config.DIR_CONFIG);
		if(!configFold.exists()){
			configFold.mkdir();
		}
		
		File file = new File(Config.base(),Config.DIR_CONFIG+"/"+fileName);
		if(!file.exists()) {
			file.createNewFile();
		}
		
		if(file.exists()) {
			if(file.isFile()) {
			    FileUtils.writeStringToFile(file, data, DefaultCharset.charset);
			}
		}
		
		Nodes nodes = Config.nodes();
		//同步config文件
		for (String node : nodes.keySet()){
			//其他服务器
			if(!node.equalsIgnoreCase(curServer)) {
				if(!node.equalsIgnoreCase("127.0.0.1")) {
					if(nodes.get(node).getApplication().getEnable() || nodes.get(node).getCenter().getEnable()){
						boolean Syncflag = executeSyncFile(Config.DIR_CONFIG+"/"+fileName , node ,nodes.get(node).nodeAgentPort());
					}
				}
			}
		}
		
	
		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);
		
		return result;
	}
	


	
	private boolean executeSyncFile(String syncFilePath , String nodeName ,int nodePort){
		  boolean syncFileFlag = false;
		  File syncFile;
		  InputStream fileInputStream = null;
		 
		try (Socket socket = new Socket(nodeName, nodePort)) {
			
			syncFile = new File(Config.base(), syncFilePath);
			fileInputStream= new FileInputStream(syncFile);
			 
			socket.setKeepAlive(true);
			socket.setSoTimeout(5000);
			DataOutputStream dos = null;
			DataInputStream dis  = null;
			try {
				dos = new DataOutputStream(socket.getOutputStream());
			    dis = new DataInputStream(socket.getInputStream());
			    
				Map<String, Object> commandObject = new HashMap<>();
				commandObject.put("command", "syncFile:"+ syncFilePath);
				commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
				dos.writeUTF(XGsonBuilder.toJson(commandObject));
				dos.flush();
				
				dos.writeUTF(syncFilePath);
				dos.flush();
				
		
				logger.info("同步文件starting.......");
				byte[] bytes = new byte[1024];
				int length =0;
				while((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
				}
				logger.info("同步文件end.......");
				
			}finally {
				dos.close();
				dis.close();
				socket.close();
				fileInputStream.close();
			}
			
			syncFileFlag = true;
		} catch (Exception ex) {
			logger.error(ex);
			syncFileFlag = false;
		}
		return syncFileFlag;
	}
	
	public static class Wi  extends GsonPropertyObject{
		
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
