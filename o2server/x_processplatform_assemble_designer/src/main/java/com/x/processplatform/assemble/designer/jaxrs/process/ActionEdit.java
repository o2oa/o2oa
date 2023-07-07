package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.Date;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;

class ActionEdit extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		WrapProcess wrap = this.convertToWrapIn(jsonElement, WrapProcess.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(id, Process.class);
			if (null == process) {
				throw new ExceptionProcessNotExisted(id);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(process.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			Process oldProcess = (Process) BeanUtils.cloneBean(process);
			emc.beginTransaction(Process.class);
			emc.beginTransaction(Agent.class);
			emc.beginTransaction(Begin.class);
			emc.beginTransaction(Cancel.class);
			emc.beginTransaction(Choice.class);
			emc.beginTransaction(Delay.class);
			emc.beginTransaction(Embed.class);
			emc.beginTransaction(End.class);
			emc.beginTransaction(Invoke.class);
			emc.beginTransaction(Manual.class);
			emc.beginTransaction(Merge.class);
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Publish.class);
			emc.beginTransaction(Route.class);
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			WrapProcess.inCopier.copy(wrap, process);
			this.updateCreatePersonLastUpdatePerson(effectivePerson, business, process);
			this.updateEdition(oldProcess, process);
			process.setLastUpdateTime(new Date());
			emc.check(process, CheckPersistType.all);
			updateAgent(business, wrap.getAgentList(), process);
			updateBegin(business, wrap.getBegin(), process);
			updateCancel(business, wrap.getCancelList(), process);
			updateChoice(business, wrap.getChoiceList(), process);
			updateDelay(business, wrap.getDelayList(), process);
			updateEmbed(business, wrap.getEmbedList(), process);
			updateEnd(business, wrap.getEndList(), process);
			updateInvoke(business, wrap.getInvokeList(), process);
			updateManual(business, wrap.getManualList(), process);
			updateMerge(business, wrap.getMergeList(), process);
			updateParallel(business, wrap.getParallelList(), process);
			updatePublish(business, wrap.getPublishList(), process);
			updateRoute(business, wrap.getRouteList(), process);
			updateService(business, wrap.getServiceList(), process);
			updateSplit(business, wrap.getSplitList(), process);
			emc.commit();
			cacheNotify();
			// 保存历史版本
			ThisApplication.processVersionQueue.send(new ProcessVersion(process.getId(), jsonElement, effectivePerson.getDistinguishedName()));
			Wo wo = new Wo();
			wo.setId(process.getId());
			result.setData(wo);
			MessageFactory.process_update(process);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -696493651089351788L;

	}

	private void updateCreatePersonLastUpdatePerson(EffectivePerson effectivePerson, Business business, Process process)
			throws Exception {
		process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		String name = business.organization().person().get(process.getCreatorPerson());
		if (StringUtils.isEmpty(name)) {
			process.setCreatorPerson(effectivePerson.getDistinguishedName());
		} else {
			process.setCreatorPerson(name);
		}
	}

	private void updateEdition(Process oldProcess, Process process) {
		// 更新流程时保持流程的版本信息不变，但当不存在版本信息则添加版本信息
		if (StringUtils.isEmpty(oldProcess.getEdition())) {
			process.setEdition(process.getId());
			process.setEditionEnable(true);
			process.setEditionNumber(1.0);
			process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
		} else {
			process.setEdition(oldProcess.getEdition());
			process.setEditionEnable(oldProcess.getEditionEnable());
			process.setEditionNumber(oldProcess.getEditionNumber());
			process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
		}
	}
}
