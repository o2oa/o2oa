package com.x.query.core.express.plan;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class ProcessPlatformPlan extends Plan {

	private static final long serialVersionUID = 8346759115447768182L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPlatformPlan.class);

	public ProcessPlatformPlan() {
		this.selectList = new SelectEntries();
		this.where = new WhereEntry();
		this.filterList = new TreeList<FilterEntry>();
	}

	public WhereEntry where = new WhereEntry();

	@Override
	void adjust() throws Exception {
		this.adjustRuntime();
		this.adjustWhere();
		// 先调整slectEntry 顺序不能改
		this.adjustSelectList();
	}

	private void adjustRuntime() {
		if (null == this.runtime) {
			this.runtime = new Runtime();
		}
		this.runtime.person = StringUtils.trimToEmpty(this.runtime.person);
		if (null == this.runtime.parameter) {
			this.runtime.parameter = new HashMap<String, String>();
		}
	}

	private void adjustWhere() throws Exception {
		if (null == this.where) {
			this.where = new WhereEntry();
		}
		this.where.dateRange.adjust();
	}

	private void adjustSelectList() {
		SelectEntries list = new SelectEntries();
		for (SelectEntry o : ListTools.trim(this.selectList, true, true)) {
			if (BooleanUtils.isTrue(o.available())) {
				list.add(o);
			}
		}
		this.selectList = list;
	}

	@Override
	List<String> listBundle() throws Exception {
		List<String> jobs;
		switch (StringUtils.trim(this.where.scope)) {
		case (SCOPE_ALL):
			jobs = ListUtils.union(this.listBundleWorkCompleted(), this.listBundleWork());
			break;
		case (SCOPE_WORKCOMPLETED):
			jobs = this.listBundleWorkCompleted();
			break;
		default:
			jobs = this.listBundleWork();
			break;
		}
		if (BooleanUtils.isTrue(this.where.accessible) && (StringUtils.isNotEmpty(runtime.person))) {
			jobs = this.listBundleAccessible(jobs, runtime.person, threadPool);
		}
		// 针对DataItem进行判断
		List<FilterEntry> filterEntries = new TreeList<>();
		for (FilterEntry o : ListTools.trim(this.filterList, true, true)) {
			if (BooleanUtils.isTrue(o.available())) {
				filterEntries.add(o);
			}
		}
		if (!filterEntries.isEmpty()) {
			jobs = listBundleFilterEntry(jobs, filterEntries, threadPool);
		}
		filterEntries.clear();
		for (FilterEntry o : ListTools.trim(this.runtime.filterList, true, true)) {
			if (BooleanUtils.isTrue(o.available())) {
				filterEntries.add(o);
			}
		}
		if (!filterEntries.isEmpty()) {
			jobs = listBundleFilterEntry(jobs, filterEntries, threadPool);
		}
		return jobs;
	}

	private List<String> listBundleWork() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Work> root = cq.from(Work.class);
			cq.select(root.get(Work_.job)).where(this.where.workPredicate(cb, root));
			List<String> jobs = em.createQuery(cq).getResultList();
			return jobs.stream().distinct().collect(Collectors.toList());
		}
	}

	private List<String> listBundleWorkCompleted() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(WorkCompleted.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<WorkCompleted> root = cq.from(WorkCompleted.class);
			cq.select(root.get(WorkCompleted_.job)).where(this.where.workCompletedPredicate(cb, root));
			List<String> jobs = em.createQuery(cq).getResultList();
			return jobs.stream().distinct().collect(Collectors.toList());
		}
	}

	private List<String> listBundleAccessible(List<String> jobs, String person, ExecutorService threadPool)
			throws Exception {
		List<String> list = new TreeList<>();
		List<CompletableFuture<List<String>>> futures = new TreeList<>();
		for (List<String> _part_bundles : ListTools.batch(jobs, 500)) {
			CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					EntityManager em = emc.get(Review.class);
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<String> cq = cb.createQuery(String.class);
					Root<Review> root = cq.from(Review.class);
					HashMap<String, String> map = new HashMap<>();
					_part_bundles.stream().forEach(o -> map.put(o, o));
					Expression<Set<String>> expression = cb.keys(map);
					Predicate p = cb.isMember(root.get(Review_.job), expression);
					p = cb.and(p, cb.equal(root.get(Review_.person), person));
					cq.select(root.get(Review_.job)).where(p);
					List<String> parts = em.createQuery(cq).getResultList();
					return parts.stream().distinct().collect(Collectors.toList());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new TreeList<>();
			}, threadPool);
			futures.add(future);
		}
		for (CompletableFuture<List<String>> future : futures) {
			list.addAll(future.get(300, TimeUnit.SECONDS));
			LOGGER.debug("批次数据填充完成.");
		}
		LOGGER.debug("开始过滤权限完成,完成后剩余: {} 个bunlde.", () -> list.size());
		return list;
	}

	private List<String> listBundleFilterEntry(List<String> jobs, List<FilterEntry> filterEntries,
			ExecutorService threadPool) throws Exception {
		List<String> partJobs = new TreeList<>();
		List<List<String>> batchJobs = ListTools.batch(jobs, 500);
		for (int i = 0; i < filterEntries.size(); i++) {
			FilterEntry f = filterEntries.get(i);
			LOGGER.debug("listBundle_filterEntry:{}.", () -> f);
			List<String> os = new TreeList<>();
			List<CompletableFuture<List<String>>> futures = new TreeList<>();
			for (List<String> _batch : batchJobs) {
				CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						EntityManager em = emc.get(Item.class);
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<String> cq = cb.createQuery(String.class);
						Root<Item> root = cq.from(Item.class);
						Predicate p = root.get(Item_.bundle).in(_batch);
						p = f.toPredicate(cb, root, this.runtime, p);
						cq.select(root.get(Item_.bundle)).where(p);
						List<String> parts = em.createQuery(cq).getResultList();
						return parts.stream().distinct().collect(Collectors.toList());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new TreeList<>();
				}, threadPool);
				futures.add(future);
			}
			for (CompletableFuture<List<String>> future : futures) {
				os.addAll(future.get(300, TimeUnit.SECONDS));
				LOGGER.debug("批次数据填充完成.");
			}
			// 不等于在这里单独通过等于处理
			if (Comparison.isNotEquals(f.comparison)) {
				os = ListUtils.subtract(jobs, os);
			}
			if (i == 0) {
				partJobs.addAll(os);
			} else {
				if (StringUtils.equals("and", f.logic)) {
					partJobs = ListUtils.intersection(partJobs, os);
				} else {
					partJobs = ListUtils.union(partJobs, os);
				}
			}
		}
		jobs = ListUtils.intersection(jobs, partJobs);
		return jobs;
	}

	public static class WhereEntry extends GsonPropertyObject {

		private static final long serialVersionUID = 8233208785074889649L;

		public Boolean accessible = false;

		public String scope = SCOPE_WORK;

		public List<ApplicationEntry> applicationList = new TreeList<>();
		public List<ProcessEntry> processList = new TreeList<>();
		public DateRangeEntry dateRange;
		public List<CreatorPersonEntry> creatorPersonList;
		public List<CreatorUnitEntry> creatorUnitList;
		public List<CreatorIdentityEntry> creatorIdentityList;

		Boolean available() {
			return StringUtils.equals(this.scope, SCOPE_WORK) || StringUtils.equals(this.scope, SCOPE_WORKCOMPLETED)
					|| StringUtils.equals(this.scope, SCOPE_ALL);
		}

		public static class ApplicationEntry {

			public String name;

			public String alias;

			public String id;

		}

		public static class ProcessEntry {

			public String name;

			public String alias;

			public String id;

		}

		public static class CreatorUnitEntry {

			public String name;

			public String id;

		}

		public static class CreatorIdentityEntry {

			public String name;

			public String id;

		}

		public static class CreatorPersonEntry {

			public String name;

			public String id;

		}

		private Predicate workPredicate(CriteriaBuilder cb, Root<Work> root) throws Exception {
			List<Predicate> ps = new TreeList<>();
			ps.add(this.workPredicateApplication(cb, root));
			ps.add(this.workPredicateCreator(cb, root));
			ps.add(this.workPredicateDate(cb, root));
			ps = ListTools.trim(ps, true, false);
			if (ps.isEmpty()) {
				throw new IllegalAccessException("where is empty.");
			}
			return cb.and(ps.toArray(new Predicate[] {}));
		}

		private Predicate workCompletedPredicate(CriteriaBuilder cb, Root<WorkCompleted> root) throws Exception {
			List<Predicate> ps = new TreeList<>();
			ps.add(this.workCompletedPredicateApplication(cb, root));
			ps.add(this.workCompletedPredicateCreator(cb, root));
			ps.add(this.workCompletedPredicateDate(cb, root));
			ps = ListTools.trim(ps, true, false);
			if (ps.isEmpty()) {
				throw new IllegalAccessException("where is empty.");
			}
			return cb.and(ps.toArray(new Predicate[] {}));
		}

		private Predicate workPredicateApplication(CriteriaBuilder cb, Root<Work> root) throws Exception {
			List<String> applicationIds = ListTools.extractField(this.applicationList, JpaObject.id_FIELDNAME,
					String.class, true, true);
			List<String> processIds = ListTools.extractField(this.processList, JpaObject.id_FIELDNAME, String.class,
					true, true);
			applicationIds = applicationIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
			processIds = processIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
			if (applicationIds.isEmpty() && processIds.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!applicationIds.isEmpty()) {
				p = cb.or(p, root.get(Work_.application).in(applicationIds));
			}
			if (!processIds.isEmpty()) {
				p = cb.or(p, root.get(Work_.process).in(processIds));
			}
			return p;
		}

		private Predicate workPredicateCreator(CriteriaBuilder cb, Root<Work> root) throws Exception {
			List<String> creatorUnits = ListTools.extractField(this.creatorUnitList, "name", String.class, true, true);
			List<String> creatorPersons = ListTools.extractField(this.creatorPersonList, "name", String.class, true,
					true);
			List<String> creatorIdentitys = ListTools.extractField(this.creatorIdentityList, "name", String.class, true,
					true);
			if (creatorUnits.isEmpty() && creatorPersons.isEmpty() && creatorIdentitys.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!creatorUnits.isEmpty()) {
				p = cb.or(p, root.get(Work_.creatorUnit).in(creatorUnits));
			}
			if (!creatorPersons.isEmpty()) {
				p = cb.or(p, root.get(Work_.creatorPerson).in(creatorPersons));
			}
			if (!creatorIdentitys.isEmpty()) {
				p = cb.or(p, root.get(Work_.creatorIdentity).in(creatorIdentitys));
			}
			return p;
		}

		private Predicate workPredicateDate(CriteriaBuilder cb, Root<Work> root) throws Exception {
			if (null == this.dateRange || (!this.dateRange.available())) {
				return null;
			}
			if (null == this.dateRange.start) {
				return cb.lessThanOrEqualTo(root.get(Work_.startTime), this.dateRange.completed);
			} else if (null == this.dateRange.completed) {
				return cb.greaterThanOrEqualTo(root.get(Work_.startTime), this.dateRange.start);
			} else {
				return cb.between(root.get(Work_.startTime), this.dateRange.start, this.dateRange.completed);
			}
		}

		private Predicate workCompletedPredicateApplication(CriteriaBuilder cb, Root<WorkCompleted> root)
				throws Exception {
			List<String> applicationIds = ListTools.extractField(this.applicationList, JpaObject.id_FIELDNAME,
					String.class, true, true);
			List<String> processIds = ListTools.extractField(this.processList, JpaObject.id_FIELDNAME, String.class,
					true, true);
			applicationIds = applicationIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
			processIds = processIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
			if (applicationIds.isEmpty() && processIds.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!applicationIds.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.application).in(applicationIds));
			}
			if (!processIds.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.process).in(processIds));
			}
			return p;
		}

		private Predicate workCompletedPredicateCreator(CriteriaBuilder cb, Root<WorkCompleted> root) throws Exception {
			List<String> creatorUnits = ListTools.extractField(this.creatorUnitList, "name", String.class, true, true);
			List<String> creatorPersons = ListTools.extractField(this.creatorPersonList, "name", String.class, true,
					true);
			List<String> creatorIdentitys = ListTools.extractField(this.creatorIdentityList, "name", String.class, true,
					true);
			if (creatorUnits.isEmpty() && creatorPersons.isEmpty() && creatorIdentitys.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!creatorUnits.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.creatorUnit).in(creatorUnits));
			}
			if (!creatorPersons.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.creatorPerson).in(creatorPersons));
			}
			if (!creatorIdentitys.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.creatorIdentity).in(creatorIdentitys));
			}
			return p;
		}

		private Predicate workCompletedPredicateDate(CriteriaBuilder cb, Root<WorkCompleted> root) {
			if (null == this.dateRange || (!this.dateRange.available())) {
				return null;
			}
			if (null == this.dateRange.start) {
				return cb.lessThanOrEqualTo(root.get(WorkCompleted_.startTime), this.dateRange.completed);
			} else if (null == this.dateRange.completed) {
				return cb.greaterThanOrEqualTo(root.get(WorkCompleted_.startTime), this.dateRange.start);
			} else {
				return cb.and(cb.greaterThanOrEqualTo(root.get(WorkCompleted_.startTime), this.dateRange.start),
						cb.lessThanOrEqualTo(root.get(WorkCompleted_.startTime), this.dateRange.completed));
			}
		}
	}
}
