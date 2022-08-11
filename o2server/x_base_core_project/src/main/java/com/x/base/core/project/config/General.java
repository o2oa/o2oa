package com.x.base.core.project.config;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class General extends ConfigObject {

	private static final long serialVersionUID = 4393280516414081348L;
	private static final Boolean DEFAULT_WEBSOCKETENABLE = true;
	private static final Boolean DEFAULT_CONFIGAPIENABLE = true;
	private static final List<String> DEFAULT_SCRIPTINGBLOCKEDCLASSES = Arrays.asList(Runtime.class.getName(),
			File.class.getName(), Path.class.getName());
	private static final Boolean DEFAULT_REQUESTLOGENABLE = false;
	private static final Integer DEFAULT_REQUESTLOGRETAINDAYS = 7;
	private static final Boolean DEFAULT_REQUESTLOGBODYENABLE = false;

	private static final Boolean DEFAULT_DEPLOYRESOURCEENABLE = false;
	private static final Boolean DEFAULT_DEPLOYWARENABLE = false;

	public static General defaultInstance() {
		General o = new General();
		o.webSocketEnable = DEFAULT_WEBSOCKETENABLE;
		o.configApiEnable = DEFAULT_CONFIGAPIENABLE;
		o.scriptingBlockedClasses = DEFAULT_SCRIPTINGBLOCKEDCLASSES;
		o.requestLogEnable = DEFAULT_REQUESTLOGENABLE;
		o.requestLogRetainDays = DEFAULT_REQUESTLOGRETAINDAYS;
		o.requestLogBodyEnable = DEFAULT_REQUESTLOGBODYENABLE;
		o.deployResourceEnable = DEFAULT_DEPLOYRESOURCEENABLE;
		o.deployWarEnable = DEFAULT_DEPLOYWARENABLE;
		return o;
	}

	@FieldDescribe("启用访问日志功能.")
	private Boolean requestLogEnable;

	@FieldDescribe("访问日志记录天数,默认7天.")
	private Integer requestLogRetainDays;

	@FieldDescribe("访问日志是否记录post或者put的body内容,只对content-type为application/json的请求有效.")
	private Boolean requestLogBodyEnable;

	@FieldDescribe("是否启用webSocket链接.")
	private Boolean webSocketEnable;

	@FieldDescribe("允许通过接口修改系统配置.")
	private Boolean configApiEnable;

	@FieldDescribe("是否允许部署war包.")
	private Boolean deployWarEnable;

	@FieldDescribe("是否允许部署静态资源.")
	private Boolean deployResourceEnable;

	@FieldDescribe("脚本中禁止用的类名,保持为空则默认禁用Runtime,File,Path.")
	private List<String> scriptingBlockedClasses;

	public Boolean getRequestLogEnable() {
		return BooleanUtils.isTrue(this.requestLogEnable);
	}

	public Integer getRequestLogRetainDays() {
		return (null == this.requestLogRetainDays || this.requestLogRetainDays < 1) ? DEFAULT_REQUESTLOGRETAINDAYS
				: this.requestLogRetainDays;
	}

	public Boolean getRequestLogBodyEnable() {
		return BooleanUtils.isTrue(this.requestLogBodyEnable);
	}

	public List<String> getScriptingBlockedClasses() {
		return (null == this.scriptingBlockedClasses) ? DEFAULT_SCRIPTINGBLOCKEDCLASSES : this.scriptingBlockedClasses;
	}

	public Boolean getWebSocketEnable() {
		return null == this.webSocketEnable ? DEFAULT_WEBSOCKETENABLE : this.webSocketEnable;
	}

	public Boolean getConfigApiEnable() {
		return null == this.configApiEnable ? DEFAULT_CONFIGAPIENABLE : this.configApiEnable;
	}

	public Boolean getDeployWarEnable() {

		return null == this.deployWarEnable ? DEFAULT_DEPLOYWARENABLE : this.deployWarEnable;
	}

	public Boolean getDeployResourceEnable() {

		return null == this.deployResourceEnable ? DEFAULT_DEPLOYRESOURCEENABLE : this.deployResourceEnable;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_GENERAL);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_GENERAL);
	}

}
