package com.x.cms.assemble.control.jaxrs.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.enums.DesignerType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WiDesigner;
import com.x.base.core.project.jaxrs.WrapDesigner;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;

class ActionSearch extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSearch.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		LOGGER.debug("{}开始内容管理设计搜索，关键字：{}", effectivePerson.getDistinguishedName(), wi.getKeyword());
		if (StringUtils.isBlank(wi.getKeyword())) {
			throw new ExceptionFieldEmpty("keyword");
		}

		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> resWos = new ArrayList<>();
		List<CompletableFuture<List<Wo>>> list = new ArrayList<>();
		Map<String, List<String>> designerMap = wi.getAppDesigner();
		List<String> appList = wi.getAppIdList();
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.form.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.form.toString()))) {
			list.add(searchForm(wi, appList, designerMap.get(DesignerType.form.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.script.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.script.toString()))) {
			list.add(searchScript(wi, appList, designerMap.get(DesignerType.script.toString())));
		}
		for (CompletableFuture<List<Wo>> cf : list) {
			if (resWos.size() < 50) {
				resWos.addAll(cf.get(60, TimeUnit.SECONDS));
			}
		}
		if (resWos.size() > 50) {
			resWos = resWos.subList(0, 50);
		}
		result.setData(resWos);
		result.setCount((long) resWos.size());
		return result;
	}

	private CompletableFuture<List<Wo>> searchScript(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoScript> woScripts;
				if (ListTools.isNotEmpty(designerIdList)) {
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.id_FIELDNAME, designerIdList);
				} else if (ListTools.isNotEmpty(appIdList)) {
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.appId_FIELDNAME, appIdList);
				} else {
					woScripts = emc.fetchAll(Script.class, WoScript.copier);

				}

				for (WoScript woScript : woScripts) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoScript.copier.getCopyFields(), woScript,
							wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						AppInfo appInfo = emc.fetch(woScript.getAppId(), AppInfo.class,
								ListTools.toList(AppInfo.id_FIELDNAME, AppInfo.appName_FIELDNAME));
						if (appInfo != null) {
							wo.setAppId(appInfo.getId());
							wo.setAppName(appInfo.getAppName());
						}
						wo.setDesignerId(woScript.getId());
						wo.setDesignerName(woScript.getName());
						wo.setDesignerType(DesignerType.script.toString());
						wo.setUpdateTime(woScript.getUpdateTime());
						wo.setPatternList(map);
						resWos.add(wo);
					}
				}
				woScripts.clear();
				woScripts = null;
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<Wo>> searchForm(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> formIds = designerIdList;
				if (ListTools.isEmpty(formIds)) {
					formIds = business.getFormFactory().listByAppIds(appIdList);
				}
				for (List<String> partFormIds : ListTools.batch(formIds, 100)) {
					List<WoForm> woForms = emc.fetchIn(Form.class, WoForm.copier, Form.id_FIELDNAME, partFormIds);
					for (WoForm woForm : woForms) {
						Map<String, String> map = PropertyTools.fieldMatchKeyword(WoForm.copier.getCopyFields(), woForm,
								wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
						if (!map.isEmpty()) {
							Wo wo = new Wo();
							AppInfo appInfo = emc.fetch(woForm.getAppId(), AppInfo.class,
									ListTools.toList(AppInfo.id_FIELDNAME, AppInfo.appName_FIELDNAME));
							if (appInfo != null) {
								wo.setAppId(appInfo.getId());
								wo.setAppName(appInfo.getAppName());
							}
							wo.setDesignerId(woForm.getId());
							wo.setDesignerName(woForm.getName());
							wo.setDesignerType(DesignerType.form.toString());
							wo.setUpdateTime(woForm.getUpdateTime());
							wo.setPatternList(map);
							resWos.add(wo);
						}
					}
					woForms.clear();
					woForms = null;
				}

			} catch (Exception e) {
				LOGGER.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	public static class Wi extends WiDesigner {

	}

	public static class Wo extends WrapDesigner {

	}

	public static class WoScript extends Script {

		static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				JpaObject.singularAttributeField(Script.class, true, false), null);

	}

	public static class WoForm extends Form {

		static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				JpaObject.singularAttributeField(Form.class, true, false), null);

	}

}
