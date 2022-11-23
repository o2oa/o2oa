package com.x.query.assemble.surface.jaxrs.index;

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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.bean.tuple.Quadruple;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.express.assemble.surface.jaxrs.index.ActionPostWi;
import com.x.query.core.express.assemble.surface.jaxrs.index.ActionPostWo;
import com.x.query.core.express.index.Facets;
import com.x.query.core.express.index.Filter;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.WoField;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionPost extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPost.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.info("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        result.setData(wo);

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        Integer rows = Indexs.rows(wi.getSize());
        Integer start = Indexs.start(wi.getPage(), rows);

        Set<String> readers = new TreeSet<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            String person = business.index().who(effectivePerson, wi.getPerson());
            wi.getDirectoryList().stream().forEach(o -> {
                try {
                    readers.addAll(business.index().determineReaders(person, o.getCategory(), o.getKey()));
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            });
        }

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
        IndexReader[] indexReaders = this.indexReaders(wi.getDirectoryList());
        if (indexReaders.length == 0) {
            return result;
        }
        try (MultiReader multiReader = new MultiReader(indexReaders)) {
            IndexSearcher searcher = new IndexSearcher(multiReader);
            wo.setDynamicFieldList(getDynamicFieldList(categories, multiReader));
            TopFieldCollector topFieldCollector = TopFieldCollector.create(sort(wi.getSort()), 1000, 1000);
            List<Pair<String, FirstPassGroupingCollector<BytesRef>>> firstPassGroupingCollectorPairs = this
                    .adjustFacetField(categories,
                            wi.getFilterList().stream().map(Filter::getField).collect(Collectors.toList()))
                    .stream()
                    .<Pair<String, FirstPassGroupingCollector<BytesRef>>>map(o -> {
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
            searcher.search(query, MultiCollector.wrap(topFieldCollector, MultiCollector
                    .wrap(firstPassGroupingCollectorPairs.stream().map(Pair::second).collect(Collectors.toList()))));
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
                    }).filter(o -> o.second().isPresent())
                    .<Pair<String, TopGroupsCollector<BytesRef>>>map(param -> {
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

    private void initWo(Wo wo, List<String> categories) {
        wo.setFixedFieldList(this.getFixedFieldList(categories));
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
        if (ListTools.isEmpty(fixedFieldList) && ListTools.isEmpty(dynamicFieldList)) {
            list.addAll(wo.getFixedFieldList().stream().map(WoField::getField).collect(Collectors.toList()));
        } else {
            list.addAll(fixedFieldList);
            list.addAll(dynamicFieldList);
        }
        return list;
    }

    @Schema(name = "com.x.custom.index.assemble.control.jaxrs.index.ActionPost$Wo")
    public class Wo extends ActionPostWo {

        private static final long serialVersionUID = 3751674531291729956L;

    }

    @Schema(name = "com.x.custom.index.assemble.control.jaxrs.index.ActionPost$Wi")
    public class Wi extends ActionPostWi {

        private static final long serialVersionUID = -4646809016933808952L;

    }

}
