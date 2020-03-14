package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class Portal extends ConfigObject {

	public static Portal defaultInstance() {
		return new Portal();
	}

	public Portal() {
		this.indexPage = new IndexPage();
	}

	@FieldDescribe("定制首页面设置.")
	private IndexPage indexPage;

	public IndexPage getIndexPage() {
		if (null == indexPage) {
			this.indexPage = new IndexPage();
		}
		return this.indexPage;
	}

	public void setIndexPage(IndexPage indexPage) {
		this.indexPage = indexPage;
	}

	public static class IndexPage extends ConfigObject {

		public static IndexPage defaultInstance() {
			return new IndexPage();
		}

		public IndexPage() {
			this.enable = false;
			this.portal = "";
			this.page = "";
		}

		@FieldDescribe("是否启用定制的首页面.")
		private Boolean enable;
		@FieldDescribe("指定首页面所属的portal,可以用id,name,alias.")
		private String portal;
		@FieldDescribe("指定的首页面,可以使用name,alias,id")
		private String page;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getPortal() {
			return portal;
		}

		public void setPortal(String portal) {
			this.portal = portal;
		}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}

	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_PORTAL);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}
}