package com.x.processplatform.service.processing.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Activity;
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
import com.x.processplatform.core.entity.element.Mapping;
import com.x.processplatform.core.entity.element.Mapping_;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Route_;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Script_;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class ElementFactory extends AbstractFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElementFactory.class);

	public ElementFactory(Business business) throws Exception {
		super(business);
	}

	// 取得属于指定Process 的设计元素
	@SuppressWarnings("unchecked")
	public <T extends JpaObject> List<T> listWithProcess(Class<T> clz, Process process) throws Exception {
		List<T> list = new ArrayList<>();
		CacheCategory cacheCategory = new CacheCategory(clz);
		CacheKey cacheKey = new CacheKey("listWithProcess", process.getId());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			list = (List<T>) optional.get();
		} else {
			EntityManager em = this.entityManagerContainer().get(clz);
			List<T> os = this.entityManagerContainer().listEqual(clz, Activity.process_FIELDNAME, process.getId());
			for (T t : os) {
				em.detach(t);
				list.add(t);
			}
			// 将object改为unmodifiable
			list = Collections.unmodifiableList(list);
			CacheManager.put(cacheCategory, cacheKey, list);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends JpaObject> T get(String id, Class<T> clz) throws Exception {
		CacheCategory cacheCategory = new CacheCategory(clz);
		CacheKey cacheKey = new CacheKey(id);
		T t = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			t = (T) optional.get();
		} else {
			t = this.entityManagerContainer().find(id, clz);
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
				CacheManager.put(cacheCategory, cacheKey, t);
			}
		}
		return t;
	}

	public Activity getActivity(String id) throws Exception {
		Activity activity = null;
		activity = this.get(id, ActivityType.manual);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.begin);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.cancel);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.choice);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.delay);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.embed);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.split);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.invoke);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.agent);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.merge);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.parallel);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.publish);
		if (null != activity) {
			return activity;
		}
		activity = this.get(id, ActivityType.service);
		if (null != activity) {
			return activity;
		}
		return this.get(id, ActivityType.end);
	}

	public Activity get(String id, ActivityType activityType) throws Exception {
		switch (activityType) {
		case agent:
			return this.get(id, Agent.class);
		case begin:
			return this.get(id, Begin.class);
		case cancel:
			return this.get(id, Cancel.class);
		case choice:
			return this.get(id, Choice.class);
		case delay:
			return this.get(id, Delay.class);
		case embed:
			return this.get(id, Embed.class);
		case end:
			return this.get(id, End.class);
		case invoke:
			return this.get(id, Invoke.class);
		case manual:
			return this.get(id, Manual.class);
		case merge:
			return this.get(id, Merge.class);
		case parallel:
			return this.get(id, Parallel.class);
		case publish:
			return this.get(id, Publish.class);
		case service:
			return this.get(id, Service.class);
		case split:
			return this.get(id, Split.class);
		default:
			return null;
		}
	}

	// 用Process的updateTime作为缓存值
	public Begin getBeginWithProcess(String id) throws Exception {
		Begin begin = null;
		CacheCategory cacheCategory = new CacheCategory(Begin.class);
		CacheKey cacheKey = new CacheKey("getBeginWithProcess", id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			begin = (Begin) optional.get();
		} else {
			begin = this.entityManagerContainer().firstEqual(Begin.class, Activity.process_FIELDNAME, id);
			if (begin != null) {
				this.entityManagerContainer().get(Begin.class).detach(begin);
				CacheManager.put(cacheCategory, cacheKey, begin);
			}
		}
		return begin;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithChoice(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		CacheCategory cacheCategory = new CacheCategory(Route.class);
		CacheKey cacheKey = new CacheKey(id, Choice.class.getName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			list = (List<Route>) optional.get();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Choice choice = this.get(id, Choice.class);
			if (null != choice) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(choice.getRouteList());
				list = em.createQuery(cq.where(p).orderBy(cb.asc(root.get(Route_.orderNumber)))).getResultList();
				for (Route route : list) {
					em.detach(route);
				}
				CacheManager.put(cacheCategory, cacheKey, list);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithManual(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		CacheCategory cacheCategory = new CacheCategory(Route.class);
		CacheKey cacheKey = new CacheKey(id, Manual.class.getName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			list = (List<Route>) optional.get();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Manual manual = this.get(id, Manual.class);
			if (null != manual) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(manual.getRouteList());
				list = em.createQuery(cq.where(p).orderBy(cb.asc(root.get(Route_.orderNumber)))).getResultList();
				for (Route route : list) {
					em.detach(route);
				}
				CacheManager.put(cacheCategory, cacheKey, list);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithParallel(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		CacheCategory cacheCategory = new CacheCategory(Route.class);
		CacheKey cacheKey = new CacheKey(id, Parallel.class.getName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			list = (List<Route>) optional.get();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Parallel parallel = this.get(id, Parallel.class);
			if (null != parallel) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(parallel.getRouteList());
				list = em.createQuery(cq.where(p).orderBy(cb.asc(root.get(Route_.orderNumber)))).getResultList();
				for (Route route : list) {
					em.detach(route);
				}
				CacheManager.put(cacheCategory, cacheKey, list);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Script> listScriptNestedWithApplicationWithUniqueName(String applicationId, String uniqueName)
			throws Exception {
		List<Script> list = new ArrayList<>();
		CacheCategory cacheCategory = new CacheCategory(Script.class);
		CacheKey cacheKey = new CacheKey("listScriptNestedWithApplicationWithUniqueName", applicationId, uniqueName);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			list = (List<Script>) optional.get();
		} else {
			List<String> names = new ArrayList<>();
			names.add(uniqueName);
			while (!names.isEmpty()) {
				List<String> loops = new ArrayList<>();
				for (String name : names) {
					Script o = this.getScriptWithApplicationWithUniqueName(applicationId, name);
					if (null != o) {
						this.entityManagerContainer().get(Script.class).detach(o);
						if (!list.contains(o)) {
							list.add(o);
							loops.addAll(o.getDependScriptList());
						}
					}
				}
				names = loops;
			}
			Collections.reverse(list);
			CacheManager.put(cacheCategory, cacheKey, list);
		}
		return list;
	}

	private Script getScriptWithApplicationWithUniqueName(String applicationId, String uniqueName) throws Exception {
		Script script = null;
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), uniqueName);
		p = cb.or(p, cb.equal(root.get(Script_.alias), uniqueName));
		p = cb.or(p, cb.equal(root.get(Script_.id), uniqueName));
		p = cb.and(p, cb.equal(root.get(Script_.application), applicationId));
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			script = list.get(0);
		}
		return script;
	}

	public List<Route> listRouteWithActvity(String id, ActivityType activityType) throws Exception {
		List<Route> list = new ArrayList<>();
		// 在执行beforeArrive时如果在begin环节设置了脚本,那么返回为空.需要单独判断段
		if (null == activityType) {
			return list;
		}
		switch (activityType) {
		case agent:
			Agent agent = this.get(id, Agent.class);
			list.add(this.get(agent.getRoute(), Route.class));
			break;
		case begin:
			Begin begin = this.get(id, Begin.class);
			list.add(this.get(begin.getRoute(), Route.class));
			break;
		case cancel:
			break;
		case choice:
			Choice choice = this.get(id, Choice.class);
			for (String str : choice.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case delay:
			Delay delay = this.get(id, Delay.class);
			list.add(this.get(delay.getRoute(), Route.class));
			break;
		case embed:
			Embed embed = this.get(id, Embed.class);
			list.add(this.get(embed.getRoute(), Route.class));
			break;
		case end:
			break;
		case invoke:
			Invoke invoke = this.get(id, Invoke.class);
			list.add(this.get(invoke.getRoute(), Route.class));
			break;
		case manual:
			Manual manual = this.get(id, Manual.class);
			for (String str : manual.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case merge:
			Merge merge = this.get(id, Merge.class);
			list.add(this.get(merge.getRoute(), Route.class));
			break;
		case parallel:
			Parallel parallel = this.get(id, Parallel.class);
			for (String str : parallel.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case publish:
			Publish publish = this.get(id, Publish.class);
			for (String str : publish.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case service:
			Service service = this.get(id, Service.class);
			list.add(this.get(service.getRoute(), Route.class));
			break;
		case split:
			Split split = this.get(id, Split.class);
			list.add(this.get(split.getRoute(), Route.class));
			break;
		default:
			break;
		}
		return ListTools.trim(list, true, true);
	}

	public List<String> listFormWithProcess(Process process) throws Exception {
		List<String> ids = new ArrayList<>();
		this.listWithProcess(Agent.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Begin.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Cancel.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Choice.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Delay.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Embed.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(End.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Invoke.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Manual.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Merge.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Parallel.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Publish.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Service.class, process).forEach(o -> ids.add(o.getForm()));
		this.listWithProcess(Split.class, process).forEach(o -> ids.add(o.getForm()));
		return ListTools.trim(ids, true, true);
	}

	@SuppressWarnings("unchecked")
	public List<Mapping> listMappingEffectiveWithApplicationAndProcess(String application, String process)
			throws Exception {
		final List<Mapping> list = new ArrayList<>();
		CacheCategory cacheCategory = new CacheCategory(Mapping.class);
		CacheKey cacheKey = new CacheKey("listMappingEffectiveWithApplicationAndProcess", application, process);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			list.addAll((List<Mapping>) optional.get());
		} else {
			EntityManager em = this.entityManagerContainer().get(Mapping.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Mapping> cq = cb.createQuery(Mapping.class);
			Root<Mapping> root = cq.from(Mapping.class);
			Predicate p = cb.equal(root.get(Mapping_.enable), true);
			p = cb.and(p, cb.equal(root.get(Mapping_.application), application));
			p = cb.and(p, cb.or(cb.equal(root.get(Mapping_.process), process), cb.equal(root.get(Mapping_.process), ""),
					cb.isNull(root.get(Mapping_.process))));
			List<Mapping> os = em.createQuery(cq.where(p)).getResultList();
			for (Mapping mapping : os) {
				em.detach(mapping);
			}
			os.stream().collect(Collectors.groupingBy(o -> o.getApplication() + o.getTableName()))
					.forEach((k, v) -> list.add(v.stream().filter(i -> StringUtils.isNotEmpty(i.getProcess()))
							.findFirst().orElse(v.get(0))));
			CacheManager.put(cacheCategory, cacheKey, list);
		}
		return list;
	}

	private static final String GETCOMPILEDSCRIPT = "getCompiledScript";

	public Source getCompiledScript(String applicationId, Activity o, String event) throws Exception {
		CacheCategory cacheCategory = new CacheCategory(o.getClass(), Script.class);
		CacheKey cacheKey = new CacheKey(GETCOMPILEDSCRIPT, applicationId, o.getId(), event);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		Source source = null;
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			String scriptName = null;
			String scriptText = null;
			switch (event) {
			case Business.EVENT_BEFOREARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.afterArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.afterArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREEXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTEREXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.afterExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.afterExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.beforeInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.afterInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.afterInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALTASKEXPIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.taskExpireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.taskExpireScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALTASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.taskScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.taskScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALSTAY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.manualStayScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.manualStayScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALBEFORETASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.manualBeforeTaskScript_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Manual.manualBeforeTaskScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALAFTERTASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.manualAfterTaskScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.manualAfterTaskScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALAFTERPROCESSING:
				scriptName = Objects
						.toString(PropertyUtils.getProperty(o, Manual.MANUALAFTERPROCESSINGSCRIPT_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Manual.MANUALAFTERPROCESSINGSCRIPTTEXT_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXWSPARAMETER:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsParameterScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsParameterScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSPARAMETER:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsParameterScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsParameterScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXWSRESPONSE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsResponseScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxwsResponseScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSRESPONSE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsResponseScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsResponseScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSBODY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsBodyScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsBodyScriptText_FIELDNAME));
				break;
			case Business.EVENT_PUBLISHCMSBODY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Publish.targetAssignDataScript_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Publish.targetAssignDataScriptText_FIELDNAME));
				break;
			case Business.EVENT_PUBLISHCMSCREATOR:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Publish.cmsCreatorScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Publish.cmsCreatorScriptText_FIELDNAME));
				break;
			case Business.EVENT_INVOKEJAXRSHEAD:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsHeadScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Invoke.jaxrsHeadScriptText_FIELDNAME));
				break;
			case Business.EVENT_READ:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.readScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.readScriptText_FIELDNAME));
				break;
			case Business.EVENT_REVIEW:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Manual.reviewScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Manual.reviewScriptText_FIELDNAME));
				break;
			case Business.EVENT_AGENT:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Agent.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Agent.scriptText_FIELDNAME));
				break;
			case Business.EVENT_SERVICE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Service.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Service.scriptText_FIELDNAME));
				break;
			case Business.EVENT_AGENTINTERRUPT:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Agent.agentInterruptScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Agent.agentInterruptScriptText_FIELDNAME));
				break;
			case Business.EVENT_DELAY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Delay.delayScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Delay.delayScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDTARGETASSIGNDATA:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Embed.targetAssginDataScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Embed.targetAssginDataScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDTARGETIDENTITY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Embed.targetIdentityScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Embed.targetIdentityScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDTARGETTITLE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Embed.targetTitleScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Embed.targetTitleScriptText_FIELDNAME));
				break;
			case Business.EVENT_EMBEDCOMPLETED:
				if (o instanceof Embed) {
					Embed embed = (Embed) o;
					scriptName = embed.getCompletedScript();
					scriptText = embed.getCompletedScriptText();
				}
				break;
			case Business.EVENT_EMBEDCOMPLETEDCANCEL:
				if (o instanceof Embed) {
					Embed embed = (Embed) o;
					scriptName = embed.getCompletedCancelScript();
					scriptText = embed.getCompletedCancelScriptText();
				}
				break;
			case Business.EVENT_EMBEDCOMPLETEDEND:
				if (o instanceof Embed) {
					Embed embed = (Embed) o;
					scriptName = embed.getCompletedEndScript();
					scriptText = embed.getCompletedEndScriptText();
				}
				break;
			case Business.EVENT_SPLIT:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Split.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Split.scriptText_FIELDNAME));
				break;
			default:
				break;
			}
			StringBuilder sb = new StringBuilder();
			try {
				if (StringUtils.isNotEmpty(scriptName)) {
					List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(scriptText)) {
					sb.append(scriptText).append(System.lineSeparator());
				}
				source = GraalvmScriptingFactory.functionalization(sb.toString());
				CacheManager.put(cacheCategory, cacheKey, source);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return source;
	}

	public Source getCompiledScript(String applicationId, String scriptName, String scriptText) {
		StringBuilder sb = new StringBuilder();
		Source source = null;
		try {
			if (StringUtils.isNotEmpty(scriptName)) {
				List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
				for (Script script : list) {
					sb.append(script.getText()).append(System.lineSeparator());
				}
			}
			if (StringUtils.isNotEmpty(scriptText)) {
				sb.append(scriptText).append(System.lineSeparator());
			}
			source = GraalvmScriptingFactory.functionalization(sb.toString());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return source;
	}

	public Source getCompiledScript(String applicationId, Route o, String event) throws Exception {
		CacheCategory cacheCategory = new CacheCategory(Route.class, Script.class);
		CacheKey cacheKey = new CacheKey(GETCOMPILEDSCRIPT, applicationId, o.getId(), event);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		Source source = null;
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			String scriptName = null;
			String scriptText = null;
			switch (event) {
			case Business.EVENT_ROUTEAPPENDTASKIDENTITY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Route.appendTaskIdentityScript_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Route.appendTaskIdentityScriptText_FIELDNAME));
				break;
			case Business.EVENT_ROUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Route.script_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Route.scriptText_FIELDNAME));
				break;
			default:
				break;
			}
			StringBuilder sb = new StringBuilder();
			try {
				if (StringUtils.isNotEmpty(scriptName)) {
					List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(scriptText)) {
					sb.append(scriptText).append(System.lineSeparator());
				}
				source = GraalvmScriptingFactory.functionalization(sb.toString());
				CacheManager.put(cacheCategory, cacheKey, source);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return source;
	}

	public Source getCompiledScript(String applicationId, Process o, String event) throws Exception {
		CacheCategory cacheCategory = new CacheCategory(Process.class, Script.class);
		CacheKey cacheKey = new CacheKey(GETCOMPILEDSCRIPT, applicationId, o.getId(), event);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		Source source = null;
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			String scriptName = null;
			String scriptText = null;
			switch (event) {
			case Business.EVENT_BEFOREARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.beforeArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.beforeArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERARRIVE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterArriveScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterArriveScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREEXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.beforeExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.beforeExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTEREXECUTE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterExecuteScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterExecuteScriptText_FIELDNAME));
				break;
			case Business.EVENT_BEFOREINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.beforeInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.beforeInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_AFTERINQUIRE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterInquireScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterInquireScriptText_FIELDNAME));
				break;
			case Business.EVENT_PROCESSAFTERBEGIN:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterBeginScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterBeginScriptText_FIELDNAME));
				break;
			case Business.EVENT_PROCESSAFTEREND:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.afterEndScript_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.afterEndScriptText_FIELDNAME));
				break;
			case Business.EVENT_MANUALSTAY:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.MANUALSTAYSCRIPT_FIELDNAME));
				scriptText = Objects.toString(PropertyUtils.getProperty(o, Process.MANUALSTAYSCRIPTTEXT_FIELDNAME));
				break;
			case Business.EVENT_MANUALBEFORETASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.MANUALBEFORETASKSCRIPT_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Process.MANUALBEFORETASKSCRIPTTEXT_FIELDNAME));
				break;
			case Business.EVENT_MANUALAFTERTASK:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.MANUALAFTERTASKSCRIPT_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Process.MANUALAFTERTASKSCRIPTTEXT_FIELDNAME));
				break;
			case Business.EVENT_MANUALAFTERPROCESSING:
				scriptName = Objects
						.toString(PropertyUtils.getProperty(o, Process.MANUALAFTERPROCESSINGSCRIPT_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Process.MANUALAFTERPROCESSINGSCRIPTTEXT_FIELDNAME));
				break;
			case Business.EVENT_PERMISSIONWRITE:
				scriptName = Objects.toString(PropertyUtils.getProperty(o, Process.PERMISSIONWRITESCRIPT_FIELDNAME));
				scriptText = Objects
						.toString(PropertyUtils.getProperty(o, Process.PERMISSIONWRITESCRIPTTEXT_FIELDNAME));
				break;
			default:
				break;
			}
			StringBuilder sb = new StringBuilder();
			try {
				if (StringUtils.isNotEmpty(scriptName)) {
					List<Script> list = listScriptNestedWithApplicationWithUniqueName(applicationId, scriptName);
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(scriptText)) {
					sb.append(scriptText).append(System.lineSeparator());
				}
				source = GraalvmScriptingFactory.functionalization(sb.toString());
				CacheManager.put(cacheCategory, cacheKey, source);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return source;
	}

	public Source getCompiledScript(Activity activity, String event, String name, String code) {
		CacheCategory cacheCategory = new CacheCategory(activity.getClass(), Script.class);
		CacheKey cacheKey = new CacheKey(GETCOMPILEDSCRIPT, activity.getId(), event, name, code);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		Source source = null;
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			try {
				source = GraalvmScriptingFactory.functionalization(code);
				CacheManager.put(cacheCategory, cacheKey, source);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return source;
	}

	/**
	 * 根据活动节点查找合适的显示表单.
	 *
	 * @param actvityId
	 * @return
	 * @throws Exception
	 */
	public String lookupSuitableForm(String processId, String actvityId) throws Exception {
		if (StringUtils.isEmpty(actvityId)) {
			return null;
		}
		Process process = this.get(processId, Process.class);
		if (null != process) {
			String value = lookupSuitableFormFromActivity(processId, actvityId);
			if (StringUtils.isNotEmpty(value)) {
				return value;
			}
			Application application = this.get(process.getApplication(), Application.class);
			if ((null != application) && (StringUtils.isNotEmpty(application.getDefaultForm()))) {
				Form form = this.get(application.getDefaultForm(), Form.class);
				if (null != form) {
					return form.getId();
				}
			}
		}
		return null;
	}

	private String lookupSuitableFormFromActivity(String applicationId, String actvityId) throws Exception {
		List<String> excludes = new ArrayList<>();
		List<String> temp = new ArrayList<>();
		List<String> list = new ArrayList<>();
		list.add(actvityId);
		Activity activity;
		do {
			temp.clear();
			for (String id : list) {
				activity = this.getActivity(id);
				if (null != activity) {
					String value = checkForm(activity);
					if (StringUtils.isNotEmpty(value)) {
						return value;
					} else {
						temp.addAll(this.listActivityCanRouteToActvity(applicationId, activity.getId()));
					}
				}
			}
			temp = ListUtils.subtract(temp, excludes);
			list.clear();
			list.addAll(temp);
			excludes.addAll(temp);
		} while (!list.isEmpty());
		return null;
	}

	private String checkForm(Activity activity) throws Exception {
		if (StringUtils.isEmpty(activity.getForm())) {
			return null;
		}
		Form form = this.get(activity.getForm(), Form.class);
		if (null != form) {
			return form.getId();
		}
		return null;
	}

	private List<String> listActvityWithProcessWithRoute(String processId, String routeId) throws Exception {
		List<String> list = new ArrayList<>();
		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Agent.class, Activity.process_FIELDNAME, processId,
				Agent.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Begin.class, Activity.process_FIELDNAME, processId,
				Begin.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndIsMember(Choice.class, Activity.process_FIELDNAME,
				processId, Choice.routeList_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Delay.class, Activity.process_FIELDNAME, processId,
				Delay.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Embed.class, Activity.process_FIELDNAME, processId,
				Embed.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Invoke.class, Activity.process_FIELDNAME, processId,
				Invoke.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndIsMember(Manual.class, Activity.process_FIELDNAME,
				processId, Manual.routeList_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Merge.class, Activity.process_FIELDNAME, processId,
				Merge.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndIsMember(Parallel.class, Activity.process_FIELDNAME,
				processId, Parallel.routeList_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Publish.class, Activity.process_FIELDNAME, processId,
				Publish.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Service.class, Activity.process_FIELDNAME, processId,
				Service.route_FIELDNAME, routeId));

		list.addAll(this.entityManagerContainer().idsEqualAndEqual(Split.class, Activity.process_FIELDNAME, processId,
				Split.route_FIELDNAME, routeId));

		return ListTools.trim(list, true, true);
	}

	private List<String> listActivityCanRouteToActvity(String processId, String activityId) throws Exception {
		List<String> list = new ArrayList<>();
		List<String> ids = this.entityManagerContainer().idsEqualAndEqual(Route.class, Route.process_FIELDNAME,
				processId, Route.activity_FIELDNAME, activityId);
		for (String s : ids) {
			list.addAll(listActvityWithProcessWithRoute(processId, s));
		}
		return ListTools.trim(list, true, true);
	}

}
