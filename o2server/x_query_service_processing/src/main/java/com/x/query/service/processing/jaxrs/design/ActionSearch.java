package com.x.query.service.processing.jaxrs.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WiDesigner;
import com.x.base.core.project.jaxrs.WrapDesigner;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;

class ActionSearch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSearch.class);
	private static ReentrantLock lock = new ReentrantLock();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isBlank(wi.getKeyword())) {
			throw new ExceptionFieldEmpty("keyword");
		}
		logger.debug("{}搜索设计：{}的关键字：{}", effectivePerson.getDistinguishedName(), wi.getModuleList(), wi.getKeyword());
		if (ListTools.isNotEmpty(wi.getModuleList())) {
			result.setData(search(wi, effectivePerson));
		} else {
			lock.lock();
			try {
				result.setData(search(wi, effectivePerson));
			} finally {
				lock.unlock();
			}
		}
		return result;
	}

	private Wo search(final Wi wi, EffectivePerson effectivePerson) throws Exception {
		Business business = new Business();
		final Map<String, List<WiDesigner.ModuleApp>> moduleMap = new HashMap<>();
		if (!ListTools.isEmpty(wi.getModuleList())) {
			for (Module module : wi.getModuleList()) {
				if (module.getModuleType().equalsIgnoreCase(ModuleType.cms.toString())
						&& business.isCmsManager(effectivePerson)) {
					moduleMap.put(ModuleType.cms.toString(), module.getModuleAppList());
				}
				if (module.getModuleType().equalsIgnoreCase(ModuleType.portal.toString())
						&& business.isPortalManager(effectivePerson)) {
					moduleMap.put(ModuleType.portal.toString(), module.getModuleAppList());
				}
				if (module.getModuleType().equalsIgnoreCase(ModuleType.processPlatform.toString())
						&& business.isProcessManager(effectivePerson)) {
					moduleMap.put(ModuleType.processPlatform.toString(), module.getModuleAppList());
				}
				if (module.getModuleType().equalsIgnoreCase(ModuleType.query.toString())
						&& business.isManager(effectivePerson)) {
					moduleMap.put(ModuleType.query.toString(), module.getModuleAppList());
				}
				if (module.getModuleType().equalsIgnoreCase(ModuleType.service.toString())
						&& business.isServiceManager(effectivePerson)) {
					moduleMap.put(ModuleType.service.toString(), module.getModuleAppList());
				}
			}
		} else {
			List<WiDesigner.ModuleApp> list = new ArrayList<>();
			if (business.isCmsManager(effectivePerson)) {
				moduleMap.put(ModuleType.cms.toString(), list);
			}
			if (business.isPortalManager(effectivePerson)) {
				moduleMap.put(ModuleType.portal.toString(), list);
			}
			if (business.isProcessManager(effectivePerson)) {
				moduleMap.put(ModuleType.processPlatform.toString(), list);
			}
			if (business.isManager(effectivePerson)) {
				moduleMap.put(ModuleType.query.toString(), list);
			}
			if (business.isServiceManager(effectivePerson)) {
				moduleMap.put(ModuleType.service.toString(), list);
			}
		}
		Wo wo = new Wo();
		wo.setProcessPlatformList(searchApp(wi, moduleMap, ModuleType.processPlatform.toString(),
				x_processplatform_assemble_designer.class));
		wo.setPortalList(searchApp(wi, moduleMap, ModuleType.portal.toString(), x_portal_assemble_designer.class));
		wo.setCmsList(searchApp(wi, moduleMap, ModuleType.cms.toString(), x_cms_assemble_control.class));
		wo.setQueryList(searchApp(wi, moduleMap, ModuleType.query.toString(), x_query_assemble_designer.class));
		wo.setServiceList(searchApp(wi, moduleMap, ModuleType.service.toString(), x_program_center.class));
		return wo;
	}

	private List<WrapDesigner> searchApp(final Wi wi, final Map<String, List<WiDesigner.ModuleApp>> moduleMap,
			final String moduleType, final Class<?> applicationClass) {
		List<WrapDesigner> swList = null;
		if (moduleMap.containsKey(moduleType)) {
			try {
				WiDesigner wiDesigner = new WiDesigner();
				BeanUtils.copyProperties(wiDesigner, wi);
				wiDesigner.setModuleAppList(moduleMap.get(moduleType));
				List<WrapDesigner> designerList = ThisApplication.context().applications()
						.postQuery(applicationClass, Applications.joinQueryUri("designer", "search"), wiDesigner)
						.getDataAsList(WrapDesigner.class);
				logger.debug("设计搜索关联{}的匹配设计个数：{}", moduleType, designerList.size());
				getSearchRes(wi, designerList);
				swList = designerList;
			} catch (Exception e) {
				logger.error(e);
			}
			if (swList != null && swList.size() > 2) {
				try {
					SortTools.desc(swList, "designerType", "appId");
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		return swList;
	}

	private void getSearchRes(final Wi wi, List<WrapDesigner> designerList) {
		if (!ListTools.isEmpty(designerList)) {
			for (WrapDesigner designer : designerList) {
				WrapDesigner.DesignerPattern pattern = designer.getScriptDesigner();
				if (pattern != null) {
					Map<Integer, String> lines = patternLines(
							designer.getDesignerId() + "-" + designer.getUpdateTime().getTime(), wi.getKeyword(),
							pattern.getPropertyValue(), wi.getCaseSensitive(), wi.getMatchWholeWord(),
							wi.getMatchRegExp());
					pattern.setLines(lines);
					pattern.setPropertyValue(null);
				}
			}
		}
	}

	public static class Wi extends GsonPropertyObject {
		private static final long serialVersionUID = 4015406081411685640L;

		@FieldDescribe("搜索关键字.")
		private String keyword;
		@FieldDescribe("搜索设计类型：script|form|page|widget|process|view|table|stat|statement")
		private List<String> designerTypes;
		@FieldDescribe("是否区分大小写.")
		private Boolean caseSensitive;
		@FieldDescribe("是否全字匹配.")
		private Boolean matchWholeWord;
		@FieldDescribe("是否正则表达式匹配.")
		private Boolean matchRegExp;
		@FieldDescribe("限制查询的模块列表(模块类型：processPlatform|cms|portal|query|service)<br/>\r\n")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "Module", fieldValue = "{\"moduleType\": \"processPlatform\", \"moduleAppList\": [{\"appId\":\"\", \"designerList\": [{\"designerType\":\"process\", \"designerIdList\": []}]}]}")
		private List<Module> moduleList;

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public List<String> getDesignerTypes() {
			return designerTypes;
		}

		public void setDesignerTypes(List<String> designerTypes) {
			this.designerTypes = designerTypes;
		}

		public Boolean getCaseSensitive() {
			return caseSensitive;
		}

		public void setCaseSensitive(Boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
		}

		public Boolean getMatchWholeWord() {
			return matchWholeWord;
		}

		public void setMatchWholeWord(Boolean matchWholeWord) {
			this.matchWholeWord = matchWholeWord;
		}

		public Boolean getMatchRegExp() {
			return matchRegExp;
		}

		public void setMatchRegExp(Boolean matchRegExp) {
			this.matchRegExp = matchRegExp;
		}

		public List<Module> getModuleList() {
			return moduleList;
		}

		public void setModuleList(List<Module> moduleList) {
			this.moduleList = moduleList;
		}
	}

	public static class Module extends GsonPropertyObject {
		@FieldDescribe("模块的应用列表.")
		private List<WiDesigner.ModuleApp> moduleAppList = new ArrayList<>();
		@FieldDescribe("模块类型.")
		private String moduleType;

		public List<WiDesigner.ModuleApp> getModuleAppList() {
			return moduleAppList;
		}

		public void setModuleAppList(List<WiDesigner.ModuleApp> moduleAppList) {
			this.moduleAppList = moduleAppList;
		}

		public String getModuleType() {
			return moduleType;
		}

		public void setModuleType(String moduleType) {
			this.moduleType = moduleType;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 8169092162410529422L;

		private List<WrapDesigner> processPlatformList;

		private List<WrapDesigner> cmsList;

		private List<WrapDesigner> portalList;

		private List<WrapDesigner> queryList;

		private List<WrapDesigner> serviceList;

		public List<WrapDesigner> getProcessPlatformList() {
			return processPlatformList;
		}

		public void setProcessPlatformList(List<WrapDesigner> processPlatformList) {
			this.processPlatformList = processPlatformList;
		}

		public List<WrapDesigner> getCmsList() {
			return cmsList;
		}

		public void setCmsList(List<WrapDesigner> cmsList) {
			this.cmsList = cmsList;
		}

		public List<WrapDesigner> getPortalList() {
			return portalList;
		}

		public void setPortalList(List<WrapDesigner> portalList) {
			this.portalList = portalList;
		}

		public List<WrapDesigner> getQueryList() {
			return queryList;
		}

		public void setQueryList(List<WrapDesigner> queryList) {
			this.queryList = queryList;
		}

		public List<WrapDesigner> getServiceList() {
			return serviceList;
		}

		public void setServiceList(List<WrapDesigner> serviceList) {
			this.serviceList = serviceList;
		}
	}

}
