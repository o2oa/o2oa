package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapAgent;
import com.x.processplatform.core.entity.element.wrap.WrapBegin;
import com.x.processplatform.core.entity.element.wrap.WrapCancel;
import com.x.processplatform.core.entity.element.wrap.WrapChoice;
import com.x.processplatform.core.entity.element.wrap.WrapDelay;
import com.x.processplatform.core.entity.element.wrap.WrapEmbed;
import com.x.processplatform.core.entity.element.wrap.WrapEnd;
import com.x.processplatform.core.entity.element.wrap.WrapInvoke;
import com.x.processplatform.core.entity.element.wrap.WrapManual;
import com.x.processplatform.core.entity.element.wrap.WrapMerge;
import com.x.processplatform.core.entity.element.wrap.WrapParallel;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;
import com.x.processplatform.core.entity.element.wrap.WrapRoute;
import com.x.processplatform.core.entity.element.wrap.WrapService;
import com.x.processplatform.core.entity.element.wrap.WrapSplit;

class ActionLeadOut extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLeadOut.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("id:{}.", id);
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
			result.setData(this.get(business, process));
			return result;
		}
	}

	public static class Wo extends WrapProcess {

		private static final long serialVersionUID = 1439909268641168987L;

	}

	private Wo get(Business business, Process process) throws Exception {
		WrapProcess wrap = Wo.outCopier.copy(process);
		wrap.setAgentList(WrapAgent.outCopier.copy(this.list(business, process, Agent.class)));
		List<Begin> begins = this.list(business, process, Begin.class);
		if (ListTools.isNotEmpty(begins)) {
			wrap.setBegin(WrapBegin.outCopier.copy(begins.get(0)));
		}
		wrap.setCancelList(WrapCancel.outCopier.copy(this.list(business, process, Cancel.class)));
		wrap.setChoiceList(WrapChoice.outCopier.copy(this.list(business, process, Choice.class)));
		wrap.setDelayList(WrapDelay.outCopier.copy(this.list(business, process, Delay.class)));
		wrap.setEmbedList(WrapEmbed.outCopier.copy(this.list(business, process, Embed.class)));
		wrap.setEndList(WrapEnd.outCopier.copy(this.list(business, process, End.class)));
		wrap.setInvokeList(WrapInvoke.outCopier.copy(this.list(business, process, Invoke.class)));
		wrap.setManualList(WrapManual.outCopier.copy(this.list(business, process, Manual.class)));
		wrap.setMergeList(WrapMerge.outCopier.copy(this.list(business, process, Merge.class)));
		wrap.setParallelList(WrapParallel.outCopier.copy(this.list(business, process, Parallel.class)));
		wrap.setServiceList(WrapService.outCopier.copy(this.list(business, process, Service.class)));
		wrap.setSplitList(WrapSplit.outCopier.copy(this.list(business, process, Split.class)));
		wrap.setRouteList(WrapRoute.outCopier.copy(this.list(business, process, Route.class)));
		return gson.fromJson(gson.toJson(wrap), Wo.class);
	}

	private <T extends JpaObject> List<T> list(Business business, Process process, Class<T> cls) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.equal(root.get("process"), process.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

}