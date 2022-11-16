package com.x.query.assemble.surface.jaxrs.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.PointValues;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.grouping.TopGroupsCollector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.x.base.core.project.bean.ValueCountPair;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.assemble.surface.jaxrs.index.ActionPost.Wi;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.Sort;
import com.x.query.core.express.index.WoFacet;
import com.x.query.core.express.index.WoField;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    protected static final WoField IDWOFIELD = new WoField(Indexs.FIELD_ID, "标识", Indexs.FIELD_TYPE_STRING);
    protected static final CacheCategory cacheCategory = new CacheCategory(Application.class, AppInfo.class);

    protected static final List<WoField> FIXEDFIELD_APPLICATION = ListUtils
            .unmodifiableList(Arrays.asList(new WoField(Indexs.FIELD_TITLE, "标题", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATORPERSON, "创建者", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATORUNIT, "部门", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATETIME, "创建时间", Indexs.FIELD_TYPE_DATE),
                    new WoField(Indexs.FIELD_UPDATETIME, "更新时间", Indexs.FIELD_TYPE_DATE),
                    new WoField(Indexs.FIELD_SERIAL, "文号", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_PROCESSNAME, "流程", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_COMPLETED, "已完成", Indexs.FIELD_TYPE_BOOLEAN)));
    protected static final List<WoField> FIXEDFIELD_APPINFO = ListUtils
            .unmodifiableList(Arrays.asList(new WoField(Indexs.FIELD_TITLE, "标题", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATORPERSON, "创建者", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATORUNIT, "部门", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATETIME, "创建时间", Indexs.FIELD_TYPE_DATE),
                    new WoField(Indexs.FIELD_UPDATETIME, "更新时间", Indexs.FIELD_TYPE_DATE),
                    new WoField(Indexs.FIELD_CATEGORYNAME, "分类", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_DESCRIPTION, "说明", Indexs.FIELD_TYPE_STRING)));

    protected static final List<String> FACET_FIELDS = ListUtils
            .unmodifiableList(Arrays.asList(Indexs.FIELD_CREATETIMEMONTH, Indexs.FIELD_UPDATETIMEMONTH,
                    Indexs.FIELD_CREATORUNIT, Indexs.FIELD_CREATORPERSON));

    protected Optional<Query> searchQuery(String query, Analyzer analyzer) throws ParseException {
        query = Indexs.alignQuery(query);
        if (StringUtils.isBlank(query)) {
            return Optional.of(new MatchAllDocsQuery());
        }
        BooleanQuery.Builder analyzerQuery = new BooleanQuery.Builder();
        analyzerQuery.add(new QueryParser(Indexs.FIELD_TITLE, analyzer).parse(query), BooleanClause.Occur.SHOULD);
        analyzerQuery.add(new QueryParser(Indexs.FIELD_SUMMARY, analyzer).parse(query),
                BooleanClause.Occur.SHOULD);
        analyzerQuery.add(new QueryParser(Indexs.FIELD_BODY, analyzer).parse(query), BooleanClause.Occur.SHOULD);
        return Optional.of(analyzerQuery.build());
    }

    protected org.apache.lucene.search.Sort sort(Sort sort) {
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

    protected List<WoFacet> writeFacets(List<Pair<String, TopGroupsCollector<BytesRef>>> topGroupsCollectorPairs)
            throws Exception {
        final String facetGroupOrder = Config.query().index().getFacetGroupOrder();
        return topGroupsCollectorPairs.stream().map(param -> {
            WoFacet woFacet = new WoFacet();
            woFacet.setField(param.first());
            TopGroups<BytesRef> topGroups = param.second().getTopGroups(0);
            if (null != topGroups) {
                List<ValueCountPair> list = Arrays.stream(topGroups.groups).map(o -> {
                    ValueCountPair valueCountPair = new ValueCountPair();
                    // 存在可能为null
                    valueCountPair.setValue(null == o.groupValue ? "" : o.groupValue.utf8ToString());
                    valueCountPair.setCount(o.totalHits.value);
                    return valueCountPair;
                }).collect(Collectors.toList());
                if (StringUtils.equalsIgnoreCase(facetGroupOrder,
                        com.x.base.core.project.config.Query.Index.FACETGROUPORDER_KEYDESC)) {
                    Collections.reverse(list);
                } else if (StringUtils.equalsIgnoreCase(facetGroupOrder,
                        com.x.base.core.project.config.Query.Index.FACETGROUPORDER_COUNTASC)) {
                    list = list.stream().sorted(Comparator.nullsLast(Comparator.comparing(ValueCountPair::getCount)))
                            .collect(Collectors.toList());
                } else if (StringUtils.equalsIgnoreCase(facetGroupOrder,
                        com.x.base.core.project.config.Query.Index.FACETGROUPORDER_COUNTDESC)) {
                    list = list.stream()
                            .sorted(Comparator.nullsLast(Comparator.comparing(ValueCountPair::getCount)).reversed())
                            .collect(Collectors.toList());
                }
                woFacet.setValueCountPairList(list);
            }
            return woFacet;
        }).collect(Collectors.toList());
    }

    protected IndexReader[] indexReaders(List<com.x.query.core.express.index.Directory> dirs) {
        return dirs.stream().map(o -> Indexs.directory(o.getCategory(), o.getKey(), true))
                .filter(Optional::isPresent).map(Optional::get).map(o -> {
                    try {
                        return DirectoryReader.open(o);
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                    return null;
                }).filter(o -> !Objects.isNull(o)).toArray(s -> new IndexReader[s]);
    }

    protected List<WoField> getFixedFieldList(List<String> list) {
        List<WoField> woFields = new ArrayList<>();
        if (list.contains(Indexs.CATEGORY_PROCESSPLATFORM)) {
            woFields.addAll(FIXEDFIELD_APPLICATION);
        } else if (list.contains(Indexs.CATEGORY_CMS)) {
            woFields.addAll(FIXEDFIELD_APPINFO);
        }
        return ListTools.trim(woFields, true, true);
    }

    protected List<WoField> getDynamicFieldList(IndexReader reader) {
        List<WoField> list = org.apache.lucene.luke.models.util.IndexUtils.getFieldNames(reader).stream()
                .filter(o -> StringUtils.startsWithAny(o, Indexs.PREFIX_FIELD_DATA_STRING,
                        Indexs.PREFIX_FIELD_DATA_STRINGS, Indexs.PREFIX_FIELD_DATA_BOOLEAN,
                        Indexs.PREFIX_FIELD_DATA_BOOLEANS, Indexs.PREFIX_FIELD_DATA_NUMBER,
                        Indexs.PREFIX_FIELD_DATA_NUMBERS, Indexs.PREFIX_FIELD_DATA_DATE,
                        Indexs.PREFIX_FIELD_DATA_DATES))
                .distinct().map(o -> org.apache.lucene.luke.models.util.IndexUtils.getFieldInfo(reader, o)).map(o -> {
                    // LegacyNumericUtils.
                    WoField woField = new WoField();
                    woField.setField(o.getName());
                    if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_STRING)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_STRING));
                        woField.setFieldType(Indexs.FIELD_TYPE_STRING);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_STRINGS)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_STRINGS));
                        woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_BOOLEAN)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_BOOLEAN));
                        woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_BOOLEANS)) {
                        woField.setName(
                                StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_BOOLEANS));
                        woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_NUMBER)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_NUMBER));
                        woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_NUMBERS)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_NUMBERS));
                        woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_DATE)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_DATE));
                        woField.setFieldType(Indexs.FIELD_TYPE_DATE);
                    } else if (StringUtils.startsWith(o.getName(), Indexs.PREFIX_FIELD_DATA_DATES)) {
                        woField.setName(StringUtils.substringAfter(o.getName(), Indexs.PREFIX_FIELD_DATA_DATES));
                        woField.setFieldType(Indexs.FIELD_TYPE_DATES);
                    }

                    return woField;
                }).collect(Collectors.toList());
        list.stream().filter(o -> StringUtils.equalsAny(o.getFieldType(), Indexs.FIELD_TYPE_NUMBER,
                Indexs.FIELD_TYPE_NUMBER)).forEach(o -> {
                    try {
                        Long maxLong = null;
                        Long minLong = null;
                        byte[] max = PointValues.getMaxPackedValue(reader, o.getField());
                        if (null != max) {
                            maxLong = org.apache.lucene.util.NumericUtils.sortableBytesToLong(max, 0);
                        }
                        byte[] min = PointValues.getMinPackedValue(reader, o.getField());
                        if (null != min) {
                            minLong = org.apache.lucene.util.NumericUtils.sortableBytesToLong(min, 0);
                        }
                        if ((null != maxLong) || (null != minLong)) {
                            if (StringUtils.equalsIgnoreCase(o.getFieldType(), Indexs.FIELD_TYPE_NUMBER)) {
                                if (null != maxLong) {
                                    o.setMax(NumericUtils.sortableLongToDouble(maxLong));
                                }
                                if (null != minLong) {
                                    o.setMin(NumericUtils.sortableLongToDouble(minLong));
                                }
                            }
                            if (StringUtils.equalsIgnoreCase(o.getFieldType(), Indexs.PREFIX_FIELD_DATA_DATE)) {
                                if (null != maxLong) {
                                    o.setMax(DateTools.format(new Date(maxLong)));
                                }
                                if (null != minLong) {
                                    o.setMin(DateTools.format(new Date(minLong)));
                                }
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                });
        return list;
    }

    protected List<String> categories(List<com.x.query.core.express.index.Directory> dirs) {
        if (ListTools.isEmpty(dirs)) {
            return new ArrayList<>();
        }
        return dirs.stream()
                .map(com.x.query.core.express.index.Directory::getCategory).filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    protected List<String> adjustFacetField(List<String> categories, List<String> filters) {
        List<String> list = FACET_FIELDS.stream().filter(o -> (!filters.contains(o))).collect(Collectors.toList());
        if (list.contains(Indexs.FIELD_PROCESSNAME)) {
            list.removeAll(Arrays.asList(Indexs.FIELD_APPLICATIONNAME, Indexs.FIELD_PROCESSNAME,
                    Indexs.FIELD_APPNAME, Indexs.FIELD_CATEGORYNAME));
        }
        if (list.contains(Indexs.FIELD_APPLICATIONNAME)) {
            list.removeAll(Arrays.asList(Indexs.FIELD_APPLICATIONNAME, Indexs.FIELD_APPNAME,
                    Indexs.FIELD_CATEGORYNAME));
        }
        if (list.contains(Indexs.FIELD_CATEGORYNAME)) {
            list.removeAll(Arrays.asList(Indexs.FIELD_APPNAME, Indexs.FIELD_CATEGORYNAME,
                    Indexs.FIELD_APPLICATIONNAME, Indexs.FIELD_PROCESSNAME));
        }
        if (list.contains(Indexs.FIELD_APPNAME)) {
            list.removeAll(Arrays.asList(Indexs.FIELD_APPNAME, Indexs.FIELD_APPLICATIONNAME,
                    Indexs.FIELD_PROCESSNAME));
        }
        if (!ListTools.contains(categories, Indexs.CATEGORY_PROCESSPLATFORM)) {
            list.remove(Indexs.FIELD_COMPLETED);
        }
        return list;
    }
}
