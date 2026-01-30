package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities;

import java.util.List;

/**
 * @author sword
 */
public class FileInfoModel {

	private FileInfo fileInfo;

	private UserInfo userInfo;

	private List<FileHistory> fileHistoryList;

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public List<FileHistory> getFileHistoryList() {
		return fileHistoryList;
	}

	public void setFileHistoryList(List<FileHistory> fileHistoryList) {
		this.fileHistoryList = fileHistoryList;
	}
}
