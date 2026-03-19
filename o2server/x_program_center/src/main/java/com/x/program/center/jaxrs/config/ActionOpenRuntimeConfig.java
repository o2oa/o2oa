package com.x.program.center.jaxrs.config;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;

/**
 * 获取运行中的配置文件
 * @author sword
 */
public class ActionOpenRuntimeConfig extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOpenRuntimeConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson,JsonElement jsonElement) throws Exception {
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

		File file = new File(Config.base(),"config/"+fileName);

		if(file.exists() && file.isFile()) {
			String json = FileUtils.readFileToString(file, DefaultCharset.charset);
			if(StringUtils.isNotBlank(json)) {
				wo.setFileContent(json);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject{

		@FieldDescribe("config文件名")
		private String fileName;

		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("config文件内容")
		private String fileContent;

		public String getFileContent() {
			return fileContent;
		}

		public void setFileContent(String fileContent) {
			this.fileContent = fileContent;
		}
	}

}
