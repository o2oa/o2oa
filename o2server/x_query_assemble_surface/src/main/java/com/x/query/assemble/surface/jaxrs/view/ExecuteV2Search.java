package com.x.query.assemble.surface.jaxrs.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import com.google.gson.Gson;
import com.hankcs.lucene.HanLPAnalyzer;
import com.rometools.utils.Strings;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.View;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.plan.CmsPlan;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Runtime;

public class ExecuteV2Search extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteV2Search.class);

	private static Gson gson = XGsonBuilder.instance();

	public Pair<List<String>, Long> search(View view, Runtime runtime, String search, Integer page, Integer size)
			throws Exception {
		List<String> list = new ArrayList<>();
		Optional<Directory> opt = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true);
		if (Strings.isBlank(search)) {
			LOGGER.warn("search query string is empty.");
			return Pair.of(list, 0L);
		}
		if (opt.isEmpty()) {
			LOGGER.warn("search directory not exist.");
			return Pair.of(list, 0L);
		}
		try (HanLPAnalyzer analyzer = new HanLPAnalyzer();
				Directory directory = opt.get();
				DirectoryReader reader = DirectoryReader.open(directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);

			Query permissionFilter = termInSet(Indexs.FIELD_READERS, runtime.authList);
			Query scopeFilter = scopeFilter(view, runtime);
			Query bodyQuery = bodyQuery(search, analyzer);
			Query attachmentQuery = attachmentQuery(search, analyzer);
			Query itemQuery = this.itemQuery(search);
			Query recencyQuery = this.recencyQuery();

			// 1) 关键词子查询：至少一个字段要命中
			BooleanQuery.Builder keywordShoulds = new BooleanQuery.Builder();
			keywordShoulds.add(bodyQuery, BooleanClause.Occur.SHOULD);
			keywordShoulds.add(attachmentQuery, BooleanClause.Occur.SHOULD);
			keywordShoulds.add(itemQuery, BooleanClause.Occur.SHOULD);
			keywordShoulds.setMinimumNumberShouldMatch(1);

			// 2) 外层 Query：权限/范围做 FILTER，关键词子查询做 MUST，时间加分做 SHOULD
			BooleanQuery.Builder qb = new BooleanQuery.Builder();
			qb.add(permissionFilter, BooleanClause.Occur.FILTER); // 不计分
			qb.add(scopeFilter, BooleanClause.Occur.FILTER); // 不计分
			qb.add(keywordShoulds.build(), BooleanClause.Occur.MUST); // 👈 必须至少有一个关键词命中
			qb.add(recencyQuery, BooleanClause.Occur.SHOULD); // 👈 只加分，不影响是否命中

			BooleanQuery query = qb.build();

			// Page with searchAfter
			TopDocs pageDocs = page(searcher, query, page, size);
			for (ScoreDoc sd : pageDocs.scoreDocs) {
				Document document = searcher.doc(sd.doc);
				list.add(document.getField(Indexs.FIELD_ID).stringValue());
				if (LOGGER.isDebugEnabled()) {
					Explanation exp = searcher.explain(query, sd.doc);
					LOGGER.debug("id={}, explain:{}", document.get(Indexs.FIELD_ID), exp.toString());
				}
			}
			long total = pageDocs.totalHits == null ? list.size() : pageDocs.totalHits.value;
			return Pair.of(list, total);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Pair.of(list, 0L);
	}

	private Query scopeFilter(View view, Runtime runtime) throws Exception {
		if (View.TYPE_CMS.equals(view.getType())) {
			return this.cmsScopeFilter(view).orElseThrow(() -> new ExceptionAccessDenied(runtime.person));
		} else {
			return this.processPlatformScopeFilter(view).orElseThrow(() -> new ExceptionAccessDenied(runtime.person));
		}
	}

	private Optional<Query> cmsScopeFilter(View view) {
		CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
		final List<String> appInfoList = new ArrayList<>();
		final List<String> categoryInfoList = new ArrayList<>();
		cmsPlan.where.appInfoList.stream().forEach(o -> appInfoList.add(o.id));
		cmsPlan.where.categoryInfoList.stream().forEach(o -> categoryInfoList.add(o.id));
		if (appInfoList.isEmpty() && categoryInfoList.isEmpty()) {
			return Optional.empty();
		}
		if (!appInfoList.isEmpty() && !categoryInfoList.isEmpty()) {
			BooleanQuery.Builder b = new BooleanQuery.Builder();
			b.add(termInSet(Indexs.FIELD_APPID, appInfoList), BooleanClause.Occur.SHOULD);
			b.add(termInSet(Indexs.FIELD_CATEGORYID, categoryInfoList), BooleanClause.Occur.SHOULD);
			b.setMinimumNumberShouldMatch(1);
			return Optional.of(b.build());
		} else if (!appInfoList.isEmpty()) {
			return Optional.of(termInSet(Indexs.FIELD_APPID, appInfoList));
		} else {
			return Optional.of(termInSet(Indexs.FIELD_CATEGORYID, categoryInfoList));
		}
	}

	private Optional<Query> processPlatformScopeFilter(View view) throws Exception {
		ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
		this.setProcessEdition(processPlatformPlan);
		final List<String> processIdList = new ArrayList<>();
		processPlatformPlan.where.processList.stream().forEach(o -> processIdList.add(o.id));
		if (processIdList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(termInSet(Indexs.FIELD_PROCESS, processIdList));
	}

	/**
	 * Paginate results using searchAfter. This does not require holding state
	 * between requests. For page N, we iterate N-1 times to get the last ScoreDoc
	 * and then fetch the next chunk.
	 */
	private static TopDocs page(IndexSearcher searcher, Query q, int page, int size) throws IOException {
		ScoreDoc last = null;
		for (int i = 1; i < page; i++) {
			TopDocs td = searcher.searchAfter(last, q, size, Sort.RELEVANCE);
			if (td.scoreDocs.length == 0) {
				return emptyTopDocs();
			}
			last = td.scoreDocs[td.scoreDocs.length - 1];
		}
		return searcher.searchAfter(last, q, size, Sort.RELEVANCE);
	}

	private static Query termInSet(String field, List<String> values) {
		List<BytesRef> brs = new ArrayList<>(values.size());
		for (String v : values) {
			brs.add(new BytesRef(v));
		}
		return new TermInSetQuery(field, brs);
	}

	private static TopDocs emptyTopDocs() {
		return new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[0]);
	}

	private Query recencyQuery() {
		long now = System.currentTimeMillis();
		long pivot = TimeUnit.DAYS.toMillis(21); // “半衰期”：与 now 相差 n 天时拿到一半加分
		return LongPoint.newDistanceFeatureQuery(Indexs.FIELD_UPDATETIME, 5f, now, pivot);
	}

	private Query bodyQuery(String search, Analyzer analyzer) throws ParseException {
		QueryParser bodyParser = new QueryParser(Indexs.FIELD_BODY, analyzer);
		return new BoostQuery(bodyParser.parse(QueryParserBase.escape(search)), 5f);
	}

	private Query attachmentQuery(String search, Analyzer analyzer) throws ParseException {
		QueryParser attachmentParser = new QueryParser(Indexs.FIELD_ATTACHMENT, analyzer);
		return new BoostQuery(attachmentParser.parse(QueryParserBase.escape(search)), 3f);
	}

	private Query itemQuery(String search) {
		Query exactItemQuery = new BoostQuery(new TermQuery(new Term(Indexs.FIELD_ITEMLIST, search)), 10f);
		Query prefixItemQuery = new BoostQuery(new PrefixQuery(new Term(Indexs.FIELD_ITEMLIST, search)), 5f);
		Query suffixItemQuery = new BoostQuery(new PrefixQuery(
				new Term(Indexs.FIELD_REVERSEDITEMLIST, new StringBuilder(search).reverse().toString())), 5f);
		return new DisjunctionMaxQuery(Arrays.asList(exactItemQuery, prefixItemQuery, suffixItemQuery),
				/* tieBreaker */ 0.0f);
	}

}
