package com.x.okr.assemble.control;

import java.io.Serializable;

import com.x.base.core.gson.GsonPropertyObject;

public class OkrUserCache extends GsonPropertyObject implements Serializable {	

	private static final long serialVersionUID = 1L;
	/**
	 * 操作用户的姓名
	 */
	private String operationUserName = null;
	/**
	 * 操作用户的组织名称
	 */
	private String operationUserOrganizationName = null;
	/**
	 * 操作用户的公司名称
	 */
	private String operationUserCompanyName = null;
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
	private String loginUserOrganizationName = null;
	/**
	 * 代理用户的公司名称
	 */
	private String loginUserCompanyName = null;
	
	private boolean okrSystemAdmin = false; //系统管理员

	public boolean isOkrSystemAdmin() {
		return okrSystemAdmin;
	}
	public void setOkrSystemAdmin(boolean okrSystemAdmin) {
		this.okrSystemAdmin = okrSystemAdmin;
	}
	public String getOperationUserName() {
		return operationUserName;
	}
	public void setOperationUserName(String operationUserName) {
		this.operationUserName = operationUserName;
	}
	public String getOperationUserOrganizationName() {
		return operationUserOrganizationName;
	}
	public void setOperationUserOrganizationName(String operationUserOrganizationName) {
		this.operationUserOrganizationName = operationUserOrganizationName;
	}
	public String getOperationUserCompanyName() {
		return operationUserCompanyName;
	}
	public void setOperationUserCompanyName(String operationUserCompanyName) {
		this.operationUserCompanyName = operationUserCompanyName;
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
	public String getLoginUserOrganizationName() {
		return loginUserOrganizationName;
	}
	public void setLoginUserOrganizationName(String loginUserOrganizationName) {
		this.loginUserOrganizationName = loginUserOrganizationName;
	}
	public String getLoginUserCompanyName() {
		return loginUserCompanyName;
	}
	public void setLoginUserCompanyName(String loginUserCompanyName) {
		this.loginUserCompanyName = loginUserCompanyName;
	}
	
}
