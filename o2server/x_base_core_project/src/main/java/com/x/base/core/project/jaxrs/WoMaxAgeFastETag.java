package com.x.base.core.project.jaxrs;

import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.Date;

public class WoMaxAgeFastETag extends GsonPropertyObject {

	private static final long serialVersionUID = -8408799363043605326L;

	private String fastETag;

	private Integer maxAge = null;

	/**
	 * 设置了maxAge才会生效
	 */
	private Date lastModified = null;

	public String getFastETag() {
		return fastETag;
	}

	public void setFastETag(String fastETag) {
		this.fastETag = fastETag;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}
