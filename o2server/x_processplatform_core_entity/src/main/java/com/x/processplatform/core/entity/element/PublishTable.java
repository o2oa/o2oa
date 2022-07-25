package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author sword
 */
public class PublishTable extends JsonProperties {

	private static final long serialVersionUID = 6232849915945948766L;

	public static final String TABLE_DATA_BY_SCRIPT = "script";
	public static final String TABLE_DATA_BY_PATH = "dataPath";

	@FieldDescribe("数据表名称")
	private String tableName;

	@FieldDescribe("获取数据方式")
	private String queryTableDataBy;

	@FieldDescribe("数据路径")
	private String queryTableDataPath;

	@FieldDescribe("数据脚本.")
	private String targetAssignDataScript;

	@FieldDescribe("数据脚本文本.")
	private String targetAssignDataScriptText;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQueryTableDataBy() {
		return queryTableDataBy;
	}

	public void setQueryTableDataBy(String queryTableDataBy) {
		this.queryTableDataBy = queryTableDataBy;
	}

	public String getQueryTableDataPath() {
		return queryTableDataPath;
	}

	public void setQueryTableDataPath(String queryTableDataPath) {
		this.queryTableDataPath = queryTableDataPath;
	}

	public String getTargetAssignDataScript() {
		return targetAssignDataScript;
	}

	public void setTargetAssignDataScript(String targetAssignDataScript) {
		this.targetAssignDataScript = targetAssignDataScript;
	}

	public String getTargetAssignDataScriptText() {
		return targetAssignDataScriptText;
	}

	public void setTargetAssignDataScriptText(String targetAssignDataScriptText) {
		this.targetAssignDataScriptText = targetAssignDataScriptText;
	}
}
