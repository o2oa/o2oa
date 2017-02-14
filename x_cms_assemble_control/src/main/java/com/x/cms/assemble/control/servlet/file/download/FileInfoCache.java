package com.x.cms.assemble.control.servlet.file.download;

import com.x.cms.core.entity.FileInfo;

public class FileInfoCache {

	private FileInfo fileInfo;

	private byte[] bytes;

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
