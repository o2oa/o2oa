package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
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

class ActionFilterCreateEntry extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFilterCreateEntry.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{} ActionFilterCreatorEntry.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = get(effectivePerson);
		result.setData(wo);
		return result;
	}

	private Wo get(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wo wo = new Wo();
			CompletableFuture<List<NameValueCountPair>> applicationFuture = this.applicationFuture(business,
					effectivePerson);
			CompletableFuture<List<NameValueCountPair>> processFuture = this.processFuture(business, effectivePerson);
			CompletableFuture<List<NameValueCountPair>> startTimeMonthFuture = this.startTimeMonthFuture(business,
					effectivePerson);
			wo.setApplicationList(
					applicationFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setProcessList(processFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setStartTimeMonthList(
					startTimeMonthFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			return wo;
		}
	}

	public static class Wo {

		@FieldDescribe("可选择的应用")
		private List<NameValueCountPair> applicationList = new ArrayList<>();

		@FieldDescribe("可选择的流程")
		private List<NameValueCountPair> processList = new ArrayList<>();

		@FieldDescribe("可选择的开始月份")
		private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

		public List<NameValueCountPair> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(
				List<NameValueCountPair> applicationList) {
			this.applicationList = applicationList;
		}

		public List<NameValueCountPair> getProcessList() {
			return processList;
		}

		public void setProcessList(List<NameValueCountPair> processList) {
			this.processList = processList;
		}

		public List<NameValueCountPair> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(
				List<NameValueCountPair> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}
	}

	private CompletableFuture<List<NameValueCountPair>> applicationFuture(Business business,
			EffectivePerson effectivePerson) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Review.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Review> root = cq.from(Review.class);
				Predicate p = cb.and(cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName()),
						cb.equal(root.get(Review_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Review_.application)).distinct(true).where(p))
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

	private CompletableFuture<List<NameValueCountPair>> processFuture(Business business,
			EffectivePerson effectivePerson) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Review.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Review> root = cq.from(Review.class);
				Predicate p = cb.and(cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName()),
						cb.equal(root.get(Review_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Review_.process)).distinct(true).where(p))
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

	private CompletableFuture<List<NameValueCountPair>> startTimeMonthFuture(Business business,
			EffectivePerson effectivePerson) {
		return CompletableFuture.supplyAsync(() -> {
			List<NameValueCountPair> list = new ArrayList<>();
			try {
				EntityManager em = business.entityManagerContainer().get(Review.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Review> root = cq.from(Review.class);
				Predicate p = cb.and(cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName()),
						cb.equal(root.get(Review_.creatorPerson), effectivePerson.getDistinguishedName()));
				List<String> os = em.createQuery(cq.select(root.get(Review_.startTimeMonth)).distinct(true).where(p))
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
