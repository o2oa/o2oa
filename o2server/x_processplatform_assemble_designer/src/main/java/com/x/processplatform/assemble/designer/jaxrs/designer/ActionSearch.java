package com.x.processplatform.assemble.designer.jaxrs.designer;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.enums.DesignerType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
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
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.wrap.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class ActionSearch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSearch.class);
	private final static String DESIGN_PROCESS_ACTIVITY = "activity";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if(!effectivePerson.isManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		logger.debug("{}开始流程平台设计搜索，关键字：{}", effectivePerson.getDistinguishedName(), wi.getKeyword());
		if(StringUtils.isBlank(wi.getKeyword())){
			throw new ExceptionFieldEmpty("keyword");
		}
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> resWos = new ArrayList<>();
		List<CompletableFuture<List<Wo>>> list = new ArrayList<>();
		Map<String, List<String>> designerMap = wi.getAppDesigner();
		List<String> appList = wi.getAppIdList();
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.form.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.form.toString()))){
			list.add(searchForm(wi, appList, designerMap.get(DesignerType.form.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.script.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.script.toString()))){
			list.add(searchScript(wi, appList, designerMap.get(DesignerType.script.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.process.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.process.toString()))){
			resWos.addAll(searchProcess(wi, appList, designerMap.get(DesignerType.process.toString())));
		}
		for (CompletableFuture<List<Wo>> cf : list){
			if(resWos.size()<50) {
				resWos.addAll(cf.get(60, TimeUnit.SECONDS));
			}
		}
		if (resWos.size()>50){
			resWos = resWos.subList(0, 50);
		}
		result.setData(resWos);
		result.setCount((long)resWos.size());
		return result;
	}

	private CompletableFuture<List<Wo>> searchScript(final Wi wi, final List<String> appIdList, final List<String> designerIdList) {
		CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoScript> woScripts;
				if (ListTools.isNotEmpty(designerIdList)) {
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.id_FIELDNAME, designerIdList);
				}else if (ListTools.isNotEmpty(appIdList)) {
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.application_FIELDNAME, appIdList);
				} else {
					woScripts = emc.fetchAll(Script.class, WoScript.copier);
				}
				for (WoScript woScript : woScripts) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoScript.copier.getCopyFields(), woScript, wi.getKeyword(),
							wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
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
			}catch (Exception e){
				logger.error(e);
			}
			return resWos;
		});
		return cf;
	}

	private CompletableFuture<List<Wo>> searchForm(final Wi wi, final List<String> appIdList, final List<String> designerIdList) {
		CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> formIds = designerIdList;
				if(ListTools.isEmpty(formIds)) {
					formIds = business.form().listWithApplications(appIdList);
				}
				for (List<String> partFormIds : ListTools.batch(formIds, 100)) {
					List<WoForm> woForms = emc.fetchIn(Form.class, WoForm.copier, Form.id_FIELDNAME, partFormIds);
					for (WoForm woForm : woForms) {
						Map<String, String> map = PropertyTools.fieldMatchKeyword(WoForm.copier.getCopyFields(), woForm, wi.getKeyword(),
								wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
						if (!map.isEmpty()) {
							Wo wo = new Wo();
							Application app = emc.fetch(woForm.getApplication(), Application.class,
									ListTools.toList(Application.id_FIELDNAME, Application.name_FIELDNAME));
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

			}catch (Exception e){
				logger.error(e);
			}
			return resWos;
		});
		return cf;
	}

	private List<Wo> searchProcess(final Wi wi, final List<String> appIdList, final List<String> designerIdList) {
		List<List<String>> batchList = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> processIds = designerIdList;
			if(ListTools.isEmpty(processIds)) {
				Business business = new Business(emc);
				processIds = business.process().listWithApplications(appIdList);
			}
			batchList = ListTools.batch(processIds, 20);
		}catch (Exception e){
			logger.error(e);
		}
		Executor executor = Executors.newFixedThreadPool(batchList.size());
		List<CompletableFuture<List<Wo>>> cfList = new ArrayList<>();
		for (List<String> partProcessIds : batchList) {
			CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
				List<Wo> resWos = new ArrayList<>();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<Process> processList = emc.list(Process.class, partProcessIds);
					for (Process process : processList) {
						try {
							Wo wo = doProcessSearch(business, process, wi);
							if (wo!=null){
								resWos.add(wo);
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
					processList.clear();
					processList = null;
				}catch (Exception e){
					logger.error(e);
				}
				return resWos;
			}, executor);
			cfList.add(cf);
		}
		List<Wo> woList = new ArrayList<>();
		for (CompletableFuture<List<Wo>> cf : cfList){
			try {
				woList.addAll(cf.get(30, TimeUnit.SECONDS));
			} catch (Exception e){
				logger.error(e);
			}
		}
		return woList;
	}


	private Wo doProcessSearch(Business business, Process process, Wi wi) throws Exception {
		Wo wo = null;

		Map<String, String> map;
		if(!StringTools.matchKeyword(wi.getKeyword(), XGsonBuilder.toJson(process), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())) {
			map = PropertyTools.fieldMatchKeyword(WrapProcess.outCopier.getCopyFields(), process, wi.getKeyword(),
					wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
			if (!map.isEmpty()) {
				if(wo == null){
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(DesignerType.process.toString(), map);
			}
		}

		List<Agent> agentList = business.entityManagerContainer().list(Agent.class,
				business.agent().listWithProcess(process.getId()));
		if(!agentList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(agentList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Agent active : agentList){
				map = PropertyTools.fieldMatchKeyword(WrapAgent.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		agentList.clear();
		agentList = null;

		Begin begin = business.entityManagerContainer().find(business.begin().getWithProcess(process.getId()), Begin.class);
		if(begin != null && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(begin), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			Begin active = begin;
			map = PropertyTools.fieldMatchKeyword(WrapBegin.outCopier.getCopyFields(), active, wi.getKeyword(),
					wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
			if (!map.isEmpty()) {
				if(wo == null){
					wo = this.getProcessWo(business, process);
				}
				wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
			}
		}

		List<Cancel> cancelList =  business.entityManagerContainer().list(Cancel.class,
				business.cancel().listWithProcess(process.getId()));
		if(!cancelList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(cancelList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Cancel active : cancelList){
				map = PropertyTools.fieldMatchKeyword(WrapCancel.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		cancelList.clear();
		cancelList = null;

		List<Choice> choiceList =  business.entityManagerContainer().list(Choice.class,
				business.choice().listWithProcess(process.getId()));
		if(!choiceList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(choiceList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Choice active : choiceList){
				map = PropertyTools.fieldMatchKeyword(WrapChoice.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		choiceList.clear();
		choiceList = null;

		List<Delay> delayList =  business.entityManagerContainer().list(Delay.class,
				business.delay().listWithProcess(process.getId()));
		if(!delayList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(delayList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Delay active : delayList){
				map = PropertyTools.fieldMatchKeyword(WrapDelay.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		delayList.clear();
		delayList = null;

		List<Embed> embedList = business.entityManagerContainer().list(Embed.class,
				business.embed().listWithProcess(process.getId()));
		if(!embedList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(embedList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Embed active : embedList){
				map = PropertyTools.fieldMatchKeyword(WrapEmbed.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		embedList.clear();
		embedList = null;

		List<End> endList = business.entityManagerContainer().list(End.class, business.end().listWithProcess(process.getId()));
		if(!endList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(endList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (End active : endList){
				map = PropertyTools.fieldMatchKeyword(WrapEnd.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		endList.clear();
		endList = null;

		List<Invoke> invokeList = business.entityManagerContainer().list(Invoke.class, business.invoke().listWithProcess(process.getId()));
		if(!invokeList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(invokeList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Invoke active : invokeList){
				map = PropertyTools.fieldMatchKeyword(WrapInvoke.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		invokeList.clear();
		invokeList = null;

		List<Manual> manualList = business.entityManagerContainer().list(Manual.class, business.manual().listWithProcess(process.getId()));
		if(!manualList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(manualList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Manual active : manualList){
				map = PropertyTools.fieldMatchKeyword(WrapManual.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		manualList.clear();
		manualList = null;

		List<Merge> mergeList = business.entityManagerContainer().list(Merge.class, business.merge().listWithProcess(process.getId()));
		if(!mergeList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(mergeList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Merge active : mergeList){
				map = PropertyTools.fieldMatchKeyword(WrapMerge.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		mergeList.clear();
		mergeList = null;

		List<Message> messageList = business.entityManagerContainer().list(Message.class, business.message().listWithProcess(process.getId()));
		if(!messageList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(messageList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Message active : messageList){
				map = PropertyTools.fieldMatchKeyword(WrapMessage.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		messageList.clear();
		messageList = null;

		List<Parallel> parallelList = business.entityManagerContainer().list(Parallel.class, business.parallel().listWithProcess(process.getId()));
		if(!parallelList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(parallelList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Parallel active : parallelList){
				map = PropertyTools.fieldMatchKeyword(WrapParallel.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		parallelList.clear();
		parallelList = null;

		List<Service> serviceList = business.entityManagerContainer().list(Service.class, business.service().listWithProcess(process.getId()));
		if(!serviceList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(serviceList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Service active : serviceList){
				map = PropertyTools.fieldMatchKeyword(WrapService.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		serviceList.clear();
		serviceList = null;

		List<Split> splitList = business.entityManagerContainer().list(Split.class, business.split().listWithProcess(process.getId()));
		if(!splitList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(splitList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Split active : splitList){
				map = PropertyTools.fieldMatchKeyword(WrapSplit.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		splitList.clear();
		splitList = null;

		List<Route> routeList = business.entityManagerContainer().list(Route.class, business.route().listWithProcess(process.getId()));
		if(!routeList.isEmpty() && StringTools.matchKeyword(wi.getKeyword(),
				XGsonBuilder.toJson(routeList), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp())){
			if(wo == null){
				wo = this.getProcessWo(business, process);
			}
			for (Route active : routeList){
				map = PropertyTools.fieldMatchKeyword(WrapRoute.outCopier.getCopyFields(), active, wi.getKeyword(),
						wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
				if (!map.isEmpty()) {
					if(wo == null){
						wo = this.getProcessWo(business, process);
					}
					wo.addPatternList(DESIGN_PROCESS_ACTIVITY, active.getId(), active.getName(), map);
				}
			}
		}
		routeList.clear();
		routeList = null;

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

	}

	public static class Wo extends WrapDesigner{

	}

	public static class WoScript extends Script {

		static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				JpaObject.singularAttributeField(Script.class, true, false),null);

	}

	public static class WoForm extends Form {

		static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				JpaObject.singularAttributeField(Form.class, true, false),null);

	}

	public static class WoProcess extends WrapProcess {

		private static final long serialVersionUID = -8507786999314667403L;

	}

}
