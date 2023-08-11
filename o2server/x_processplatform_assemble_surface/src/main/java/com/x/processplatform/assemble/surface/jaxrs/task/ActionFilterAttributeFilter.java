package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionFilterAttributeFilterWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionFilterAttributeFilterWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionFilterAttributeFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFilterAttributeFilter.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = get(effectivePerson, wi);
		result.setData(wo);
		return result;
	}

	private Wo get(EffectivePerson effectivePerson, Wi wi) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wo wo = new Wo();
			CompletableFuture<List<NameValueCountPair>> applicationFuture = this.applicationFuture(business,
					effectivePerson, wi);
			CompletableFuture<List<NameValueCountPair>> processFuture = this.processFuture(business, effectivePerson,
					wi);
			CompletableFuture<List<NameValueCountPair>> creatorUnitFuture = this.creatorUnitFuture(business,
					effectivePerson, wi);
			CompletableFuture<List<NameValueCountPair>> startTimeMonthFuture = this.startTimeMonthFuture(business,
					effectivePerson, wi);
			CompletableFuture<List<NameValueCountPair>> activityNameFuture = this.activityNameFuture(business,
					effectivePerson, wi);
			wo.setApplicationList(
					applicationFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setProcessList(processFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setCreatorUnitList(
					creatorUnitFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setStartTimeMonthList(
					startTimeMonthFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setActivityNameList(
					activityNameFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			return wo;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionFilterAttributeFilter$Wi")
	public class Wi extends ActionFilterAttributeFilterWi {
		private static final long serialVersionUID = 3450169937128265207L;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionFilterAttributeFilter$Wo")
	public static class Wo extends ActionFilterAttributeFilterWo {
		private static final long serialVersionUID = -5983693898737479512L;
	}

	private CompletableFuture<List<NameValueCountPair>> applicationFuture(Business business,
			EffectivePerson effectivePerson, Wi wi) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Task.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Task> root = cq.from(Task.class);
				Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
				p = filter(cb, root, p, wi);
				List<String> os = em.createQuery(cq.select(root.get(Task_.application)).distinct(true).where(p))
						.getResultList();
				list = os.stream().filter(StringUtils::isNotEmpty).map(o -> {
					NameValueCountPair pair = new NameValueCountPair();
					try {
						Application application = business.application().pick(o);
						if (null != application) {
							pair.setValue(application.getId());
							pair.setName(application.getName());
						} else {
							pair.setValue(o);
							pair.setName(o);
						}
					} catch (Exception e) {
						LOGGER.error(e);
					}
					return pair;
				}).sorted(Comparator.comparing(o -> Objects.toString(o.getName()))).collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private Predicate filter(CriteriaBuilder cb, Root<Task> root, Predicate p, Wi wi) {
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Task_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Task_.process).in(wi.getProcessList()));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Task_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Task_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Task_.activityName).in(wi.getActivityNameList()));
		}
		return p;
	}

	private CompletableFuture<List<NameValueCountPair>> processFuture(Business business,
			EffectivePerson effectivePerson, Wi wi) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Task.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Task> root = cq.from(Task.class);
				Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
				p = filter(cb, root, p, wi);
				List<String> os = em.createQuery(cq.select(root.get(Task_.process)).distinct(true).where(p))
						.getResultList();
				list = os.stream().filter(StringUtils::isNotEmpty).map(o -> {
					NameValueCountPair pair = new NameValueCountPair();
					try {
						Process process = business.process().pick(o);
						if (null != process) {
							pair.setValue(process.getId());
							pair.setName(process.getName());
						} else {
							pair.setValue(o);
							pair.setName(o);
						}
					} catch (Exception e) {
						LOGGER.error(e);
					}
					return pair;
				}).sorted(Comparator.comparing(o -> Objects.toString(o.getName()))).collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<NameValueCountPair>> creatorUnitFuture(Business business,
			EffectivePerson effectivePerson, Wi wi) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Task.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Task> root = cq.from(Task.class);
				Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
				p = filter(cb, root, p, wi);
				List<String> os = em.createQuery(cq.select(root.get(Task_.creatorUnit)).distinct(true).where(p))
						.getResultList();
				list = os.stream().filter(StringUtils::isNotEmpty).map(o -> {
					NameValueCountPair pair = new NameValueCountPair();
					pair.setValue(o);
					pair.setName(StringUtils.defaultString(StringUtils.substringBefore(o, "@"), o));
					return pair;
				}).sorted(Comparator.comparing(o -> Objects.toString(o.getName()))).collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<NameValueCountPair>> activityNameFuture(Business business,
			EffectivePerson effectivePerson, Wi wi) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Task.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Task> root = cq.from(Task.class);
				Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
				p = filter(cb, root, p, wi);
				List<String> os = em.createQuery(cq.select(root.get(Task_.activityName)).distinct(true).where(p))
						.getResultList();
				list = os.stream().filter(StringUtils::isNotEmpty).map(o -> {
					NameValueCountPair pair = new NameValueCountPair();
					pair.setValue(o);
					pair.setName(o);
					return pair;
				}).sorted(Comparator.comparing(o -> Objects.toString(o.getName()))).collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<NameValueCountPair>> startTimeMonthFuture(Business business,
			EffectivePerson effectivePerson, Wi wi) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Task.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Task> root = cq.from(Task.class);
				Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
				p = filter(cb, root, p, wi);
				List<String> os = em.createQuery(cq.select(root.get(Task_.startTimeMonth)).distinct(true).where(p))
						.getResultList();
				list = os.stream().filter(StringUtils::isNotEmpty).map(o -> {
					NameValueCountPair pair = new NameValueCountPair();
					pair.setValue(o);
					pair.setName(o);
					return pair;
				}).sorted(Comparator.comparing(o -> Objects.toString(o.getName()))).collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}
}