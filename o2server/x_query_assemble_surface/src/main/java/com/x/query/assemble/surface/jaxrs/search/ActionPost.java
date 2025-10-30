package com.x.query.assemble.surface.jaxrs.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.grouping.FirstPassGroupingCollector;
import org.apache.lucene.search.grouping.SearchGroup;
import org.apache.lucene.search.grouping.TermGroupSelector;
import org.apache.lucene.search.grouping.TopGroupsCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import com.google.gson.JsonElement;
import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.express.assemble.surface.jaxrs.search.ActionPostWi;
import com.x.query.core.express.assemble.surface.jaxrs.search.ActionPostWo;
import com.x.query.core.express.index.Facets;
import com.x.query.core.express.index.Filter;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionPost extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPost.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		result.setData(wo);
		if (BooleanUtils.isNotTrue(Config.query().index().getSearchEnable())) {
			return result;
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<String> readers = readers(effectivePerson, wi);
		Optional<Directory> optional = (StringUtils.isEmpty(wi.getCategory()) || StringUtils.isEmpty(wi.getKey()))
				? Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true)
				: Indexs.directory(wi.getCategory(), wi.getKey(), true);
		if (optional.isEmpty()) {
			LOGGER.warn("search directory not exist.");
			return result;
		}
		try (Directory directory = optional.get();
				DirectoryReader reader = DirectoryReader.open(directory);
				Analyzer analyzer = new HanLPAnalyzer()) {
			Optional<Query> searchQuery = this.searchQuery(wi.getQuery(), analyzer);
			if (searchQuery.isEmpty()) {
				return result;
			}
			Optional<Query> readersQuery = Indexs.readersQuery(readers);
			List<Query> filterQueries = Indexs.filterQueries(wi.getFilterList());

			BooleanQuery.Builder qb = new BooleanQuery.Builder();
			if (readersQuery.isPresent()) {
				qb.add(readersQuery.get(), Occur.FILTER);
			}
			for (Query q : filterQueries) {
				qb.add(q, Occur.FILTER);
			}

			Query bodyQuery = bodyQuery(wi.getQuery(), analyzer);
			Query attachmentQuery = attachmentQuery(wi.getQuery(), analyzer);
			Query itemQuery = this.itemQuery(wi.getQuery());
			Query recencyQuery = this.recencyQuery();

			BooleanQuery query = qb.add(bodyQuery, BooleanClause.Occur.SHOULD)
					.add(attachmentQuery, BooleanClause.Occur.SHOULD).add(itemQuery, BooleanClause.Occur.SHOULD)
					.add(recencyQuery, BooleanClause.Occur.SHOULD).setMinimumNumberShouldMatch(1).build();

			LOGGER.debug("search lucene query:{}.", query::toString);
			IndexSearcher searcher = new IndexSearcher(reader);

			TopDocs pageDocs = page(searcher, query, wi.getPage(), wi.getSize());
			for (ScoreDoc sd : pageDocs.scoreDocs) {
				Document document = searcher.doc(sd.doc);
				Map<String, Object> map = DISPLAY_FIELDS.stream().map(f -> Pair.of(f, document.getField(f)))
						.filter(param -> null != param.second()).map(indexFieldValue)
						.collect(Collectors.toMap(Pair::first, Pair::second));
				wo.getDocumentList().add(map);
			}
			long total = pageDocs.totalHits == null ? wo.getDocumentList().size() : pageDocs.totalHits.value;
			wo.setCount(total);
			return result;
		}

