package com.x.processplatform.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.wrap.*;

class ActionPrepareCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		// logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = this.adjustForCreate(business, wi);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> adjustForCreate(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Application exist_application = business.entityManagerContainer().find(wi.getId(), Application.class);
		if (null != exist_application) {
			wos.add(new Wo(wi.getId(), JpaObject.createId()));
		}
		for (WrapForm wrap : wi.getFormList()) {
			Form exist_form = business.entityManagerContainer().find(wrap.getId(), Form.class);
			if (null != exist_form) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
			for (WrapFormField wrapFormField : wrap.getFormFieldList()) {
				FormField exist_formField = business.entityManagerContainer().find(wrapFormField.getId(),
						FormField.class);
				if (null != exist_formField) {
					wos.add(new Wo(wrapFormField.getId(), JpaObject.createId()));
				}
			}
		}
		for (WrapScript wrap : wi.getScriptList()) {
			Script exist_script = business.entityManagerContainer().find(wrap.getId(), Script.class);
			if (null != exist_script) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapFile wrap : wi.getFileList()) {
			File exist_file = business.entityManagerContainer().find(wrap.getId(), File.class);
			if (null != exist_file) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapApplicationDict wrap : wi.getApplicationDictList()) {
			ApplicationDict exist_applicationDict = business.entityManagerContainer().find(wrap.getId(),
					ApplicationDict.class);
			if (null != exist_applicationDict) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapProcess wrapProcess : wi.getProcessList()) {
			Process exist_process = business.entityManagerContainer().find(wrapProcess.getId(), Process.class);
			if (null != exist_process) {
				wos.add(new Wo(wrapProcess.getId(), JpaObject.createId()));
			}
			for (WrapAgent wrap : wrapProcess.getAgentList()) {
				Agent exist_agent = business.entityManagerContainer().find(wrap.getId(), Agent.class);
				if (null != exist_agent) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			if (null != wrapProcess.getBegin()) {
				Begin exist_begin = business.entityManagerContainer().find(wrapProcess.getBegin().getId(), Begin.class);
				if (null != exist_begin) {
					wos.add(new Wo(wrapProcess.getBegin().getId(), JpaObject.createId()));
				}
			}
			for (WrapCancel wrap : wrapProcess.getCancelList()) {
				Cancel exist_cancel = business.entityManagerContainer().find(wrap.getId(), Cancel.class);
				if (null != exist_cancel) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapChoice wrap : wrapProcess.getChoiceList()) {
				Choice exist_choice = business.entityManagerContainer().find(wrap.getId(), Choice.class);
				if (null != exist_choice) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapDelay wrap : wrapProcess.getDelayList()) {
				Delay exist_delay = business.entityManagerContainer().find(wrap.getId(), Delay.class);
				if (null != exist_delay) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapEmbed wrap : wrapProcess.getEmbedList()) {
				Embed exist_embed = business.entityManagerContainer().find(wrap.getId(), Embed.class);
				if (null != exist_embed) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapEnd wrap : wrapProcess.getEndList()) {
				End exist_end = business.entityManagerContainer().find(wrap.getId(), End.class);
				if (null != exist_end) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapInvoke wrap : wrapProcess.getInvokeList()) {
				Invoke exist_invoke = business.entityManagerContainer().find(wrap.getId(), Invoke.class);
				if (null != exist_invoke) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapManual wrap : wrapProcess.getManualList()) {
				Manual exist_manual = business.entityManagerContainer().find(wrap.getId(), Manual.class);
				if (null != exist_manual) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapMerge wrap : wrapProcess.getMergeList()) {
				Merge exist_merge = business.entityManagerContainer().find(wrap.getId(), Merge.class);
				if (null != exist_merge) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapParallel wrap : wrapProcess.getParallelList()) {
				Parallel exist_parallel = business.entityManagerContainer().find(wrap.getId(), Parallel.class);
				if (null != exist_parallel) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapPublish wrap : wrapProcess.getPublishList()) {
				Publish exist_parallel = business.entityManagerContainer().find(wrap.getId(), Publish.class);
				if (null != exist_parallel) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapService wrap : wrapProcess.getServiceList()) {
				Service exist_service = business.entityManagerContainer().find(wrap.getId(), Service.class);
				if (null != exist_service) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapSplit wrap : wrapProcess.getSplitList()) {
				Split exist_split = business.entityManagerContainer().find(wrap.getId(), Split.class);
				if (null != exist_split) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
			for (WrapRoute wrap : wrapProcess.getRouteList()) {
				Route exist_route = business.entityManagerContainer().find(wrap.getId(), Route.class);
				if (null != exist_route) {
					wos.add(new Wo(wrap.getId(), JpaObject.createId()));
				}
			}
		}
		return wos;
	}

	public static class Wi extends WrapProcessPlatform {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WrapPair {
		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}

	}

}
