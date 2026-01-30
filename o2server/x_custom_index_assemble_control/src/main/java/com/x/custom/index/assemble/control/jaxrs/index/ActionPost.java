package com.x.custom.index.assemble.control.jaxrs.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.grouping.FirstPassGroupingCollector;
import org.apache.lucene.search.grouping.SearchGroup;
import org.apache.lucene.search.grouping.TermGroupSelector;
import org.apache.lucene.search.grouping.TopGroupsCollector;
import org.apache.lucene.util.BytesRef;

import com.google.gson.JsonElement;
import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.bean.tuple.Quadruple;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;
import com.x.query.core.express.assemble.surface.jaxrs.index.ActionPostWi;
import com.x.query.core.express.assemble.surface.jaxrs.index.ActionPostWo;
import com.x.query.core.express.index.Facets;
import com.x.query.core.express.index.Filter;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.Sort;
import com.x.query.core.express.index.WoField;

class ActionPost extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPost.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		result.setData(wo);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		Integer rows = Indexs.rows(wi.getSize());
		Integer start = Indexs.start(wi.getPage(), rows);

		Set<String> readers = resolveReaders(effectivePerson, wi);

		List<String> categories = this.categories(wi.getDirectoryList());

		initWo(wo, categories);

		Optional<Query> searchQuery = searchQuery(wi.getQuery(), new HanLPAnalyzer());
		Optional<Query> readersQuery = Indexs.readersQuery(readers);
		List<Query> filterQueries = Indexs.filterQueries(wi.getFilterList());
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		Stream.of(searchQuery, readersQuery).filter(Optional::isPresent)
				.forEach(o -> builder.add(o.get(), BooleanClause.Occur.MUST));
		filterQueries.stream().forEach(o -> builder.add(o, BooleanClause.Occur.MUST));
		Query query = builder.build();
		LOGGER.debug("index lucene query:{}.", query::toString);
		IndexReader[] indexReaders = Indexs.indexReaders(wi.getDirectoryList());
		if (indexReaders.length == 0) {
			return result;
		}
		try (MultiReader multiReader = new MultiReader(indexReaders)) {
			IndexSearcher searcher = new IndexSearcher(multiReader);
			wo.setDynamicFieldList(getDynamicFieldList(categories, multiReader));
			TopFieldCollector topFieldCollector = TopFieldCollector.create(sort(wi.getSort()),
					Config.query().index().getSearchMaxHits(), Config.query().index().getSearchMaxHits());
			List<Pair<String, FirstPassGroupingCollector<BytesRef>>> firstPassGroupingCollectorPairs = Indexs
					.adjustFacetField(categories,
							wi.getFilterList().stream().map(Filter::getField).collect(Collectors.toList()))
					.stream().<Pair<String, FirstPassGroupingCollector<BytesRef>>>map(o -> {
						try {
							return Pair.of(o,
									new FirstPassGroupingCollector<>(new TermGroupSelector(o),
											org.apache.lucene.search.Sort.INDEXORDER,
											Config.query().index().getFacetMaxGroups()));
						} catch (Exception ex) {
							LOGGER.error(ex);
						}
						return null;
					}).filter(o -> !Objects.isNull(o)).collect(Collectors.toList());

			List<Collector> collectors = firstPassGroupingCollectorPairs.stream().map(Pair::second)
					.collect(Collectors.toList());
			collectors.add(topFieldCollector);
			searcher.search(query, MultiCollector.wrap(collectors));
			writeDocument(searcher, topFieldCollector, start, rows, wo, wi.getFixedFieldList(),
					wi.getDynamicFieldList());
			List<Pair<String, TopGroupsCollector<BytesRef>>> topGroupsCollectorPairs = firstPassGroupingCollectorPairs
					.stream().<Pair<String, Optional<Collection<SearchGroup<BytesRef>>>>>map(param -> {
						Collection<SearchGroup<BytesRef>> topGroups = null;
						try {
							topGroups = param.second().getTopGroups(0);
						} catch (Exception e) {
							LOGGER.error(e);
						}
						if (Objects.isNull(topGroups)) {
							return Pair.of(param.first(), Optional.empty());
						} else {
							return Pair.of(param.first(), Optional.of(topGroups));
						}
					}).filter(o -> o.second().isPresent()).<Pair<String, TopGroupsCollector<BytesRef>>>map(param -> {
						try {
							return Pair.of(param.first(),
									new TopGroupsCollector<>(new TermGroupSelector(param.first()), param.second().get(),
											org.apache.lucene.search.Sort.INDEXORDER,
											org.apache.lucene.search.Sort.INDEXORDER,
											Config.query().index().getSearchMaxHits(), false));
						} catch (Exception e) {
							LOGGER.error(e);
						}
						return null;
					}).filter(o -> !Objects.isNull(o)).collect(Collectors.toList());
			if (!topGroupsCollectorPairs.isEmpty()) {
				searcher.search(query, MultiCollector
						.wrap(topGroupsCollectorPairs.stream().map(Pair::second).collect(Collectors.toList())));
				wo.setFacetList(Facets.topGroupsCollector(topGroupsCollectorPairs));
			}
		}
		return result;
	}

	private Set<String> resolveReaders(EffectivePerson effectivePerson, Wi wi) throws Exception {
		Set<String> readers = new TreeSet<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (StringUtils.isNotBlank(wi.getRevealId())) {
				Reveal reveal = emc.flag(wi.getRevealId(), Reveal.class);
				if ((null != reveal) && (BooleanUtils.isTrue(reveal.getIgnorePermission()))) {
					return readers;
				}
			}
			String person = business.index().who(effectivePerson, wi.getPerson());
			wi.getDirectoryList().stream().forEach(o -> {
				try {
					readers.addAll(business.index().determineReaders(person, o.getCategory(), o.getKey()));
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
		return readers;
	}

	private void initWo(Wo wo, List<String> categories) {
		wo.setFixedFieldList(Business.listFixedField(categories));
	}

	private void writeDocument(IndexSearcher searcher, TopFieldCollector topFieldCollector, int start, int rows, Wo wo,
			List<String> fixedFieldList, List<String> dynamicFieldList) {
		List<String> outFields = outFields(wo, fixedFieldList, dynamicFieldList);
		TopDocs topDocs = topFieldCollector.topDocs(start, rows);
		wo.setCount(topDocs.totalHits.value);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		if (null != scoreDocs) {
			Arrays.stream(scoreDocs).forEach(o -> {
				try {
					org.apache.lucene.document.Document document = searcher.doc(o.doc);
					Map<String, Object> map = outFields.stream()
							.map(f -> Quadruple.of(Indexs.judgeField(f), document.getFields(f)))
							.filter(param -> param.fourth().length > 0)
							.map(p -> Pair.of(p.first(), Indexs.indexableFieldValue(p.fourth(), p.third())))
							.collect(Collectors.toMap(Pair::first, Pair::second));
					wo.getDocumentList().add(map);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private List<String> outFields(Wo wo, List<String> fixedFieldList, List<String> dynamicFieldList) {
		List<String> list = new ArrayList<>();
		list.add(Indexs.FIELD_ID);
		list.add(Indexs.FIELD_CATEGORY);
		list.add(Indexs.FIELD_TYPE);
		if (ListTools.isEmpty(fixedFieldList) && ListTools.isEmpty(dynamicFieldList)) {
			list.addAll(wo.getFixedFieldList().stream().map(WoField::getField).collect(Collectors.toList()));
		} else {
			list.addAll(fixedFieldList);
			list.addAll(dynamicFieldList);
		}
		return list;
	}

	public class Wo extends ActionPostWo {

		private static final long serialVersionUID = 3751674531291729956L;

	}

	public class Wi extends ActionPostWi {

		private static final long serialVersionUID = -4646809016933808952L;

		@FieldDescribe("展示标识")
		private String revealId;

		public String getRevealId() {
			return revealId;
		}

		public void setRevealId(String revealId) {
			this.revealId = revealId;
		}

	}

}
