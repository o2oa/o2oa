package com.x.program.center.jaxrs.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;

/**
 * 获取配置文件
 * @author sword
 */
public class ActionOpen extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOpen.class);
	private static final String NODE_CONFIG = "node";

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		String fileName = wi.getFileName();
		if (StringUtils.isBlank(fileName)) {
			throw new ExceptionNameEmpty();
		}
		if(!StringTools.isFileName(fileName)){
			throw new ExceptionIllegalFileName(fileName);
		}

		if(NODE_CONFIG.equals(fileName)){
			List<NodeInfo> nodeInfoList = new ArrayList<>();
			for (String key : Config.nodes().keySet()) {
				File file = new File(Config.base(),"config/"+NODE_CONFIG+"_"+key+".json");
				if (!file.exists()) {
					file = new File(Config.base(),"configSample/"+NODE_CONFIG+"_"+key+".json");
				}
				if(file.exists()){
					String json = FileUtils.readFileToString(file, DefaultCharset.charset);
					NodeInfo nodeInfo = new NodeInfo();
					nodeInfo.setNodeAddress(key);
					nodeInfo.setNode(gson.fromJson(json, Node.class));
					nodeInfoList.add(nodeInfo);
				}
			}
			wo.setFileContent(gson.toJson(nodeInfoList));
			wo.setSample(false);
		}else {
			File file = new File(Config.base(), "config/" + fileName);
			wo.setSample(false);

			if (!file.exists()) {
				file = new File(Config.base(), "configSample/" + fileName);
				wo.setSample(true);
			}

			if (file.exists()) {
				if (file.isFile()) {
					String json = FileUtils.readFileToString(file, DefaultCharset.charset);
					wo.setFileContent(json);
				}
			}
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);
		return result;
	}

	public static class Wi  extends GsonPropertyObject{

		@FieldDescribe("文件名")
		private String fileName;

		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("执行时间")
		private String time;

		@FieldDescribe("执行结果")
		private String status;

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

	}

	public class NodeInfo {
		private String nodeAddress;
		private Node node;

		public String getNodeAddress() {
			return nodeAddress;
		}

		public void setNodeAddress(String nodeAddress) {
			this.nodeAddress = nodeAddress;
		}

		public Node getNode() {
			return node;
		}

		public void setNode(Node node) {
			this.node = node;
		}
	}

}
