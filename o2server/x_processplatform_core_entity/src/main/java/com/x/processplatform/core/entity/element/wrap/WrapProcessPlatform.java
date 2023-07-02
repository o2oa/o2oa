package com.x.processplatform.core.entity.element.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Application;

public class WrapProcessPlatform extends Application {

	private static final long serialVersionUID = 1863166064194774704L;

	public static final WrapCopier<Application, WrapProcessPlatform> outCopier = WrapCopierFactory.wo(Application.class,
			WrapProcessPlatform.class, null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapProcessPlatform, Application> inCopier = WrapCopierFactory
			.wi(WrapProcessPlatform.class, Application.class, null, ListTools.toList(FieldsUnmodifyExcludeId,
					creatorPerson_FIELDNAME, lastUpdatePerson_FIELDNAME, lastUpdateTime_FIELDNAME), false);

	@FieldDescribe("导出的流程")
	private List<WrapProcess> processList = new ArrayList<>();
	@FieldDescribe("导出的表单")
	private List<WrapForm> formList = new ArrayList<>();
	@FieldDescribe("导出的数据字典")
	private List<WrapApplicationDict> applicationDictList = new ArrayList<>();
	@FieldDescribe("导出的脚本")
	private List<WrapScript> scriptList = new ArrayList<>();
	@FieldDescribe("导出的文件")
	private List<WrapFile> fileList = new ArrayList<>();

	public List<String> listFormId() throws Exception {
		return ListTools.extractProperty(this.getFormList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listProcessId() throws Exception {
		return ListTools.extractProperty(this.getProcessList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listApplicationDictId() throws Exception {
		return ListTools.extractProperty(this.getApplicationDictList(), JpaObject.id_FIELDNAME, String.class, true,
				true);
	}

	public List<String> listScriptId() throws Exception {
		return ListTools.extractProperty(this.getScriptList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listFileId() throws Exception {
		return ListTools.extractProperty(this.getFileList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<WrapProcess> getProcessList() {
		return processList;
	}

	public void setProcessList(List<WrapProcess> processList) {
		this.processList = processList;
	}

	public List<WrapForm> getFormList() {
		return formList;
	}

	public void setFormList(List<WrapForm> formList) {
		this.formList = formList;
	}

	public List<WrapApplicationDict> getApplicationDictList() {
		return applicationDictList;
	}

	public void setApplicationDictList(List<WrapApplicationDict> applicationDictList) {
		this.applicationDictList = applicationDictList;
	}

	public List<WrapScript> getScriptList() {
		return scriptList;
	}

	public void setScriptList(List<WrapScript> scriptList) {
		this.scriptList = scriptList;
	}

	public List<WrapFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<WrapFile> fileList) {
		this.fileList = fileList;
	}

}
