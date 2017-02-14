package com.x.file.assemble.control.jaxrs.complex;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.assemble.control.wrapout.WrapOutFolder;

@Wrap
public class WrapOutComplex extends GsonPropertyObject {
	List<WrapOutAttachment> attachmentList;
	List<WrapOutFolder> folderList;

	public List<WrapOutAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<WrapOutAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<WrapOutFolder> getFolderList() {
		return folderList;
	}

	public void setFolderList(List<WrapOutFolder> folderList) {
		this.folderList = folderList;
	}
}