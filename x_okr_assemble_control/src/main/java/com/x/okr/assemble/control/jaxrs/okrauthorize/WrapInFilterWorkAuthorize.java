package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInFilterWorkAuthorize.class )
public class WrapInFilterWorkAuthorize {
	
	@EntityFieldDescribe( "授权工作ID." )
	private String workId = null;

	@EntityFieldDescribe( "授权者身份." )
	private String authorizeIdentity = null;
	
	@EntityFieldDescribe( "承担者身份." )
	private String undertakerIdentity = null;

	@EntityFieldDescribe( "授权意见." )
	private String authorizeOpinion = null;

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getAuthorizeIdentity() {
		return authorizeIdentity;
	}

	public void setAuthorizeIdentity(String authorizeIdentity) {
		this.authorizeIdentity = authorizeIdentity;
	}

	public String getUndertakerIdentity() {
		return undertakerIdentity;
	}

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
