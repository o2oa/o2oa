package com.x.program.center.jaxrs.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.Structure;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import com.x.query.core.entity.wrap.WrapQuery;

class ActionCompareUpload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCompareUpload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, String fileName, FormDataContentDisposition disposition)
			throws Exception {
		logger.debug(effectivePerson, "name: {}.", disposition.getName());
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String json = new String(bytes, DefaultCharset.charset);
		WrapModule module = XGsonBuilder.instance().fromJson(json, WrapModule.class);

		if (StringUtils.isEmpty(fileName)) {
			fileName = this.fileName(disposition);
		}
		if(fileName.indexOf(".")==-1){
			fileName = fileName + ".xapp";
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Structure.class);
			Structure structure = new Structure(mapping.getName(), fileName);
			structure.saveContent(mapping, bytes, fileName);
			emc.beginTransaction(Structure.class);
			emc.persist(structure, CheckPersistType.all);
			emc.commit();
			wo.setFlag(structure.getId());
		}
		for (WrapProcessPlatform o : module.getProcessPlatformList()) {
			ActionResponse r = ThisApplication.context().applications().putQuery(effectivePerson.getDebugger(),
					x_processplatform_assemble_designer.class, Applications.joinQueryUri("input", "compare"), o);
			wo.getProcessPlatformList().add(r.getData(JsonElement.class));
		}
		for (WrapCms o : module.getCmsList()) {
			ActionResponse r = ThisApplication.context().applications().putQuery(effectivePerson.getDebugger(),
					x_cms_assemble_control.class, Applications.joinQueryUri("input", "compare"), o);
			wo.getCmsList().add(r.getData(JsonElement.class));
		}
		for (WrapPortal o : module.getPortalList()) {
			ActionResponse r = ThisApplication.context().applications().putQuery(effectivePerson.getDebugger(),
					x_portal_assemble_designer.class, Applications.joinQueryUri("input", "compare"), o);
			wo.getPortalList().add(r.getData(JsonElement.class));
		}
		for (WrapQuery o : module.getQueryList()) {
			ActionResponse r = ThisApplication.context().applications().putQuery(effectivePerson.getDebugger(),
					x_query_assemble_designer.class, Applications.joinQueryUri("input", "compare"), o);
			wo.getQueryList().add(r.getData(JsonElement.class));
		}
		for (WrapServiceModule o : module.getServiceModuleList()) {
			ActionResponse r = CipherConnectionAction.put(false,
					Config.url_x_program_center_jaxrs("input", "compare"), o);
			wo.getServiceModuleList().add(r.getData(JsonElement.class));
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("标识")
		private String flag;

		@FieldDescribe("流程")
		List<JsonElement> processPlatformList = new ArrayList<>();

		@FieldDescribe("门户")
		List<JsonElement> portalList = new ArrayList<>();

		@FieldDescribe("内容管理")
		List<JsonElement> cmsList = new ArrayList<>();

		@FieldDescribe("统计")
		List<JsonElement> queryList = new ArrayList<>();

		@FieldDescribe("服务")
		List<JsonElement> serviceModuleList = new ArrayList<>();

		public List<JsonElement> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<JsonElement> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<JsonElement> getPortalList() {
			return portalList;
		}

		public void setPortalList(List<JsonElement> portalList) {
			this.portalList = portalList;
		}

		public List<JsonElement> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<JsonElement> cmsList) {
			this.cmsList = cmsList;
		}

		public List<JsonElement> getQueryList() {
			return queryList;
		}

		public void setQueryList(List<JsonElement> queryList) {
			this.queryList = queryList;
		}

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public List<JsonElement> getServiceModuleList() {
			return serviceModuleList;
		}

		public void setServiceModuleList(List<JsonElement> serviceModuleList) {
			this.serviceModuleList = serviceModuleList;
		}
	}

}