package com.x.cms.assemble.control.jaxrs.document.entity;

import java.util.List;

import com.x.cms.assemble.control.jaxrs.document.WrapOutDocumentSimple;

public class DocumentCacheForFilter {

	private Long total = 0L;
	
	private List<WrapOutDocumentSimple> documentList = null;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<WrapOutDocumentSimple> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<WrapOutDocumentSimple> documentList) {
		this.documentList = documentList;
	}	
}
