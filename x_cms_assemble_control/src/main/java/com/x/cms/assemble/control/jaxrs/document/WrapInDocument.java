package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.Document;

@Wrap(Document.class)
public class WrapInDocument extends Document {
	
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

	private String identity = null;
	
	private String[] dataPaths = null;
	
	private String wf_jobId = null;
	
	private String wf_workId = null;
	
	private String[] wf_attachmentIds = null;
	
	private Map<?, ?> docData = null;
	
	private List<PermissionInfo> permissionList = null;
	
	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public List<PermissionInfo> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List<PermissionInfo> permissionList) {
		this.permissionList = permissionList;
	}

	public String[] getDataPaths() {
		if( dataPaths != null && dataPaths.length == 1 && dataPaths[0].equals("null")){
			return null;
		}
		return dataPaths;
	}

	public void setDataPaths(String[] dataPaths) {
		this.dataPaths = dataPaths;
	}

	public Map<?, ?> getDocData() {
		return docData;
	}

	public void setDocData(Map<?, ?> docData) {
		this.docData = docData;
	}

	public String getWf_jobId() {
		return wf_jobId;
	}

	public String getWf_workId() {
		return wf_workId;
	}

	public String[] getWf_attachmentIds() {
		return wf_attachmentIds;
	}

	public void setWf_jobId(String wf_jobId) {
		this.wf_jobId = wf_jobId;
	}

	public void setWf_workId(String wf_workId) {
		this.wf_workId = wf_workId;
	}

	public void setWf_attachmentIds(String[] wf_attachmentIds) {
		this.wf_attachmentIds = wf_attachmentIds;
	}	
}