package com.x.program.init.jaxrs.externaldatasources;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.ExternalDataSource;
import com.x.base.core.project.config.ExternalDataSources;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionValidate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionValidate.class);

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		List<Wo> wos = new ArrayList<>();

		ExternalDataSources externalDataSources = wi.getExternalDataSources();

		for (ExternalDataSource externalDataSource : externalDataSources) {
			Wo wo = new Wo();
			wo.setUrl(externalDataSource.getUrl());
			String driver = SlicePropertiesBuilder.driverClassNameOfUrl(externalDataSource.getUrl());
			wo.setDirver(driver);
			Class.forName(driver);
			try (Connection conn = DriverManager.getConnection(externalDataSource.getUrl(),
					externalDataSource.getUsername(), externalDataSource.getPassword())) {
				wo.setSuccess(true);
			} catch (Exception e) {
				wo.setSuccess(false);
				wo.setFailureMessage(e.getMessage());
			}
			wos.add(wo);
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -8229921649330471330L;

		private Boolean success;
		private String url;
		private String dirver;
		private String failureMessage;

		public Boolean getSuccess() {
			return success;
		}

		public void setSuccess(Boolean success) {
			this.success = success;
		}

		public String getFailureMessage() {
			return failureMessage;
		}

		public void setFailureMessage(String failureMessage) {
			this.failureMessage = failureMessage;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDirver() {
			return dirver;
		}

		public void setDirver(String dirver) {
			this.dirver = dirver;
		}

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -7076689129338985192L;

		@FieldDescribe("外部数据源.")
		private ExternalDataSources externalDataSources;

		public ExternalDataSources getExternalDataSources() {
			return externalDataSources;
		}

		public void setExternalDataSources(ExternalDataSources externalDataSources) {
			this.externalDataSources = externalDataSources;
		}

	}

}