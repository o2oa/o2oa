package com.x.query.core.express.plan;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
import com.x.base.core.entity.dataitem.ItemCategory;
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
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class ProcessPlatformPlan extends Plan {

	private static Logger logger = LoggerFactory.getLogger(ProcessPlatformPlan.class);

	public ProcessPlatformPlan() {

	}

	public ProcessPlatformPlan(Runtime runtime) {
		this.runtime = runtime;
		this.selectList = new SelectEntries();
		this.where = new WhereEntry();
		this.filterList = new TreeList<FilterEntry>();
		// this.calculate = new Calculate();
		this.columnList = new TreeList<String>();
	}

	public WhereEntry where = new WhereEntry();

	void adjust() throws Exception {
		this.adjustRuntime();
		this.adjustWhere();
		/* 先调整slectEntry 顺序不能改 */
		this.adjustSelectList();
		// this.adjustCalculate();
	}

	private void adjustRuntime() throws Exception {
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

	private void adjustSelectList() throws Exception {
		SelectEntries list = new SelectEntries();
		for (SelectEntry o : ListTools.trim(this.selectList, true, true)) {
			if (o.available()) {
				list.add(o);
			}
		}
		this.selectList = list;
	}

	List<String> listBundle(EntityManagerContainer emc) throws Exception {
		List<String> jobs = new TreeList<>();
		switch (StringUtils.trim(this.where.scope)) {
		case (SCOPE_ALL):
			jobs = ListUtils.union(this.listBundle_workCompleted(emc), this.listBundle_work(emc));
			break;
		case (SCOPE_WORKCOMPLETED):
			jobs = this.listBundle_workCompleted(emc);
			break;
		default:
			jobs = this.listBundle_work(emc);
			break;
		}
		if (BooleanUtils.isTrue(this.where.accessible)) {
			if (StringUtils.isNotEmpty(runtime.person)) {
				jobs = this.listBundle_accessible(emc, jobs, runtime.person);
			}
		}
		/** 针对DataItem进行判断 */
		List<FilterEntry> filterEntries = new TreeList<>();
		for (FilterEntry _o : ListTools.trim(this.filterList, true, true)) {
			if (_o.available()) {
				filterEntries.add(_o);
			}
		}
		if (!filterEntries.isEmpty()) {
			jobs = listBundle_filterEntry(emc, jobs, filterEntries);
		}
		filterEntries.clear();
		for (FilterEntry _o : ListTools.trim(this.runtime.filterList, true, true)) {
			if (_o.available()) {
				filterEntries.add(_o);
			}
		}
		if (!filterEntries.isEmpty()) {
			jobs = listBundle_filterEntry(emc, jobs, filterEntries);
		}
		return jobs;
	}

	private List<String> listBundle_work(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		cq.select(root.get(Work_.job)).distinct(true).where(this.where.workPredicate(cb, root));
		List<String> jobs = em.createQuery(cq).getResultList();
		return jobs;
	}

	private List<String> listBundle_workCompleted(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		cq.select(root.get(WorkCompleted_.job)).distinct(true).where(this.where.workCompletedPredicate(cb, root));
		List<String> jobs = em.createQuery(cq).getResultList();
		return jobs;
	}

	private List<String> listBundle_accessible(EntityManagerContainer emc, List<String> jobs, String person)
			throws Exception {
		logger.debug("开始过滤权限.");
		List<String> list = new TreeList<>();
		List<CompletableFuture<List<String>>> futures = new TreeList<>();
		for (List<String> _part_bundles : ListTools.batch(jobs, SQL_STATEMENT_IN_BATCH)) {
			CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
				try {
					EntityManager em = emc.get(Review.class);
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<String> cq = cb.createQuery(String.class);
					Root<Review> root = cq.from(Review.class);
					HashMap<String, String> map = new HashMap<>();
					_part_bundles.stream().forEach(o -> {
						map.put(o, o);
					});
					Expression<Set<String>> expression = cb.keys(map);
					Predicate p = cb.isMember(root.get(Review_.job), expression);
					p = cb.and(p, cb.equal(root.get(Review_.person), person));
					cq.select(root.get(Review_.job)).distinct(true).where(p);
					return em.createQuery(cq).getResultList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new TreeList<String>();
			});
			futures.add(future);
		}
		for (CompletableFuture<List<String>> future : futures) {
			list.addAll(future.get(300, TimeUnit.SECONDS));
			logger.debug("批次数据填充完成.");
		}
		logger.debug("开始过滤权限完成,完成后剩余: {} 个bunlde.", list.size());
		return list;
	}

	private List<String> listBundle_filterEntry(EntityManagerContainer emc, List<String> jobs,
			List<FilterEntry> filterEntries) throws Exception {
		/** 运行FilterEntry */
		List<String> partJobs = new TreeList<>();
		List<List<String>> batch_jobs = ListTools.batch(jobs, SQL_STATEMENT_IN_BATCH);
		for (int i = 0; i < filterEntries.size(); i++) {
			FilterEntry f = filterEntries.get(i);
			logger.debug("listBundle_filterEntry:{}.", f);
			List<String> os = new TreeList<>();
			List<CompletableFuture<List<String>>> futures = new TreeList<>();
			for (List<String> _batch : batch_jobs) {
				CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
					try {
						EntityManager em = emc.get(Item.class);
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<String> cq = cb.createQuery(String.class);
						Root<Item> root = cq.from(Item.class);
						Predicate p = f.toPredicate(cb, root, this.runtime, ItemCategory.pp);
						logger.debug("predicate:{}.", p);
						p = cb.and(p, cb.isMember(root.get(Item_.bundle), cb.literal(_batch)));
						cq.select(root.get(Item_.bundle)).where(p);
						return em.createQuery(cq).getResultList();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new TreeList<String>();
				});
				futures.add(future);
			}
			for (CompletableFuture<List<String>> future : futures) {
				os.addAll(future.get(300, TimeUnit.SECONDS));
				logger.debug("批次数据填充完成.");
			}
			/** 不等于在这里单独通过等于处理 */
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

		public WhereEntry() {

		}

		public Boolean accessible = false;

		public String scope = SCOPE_WORK;

		public List<ApplicationEntry> applicationList = new TreeList<>();
		public List<ProcessEntry> processList = new TreeList<>();
		public DateRangeEntry dateRange;
		public List<CreatorPersonEntry> creatorPersonList;
		public List<CreatorUnitEntry> creatorUnitList;
		public List<CreatorIdentityEntry> creatorIdentityList;

		Boolean available() {
			if ((!StringUtils.equals(this.scope, SCOPE_WORK)) && (!StringUtils.equals(this.scope, SCOPE_WORKCOMPLETED))
					&& (!StringUtils.equals(this.scope, SCOPE_ALL))) {
				return false;
			}
			return true;
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
			ps.add(this.workPredicate_application(cb, root));
			ps.add(this.workPredicate_creator(cb, root));
			ps.add(this.workPredicate_date(cb, root));
			ps = ListTools.trim(ps, true, false);
			if (ps.isEmpty()) {
				throw new Exception("where is empty.");
			}
			return cb.and(ps.toArray(new Predicate[] {}));
		}

		private Predicate workCompletedPredicate(CriteriaBuilder cb, Root<WorkCompleted> root) throws Exception {
			List<Predicate> ps = new TreeList<>();
			ps.add(this.workCompletedPredicate_application(cb, root));
			ps.add(this.workCompletedPredicate_creator(cb, root));
			ps.add(this.workCompletedPredicate_date(cb, root));
			ps = ListTools.trim(ps, true, false);
			if (ps.isEmpty()) {
				throw new Exception("where is empty.");
			}
			return cb.and(ps.toArray(new Predicate[] {}));
		}

		private Predicate workPredicate_application(CriteriaBuilder cb, Root<Work> root) throws Exception {
			List<String> _application_ids = ListTools.extractField(this.applicationList, Application.id_FIELDNAME,
					String.class, true, true);
			List<String> _process_ids = ListTools.extractField(this.processList, Process.id_FIELDNAME, String.class,
					true, true);
			_application_ids = _application_ids.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_process_ids = _process_ids.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			if (_application_ids.isEmpty() && _process_ids.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!_application_ids.isEmpty()) {
				p = cb.or(p, root.get(Work_.application).in(_application_ids));
			}
			if (!_process_ids.isEmpty()) {
				p = cb.or(p, root.get(Work_.process).in(_process_ids));
			}
			return p;
		}

		private Predicate workPredicate_creator(CriteriaBuilder cb, Root<Work> root) throws Exception {
			List<String> _creatorUnits = ListTools.extractField(this.creatorUnitList, "name", String.class, true, true);
			List<String> _creatorPersons = ListTools.extractField(this.creatorPersonList, "name", String.class, true,
					true);
			List<String> _creatorIdentitys = ListTools.extractField(this.creatorIdentityList, "name", String.class,
					true, true);
			if (_creatorUnits.isEmpty() && _creatorPersons.isEmpty() && _creatorIdentitys.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!_creatorUnits.isEmpty()) {
				p = cb.or(p, root.get(Work_.creatorUnit).in(_creatorUnits));
			}
			if (!_creatorPersons.isEmpty()) {
				p = cb.or(p, root.get(Work_.creatorPerson).in(_creatorPersons));
			}
			if (!_creatorIdentitys.isEmpty()) {
				p = cb.or(p, root.get(Work_.creatorIdentity).in(_creatorIdentitys));
			}
			return p;
		}

		private Predicate workPredicate_date(CriteriaBuilder cb, Root<Work> root) throws Exception {
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

		private Predicate workCompletedPredicate_application(CriteriaBuilder cb, Root<WorkCompleted> root)
				throws Exception {
			List<String> _application_ids = ListTools.extractField(this.applicationList, Application.id_FIELDNAME,
					String.class, true, true);
			List<String> _application_names = ListTools.extractField(this.applicationList, Application.name_FIELDNAME,
					String.class, true, true);
			List<String> _application_alias = ListTools.extractField(this.applicationList, Application.alias_FIELDNAME,
					String.class, true, true);
			List<String> _process_ids = ListTools.extractField(this.processList, Process.id_FIELDNAME, String.class,
					true, true);
			List<String> _process_names = ListTools.extractField(this.processList, Process.name_FIELDNAME, String.class,
					true, true);
			List<String> _process_alias = ListTools.extractField(this.processList, Process.alias_FIELDNAME,
					String.class, true, true);
			_application_ids = _application_ids.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_application_names = _application_names.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_application_alias = _application_alias.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_process_ids = _process_ids.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_process_names = _process_names.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_process_alias = _process_alias.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			if (_application_ids.isEmpty() && _application_names.isEmpty() && _application_alias.isEmpty()
					&& _process_ids.isEmpty() && _process_names.isEmpty() && _process_alias.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!_application_ids.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.application).in(_application_ids));
			}
			if (!_application_names.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.applicationName).in(_application_names));
			}
			if (!_application_alias.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.applicationAlias).in(_application_alias));
			}
			if (!_process_ids.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.process).in(_process_ids));
			}
			if (!_process_names.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.processName).in(_process_names));
			}
			if (!_process_alias.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.processAlias).in(_process_alias));
			}
			return p;
		}

		private Predicate workCompletedPredicate_creator(CriteriaBuilder cb, Root<WorkCompleted> root)
				throws Exception {
			List<String> _creatorUnits = ListTools.extractField(this.creatorUnitList, "name", String.class, true, true);
			List<String> _creatorPersons = ListTools.extractField(this.creatorPersonList, "name", String.class, true,
					true);
			List<String> _creatorIdentitys = ListTools.extractField(this.creatorIdentityList, "name", String.class,
					true, true);
			if (_creatorUnits.isEmpty() && _creatorPersons.isEmpty() && _creatorIdentitys.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!_creatorUnits.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.creatorUnit).in(_creatorUnits));
			}
			if (!_creatorPersons.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.creatorPerson).in(_creatorPersons));
			}
			if (!_creatorIdentitys.isEmpty()) {
				p = cb.or(p, root.get(WorkCompleted_.creatorIdentity).in(_creatorIdentitys));
			}
			return p;
		}

		private Predicate workCompletedPredicate_date(CriteriaBuilder cb, Root<WorkCompleted> root) throws Exception {
			if (null == this.dateRange || (!this.dateRange.available())) {
				return null;
			}
			if (null == this.dateRange.start) {
				return cb.lessThanOrEqualTo(root.get(WorkCompleted_.completedTime), this.dateRange.completed);
			} else if (null == this.dateRange.completed) {
				return cb.greaterThanOrEqualTo(root.get(WorkCompleted_.startTime), this.dateRange.start);
			} else {
				return cb.and(cb.greaterThanOrEqualTo(root.get(WorkCompleted_.startTime), this.dateRange.start),
						cb.lessThanOrEqualTo(root.get(WorkCompleted_.completedTime), this.dateRange.completed));
			}
		}
	}
}