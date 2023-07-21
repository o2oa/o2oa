package com.x.base.core.project.jaxrs;

import javax.ws.rs.core.StreamingOutput;

import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author sword
 */
public abstract class WoFile extends WoMaxAgeFastETag {

	private static final long serialVersionUID = -4566232046358204025L;
	@FieldDescribe("说明")
	private String contentDisposition;
	@FieldDescribe("类型")
	private String contentType;
	@FieldDescribe("字节内容.")
	private byte[] bytes;
	@FieldDescribe("附件流.")
	private StreamingOutput streamingOutput;
	@FieldDescribe("附件大小.")
	private Long contentLength;

	public WoFile(byte[] bytes, String contentType, String contentDisposition) {
		this.bytes = bytes;
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
	}

	public WoFile(byte[] bytes, String contentType, String contentDisposition, String fastETag) {
		this.bytes = bytes;
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
		this.setFastETag(fastETag);
	}

	public WoFile(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength) {
		this.streamingOutput = streamingOutput;
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
		this.contentLength = contentLength;
	}

	public WoFile(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
		this.streamingOutput = streamingOutput;
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
		this.contentLength = contentLength;
		this.setFastETag(fastETag);
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

	public StreamingOutput getStreamingOutput() {
		return streamingOutput;
	}

	public void setStreamingOutput(StreamingOutput streamingOutput) {
		this.streamingOutput = streamingOutput;
	}

	public Long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}
}
