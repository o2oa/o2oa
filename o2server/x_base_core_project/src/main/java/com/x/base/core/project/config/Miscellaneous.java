package com.x.base.core.project.config;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class Miscellaneous extends ConfigObject {

	private static final long serialVersionUID = 4393280516414081348L;

	private static final Boolean DEFAULT_WEBSOCKETENABLE = true;
	private static final Boolean DEFAULT_CONFIGAPIENABLE = true;
	private static final List<String> DEFAULT_SCRIPTINGBLOCKEDCLASSES = null;

	public static Miscellaneous defaultInstance() {
		Miscellaneous o = new Miscellaneous();
		o.webSocketEnable = DEFAULT_WEBSOCKETENABLE;
		o.configApiEnable = DEFAULT_CONFIGAPIENABLE;
		o.scriptingBlockedClasses = DEFAULT_SCRIPTINGBLOCKEDCLASSES;
		return new Miscellaneous();
	}

	@FieldDescribe("是否启用webSocket链接.")
	private Boolean webSocketEnable;

	@FieldDescribe("允许通过接口修改系统配置.")
	private Boolean configApiEnable;

	@FieldDescribe("脚本中禁止用的类名,保持为空则默认禁用Runtime,File,Path.")
	private List<String> scriptingBlockedClasses;

	public List<String> getScriptingBlockedClasses() {
		return this.scriptingBlockedClasses;
	}

	public Boolean getWebSocketEnable() {
		return null == this.webSocketEnable ? DEFAULT_WEBSOCKETENABLE : this.webSocketEnable;
	}

	public Boolean getConfigApiEnable() {
		return null == this.configApiEnable ? DEFAULT_CONFIGAPIENABLE : this.configApiEnable;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_MISCELLANEOUS);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_MISCELLANEOUS);
	}

}
