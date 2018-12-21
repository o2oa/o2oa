package com.x.organization.assemble.authentication.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.TokenType;
import com.x.organization.core.entity.Person;

public class WrapOutAuthentication extends Person {

	private static final long serialVersionUID = 4901269474728548509L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private TokenType tokenType;
	private String token;
	private List<String> roleList;
	private Boolean passwordExpired;

	static {
		Excludes.add("password");
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public Boolean getPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(Boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}
}
