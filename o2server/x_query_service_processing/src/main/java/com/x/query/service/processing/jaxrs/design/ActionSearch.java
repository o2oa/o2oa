package com.x.query.service.processing.jaxrs.design;

import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.query.service.processing.ThisApplication;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class ActionSearch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSearch.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		wo.setType(wi.getType());
		if(StringUtils.isBlank(wi.getKeyword())){
			throw new ExceptionFieldEmpty("keyword");
		}
		if(StringUtils.isBlank(wi.getType())){
			throw new ExceptionFieldEmpty("type");
		}
		logger.print("{}搜索全局设计：{}，关键字：{}", effectivePerson.getDistinguishedName(), wi.getType(), wi.getKeyword());
		switch (wi.getType()) {
			case "script":
				wo.setScriptWrapList(searchScript(wi));
				break;
			default:
				throw new ExceptionFieldEmpty("type");
		}
		result.setData(wo);
		return result;
	}

	private List<ScriptWo> searchScript(final Wi wi) throws Exception{
		List<ScriptWo> scriptWoList = new ArrayList<>();
		CompletableFuture<List<ScriptWo>> cmsCf = CompletableFuture.supplyAsync(() -> {
			List<ScriptWo> swList = new ArrayList<>();
			try {
				List<WrapScript> scriptList = ThisApplication.context().applications().getQuery(x_cms_assemble_control.class,
						Applications.joinQueryUri("script", "list", "manager"), null).getDataAsList(WrapScript.class);
				logger.print("CMS的脚本个数：{}",scriptList.size());
				getScriptSearchRes(wi,"cms",  swList, scriptList);
			} catch (Exception e) {
				logger.error(e);
			}
			if (swList.size()>2){
				try {
					SortTools.desc(swList, "appId");
				} catch (Exception e) {
				}
			}
			return swList;
		});

		CompletableFuture<List<ScriptWo>> portalCf = CompletableFuture.supplyAsync(() -> {
			List<ScriptWo> swList = new ArrayList<>();
			try {
				List<WrapScript> scriptList = ThisApplication.context().applications().getQuery(x_portal_assemble_designer.class,
						Applications.joinQueryUri("script", "list", "manager"), null).getDataAsList(WrapScript.class);
				logger.print("门户的脚本个数：{}",scriptList.size());
				getScriptSearchRes(wi,"portal",  swList, scriptList);
			} catch (Exception e) {
				logger.error(e);
			}
			if (swList.size()>2){
				try {
					SortTools.desc(swList, "appId");
				} catch (Exception e) {
				}
			}
			return swList;
		});

		CompletableFuture<List<ScriptWo>> processPlatformCf = CompletableFuture.supplyAsync(() -> {
			List<ScriptWo> swList = new ArrayList<>();
			try {
				List<WrapScript> scriptList = ThisApplication.context().applications().getQuery(x_processplatform_assemble_designer.class,
						Applications.joinQueryUri("script", "list", "manager"), null).getDataAsList(WrapScript.class);
				logger.print("流程平台的脚本个数：{}",scriptList.size());
				getScriptSearchRes(wi,"processPlatform", swList, scriptList);
			} catch (Exception e) {
				logger.error(e);
			}
			if (swList.size()>2){
				try {
					SortTools.desc(swList, "appId");
				} catch (Exception e) {
				}
			}
			return swList;
		});

		scriptWoList.addAll(processPlatformCf.get());
		scriptWoList.addAll(portalCf.get());
		scriptWoList.addAll(cmsCf.get());

		return scriptWoList;
	}

	private void getScriptSearchRes(final Wi wi, String moduleType, List<ScriptWo> swList, List<WrapScript> scriptList){
		if (!ListTools.isEmpty(scriptList)){
			for (WrapScript script:scriptList) {
				if (keywordMatch(wi.getKeyword(), script.getText(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
					List<Integer> list = patternLines(script.getId()+"-"+script.getUpdateTime().getTime(),
							wi.getKeyword(), script.getText(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!ListTools.isEmpty(list)){
						ScriptWo scriptWo = new ScriptWo();
						scriptWo.setModuleType(moduleType);
						scriptWo.setAppId(script.getAppId());
						scriptWo.setAppName(script.getAppName());
						scriptWo.setScriptId(script.getId());
						scriptWo.setScriptName(script.getName());
						scriptWo.setPatternLines(list);
						swList.add(scriptWo);
					}
				}
			}
		}
	}

	public static class Wi extends GsonPropertyObject {
		private static final long serialVersionUID = 4015406081411685640L;

		@FieldDescribe("搜索关键字.")
		private String keyword;
		@FieldDescribe("搜索类型：script|form|process")
		private String type;
		@FieldDescribe("是否区分大小写.")
		private Boolean caseSensitive;
		@FieldDescribe("是否全字匹配.")
		private Boolean matchWholeWord;
		@FieldDescribe("是否正则表达式匹配.")
		private Boolean matchRegExp;
		@FieldDescribe("限制查询的模块列表.")
		private List<Module> moduleList;

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
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
		@FieldDescribe("模块的应用关键字.")
		private String flag;
		@FieldDescribe("模块类型：processPlatform|cms|portal|query|service")
		private String moduleType;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public String getModuleType() {
			return moduleType;
		}

		public void setModuleType(String moduleType) {
			this.moduleType = moduleType;
		}
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("搜索类型：script|form|process")
		private String type;
		@FieldDescribe("脚本搜索结果集")
		private List<ScriptWo> scriptWrapList = new ArrayList<>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<ScriptWo> getScriptWrapList() {
			return scriptWrapList;
		}

		public void setScriptWrapList(List<ScriptWo> scriptWrapList) {
			this.scriptWrapList = scriptWrapList;
		}
	}

	public static class ScriptWo extends GsonPropertyObject {
		@FieldDescribe("模块类型：processPlatform|cms|portal|query|service")
		private String moduleType;
		@FieldDescribe("应用ID")
		private String appId;
		@FieldDescribe("应用名称")
		private String appName;
		@FieldDescribe("脚本Id")
		private String scriptId;
		@FieldDescribe("脚本名称")
		private String scriptName;
		@FieldDescribe("匹配行")
		private List<Integer> patternLines;

		public String getModuleType() {
			return moduleType;
		}

		public void setModuleType(String moduleType) {
			this.moduleType = moduleType;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getScriptId() {
			return scriptId;
		}

		public void setScriptId(String scriptId) {
			this.scriptId = scriptId;
		}

		public String getScriptName() {
			return scriptName;
		}

		public void setScriptName(String scriptName) {
			this.scriptName = scriptName;
		}

		public List<Integer> getPatternLines() {
			return patternLines;
		}

		public void setPatternLines(List<Integer> patternLines) {
			this.patternLines = patternLines;
		}
	}

}
