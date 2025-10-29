package com.x.query.assemble.surface.jaxrs.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import com.google.gson.Gson;
import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.View;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.plan.CmsPlan;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Runtime;

public class ExecuteV2Search extends BaseAction {

	private static Logger LOGGER = LoggerFactory.getLogger(ExecuteV2Search.class);

	private static Gson gson = XGsonBuilder.instance();

	public Pair<List<String>, Long> search(View view, Runtime runtime, String search, Integer page, Integer size)
			throws Exception {
		if (View.TYPE_CMS.equals(view.getType())) {
			CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
			final List<String> appInfoList = new ArrayList<>();
			final List<String> catgoryInfoList = new ArrayList<>();
			cmsPlan.where.appInfoList.stream().forEach(o -> appInfoList.add(o.id));
			cmsPlan.where.categoryInfoList.stream().forEach(o -> catgoryInfoList.add(o.id));
			return cms();
		} else {
			ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
			this.setProcessEdition(processPlatformPlan);
			final List<String> processIdList = new ArrayList<>();
			processPlatformPlan.where.processList.stream().forEach(o -> processIdList.add(o.id));
			return processPlatform(processIdList, runtime.authList, search.toLowerCase(), page, size);
		}

	}

	private Pair<List<String>, Long> cms() {
		List<String> list = new ArrayList<>();
		try {

		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Pair.of(list, 0L);
	}

	private Pair<List<String>, Long> processPlatform(List<String> processList, List<String> authList, String search,
			Integer page, Integer size) {
		List<String> list = new ArrayList<>();
		Optional<Directory> opt = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true);
		if (opt.isEmpty()) {
			LOGGER.warn("search directory not exist.");
			return Pair.of(list, 0L);
		}
		try (HanLPAnalyzer analyzer = new HanLPAnalyzer();
				Directory directory = opt.get();
				DirectoryReader reader = DirectoryReader.open(directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);

			Query permissionFilter = new TermInSetQuery(Indexs.FIELD_READERS, authList.stream().filter(Objects::nonNull)
					.<BytesRef>map(BytesRef::new).collect(Collectors.toList()));

			Query processFilter = new TermInSetQuery(Indexs.FIELD_PROCESS, processList.stream().filter(Objects::nonNull)
					.<BytesRef>map(BytesRef::new).collect(Collectors.toList()));

			QueryParser bodyParser = new QueryParser(Indexs.FIELD_BODY, analyzer);
			Query boostedBodyQuery = new BoostQuery(bodyParser.parse(QueryParserBase.escape(search)), 10f);

			QueryParser attachmentParser = new QueryParser(Indexs.FIELD_ATTACHMENT, analyzer);
			Query boostedAttachmentQuery = new BoostQuery(attachmentParser.parse(QueryParserBase.escape(search)), 5f);

			Query exactItemQuery = new BoostQuery(new TermQuery(new Term(Indexs.FIELD_ITEMLIST, search)), 20f);
			Query prefixItemQuery = new BoostQuery(new PrefixQuery(new Term(Indexs.FIELD_ITEMLIST, search)), 15f);
			DisjunctionMaxQuery itemListQuery = new DisjunctionMaxQuery(Arrays.asList(exactItemQuery, prefixItemQuery),
					/* tieBreaker */ 0.0f);

			BooleanQuery.Builder qb = new BooleanQuery.Builder();
			qb.add(permissionFilter, BooleanClause.Occur.FILTER); // 不参与打分
			qb.add(processFilter, BooleanClause.Occur.FILTER); // 不参与打分

			BooleanQuery query = qb.add(boostedBodyQuery, BooleanClause.Occur.SHOULD)
					.add(boostedAttachmentQuery, BooleanClause.Occur.SHOULD)
					.add(itemListQuery, BooleanClause.Occur.SHOULD).setMinimumNumberShouldMatch(1).build();

			// Page with searchAfter
			TopDocs pageDocs = page(searcher, query, page, size);
			for (ScoreDoc sd : pageDocs.scoreDocs) {
				Document document = searcher.doc(sd.doc);
				list.add(document.getField(Indexs.FIELD_ID).stringValue());
			}
			long total = pageDocs.totalHits == null ? list.size() : pageDocs.totalHits.value;
			return Pair.of(list, total);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Pair.of(list, 0L);
	}

	/**
	 * Paginate results using searchAfter. This does not require holding state
	 * between requests. For page N, we iterate N-1 times to get the last ScoreDoc
	 * and then fetch the next chunk.
	 */
	private static TopDocs page(IndexSearcher searcher, Query q, int page, int size) throws IOException {
		ScoreDoc last = null;
		for (int i = 1; i < page; i++) {
			TopDocs td = searcher.searchAfter(last, q, size);
			if (td.scoreDocs.length == 0) {
				return emptyTopDocs();
			}
			last = td.scoreDocs[td.scoreDocs.length - 1];
		}
		return searcher.searchAfter(last, q, size);
	}

	private static TopDocs emptyTopDocs() {
		return new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[0]);
	}

}
