package com.x.query.assemble.surface.jaxrs.search;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.express.index.Indexs;

abstract class BaseAction extends StandardJaxrsAction {

    protected static final List<String> DISPLAY_FIELDS = Stream
            .<String>of(Indexs.FIELD_ID, Indexs.FIELD_CATEGORY, Indexs.FIELD_INDEXTIME,
                    Indexs.FIELD_TITLE, Indexs.FIELD_CREATETIME, Indexs.FIELD_UPDATETIME,
                    Indexs.FIELD_CREATORPERSON, Indexs.FIELD_CREATORUNIT, Indexs.FIELD_SUMMARY)
            .collect(Collectors.toUnmodifiableList());

    protected static final List<String> DATE_FIELDS = Stream
            .<String>of(Indexs.FIELD_INDEXTIME, Indexs.FIELD_CREATETIME, Indexs.FIELD_UPDATETIME)
            .collect(Collectors.toUnmodifiableList());

    // 默认维度字段
    protected static final List<String> FACET_FIELDS = Stream.<String>of(Indexs.FIELD_CATEGORY,
            Indexs.FIELD_CREATETIMEMONTH, Indexs.FIELD_UPDATETIMEMONTH,
            Indexs.FIELD_CREATORPERSON, Indexs.FIELD_CREATORUNIT).collect(Collectors.toUnmodifiableList());

    protected Optional<Query> searchQuery(String query, Analyzer analyzer) throws Exception {
        query = Indexs.alignQuery(query);
        if (StringUtils.isBlank(query)) {
            return Optional.empty();
        }
        Query titleQuery = new QueryParser(Indexs.FIELD_TITLE, analyzer).parse(query);
        Query summaryQuery = new QueryParser(Indexs.FIELD_SUMMARY, analyzer).parse(query);
        Query bodyQuery = new QueryParser(Indexs.FIELD_BODY, analyzer).parse(query);
        Query attachmentQuery = new QueryParser(Indexs.FIELD_ATTACHMENT, analyzer).parse(query);
        BooleanQuery.Builder analyzerQuery = new BooleanQuery.Builder();
        analyzerQuery.add(new BoostQuery(titleQuery, Config.query().index().getSearchTitleBoost()),
                BooleanClause.Occur.SHOULD);
        analyzerQuery.add(new BoostQuery(summaryQuery, Config.query().index().getSearchSummaryBoost()),
                BooleanClause.Occur.SHOULD);
        analyzerQuery.add(new BoostQuery(bodyQuery, Config.query().index().getSearchBodyBoost()),
                BooleanClause.Occur.SHOULD);
        analyzerQuery.add(new BoostQuery(attachmentQuery, Config.query().index().getSearchAttachmentBoost()),
                BooleanClause.Occur.SHOULD);
        return Optional.of(analyzerQuery.build());
    }

    protected Optional<Query> readersQuery(List<String> readers) {
        if (ListTools.isEmpty(readers)) {
            return Optional.empty();
        }
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        readers.stream().filter(StringUtils::isNotBlank).map(o -> new TermQuery(new Term(Indexs.FIELD_READERS, o)))
                .forEach(o -> builder.add(o, BooleanClause.Occur.SHOULD));
        return Optional.of(builder.build());
    }

    /**
     * 对默认维度字段进行判断如果过滤字段有了这个字段那么就删除这个维度
     * 
     * @param filters
     * @return
     */
    protected List<String> adjustFacetField(List<String> filters) {
        return FACET_FIELDS.stream().filter(o -> (!filters.contains(o))).collect(Collectors.toList());
    }

}