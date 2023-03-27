package com.x.portal.core.entity.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.wrap.WrapApplicationDict;
import com.x.portal.core.entity.Portal;

public class WrapPortal extends Portal {

	private static final long serialVersionUID = 8377718918930811393L;

	public static WrapCopier<Portal, WrapPortal> outCopier = WrapCopierFactory.wo(Portal.class, WrapPortal.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapPortal, Portal> inCopier = WrapCopierFactory.wi(WrapPortal.class, Portal.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

	private List<WrapPage> pageList = new ArrayList<>();
	private List<WrapScript> scriptList = new ArrayList<>();
	private List<WrapFile> fileList = new ArrayList<>();
	private List<WrapWidget> widgetList = new ArrayList<>();
	private List<WrapApplicationDict> applicationDictList = new ArrayList<>();

	public List<String> listPageId() throws Exception {
		return ListTools.extractProperty(this.getPageList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listScriptId() throws Exception {
		return ListTools.extractProperty(this.getScriptList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listFileId() throws Exception {
		return ListTools.extractProperty(this.getFileList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listWidgetId() throws Exception {
		return ListTools.extractProperty(this.getWidgetList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<WrapApplicationDict> getApplicationDictList() {
		return applicationDictList;
	}

	public List<WrapPage> getPageList() {
		return pageList;
	}

	public void setPageList(List<WrapPage> pageList) {
		this.pageList = pageList;
	}

	public List<WrapScript> getScriptList() {
		return scriptList;
	}

	public void setScriptList(List<WrapScript> scriptList) {
		this.scriptList = scriptList;
	}

	public List<WrapWidget> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(List<WrapWidget> widgetList) {
		this.widgetList = widgetList;
	}

	public List<WrapFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<WrapFile> fileList) {
		this.fileList = fileList;
	}

	public void setApplicationDictList(List<WrapApplicationDict> applicationDictList) {
		this.applicationDictList = applicationDictList;
	}

	public List<String> listApplicationDictId() throws Exception {
		return ListTools.extractProperty(this.getApplicationDictList(), JpaObject.id_FIELDNAME, String.class, true,
				true);
	}

}