//			Highlighter highlighter = highlighter(query);
//			final int facetMaxGroups = Config.query().index().getFacetMaxGroups();
//			final int searchMaxHits = Config.query().index().getSearchMaxHits();
//			// TopScoreDocCollector topScoreDocCollector =
//			// TopScoreDocCollector.create(searchMaxHits, Integer.MAX_VALUE);
//			List<Pair<String, FirstPassGroupingCollector<BytesRef>>> firstPassGroupingCollectorPairs = this
//					.adjustFacetField(wi.getFilterList().stream().map(Filter::getField).collect(Collectors.toList()))
//					.stream()
//					.<Pair<String, FirstPassGroupingCollector<BytesRef>>>map(o -> Pair.of(o,
//							new FirstPassGroupingCollector<>(new TermGroupSelector(o),
//									org.apache.lucene.search.Sort.INDEXORDER, facetMaxGroups)))
//					.collect(Collectors.toList());
//
//			List<Collector> collectors = firstPassGroupingCollectorPairs.stream().map(Pair::second)
//					.collect(Collectors.toList());
//
//			TopDocsCollector topDocsCollector = this.topDocsCollector(searchMaxHits, wi);
//			collectors.add(topDocsCollector);
//
//			searcher.search(query, MultiCollector.wrap(collectors));
//			writeDocument(searcher, analyzer, highlighter, topDocsCollector, start, rows, wo);
//			List<Pair<String, TopGroupsCollector<BytesRef>>> topGroupsCollectorPairs = firstPassGroupingCollectorPairs
//					.stream().<Pair<String, Optional<Collection<SearchGroup<BytesRef>>>>>map(param -> {
//						Collection<SearchGroup<BytesRef>> topGroups = null;
//						try {
//							topGroups = param.second().getTopGroups(0);
//						} catch (Exception e) {
//							LOGGER.error(e);
//						}
//						if (Objects.isNull(topGroups)) {
//							return Pair.of(param.first(), Optional.empty());
//						} else {
//							return Pair.of(param.first(), Optional.of(topGroups));
//						}
//					}).filter(o -> o.second().isPresent())
//					.<Pair<String, TopGroupsCollector<BytesRef>>>map(param -> Pair.of(param.first(),
//							new TopGroupsCollector<>(new TermGroupSelector(param.first()), param.second().get(),
//									org.apache.lucene.search.Sort.INDEXORDER, org.apache.lucene.search.Sort.INDEXORDER,
//									searchMaxHits, false)))
//					.collect(Collectors.toList());
//			if (!topGroupsCollectorPairs.isEmpty()) {
//				searcher.search(query, MultiCollector
//						.wrap(topGroupsCollectorPairs.stream().map(Pair::second).collect(Collectors.toList())));
//				wo.setFacetList(Facets.topGroupsCollector(topGroupsCollectorPairs));
//			}
//		}
//		return result;
	}

	private void writeDocument(IndexSearcher searcher, Analyzer analyzer, Highlighter highlighter,
			TopDocsCollector topDocsCollector, int start, int rows, Wo wo) {
		TopDocs topDocs = topDocsCollector.topDocs(start, rows);
		wo.setCount(topDocs.totalHits.value);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		if (null != scoreDocs) {
			Arrays.stream(scoreDocs).forEach(o -> {
				try {
					org.apache.lucene.document.Document document = searcher.doc(o.doc);
					Map<String, Object> map = DISPLAY_FIELDS.stream().map(f -> Pair.of(f, document.getField(f)))
							.filter(param -> null != param.second()).map(indexFieldValue)
							.collect(Collectors.toMap(Pair::first, Pair::second));
					// 高亮 null 安全
					String body = document.get(Indexs.FIELD_BODY);
					if (StringUtils.isNotBlank(body)) {
						String[] frags = highlighter.getBestFragments(analyzer, Indexs.FIELD_BODY, body,
								Config.query().index().getHighlightFragmentCount());
						if (frags != null && frags.length > 0) {
							map.put(Indexs.FIELD_HIGHLIGHTING, String.join(";", frags));
						}
					}
					wo.getDocumentList().add(map);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private Query recencyQuery() {
		long now = System.currentTimeMillis();
		long pivot = TimeUnit.DAYS.toMillis(7); // “半衰期”：与 now 相差 7 天时拿到一半加分
		return LongPoint.newDistanceFeatureQuery(Indexs.FIELD_UPDATETIME, 10f, now, pivot);
	}

	private Query bodyQuery(String search, Analyzer analyzer) throws ParseException {
		QueryParser bodyParser = new QueryParser(Indexs.FIELD_BODY, analyzer);
		return new BoostQuery(bodyParser.parse(QueryParserBase.escape(search)), 10f);
	}

	private Query attachmentQuery(String search, Analyzer analyzer) throws ParseException {
		QueryParser attachmentParser = new QueryParser(Indexs.FIELD_ATTACHMENT, analyzer);
		return new BoostQuery(attachmentParser.parse(QueryParserBase.escape(search)), 5f);
	}

	private Query itemQuery(String search) {
		Query exactItemQuery = new BoostQuery(new TermQuery(new Term(Indexs.FIELD_ITEMLIST, search)), 20f);
		Query prefixItemQuery = new BoostQuery(new PrefixQuery(new Term(Indexs.FIELD_ITEMLIST, search)), 15f);
		return new DisjunctionMaxQuery(Arrays.asList(exactItemQuery, prefixItemQuery), /* tieBreaker */ 0.0f);
	}

	private static TopDocs page(IndexSearcher searcher, Query q, int page, int size) throws IOException {
		ScoreDoc last = null;
		for (int i = 1; i < page; i++) {
			TopDocs td = searcher.searchAfter(last, q, size, org.apache.lucene.search.Sort.RELEVANCE);
			if (td.scoreDocs.length == 0) {
				last = null;
				break;
			} else {
				last = td.scoreDocs[td.scoreDocs.length - 1];
			}
		}
		return searcher.searchAfter(last, q, size, org.apache.lucene.search.Sort.RELEVANCE);
	}

	public static Highlighter highlighter(Query query) throws Exception {
		QueryScorer scorer = new QueryScorer(query);
		SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter(Config.query().index().getHighlightPre(),
				Config.query().index().getHighlightPost());
		Highlighter highlighter = new Highlighter(simpleHtmlFormatter, scorer);
		highlighter.setTextFragmenter(new SimpleFragmenter(Config.query().index().getHighlightFragmentSize()));
		return highlighter;
	}

	private static Function<Pair<String, IndexableField>, Pair<String, Object>> indexFieldValue = param -> {
		if (DATE_FIELDS.contains(param.first())) {
			Number number = param.second().numericValue();
			if (null != number) {
				return Pair.of(param.first(), new Date(number.longValue()));
			} else {
				return Pair.of(param.first(), null);
			}
		} else {
			return Pair.of(param.first(), param.second().stringValue());
		}
	};

	private TopDocsCollector topDocsCollector(int searchMaxHits, Wi wi) {
		if (null != wi.getSort() && StringUtils.isNotBlank(wi.getSort().getField())) {
			return TopFieldCollector.create(sort(wi.getSort()), searchMaxHits, Integer.MAX_VALUE);
		} else {
			return TopScoreDocCollector.create(searchMaxHits, Integer.MAX_VALUE);
		}
	}

	private List<String> readers(EffectivePerson effectivePerson, Wi wi) throws Exception {
		List<String> readers = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String person = business.index().who(effectivePerson, wi.getPerson());
			readers = business.index().determineReaders(person, Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE);
		}
		return readers;
	}

	private org.apache.lucene.search.Sort sort(Sort sort) {
		if (null == sort || StringUtils.isBlank(sort.getField())) {
			return new org.apache.lucene.search.Sort(new SortField(Indexs.FIELD_UPDATETIME, Type.LONG, true));
		} else {
			String fieldType = Indexs.judgeField(sort.getField()).third();
			if (StringUtils.equalsIgnoreCase(fieldType, Indexs.FIELD_TYPE_STRING)) {
				return new org.apache.lucene.search.Sort(new SortField(sort.getField(), Type.STRING,
						StringUtils.equals(sort.getOrder(), Sort.ORDER_DESC)));
			} else {
				return new org.apache.lucene.search.Sort(new SortField(sort.getField(), Type.LONG,
						StringUtils.equals(sort.getOrder(), Sort.ORDER_DESC)));
			}
		}
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.search.ActionPost$Wi")
	public static class Wi extends ActionPostWi {

		@FieldDescribe("排序")
		private Sort sort;

		public Sort getSort() {
			return sort;
		}

		public void setSort(Sort sort) {
			this.sort = sort;
		}

	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.search.ActionPost$Wo")
	public static class Wo extends ActionPostWo {

	}

}