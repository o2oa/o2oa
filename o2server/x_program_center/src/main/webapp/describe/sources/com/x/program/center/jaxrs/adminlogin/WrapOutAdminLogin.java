package com.x.program.center.jaxrs.adminlogin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.TokenType;

public class WrapOutAdminLogin extends GsonPropertyObject {

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private TokenType tokenType;

	private String token;

	private String name;

	static {
		Excludes.add("password");
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
