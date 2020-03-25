package com.x.program.center.jaxrs.module;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.query.core.entity.wrap.WrapQuery;

import net.sf.ehcache.Element;

public class ActionWrite extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionWrite.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			ActionResult<Wo> result = new ActionResult<>();
			Element element = cache.get(flag);
			if (null == element || null == element.getObjectValue()) {
				throw new ExceptionFlagNotExist(flag);
			}
			CacheObject cacheObject = (CacheObject) element.getObjectValue();
			WrapModule module = cacheObject.getModule();
			List<WrapPair> replaces = new ArrayList<>();
			for (WiCommand cmd : wi.getProcessPlatformList()) {
				WrapProcessPlatform o = module.getProcessPlatform(cmd.getId());
				if (null != o) {
					switch (cmd.getMethod()) {
					case "create":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_processplatform_assemble_designer.class,
										Applications.joinQueryUri("input", "prepare", "create"), o)
								.getDataAsList(WrapPair.class));
						break;
					case "cover":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_processplatform_assemble_designer.class,
										Applications.joinQueryUri("input", "prepare", "cover"), o)
								.getDataAsList(WrapPair.class));
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getCmsList()) {
				WrapCms o = module.getCms(cmd.getId());
				if (null != o) {
					switch (cmd.getMethod()) {
					case "create":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_cms_assemble_control.class,
										Applications.joinQueryUri("input", "prepare", "create"), o)
								.getDataAsList(WrapPair.class));
						break;
					case "cover":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_cms_assemble_control.class,
										Applications.joinQueryUri("input", "prepare", "cover"), o)
								.getDataAsList(WrapPair.class));
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getPortalList()) {
				WrapPortal o = module.getPortal(cmd.getId());
				if (null != o) {
					switch (cmd.getMethod()) {
					case "create":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_portal_assemble_designer.class,
										Applications.joinQueryUri("input", "prepare", "create"), o)
								.getDataAsList(WrapPair.class));
						break;
					case "cover":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_portal_assemble_designer.class,
										Applications.joinQueryUri("input", "prepare", "cover"), o)
								.getDataAsList(WrapPair.class));
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getQueryList()) {
				WrapQuery o = module.getQuery(cmd.getId());
				if (null != o) {
					switch (cmd.getMethod()) {
					case "create":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_query_assemble_designer.class,
										Applications.joinQueryUri("input", "prepare", "create"), o)
								.getDataAsList(WrapPair.class));
						break;
					case "cover":
						replaces.addAll(ThisApplication.context().applications()
								.putQuery(x_query_assemble_designer.class,
										Applications.joinQueryUri("input", "prepare", "cover"), o)
								.getDataAsList(WrapPair.class));
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getServiceModuleList()) {
				WrapServiceModule o = module.getServiceModule(cmd.getId());
				if (null != o) {
					switch (cmd.getMethod()) {
						case "create":
							replaces.addAll(CipherConnectionAction.put(false,
									Config.url_x_program_center_jaxrs("input", "prepare", "create"), o)
									.getDataAsList(WrapPair.class));
							break;
						case "cover":
							replaces.addAll(CipherConnectionAction.put(false,
									Config.url_x_program_center_jaxrs("input", "prepare", "cover"), o)
									.getDataAsList(WrapPair.class));
							break;
						default:
							break;
					}
				}
			}
			for (WiCommand cmd : wi.getProcessPlatformList()) {
				WrapProcessPlatform o = module.getProcessPlatform(cmd.getId());
				if (null != o) {
					String json = gson.toJson(o);
					for (WrapPair re : replaces) {
						json = StringUtils.replace(json, re.getFirst(), re.getSecond());
					}
					WrapProcessPlatform obj = gson.fromJson(json, WrapProcessPlatform.class);
					switch (cmd.getMethod()) {
					case "create":
						wo.getProcessPlatformList()
								.add(ThisApplication.context().applications()
										.putQuery(x_processplatform_assemble_designer.class,
												Applications.joinQueryUri("input", "create"), obj)
										.getData(WoId.class).getId());
						break;
					case "cover":
						wo.getProcessPlatformList()
								.add(ThisApplication.context().applications()
										.putQuery(x_processplatform_assemble_designer.class,
												Applications.joinQueryUri("input", "cover"), obj)
										.getData(WoId.class).getId());
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getCmsList()) {
				WrapCms o = module.getCms(cmd.getId());
				if (null != o) {
					String json = gson.toJson(o);
					for (WrapPair re : replaces) {
						json = StringUtils.replace(json, re.getFirst(), re.getSecond());
					}
					WrapCms obj = gson.fromJson(json, WrapCms.class);
					switch (cmd.getMethod()) {
					case "create":
						wo.getCmsList()
								.add(ThisApplication.context().applications()
										.putQuery(x_cms_assemble_control.class,
												Applications.joinQueryUri("input", "create"), obj)
										.getData(WoId.class).getId());
						break;
					case "cover":
						wo.getCmsList()
								.add(ThisApplication.context().applications()
										.putQuery(x_cms_assemble_control.class,
												Applications.joinQueryUri("input", "cover"), obj)
										.getData(WoId.class).getId());
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getPortalList()) {
				WrapPortal o = module.getPortal(cmd.getId());
				if (null != o) {
					String json = gson.toJson(o);
					for (WrapPair re : replaces) {
						json = StringUtils.replace(json, re.getFirst(), re.getSecond());
					}
					WrapPortal obj = gson.fromJson(json, WrapPortal.class);
					switch (cmd.getMethod()) {
					case "create":
						wo.getPortalList()
								.add(ThisApplication.context().applications()
										.putQuery(x_portal_assemble_designer.class,
												Applications.joinQueryUri("input", "create"), obj)
										.getData(WoId.class).getId());
						break;
					case "cover":
						wo.getPortalList()
								.add(ThisApplication.context().applications()
										.putQuery(x_portal_assemble_designer.class,
												Applications.joinQueryUri("input", "cover"), obj)
										.getData(WoId.class).getId());
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getQueryList()) {
				WrapQuery o = module.getQuery(cmd.getId());
				if (null != o) {
					String json = gson.toJson(o);
					for (WrapPair re : replaces) {
						json = StringUtils.replace(json, re.getFirst(), re.getSecond());
					}
					WrapQuery obj = gson.fromJson(json, WrapQuery.class);
					switch (cmd.getMethod()) {
					case "create":
						wo.getQueryList()
								.add(ThisApplication.context().applications()
										.putQuery(x_query_assemble_designer.class,
												Applications.joinQueryUri("input", "create"), obj)
										.getData(WoId.class).getId());
						break;
					case "cover":
						wo.getQueryList()
								.add(ThisApplication.context().applications()
										.putQuery(x_query_assemble_designer.class,
												Applications.joinQueryUri("input", "cover"), obj)
										.getData(WoId.class).getId());
						break;
					default:
						break;
					}
				}
			}
			for (WiCommand cmd : wi.getServiceModuleList()) {
				WrapServiceModule o = module.getServiceModule(cmd.getId());
				if (null != o) {
					String json = gson.toJson(o);
					for (WrapPair re : replaces) {
						json = StringUtils.replace(json, re.getFirst(), re.getSecond());
					}
					WrapServiceModule obj = gson.fromJson(json, WrapServiceModule.class);
					switch (cmd.getMethod()) {
						case "create":
							wo.getServiceModuleList()
									.add(CipherConnectionAction.put(false,
											Config.url_x_program_center_jaxrs("input", "create"), obj)
											.getData(WoId.class).getId());
							break;
						case "cover":
							wo.getServiceModuleList()
									.add(CipherConnectionAction.put(false,
											Config.url_x_program_center_jaxrs("input", "cover"), obj)
											.getData(WoId.class).getId());
							break;
						default:
							break;
					}
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("流程应用")
		private List<WiCommand> processPlatformList = new ArrayList<>();

		@FieldDescribe("门户应用")
		private List<WiCommand> portalList = new ArrayList<>();

		@FieldDescribe("统计应用")
		private List<WiCommand> queryList = new ArrayList<>();

		@FieldDescribe("内容管理应用")
		private List<WiCommand> cmsList = new ArrayList<>();

		@FieldDescribe("服务管理应用")
		private List<WiCommand> serviceModuleList = new ArrayList<>();

		public List<WiCommand> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<WiCommand> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<WiCommand> getPortalList() {
			return portalList;
		}

		public void setPortalList(List<WiCommand> portalList) {
			this.portalList = portalList;
		}

		public List<WiCommand> getQueryList() {
			return queryList;
		}

		public void setQueryList(List<WiCommand> queryList) {
			this.queryList = queryList;
		}

		public List<WiCommand> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<WiCommand> cmsList) {
			this.cmsList = cmsList;
		}

		public List<WiCommand> getServiceModuleList() {
			return serviceModuleList;
		}

		public void setServiceModuleList(List<WiCommand> serviceModuleList) {
			this.serviceModuleList = serviceModuleList;
		}

	}

	public static class WiCommand extends GsonPropertyObject {

		@FieldDescribe("标识")
		private String id;

		@FieldDescribe("方式")
		private String method;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("流程应用")
		private List<String> processPlatformList = new ArrayList<>();

		@FieldDescribe("门户应用")
		private List<String> portalList = new ArrayList<>();

		@FieldDescribe("统计应用")
		private List<String> queryList = new ArrayList<>();

		@FieldDescribe("内容管理应用")
		private List<String> cmsList = new ArrayList<>();

		@FieldDescribe("服务管理应用")
		private List<String> serviceModuleList = new ArrayList<>();

		public List<String> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<String> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<String> getPortalList() {
			return portalList;
		}

		public void setPortalList(List<String> portalList) {
			this.portalList = portalList;
		}

		public List<String> getQueryList() {
			return queryList;
		}

		public void setQueryList(List<String> queryList) {
			this.queryList = queryList;
		}

		public List<String> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<String> cmsList) {
			this.cmsList = cmsList;
		}

		public List<String> getServiceModuleList() {
			return serviceModuleList;
		}

		public void setServiceModuleList(List<String> serviceModuleList) {
			this.serviceModuleList = serviceModuleList;
		}
	}

}