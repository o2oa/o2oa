package com.x.query.core.entity.plan;

import java.util.Date;
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
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CmsPermissionService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class CmsPlan extends Plan {

	public CmsPlan() {
	}

	private CmsPermissionService cmsPermissionService = null;

	public CmsPlan(Runtime runtime) {
		this.runtime = runtime;
		this.selectList = new SelectEntries();
		this.where = new WhereEntry();
		this.filterList = new TreeList<FilterEntry>();
		this.calculate = new Calculate();
		this.columnList = new TreeList<String>();
		this.cmsPermissionService = new CmsPermissionService();
	}

	public WhereEntry where = new WhereEntry();

	void adjust() throws Exception {
		this.adjustRuntime();
		this.adjustWhere();
		/* 先调整slectEntry 顺序不能改 */
		this.adjustSelectList();
		this.adjustCalculate();
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

	private void adjustCalculate() throws Exception {
		if (null == this.calculate) {
			this.calculate = new Calculate();
		}
	}

	List<String> listBundle(EntityManagerContainer emc) throws Exception {
		List<String> docIds = new TreeList<>();

		Date end = null;
		Date start = new Date();

		docIds = listBundle_document(emc);
		if (ListTools.isNotEmpty(docIds)) {
			System.out.println(">>>>>>>>>>docIds_1.size:" + docIds.size());
		} else {
			System.out.println(">>>>>>>>>>docIds_1 is empty!");
		}
		end = new Date();
		System.out.println(">>>>>>>>>>docIds = listBundle_document(emc);" + (end.getTime() - start.getTime()));

		if (BooleanUtils.isTrue(this.where.accessible)) {
			if (StringUtils.isNotEmpty(runtime.person)) {
				docIds = this.listBundle_accessible(emc, docIds, runtime.person);
			}
		}
		if (ListTools.isNotEmpty(docIds)) {
			System.out.println(">>>>>>>>>>docIds_2.size:" + docIds.size());
		} else {
			System.out.println(">>>>>>>>>>docIds_2 is empty!");
		}
		end = new Date();
		System.out.println(">>>>>>>>>>docIds = this.listBundle_accessible(emc, docIds, runtime.person);;"
				+ (end.getTime() - start.getTime()));

		/** 针对DataItem进行判断 */
		List<FilterEntry> filterEntries = new TreeList<>();
		for (FilterEntry _o : ListTools.trim(this.filterList, true, true)) {
			if (_o.available()) {
				filterEntries.add(_o);
			}
		}
		if (!filterEntries.isEmpty()) {
			docIds = listBundle_filterEntry(emc, docIds, filterEntries);
		}
		if (ListTools.isNotEmpty(docIds)) {
			System.out.println(">>>>>>>>>>docIds_3.size:" + docIds.size());
		} else {
			System.out.println(">>>>>>>>>>docIds_3 is empty!");
		}
		end = new Date();
		System.out.println(">>>>>>>>>>docIds = listBundle_filterEntry(emc, docIds, filterEntries);"
				+ (end.getTime() - start.getTime()));

		filterEntries.clear();
		for (FilterEntry _o : ListTools.trim(this.runtime.filterList, true, true)) {
			if (_o.available()) {
				filterEntries.add(_o);
			}
		}

		if (!filterEntries.isEmpty()) {
			docIds = listBundle_filterEntry(emc, docIds, filterEntries);
		}
		if (ListTools.isNotEmpty(docIds)) {
			System.out.println(">>>>>>>>>>docIds_4.size:" + docIds.size());
		} else {
			System.out.println(">>>>>>>>>>docIds_4 is empty!");
		}
		end = new Date();
		System.out.println(">>>>>>>>>>docIds = listBundle_filterEntry(emc, docIds, filterEntries);"
				+ (end.getTime() - start.getTime()));

		return docIds;
	}

	/**
	 * 过滤信息类型的文档
	 * 
	 * @param emc
	 * @return
	 * @throws Exception
	 */
	private List<String> listBundle_document(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Document.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Document> root = cq.from(Document.class);
		cq.select(root.get(Document_.id)).distinct(true).where(this.where.documentPredicate(cb, root));
		System.out.println(">>>>>>SQL:" + em.createQuery(cq).toString());
		List<String> docIds = em.createQuery(cq).getResultList();
		return docIds;
	}

	private List<String> listBundle_accessible(EntityManagerContainer emc, List<String> docIds, String person)
			throws Exception {
		String documentType = "信息";
		if (StringUtils.equals(this.where.scope, SCOPE_CMS_INFO)) {
			documentType = "信息";
		} else if (StringUtils.equals(this.where.scope, SCOPE_CMS_DATA)) {
			documentType = "数据";
		} else {
			documentType = "全部";
		}

		List<String> accessibleCategoryIds = null;
		final List<String> viewableDocIds = new TreeList<>();

		if (!StringUtils.equals(this.where.scope, SCOPE_CMS_DATA)) {
			accessibleCategoryIds = cmsPermissionService.listViewableCategoryIdByPerson(emc, person, false,
					this.runtime.unitList, this.runtime.groupList, null, null, null, documentType, 2000, false);
			viewableDocIds.addAll(cmsPermissionService.lisViewableDocIdsWithFilter(emc, accessibleCategoryIds, person,
					this.runtime.unitList, this.runtime.groupList, docIds, accessibleCategoryIds, 100000));
		}

		List<String> list = new TreeList<>();
		List<CompletableFuture<List<String>>> futures = new TreeList<>();
		for (List<String> _part_jobs : ListTools.batch(docIds, SQL_STATEMENT_IN_BATCH)) {
			CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
				try {
					EntityManager em = emc.get(Document.class);
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<String> cq = cb.createQuery(String.class);
					Root<Document> root = cq.from(Document.class);
					final HashMap<String, String> map = new HashMap<>();
					_part_jobs.stream().forEach(o -> {
						map.put(o, o);
					});
					Predicate p = cb.isMember(root.get(Document_.id), cb.keys(map));
					// 如果是数据就不带权限
					if (!StringUtils.equals(this.where.scope, SCOPE_CMS_DATA)) {
						final HashMap<String, String> mapIds = new HashMap<>();
						viewableDocIds.stream().forEach(o -> {
							mapIds.put(o, o);
						});
						p = cb.and(p, cb.isMember(root.get(Document_.id), cb.keys(mapIds)));
					}
					cq.select(root.get(Document_.id)).distinct(true).where(p);
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
		}
		return list;
	}

	private List<String> listBundle_filterEntry(EntityManagerContainer emc, List<String> docIds,
			List<FilterEntry> filterEntries) throws Exception {
		/** 运行FilterEntry */
		List<String> partDocIds = new TreeList<>();
		List<List<String>> batch_docIds = ListTools.batch(docIds, SQL_STATEMENT_IN_BATCH);
		for (int i = 0; i < filterEntries.size(); i++) {
			FilterEntry f = filterEntries.get(i);
			List<String> os = new TreeList<>();
			List<CompletableFuture<List<String>>> futures = new TreeList<>();
			for (List<String> _batch : batch_docIds) {
				CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
					try {
						EntityManager em = emc.get(Item.class);
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<String> cq = cb.createQuery(String.class);
						Root<Item> root = cq.from(Item.class);
						Predicate p = f.toPredicate(cb, root, this.runtime, ItemCategory.cms);
						HashMap<String, String> map = new HashMap<>();
						_batch.stream().forEach(o -> {
							map.put(o, o);
						});
						Expression<Set<String>> expression = cb.keys(map);
						p = cb.and(p, cb.isMember(root.get(Item_.bundle), expression));
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
			}
			/** 不等于在这里单独通过等于处理 */
			if (Comparison.isNotEquals(f.comparison)) {
				os = ListUtils.subtract(docIds, os);
			}
			if (i == 0) {
				partDocIds.addAll(os);
			} else {
				if (StringUtils.equals("and", f.logic)) {
					partDocIds = ListUtils.intersection(partDocIds, os);
				} else {
					partDocIds = ListUtils.union(partDocIds, os);
				}
			}
		}
		docIds = ListUtils.intersection(docIds, partDocIds);
		return docIds;
	}

	// private List<String> listBundle_filterEntry(EntityManagerContainer emc,
	// List<String> docIds,
	// List<FilterEntry> filterEntries) throws Exception {
	// EntityManager em = emc.get(Item.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// /** 运行FilterEntry */
	// List<String> part_docIds = new TreeList<>();
	// for (int i = 0; i < filterEntries.size(); i++) {
	// FilterEntry f = filterEntries.get(i);
	// List<String> os = new TreeList<>();
	// for (List<String> _batch_docIds : ListTools.batch(docIds,
	// SQL_STATEMENT_IN_BATCH)) {
	// CriteriaQuery<String> cq = cb.createQuery(String.class);
	// Root<Item> root = cq.from(Item.class);
	// Predicate p = f.toPredicate(cb, root, this.runtime, ItemCategory.cms);
	// p = cb.and(p, root.get(Item_.bundle).in(_batch_docIds));
	// cq.select(root.get(Item_.bundle)).distinct(true).where(p);
	// // System.out.println(">>>>>>>>>listBundle_filterEntry_SQL:" +
	// // em.createQuery(cq).toString() );
	// os.addAll(em.createQuery(cq).getResultList());
	// }
	// /** 不等于在这里单独通过等于处理 */
	// if (Comparison.isNotEquals(f.comparison)) {
	// os = ListUtils.subtract(docIds, os);
	// }
	// if (i == 0) {
	// part_docIds.addAll(os);
	// } else {
	// if (StringUtils.equals("and", f.logic)) {
	// part_docIds = ListUtils.intersection(part_docIds, os);
	// } else {
	// part_docIds = ListUtils.union(part_docIds, os);
	// }
	// }
	// }
	// docIds = ListUtils.intersection(docIds, part_docIds);
	// return docIds;
	// }

	public static class WhereEntry extends GsonPropertyObject {

		public WhereEntry() {
		}

		public Boolean accessible = false;
		public String scope = SCOPE_CMS_INFO;
		public List<AppInfoEntry> appInfoList = new TreeList<>();
		public List<CategoryEntry> categoryInfoList = new TreeList<>();
		public DateRangeEntry dateRange;
		public List<String> creatorPersonList;
		public List<String> creatorUnitList;
		public List<String> creatorIdentityList;

		Boolean available() {
			if ((!StringUtils.equals(this.scope, SCOPE_CMS_INFO)) && (!StringUtils.equals(this.scope, SCOPE_CMS_DATA))
					&& (!StringUtils.equals(this.scope, SCOPE_ALL))) {
				return false;
			}
			return true;
		}

		public static class AppInfoEntry {
			public String name;
			public String alias;
			public String id;
		}

		public static class CategoryEntry {
			public String name;
			public String alias;
			public String id;
		}

		/**
		 * 从组织查询条件，信息类文档
		 * 
		 * @param cb
		 * @param root
		 * @return
		 * @throws Exception
		 */
		private Predicate documentPredicate(CriteriaBuilder cb, Root<Document> root) throws Exception {
			List<Predicate> ps = new TreeList<>();
			ps.add(this.documentPredicate_appInfo(cb, root));
			ps.add(this.documentPredicate_date(cb, root));
			if (StringUtils.equals(this.scope, SCOPE_CMS_INFO)) {
				ps.add(cb.equal(root.get(Document_.documentType), "信息"));
				ps.add(this.documentPredicate_creator(cb, root));
			} else if (StringUtils.equals(this.scope, SCOPE_CMS_DATA)) {
				ps.add(cb.equal(root.get(Document_.documentType), "数据"));
			}
			ps = ListTools.trim(ps, true, false);
			if (ps.isEmpty()) {
				throw new Exception("where is empty.");
			}
			cb.and(ps.toArray(new Predicate[] {}));
			return cb.and(ps.toArray(new Predicate[] {}));
		}

		private Predicate documentPredicate_appInfo(CriteriaBuilder cb, Root<Document> root) throws Exception {
			List<String> _appInfo_ids = ListTools.extractField(this.appInfoList, AppInfo.id_FIELDNAME, String.class,
					true, true);
			List<String> _categoryInfo_ids = ListTools.extractField(this.categoryInfoList, CategoryInfo.id_FIELDNAME,
					String.class, true, true);
			_appInfo_ids = _appInfo_ids.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			_categoryInfo_ids = _categoryInfo_ids.stream().filter(o -> {
				return StringUtils.isNotEmpty(o);
			}).collect(Collectors.toList());
			if (_appInfo_ids.isEmpty() && _categoryInfo_ids.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!_appInfo_ids.isEmpty()) {
				p = cb.or(p, root.get(Document_.appId).in(_appInfo_ids));
			}
			if (!_categoryInfo_ids.isEmpty()) {
				p = cb.or(p, root.get(Document_.categoryId).in(_categoryInfo_ids));
			}
			return p;
		}

		private Predicate documentPredicate_creator(CriteriaBuilder cb, Root<Document> root) throws Exception {
			List<String> _creatorUnits = ListTools.trim(this.creatorUnitList, true, true);
			List<String> _creatorPersons = ListTools.trim(this.creatorPersonList, true, true);
			List<String> _creatorIdentitys = ListTools.trim(this.creatorIdentityList, true, true);
			if (_creatorUnits.isEmpty() && _creatorPersons.isEmpty() && _creatorIdentitys.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (!_creatorUnits.isEmpty()) {
				p = cb.or(p, root.get(Document_.creatorUnitName).in(_creatorUnits));
			}
			if (!_creatorPersons.isEmpty()) {
				p = cb.or(p, root.get(Document_.creatorPerson).in(_creatorPersons));
			}
			if (!_creatorIdentitys.isEmpty()) {
				p = cb.or(p, root.get(Document_.creatorIdentity).in(_creatorIdentitys));
			}
			return p;
		}

		private Predicate documentPredicate_date(CriteriaBuilder cb, Root<Document> root) throws Exception {
			if (null == this.dateRange || (!this.dateRange.available())) {
				return null;
			}
			if (null == this.dateRange.start) {
				return cb.lessThanOrEqualTo(root.get(Document_.publishTime), this.dateRange.completed);
			} else if (null == this.dateRange.completed) {
				return cb.greaterThanOrEqualTo(root.get(Document_.publishTime), this.dateRange.start);
			} else {
				return cb.between(root.get(Document_.publishTime), this.dateRange.start, this.dateRange.completed);
			}
		}
	}
}