package com.x.program.center.jaxrs.agent;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.program.center.AgentEvalResources;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Agent;

class ActionExecute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecute.class);

	private static final CopyOnWriteArrayList<String> LOCK = new CopyOnWriteArrayList<>();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);
		ActionResult<Wo> result = new ActionResult<>();
		Agent agent = null;
		agent = getAgent(effectivePerson, flag);
		if (LOCK.contains(agent.getId())) {
			throw new ExceptionAgentLastNotEnd(agent.getId(), agent.getName(), agent.getAlias(),
					DateTools.format(agent.getLastStartTime()));
		}
		try {
			LOCK.add(agent.getId());
			this.stampLastStartTime(agent.getId());
			GraalvmScriptingFactory.Bindings bindings = getBindingResource();
			CacheCategory cacheCategory = new CacheCategory(Agent.class);
			CacheKey cacheKey = new CacheKey(ActionExecute.class, agent.getId());
			Source source = getSource(cacheCategory, cacheKey, agent);
			eval(source, bindings, agent);
		} finally {
			LOCK.remove(agent.getId());
		}
		this.stampLastEndTime(agent.getId());
		Wo wo = new Wo();
		wo.setId(agent.getId());
		result.setData(wo);
		return result;
	}

	private void eval(Source source, GraalvmScriptingFactory.Bindings bindings, Agent agent)
			throws ExceptionAgentExecute {
		try {
			GraalvmScriptingFactory.eval(source, bindings);
		} catch (Exception e) {
			throw new ExceptionAgentExecute(e, agent.getId(), agent.getName());
		}
	}

	private GraalvmScriptingFactory.Bindings getBindingResource() throws Exception {
		AgentEvalResources resources = new AgentEvalResources();
		resources.setContext(ThisApplication.context());
		resources.setOrganization(new Organization(ThisApplication.context()));
		resources.setApplications(ThisApplication.context().applications());
		resources.setWebservicesClient(new WebservicesClient());
		return new GraalvmScriptingFactory.Bindings().putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_RESOURCES,
				resources);
	}

	private Source getSource(CacheCategory cacheCategory, CacheKey cacheKey, Agent agent) {
		Source source = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			source = GraalvmScriptingFactory.functionalization(agent.getText());
			CacheManager.put(cacheCategory, cacheKey, source);
		}
		return source;
	}

	private Agent getAgent(EffectivePerson effectivePerson, String flag) throws Exception {
		Agent agent;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/* 判断当前用户是否有权限访问 */
			if (!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			agent = emc.flag(flag, Agent.class);
			if (null == agent) {
				throw new ExceptionAgentNotExist(flag);
			}
		}
		return agent;
	}

	private void stampLastStartTime(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Agent agent = emc.find(id, Agent.class);
			if (null != agent) {
				emc.beginTransaction(Agent.class);
				agent.setLastStartTime(new Date());
				emc.commit();
			}
		}
	}

	private void stampLastEndTime(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Agent agent = emc.find(id, Agent.class);
			if (null != agent) {
				emc.beginTransaction(Agent.class);
				agent.setLastEndTime(new Date());
				emc.commit();
			}
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 1334633437933937791L;

	}

}
