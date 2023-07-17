package com.x.query.core.express.plan;

import java.util.ArrayList;
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
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.Review_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class CmsPlan extends Plan {

	private static final long serialVersionUID = -752841556895959631L;

	public CmsPlan() {
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
		// 根据where条件查询符合条件的所有文档ID列表
		List<String> docIds = listBundleDocument();
		if (BooleanUtils.isTrue(this.where.accessible) && StringUtils.isNotEmpty(runtime.person)) {
			// 过滤可见范围
			docIds = this.listBundleAccessible(docIds, runtime.person);
		}
		// 针对DataItem进行判断和条件过滤
		List<FilterEntry> filterEntries = new TreeList<>();
		for (FilterEntry o : ListTools.trim(this.filterList, true, true)) {
			if (BooleanUtils.isTrue(o.available())) {
				filterEntries.add(o);
			}
		}
		if (!filterEntries.isEmpty()) {
			docIds = listBundleFilterEntry(docIds, filterEntries);
		}
		filterEntries.clear();
		for (FilterEntry o : ListTools.trim(this.runtime.filterList, true, true)) {
			if (BooleanUtils.isTrue(o.available())) {
				filterEntries.add(o);
			}
		}
		if (!filterEntries.isEmpty()) {
			docIds = listBundleFilterEntry(docIds, filterEntries);
		}
		return docIds;
	}

	/**
	 * 过滤信息类型的文档
	 *
	 * @return
	 * @throws Exception
	 */
	private List<String> listBundleDocument() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Document.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Document> root = cq.from(Document.class);
			cq.select(root.get(Document_.id))
					.where(this.where.documentPredicate(cb, root, this.runtime, this.filterList));
			List<String> docIds = em.createQuery(cq).getResultList();
			return docIds.stream().distinct().collect(Collectors.toList());
		}
	}

	private List<String> listBundleAccessible(List<String> docIds, String person) throws Exception {
		List<String> list = new TreeList<>();
		List<CompletableFuture<List<String>>> futures = new TreeList<>();
		for (List<String> documentId : ListTools.batch(docIds, 500)) {
			CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					EntityManager em = emc.get(Review.class);
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<String> cq = cb.createQuery(String.class);
					Root<Review> root = cq.from(Review.class);
					final HashMap<String, String> map = new HashMap<>();
					documentId.stream().forEach(o -> map.put(o, o));
					Expression<Set<String>> expression = cb.keys(map);
					Predicate p = cb.isMember(root.get(Review_.docId), expression);
					p = cb.and(p, cb.or(cb.equal(root.get(Review_.permissionObj), person),
							cb.equal(root.get(Review_.permissionObj), "*")));
					cq.select(root.get(Review_.docId)).where(p);
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
		}
		return list;
	}

	private List<String> listBundleFilterEntry(List<String> docIds, List<FilterEntry> filterEntries) throws Exception {
		List<String> partDocIds = new TreeList<>();
		List<List<String>> batchDocIds = ListTools.batch(docIds, 500);
		for (int i = 0; i < filterEntries.size(); i++) {
			FilterEntry f = filterEntries.get(i);
			List<String> os = new TreeList<>();
			List<CompletableFuture<List<String>>> futures = new TreeList<>();
			for (List<String> _batch : batchDocIds) {
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
			}
			// 不等于在这里单独通过等于处理
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

	public static class WhereEntry extends GsonPropertyObject {

		private static final long serialVersionUID = 7855901184336837554L;

		public Boolean accessible = false;
		public Boolean draft = false;
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
		private Predicate documentPredicate(CriteriaBuilder cb, Root<Document> root, Runtime runtime,
				List<FilterEntry> filterList) throws Exception {
			List<Predicate> ps = new TreeList<>();
			ps.add(this.documentPredicateCreator(cb, root));
			ps.add(this.documentPredicateAppInfo(cb, root));
			ps.add(this.documentPredicateDate(cb, root));
			ps.add(this.documentPredicateFilterOr(cb, root, runtime, filterList));
			ps.add(this.documentPredicateFilterAnd(cb, root, runtime, filterList));
			ps.add(this.documentPredicateDraft(cb, root));

			Predicate predicate = this.documentPredicateTypeScope(cb, root);
			if (predicate != null) {
				ps.add(predicate);
			}

			ps = ListTools.trim(ps, true, false);
			if (ps.isEmpty()) {
				throw new Exception("where is empty.");
			}
			cb.and(ps.toArray(new Predicate[] {}));
			return cb.and(ps.toArray(new Predicate[] {}));
		}

		private Predicate documentPredicateAppInfo(CriteriaBuilder cb, Root<Document> root) throws Exception {
			List<String> appInfoIds = ListTools.extractField(this.appInfoList, AppInfo.id_FIELDNAME, String.class, true,
					true);
			List<String> categoryInfoIds = ListTools.extractField(this.categoryInfoList, CategoryInfo.id_FIELDNAME,
					String.class, true, true);
			appInfoIds = appInfoIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
			categoryInfoIds = categoryInfoIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
			if (appInfoIds.isEmpty() && categoryInfoIds.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (ListTools.isNotEmpty(appInfoIds)) {
				if (appInfoIds.size() == 1) {
					p = cb.or(p, cb.equal(root.get(Document_.appId), appInfoIds.get(0)));
				} else {
					p = cb.or(p, root.get(Document_.appId).in(appInfoIds));
				}
			}
			if (ListTools.isNotEmpty(categoryInfoIds)) {
				if (categoryInfoIds.size() == 1) {
					p = cb.or(p, cb.equal(root.get(Document_.categoryId), categoryInfoIds.get(0)));
				} else {
					p = cb.or(p, root.get(Document_.categoryId).in(categoryInfoIds));
				}
			}
			return p;
		}

		private Predicate documentPredicateCreator(CriteriaBuilder cb, Root<Document> root) throws Exception {
			List<String> creatorUnits = ListTools.trim(this.creatorUnitList, true, true);
			List<String> creatorPersons = ListTools.trim(this.creatorPersonList, true, true);
			List<String> creatorIdentitys = ListTools.trim(this.creatorIdentityList, true, true);
			if (creatorUnits.isEmpty() && creatorPersons.isEmpty() && creatorIdentitys.isEmpty()) {
				return null;
			}
			Predicate p = cb.disjunction();
			if (ListTools.isNotEmpty(creatorUnits)) {
				if (creatorUnits.size() == 1) {
					p = cb.or(p, cb.equal(root.get(Document_.creatorUnitName), creatorUnits.get(0)));
				} else {
					p = cb.or(p, root.get(Document_.creatorUnitName).in(creatorUnits));
				}
			}
			if (ListTools.isNotEmpty(creatorPersons)) {
				if (creatorPersons.size() == 1) {
					p = cb.or(p, cb.equal(root.get(Document_.creatorPerson), creatorPersons.get(0)));
				} else {
					p = cb.or(p, root.get(Document_.creatorPerson).in(creatorPersons));
				}
			}
			if (ListTools.isNotEmpty(creatorIdentitys)) {
				if (creatorIdentitys.size() == 1) {
					p = cb.or(p, cb.equal(root.get(Document_.creatorIdentity), creatorIdentitys.get(0)));
				} else {
					p = cb.or(p, root.get(Document_.creatorIdentity).in(creatorIdentitys));
				}
			}
			return p;
		}

		private Predicate documentPredicateDate(CriteriaBuilder cb, Root<Document> root) throws Exception {
			if (null == this.dateRange || (!this.dateRange.available())) {
				return null;
			}
			Expression<Date> var1 = root.get(Document_.publishTime);
			if (BooleanUtils.isTrue(this.draft)) {
				var1 = root.get(Document_.updateTime);
			}
			if (null == this.dateRange.start) {
				return cb.lessThanOrEqualTo(var1, this.dateRange.completed);
			} else if (null == this.dateRange.completed) {
				return cb.greaterThanOrEqualTo(var1, this.dateRange.start);
			} else {
				return cb.between(var1, this.dateRange.start, this.dateRange.completed);
			}
		}

		private Predicate documentPredicateTypeScope(CriteriaBuilder cb, Root<Document> root) {
			if (StringUtils.equals(this.scope, SCOPE_CMS_DATA)) {
				return cb.equal(root.get(Document_.documentType), "数据");
			} else if (StringUtils.equals(this.scope, SCOPE_CMS_INFO)) {
				return cb.equal(root.get(Document_.documentType), "信息");
			}
			return null;
		}

		private Predicate documentPredicateDraft(CriteriaBuilder cb, Root<Document> root) {
			if (BooleanUtils.isFalse(this.draft)) {
				return cb.equal(root.get(Document_.docStatus), "published");
			}
			return null;
		}

		private Predicate documentPredicateFilterOr(CriteriaBuilder cb, Root<Document> root, Runtime runtime,
				List<FilterEntry> filterList) throws Exception {
			boolean flag = true;
			Predicate p = cb.disjunction();
			List<FilterEntry> list = new ArrayList<>();
			list.addAll(filterList);
			if (runtime.filterList != null) {
				list.addAll(runtime.filterList);
			}
			for (FilterEntry filterEntry : list) {
				if (filterEntry.path.indexOf("(") > -1 && filterEntry.path.indexOf(")") > -1) {
					String path = StringUtils.substringBetween(filterEntry.path, "(", ")").trim();
					if ("readPersonList".equals(path)) {
						flag = false;
						p = cb.or(p, cb.isMember("所有人", root.get(Document_.readPersonList)));
						p = cb.or(p, cb.isMember(runtime.person, root.get(Document_.readPersonList)));
						if (runtime.person.indexOf("@") > -1) {
							p = cb.or(p, cb.isMember(StringUtils.substringAfter(runtime.person, "@"),
									root.get(Document_.readPersonList)));
						}
					} else if ("readUnitList".equals(path)) {
						flag = false;
						if (ListTools.isNotEmpty(runtime.unitAllList)) {
							p = cb.or(p, root.get(Document_.readUnitList).in(runtime.unitAllList));
						}
					} else if ("readGroupList".equals(path)) {
						flag = false;
						if (ListTools.isNotEmpty(runtime.groupList)) {
							p = cb.or(p, root.get(Document_.readGroupList).in(runtime.groupList));
						}
					} else {
						if (!StringUtils.equals("and", filterEntry.logic)) {
							Predicate fp = filterEntry.toCmsDocumentPredicate(cb, root, runtime, path);
							flag = false;
							p = cb.or(p, fp);
						}
					}
				}
			}
			if (flag) {
				return null;
			}
			return p;
		}

		private Predicate documentPredicateFilterAnd(CriteriaBuilder cb, Root<Document> root, Runtime runtime,
				List<FilterEntry> filterList) throws Exception {
			boolean flag = true;
			Predicate p = cb.conjunction();
			List<FilterEntry> list = new ArrayList<>();
			list.addAll(filterList);
			if (runtime.filterList != null) {
				list.addAll(runtime.filterList);
			}
			for (FilterEntry filterEntry : list) {
				if (filterEntry.path.indexOf("(") > -1 && filterEntry.path.indexOf(")") > -1) {
					String path = StringUtils.substringBetween(filterEntry.path, "(", ")").trim();
					if (!"readPersonList".equals(path) && !"readUnitList".equals(path)
							&& (!"readGroupList".equals(path)) && (StringUtils.equals("and", filterEntry.logic))) {
						Predicate fp = filterEntry.toCmsDocumentPredicate(cb, root, runtime, path);
						flag = false;
						p = cb.and(p, fp);
					}
				}
			}
			if (flag) {
				return null;
			}
			return p;
		}
	}
}
