package com.x.okr.assemble.control;

import java.io.Serializable;

import com.x.base.core.project.gson.GsonPropertyObject;

public class OkrUserCache extends GsonPropertyObject implements Serializable {	

	private static final long serialVersionUID = 1L;
	/**
	 * 操作用户的姓名
	 */
	private String operationUserName = null;
	/**
	 * 操作用户的组织名称
	 */
	private String operationUserUnitName = null;
	/**
	 * 操作用户的顶层组织名称
	 */
	private String operationUserTopUnitName = null;
	/**
	 * 代理用户的身份名称
	 */
	private String loginIdentityName = null;
	/**
	 * 代理用户的姓名
	 */
	private String loginUserName = null;
	/**
	 * 代理用户的组织名称
	 */
	private String loginUserUnitName = null;
	/**
	 * 代理用户的顶层组织名称
	 */
	private String loginUserTopUnitName = null;
	
	private boolean okrManager = false; //系统管理员

	
	public boolean isOkrManager() {
		return okrManager;
	}
	public void setOkrManager(boolean okrManager) {
		this.okrManager = okrManager;
	}
	
	public String getOperationUserName() {
		return operationUserName;
	}
	public void setOperationUserName(String operationUserName) {
		this.operationUserName = operationUserName;
	}
	public String getOperationUserUnitName() {
		return operationUserUnitName;
	}
	public void setOperationUserUnitName(String operationUserUnitName) {
		this.operationUserUnitName = operationUserUnitName;
	}
	public String getOperationUserTopUnitName() {
		return operationUserTopUnitName;
	}
	public void setOperationUserTopUnitName(String operationUserTopUnitName) {
		this.operationUserTopUnitName = operationUserTopUnitName;
	}
	public String getLoginIdentityName() {
		return loginIdentityName;
	}
	public void setLoginIdentityName(String loginIdentityName) {
		this.loginIdentityName = loginIdentityName;
	}
	public String getLoginUserName() {
		return loginUserName;
	}
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}
	public String getLoginUserUnitName() {
		return loginUserUnitName;
	}
	public void setLoginUserUnitName(String loginUserUnitName) {
		this.loginUserUnitName = loginUserUnitName;
	}
	public String getLoginUserTopUnitName() {
		return loginUserTopUnitName;
	}
	public void setLoginUserTopUnitName(String loginUserTopUnitName) {
		this.loginUserTopUnitName = loginUserTopUnitName;
	}
	
}
