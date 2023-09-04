package com.x.processplatform.assemble.designer.jaxrs.designer;

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
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WiDesigner;
import com.x.base.core.project.jaxrs.WrapDesigner;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;

class ActionSearch extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSearch.class);
	private static final String DESIGN_PROCESS_ROUTE = "route";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		LOGGER.debug("{}开始流程平台设计搜索，关键字：{}", effectivePerson.getDistinguishedName(), wi.getKeyword());
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
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.process.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.process.toString()))) {
			resWos.addAll(searchProcess(wi, appList, designerMap.get(DesignerType.process.toString())));
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
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.application_FIELDNAME, appIdList);
				} else {
					woScripts = emc.fetchAll(Script.class, WoScript.copier);
				}
				for (WoScript woScript : woScripts) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoScript.copier.getCopyFields(), woScript,
							wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Application app = emc.fetch(woScript.getApplication(), Application.class,
								ListTools.toList(Application.id_FIELDNAME, Application.name_FIELDNAME));
						if (app != null) {
							wo.setAppId(app.getId());
							wo.setAppName(app.getName());
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
					formIds = business.form().listWithApplications(appIdList);
				}
				for (List<String> partFormIds : ListTools.batch(formIds, 100)) {
					List<WoForm> woForms = emc.fetchIn(Form.class, WoForm.copier, JpaObject.id_FIELDNAME, partFormIds);
					for (WoForm woForm : woForms) {
						Map<String, String> map = PropertyTools.fieldMatchKeyword(WoForm.copier.getCopyFields(), woForm,
								wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
						if (!map.isEmpty()) {
							Wo wo = new Wo();
							Application app = emc.fetch(woForm.getApplication(), Application.class,
									ListTools.toList(JpaObject.id_FIELDNAME, Application.name_FIELDNAME));
							if (app != null) {
								wo.setAppId(app.getId());
								wo.setAppName(app.getName());
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

	private List<Wo> searchProcess(final Wi wi, final List<String> appIdList, final List<String> designerIdList) {
		List<List<String>> batchList = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> processIds = designerIdList;
			if (ListTools.isEmpty(processIds)) {
				Business business = new Business(emc);
				processIds = business.process().listWithApplications(appIdList);
			}
			batchList = ListTools.batch(processIds, 20);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		List<Wo> resWos = new ArrayList<>();
		for (List<String> partProcessIds : batchList) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<Process> processList = emc.list(Process.class, partProcessIds);
				for (Process process : processList) {
					Wo wo = doProcessSearch(business, process, wi);
					if (wo != null) {
						resWos.add(wo);
					}

				}
				processList.clear();
				processList = null;
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return resWos;
	}

	private Wo doProcessSearch(Business business, Process process, Wi wi) throws Exception {
		Wo wo = null;

		if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(process), wi.getCaseSensitive(),
				wi.getMatchWholeWord(), wi.getMatchRegExp())) {
			Map<String, String> map = PropertyTools.fieldMatchKeyword(WrapProcess.outCopier.getCopyFields(), process,
					wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
			if (!map.isEmpty()) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(DesignerType.process.toString(), map);
			}
		}

		List<Agent> agentList = business.entityManagerContainer().list(Agent.class,
				business.agent().listWithProcess(process.getId()));
		for (Agent active : agentList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.agent.toString(), active.getId(), active.getName(), null);
			}
		}
		agentList.clear();

		Begin begin = business.entityManagerContainer().find(business.begin().getWithProcess(process.getId()),
				Begin.class);
		if (begin != null && StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(begin),
				wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())) {
			if (wo == null) {
				wo = this.getProcessWo(business, process);
			}
			wo.addPatternList(ActivityType.begin.toString(), begin.getId(), begin.getName(), null);
		}

		List<Cancel> cancelList = business.entityManagerContainer().list(Cancel.class,
				business.cancel().listWithProcess(process.getId()));
		for (Cancel active : cancelList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.cancel.toString(), active.getId(), active.getName(), null);
			}
		}
		cancelList.clear();

		List<Choice> choiceList = business.entityManagerContainer().list(Choice.class,
				business.choice().listWithProcess(process.getId()));
		for (Choice active : choiceList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.choice.toString(), active.getId(), active.getName(), null);
			}
		}
		choiceList.clear();

		List<Delay> delayList = business.entityManagerContainer().list(Delay.class,
				business.delay().listWithProcess(process.getId()));
		for (Delay active : delayList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.delay.toString(), active.getId(), active.getName(), null);
			}
		}
		delayList.clear();

		List<Embed> embedList = business.entityManagerContainer().list(Embed.class,
				business.embed().listWithProcess(process.getId()));
		for (Embed active : embedList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.embed.toString(), active.getId(), active.getName(), null);
			}
		}
		embedList.clear();

		List<End> endList = business.entityManagerContainer().list(End.class,
				business.end().listWithProcess(process.getId()));
		for (End active : endList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.end.toString(), active.getId(), active.getName(), null);
			}
		}
		endList.clear();

		List<Invoke> invokeList = business.entityManagerContainer().list(Invoke.class,
				business.invoke().listWithProcess(process.getId()));
		for (Invoke active : invokeList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.invoke.toString(), active.getId(), active.getName(), null);
			}
		}
		invokeList.clear();

		List<Manual> manualList = business.entityManagerContainer().list(Manual.class,
				business.manual().listWithProcess(process.getId()));
		for (Manual active : manualList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.manual.toString(), active.getId(), active.getName(), null);
			}
		}
		manualList.clear();

		List<Merge> mergeList = business.entityManagerContainer().list(Merge.class,
				business.merge().listWithProcess(process.getId()));
		for (Merge active : mergeList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.merge.toString(), active.getId(), active.getName(), null);
			}
		}
		mergeList.clear();

		List<Parallel> parallelList = business.entityManagerContainer().list(Parallel.class,
				business.parallel().listWithProcess(process.getId()));
		for (Parallel active : parallelList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.parallel.toString(), active.getId(), active.getName(), null);
			}
		}
		parallelList.clear();

		List<Publish> publishList = business.entityManagerContainer().list(Publish.class,
				business.publish().listWithProcess(process.getId()));
		for (Publish active : publishList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.publish.toString(), active.getId(), active.getName(), null);
			}
		}
		publishList.clear();

		List<Service> serviceList = business.entityManagerContainer().list(Service.class,
				business.service().listWithProcess(process.getId()));
		for (Service active : serviceList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.service.toString(), active.getId(), active.getName(), null);
			}
		}
		serviceList.clear();

		List<Split> splitList = business.entityManagerContainer().list(Split.class,
				business.split().listWithProcess(process.getId()));
		for (Split active : splitList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(ActivityType.split.toString(), active.getId(), active.getName(), null);
			}
		}
		splitList.clear();

		List<Route> routeList = business.entityManagerContainer().list(Route.class,
				business.route().listWithProcess(process.getId()));
		for (Route active : routeList) {
			if (StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(active), wi.getCaseSensitive(),
					wi.getMatchWholeWord(), wi.getMatchRegExp())) {
				if (wo == null) {
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(DESIGN_PROCESS_ROUTE, active.getId(), active.getName(), null);
			}
		}
		routeList.clear();
		return wo;
	}

	private Wo getProcessWo(Business business, Process process) throws Exception {
		Wo wo = new Wo();
		Application app = business.entityManagerContainer().fetch(process.getApplication(), Application.class,
				ListTools.toList(Application.id_FIELDNAME, Application.name_FIELDNAME));
		if (app != null) {
			wo.setAppId(app.getId());
			wo.setAppName(app.getName());
		}
		wo.setDesignerId(process.getId());
		wo.setDesignerName(process.getName());
		wo.setDesignerType(DesignerType.process.toString());
		wo.setUpdateTime(process.getUpdateTime());
		return wo;
	}

	public static class Wi extends WiDesigner {

		private static final long serialVersionUID = -6768489740977800135L;

	}

	public static class Wo extends WrapDesigner {

		private static final long serialVersionUID = 2683891172754271010L;

	}

	public static class WoScript extends Script {

		private static final long serialVersionUID = -3937947631282171475L;

		static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				JpaObject.singularAttributeField(Script.class, true, false), null);

	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = 4233799190302798407L;

		static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				JpaObject.singularAttributeField(Form.class, true, false), null);

	}

	public static class WoProcess extends WrapProcess {

		private static final long serialVersionUID = -8507786999314667403L;

	}

}
