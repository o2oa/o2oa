package com.x.pan.assemble.control.entities;

/**
 * @author sword
 */
public class FileInfo extends FileHistory {

	/**
	 * 文件ID
	 */
	private String id = "";
	/**
	 * 文件名称
	 */
	private String name = "";
	/**
	 * 文件大小
	 */
	private long size = 0L;
	/**
	 * 文件最后修改人员
	 */
	private String modifierId = "";
	/**
	 * 文件最后修改人员
	 */
	private String modifierName = "";
	/**
	 * 文件最后修改时间
	 */
	private long modifyTime;
	/**
	 * 文件权限
	 */
	private UserPermission userPermission;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getModifierId() {
		return modifierId;
	}

	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}

	public String getModifierName() {
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public UserPermission getUserPermission() {
		return userPermission;
	}

	public void setUserPermission(UserPermission userPermission) {
		this.userPermission = userPermission;
	}
}
