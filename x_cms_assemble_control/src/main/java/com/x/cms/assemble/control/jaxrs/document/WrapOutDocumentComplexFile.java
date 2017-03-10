package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.fileinfo.WrapOutFileInfo;
import com.x.cms.core.entity.FileInfo;

@Wrap( FileInfo.class )
public class WrapOutDocumentComplexFile extends WrapOutFileInfo {

	private static final long serialVersionUID = -413612098220996491L;
	private Long referencedCount;

	public Long getReferencedCount() {
		return referencedCount;
	}

	public void setReferencedCount(Long referencedCount) {
		this.referencedCount = referencedCount;
	}

}