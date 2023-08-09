package com.x.processplatform.assemble.surface.jaxrs.work;

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

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionFilterAttributeWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionFilterAttribute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFilterAttribute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			CompletableFuture<List<NameValueCountPair>> processFuture = this.processFuture(business, effectivePerson,
					application);
			CompletableFuture<List<NameValueCountPair>> creatorUnitFuture = this.creatorUnitFuture(business,
					effectivePerson, application);
			CompletableFuture<List<NameValueCountPair>> activityNameFuture = this.activityNameFuture(business,
					effectivePerson, application);
			CompletableFuture<List<NameValueCountPair>> startTimeMonthFuture = this.startTimeMonthFuture(business,
					effectivePerson, application);
			CompletableFuture<List<NameValueCountPair>> workStatusFuture = this.workStatusFuture(business,
					effectivePerson, application);

			wo.setProcessList(processFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setCreatorUnitList(
					creatorUnitFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setActivityNameList(
					activityNameFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setStartTimeMonthList(
					startTimeMonthFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setWorkStatusList(
					workStatusFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionFilterAttribute$Wo")
	public static class Wo extends ActionFilterAttributeWo {

		private static final long serialVersionUID = -1731021728382521719L;

	}

	private CompletableFuture<List<NameValueCountPair>> processFuture(Business business,
			EffectivePerson effectivePerson, Application application) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Work.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Work> root = cq.from(Work.class);
				Predicate p = cb.equal(root.get(Work_.application), application.getId());
				p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Work_.process)).distinct(true).where(p))
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
			EffectivePerson effectivePerson, Application application) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Work.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Work> root = cq.from(Work.class);
				Predicate p = cb.equal(root.get(Work_.application), application.getId());
				p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Work_.creatorUnit)).distinct(true).where(p))
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
			EffectivePerson effectivePerson, Application application) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Work.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Work> root = cq.from(Work.class);
				Predicate p = cb.equal(root.get(Work_.application), application.getId());
				p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Work_.activityName)).distinct(true).where(p))
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

	private CompletableFuture<List<NameValueCountPair>> startTimeMonthFuture(Business business,
			EffectivePerson effectivePerson, Application application) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Work.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Work> root = cq.from(Work.class);
				Predicate p = cb.equal(root.get(Work_.application), application.getId());
				p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Work_.startTimeMonth)).distinct(true).where(p))
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

	private CompletableFuture<List<NameValueCountPair>> workStatusFuture(Business business,
			EffectivePerson effectivePerson, Application application) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Work.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<WorkStatus> cq = cb.createQuery(WorkStatus.class);
				Root<Work> root = cq.from(Work.class);
				Predicate p = cb.equal(root.get(Work_.application), application.getId());
				p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<WorkStatus> os = em.createQuery(cq.select(root.get(Work_.workStatus)).distinct(true).where(p))
						.getResultList();
				list = os.stream().filter(o -> !Objects.isNull(o)).map(o -> {
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