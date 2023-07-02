package com.x.cms.core.entity.element.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;

public class WrapCms extends AppInfo {

	private static final long serialVersionUID = 1863166064194774704L;

	public static WrapCopier<AppInfo, WrapCms> outCopier = WrapCopierFactory.wo(AppInfo.class, WrapCms.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapCms, AppInfo> inCopier = WrapCopierFactory.wi(WrapCms.class, AppInfo.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

	public List<String> listCategoryInfoId() throws Exception {
		return ListTools.extractProperty(this.getCategoryInfoList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listFormId() throws Exception {
		return ListTools.extractProperty(this.getFormList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listAppDictId() throws Exception {
		return ListTools.extractProperty(this.getAppDictList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listScriptId() throws Exception {
		return ListTools.extractProperty(this.getScriptList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listFileId() throws Exception {
		return ListTools.extractProperty(this.getFileList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}
	
	@FieldDescribe("导出的分类设置")
	private List<WrapCategoryInfo> categoryInfoList = new ArrayList<>();

	@FieldDescribe("导出的表单设计")
	private List<WrapForm> formList = new ArrayList<>();
	
	@FieldDescribe("导出的数据字典")
	private List<WrapAppDict> appDictList = new ArrayList<>();

	@FieldDescribe("导出的脚本")
	private List<WrapScript> scriptList = new ArrayList<>();
	
	@FieldDescribe("导出的文件")
	private List<WrapFile> fileList = new ArrayList<>();

	@FieldDescribe("导出的栏目配置支持信息，JSON配置项")
	private String config = "{}";

	public String getConfig() { return this.config; }

	public void setConfig(final String config) { this.config = config; }

	public List<WrapForm> getFormList() {
		return formList;
	}

	public List<WrapFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<WrapFile> fileList) {
		this.fileList = fileList;
	}

	public List<WrapAppDict> getAppDictList() {
		return appDictList;
	}

	public List<WrapScript> getScriptList() {
		return scriptList;
	}

	public void setFormList(List<WrapForm> formList) {
		this.formList = formList;
	}

	public void setAppDictList(List<WrapAppDict> appDictList) {
		this.appDictList = appDictList;
	}

	public void setScriptList(List<WrapScript> scriptList) {
		this.scriptList = scriptList;
	}

	public List<WrapCategoryInfo> getCategoryInfoList() {
		return categoryInfoList;
	}

	public void setCategoryInfoList(List<WrapCategoryInfo> categoryInfoList) {
		this.categoryInfoList = categoryInfoList;
	}

}
