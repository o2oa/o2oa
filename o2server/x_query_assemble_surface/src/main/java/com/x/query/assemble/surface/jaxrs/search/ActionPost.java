package com.x.query.assemble.surface.jaxrs.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
        Integer rows = Indexs.rows(wi.getSize());
        Integer start = Indexs.start(wi.getPage(), rows);
        List<String> readers = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            String person = business.index().who(effectivePerson, wi.getPerson());
            readers = business.index().determineReaders(person, Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE);
        }
        Analyzer analyzer = new HanLPAnalyzer();
        Optional<Query> searchQuery = this.searchQuery(wi.getQuery(), analyzer);
        if (searchQuery.isEmpty()) {
            return result;
        }
        Optional<Query> readersQuery = Indexs.readersQuery(readers);
        List<Query> filterQueries = Indexs.filterQueries(wi.getFilterList());

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        Stream.of(searchQuery, readersQuery).filter(Optional::isPresent)
                .forEach(o -> builder.add(o.get(), BooleanClause.Occur.MUST));
        filterQueries.stream().forEach(o -> builder.add(o, BooleanClause.Occur.MUST));
        Query query = builder.build();
        LOGGER.debug("search lucene query:{}.", query::toString);
        Optional<Directory> optional = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true);
        if (optional.isEmpty()) {
            LOGGER.warn("search directory not exist.");
            return result;
        }
        try (DirectoryReader reader = DirectoryReader.open(optional.get())) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Highlighter highlighter = highlighter(query);
            final int facetMaxGroups = Config.query().index().getFacetMaxGroups();
            final int searchMaxHits = Config.query().index().getSearchMaxHits();
            TopScoreDocCollector topScoreDocCollector = TopScoreDocCollector.create(searchMaxHits, Integer.MAX_VALUE);
            List<Pair<String, FirstPassGroupingCollector<BytesRef>>> firstPassGroupingCollectorPairs = this
                    .adjustFacetField(
                            wi.getFilterList().stream().map(Filter::getField).collect(Collectors.toList()))
                    .stream()
                    .<Pair<String, FirstPassGroupingCollector<BytesRef>>>map(
                            o -> Pair.of(o,
                                    new FirstPassGroupingCollector<>(new TermGroupSelector(o),
                                            org.apache.lucene.search.Sort.INDEXORDER,
                                            facetMaxGroups)))
                    .collect(Collectors.toList());

            List<Collector> collectors = firstPassGroupingCollectorPairs.stream().map(Pair::second)
                    .collect(Collectors.toList());

            collectors.add(topScoreDocCollector);

            searcher.search(query, MultiCollector.wrap(collectors));
            writeDocument(searcher, analyzer, highlighter, topScoreDocCollector, start, rows, wo);
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
                    }).filter(o -> o.second().isPresent())
                    .<Pair<String, TopGroupsCollector<BytesRef>>>map(param -> Pair.of(param.first(),
                            new TopGroupsCollector<>(new TermGroupSelector(param.first()), param.second().get(),
                                    org.apache.lucene.search.Sort.INDEXORDER, org.apache.lucene.search.Sort.INDEXORDER,
                                    searchMaxHits,
                                    false)))
                    .collect(Collectors.toList());
            if (!topGroupsCollectorPairs.isEmpty()) {
                searcher.search(query, MultiCollector
                        .wrap(topGroupsCollectorPairs.stream().map(Pair::second).collect(Collectors.toList())));
                wo.setFacetList(Facets.topGroupsCollector(topGroupsCollectorPairs));
            }
        }
        return result;
    }

    private void writeDocument(IndexSearcher searcher, Analyzer analyzer, Highlighter highlighter,
            TopScoreDocCollector topScoreDocCollector, int start, int rows, Wo wo) {
        TopDocs topDocs = topScoreDocCollector.topDocs(start, rows);
        wo.setCount(topDocs.totalHits.value);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        if (null != scoreDocs) {
            Arrays.stream(scoreDocs).forEach(o -> {
                try {
                    org.apache.lucene.document.Document document = searcher.doc(o.doc);
                    Map<String, Object> map = DISPLAY_FIELDS.stream().map(f -> Pair.of(f, document.getField(f)))
                            .filter(param -> null != param.second()).map(indexFieldValue)
                            .collect(Collectors.toMap(Pair::first, Pair::second));
                    map.put(Indexs.FIELD_HIGHLIGHTING,
                            StringUtils.join(highlighter.getBestFragments(analyzer, Indexs.FIELD_BODY,
                                    document.get(Indexs.FIELD_BODY),
                                    Config.query().index().getHighlightFragmentCount()), ";"));
                    wo.getDocumentList().add(map);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            });
        }
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

    @Schema(name = "com.x.custom.index.assemble.control.jaxrs.search.ActionPost$Wi")
    public static class Wi extends ActionPostWi {

    }

    @Schema(name = "com.x.custom.index.assemble.control.jaxrs.search.ActionPost$Wo")
    public static class Wo extends ActionPostWo {

    }

}