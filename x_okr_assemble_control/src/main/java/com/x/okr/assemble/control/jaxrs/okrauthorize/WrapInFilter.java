package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkBaseInfo;

@Wrap( OkrWorkBaseInfo.class )
public class WrapInFilter extends GsonPropertyObject {
	
	private String workId = null;
	
	/**
	 * 授权者
	 */
	private String authorizeIdentity = null;
	
	/**
	 * 承担者
	 */
	private String undertakerIdentity = null;
	/**
	 * 授权意见
	 */
	private String authorizeOpinion = null;

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}
	/**
	 * 授权者
	 */
	public String getAuthorizeIdentity() {
		return authorizeIdentity;
	}
	/**
	 * 授权者
	 */
	public void setAuthorizeIdentity(String authorizeIdentity) {
		this.authorizeIdentity = authorizeIdentity;
	}
	/**
	 * 承担者
	 */
	public String getUndertakerIdentity() {
		return undertakerIdentity;
	}
	/**
	 * 承担者
	 */
	public void setUndertakerIdentity(String undertakerIdentity) {
		this.undertakerIdentity = undertakerIdentity;
	}

	public String getAuthorizeOpinion() {
		return authorizeOpinion;
	}

	public void setAuthorizeOpinion(String authorizeOpinion) {
		this.authorizeOpinion = authorizeOpinion;
	}
	
}
