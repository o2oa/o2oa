package com.x.program.center.jaxrs.config;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * @author sword
 */
public class ActionList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		logger.debug("{}操作获取配置", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		File manifestFile = new File(Config.base(), "config/manifest.json");
		if(!manifestFile.exists()){
			manifestFile = new File(Config.base(), "configSample/manifest.json");
		}
		Wo wo = new Wo();
		if (manifestFile.exists()) {
			String json = FileUtils.readFileToString(manifestFile, DefaultCharset.charset);
			FileFilter fileFilter = new WildcardFileFilter("node_*.json");
			File[] files = Config.dir_config().listFiles(fileFilter);
			JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
			String node = "node_127.0.0.1.json";
			jsonObj.remove(node);
			if (null != files && files.length > 0) {
				for (File o : files) {
					String name = StringUtils.substringBetween(o.getName(), "node_", ".json");
					jsonObj.addProperty(o.getName(), name + "服务节点配置");
				}
			} else {
				jsonObj.addProperty(node, "服务节点配置");
			}
			wo.setConfig(jsonObj.toString());
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		wo.setTime(df.format(new Date()));
		wo.setStatus("success");
		result.setData(wo);
		return result;
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
