package com.x.file.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.file.core.entity.personal.Folder;

@Wrap(Folder.class)
public class WrapOutFolder extends Folder {

	private static final long serialVersionUID = -3416878548938205004L;
	public static List<String> Excludes = new ArrayList<>();

	private Long attachmentCount;
	private Long size;
	private Long folderCount;

	public Long getAttachmentCount() {
		return attachmentCount;
	}

	public void setAttachmentCount(Long attachmentCount) {
		this.attachmentCount = attachmentCount;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getFolderCount() {
		return folderCount;
	}

	public void setFolderCount(Long folderCount) {
		this.folderCount = folderCount;
	}

	static {
		Excludes.add(JpaObject.DISTRIBUTEFACTOR);
	}
}
