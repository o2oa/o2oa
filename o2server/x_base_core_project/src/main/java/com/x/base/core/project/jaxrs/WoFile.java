package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;

public abstract class WoFile {

	@FieldDescribe("说明")
	private String contentDisposition;
	@FieldDescribe("类型")
	private String contentType;
	@FieldDescribe("字节内容.")
	private byte[] bytes;

	public WoFile(byte[] bytes, String contentType, String contentDisposition) {
		this.bytes = bytes;
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
