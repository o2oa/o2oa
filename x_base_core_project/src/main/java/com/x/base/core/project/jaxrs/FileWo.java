package com.x.base.core.project.jaxrs;

public class FileWo {

	private String contentDisposition;
	private String contentType;
	private byte[] bytes;

	public FileWo(byte[] bytes, String contentType, String contentDisposition) {
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
