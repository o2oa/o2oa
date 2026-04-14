package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities;

/**
 * @author sword
 */
public class FileHistory {
	/**
	 * 文件版本
	 */
	private int version = 0;
	/**
	 * 文件创建人员
	 */
	private String creatorId = "";
	/**
	 * 文件创建人员
	 */
	private String creatorName = "";
	/**
	 * 文件创建时间
	 */
	private long createTime = 0L;
	/**
	 * 文件下载地址
	 */
	private String downloadUrl = "";
	/**
	 * diff.zip文件下载地址
	 */
	private String diffUrl = "";
	/**
	 * 修改记录
	 */
	private String changes;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getDiffUrl() {
		return diffUrl;
	}

	public void setDiffUrl(String diffUrl) {
		this.diffUrl = diffUrl;
	}

	public String getChanges() {
		return changes;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}
}
