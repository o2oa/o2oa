package com.x.program.center.jaxrs.module;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.query.core.entity.wrap.WrapQuery;

import net.sf.ehcache.Element;

class ActionCompare extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
//			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
//				throw new ExceptionDisable();
//			}
//			if (!business.collectAccountNotEmpty()) {
//				throw new ExceptionCollectAccountEmpty();
//			}
//			if (BooleanUtils.isNotTrue(business.connectCollect())) {
//				throw new ExceptionConnectError();
//			}
//			if (BooleanUtils.isNotTrue(business.validateCollect())) {
//				throw new ExceptionValidateError();
//			}
			Req req = new Req();
			req.setName(Config.collect().getName());
			req.setPassword(Config.collect().getPassword());
			String url = Config.collect().url("/o2_collect_assemble/jaxrs/module/" + id + "/data");
			
			ActionResponse ar = ConnectionAction.post(url, null, req);
			Resp resp = ar.getData(Resp.class);
			WrapModule module = gson.fromJson(resp.getValue(), WrapModule.class);
			CacheObject cacheObject = new CacheObject();
			cacheObject.setModule(module);
			String flag = StringTools.uniqueToken();
			cache.put(new Element(flag, cacheObject));
			wo.setFlag(flag);
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

		@FieldDescribe("查询")
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

	public static class Req extends GsonPropertyObject {

		private String name;
		private String password;
		private List<String> categoryList = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public List<String> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(List<String> categoryList) {
			this.categoryList = categoryList;
		}
	}

	public static class Resp extends WrapString {
	}

}