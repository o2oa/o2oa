package com.x.query.assemble.surface.jaxrs.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.google.common.collect.ImmutableMap;
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
import com.x.cms.core.entity.Document;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.Sort;
import com.x.query.core.express.index.WoFacet;
import com.x.query.core.express.index.WoField;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    private static final Map<String, String> PROCESSPLATFORM_FIELDNAME = ImmutableMap.<String, String>builder()
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN + TaskCompleted.completed_FIELDNAME, "已完成")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.creatorUnitLevelName_FIELDNAME, "创建者部门层级名")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.application_FIELDNAME, "应用标识")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.applicationName_FIELDNAME, "应用名称")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.applicationAlias_FIELDNAME, "应用别名")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.process_FIELDNAME, "流程标识")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.processName_FIELDNAME, "流程名称")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.processAlias_FIELDNAME, "流程别名")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.job_FIELDNAME, "任务")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.serial_FIELDNAME, "编号")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN + WorkCompleted.expired_FIELDNAME, "超时")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + Work.activityName_FIELDNAME, "活动环节")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE + WorkCompleted.expireTime_FIELDNAME, "截至时间")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS + Indexs.FIELD_ROCESSPLATFORM_TASKPERSONNAMES, "当前处理人")
            .put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS + Indexs.FIELD_ROCESSPLATFORM_PRETASKPERSONNAMES, "前序处理人")
            .build();

    private static final Map<String, String> CMS_FIELDNAME = ImmutableMap.<String, String>builder()
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.appId_FIELDNAME, "栏目标识")
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.appName_FIELDNAME, "栏目名称")
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.appAlias_FIELDNAME, "栏目别名")
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.categoryId_FIELDNAME, "分类标识")
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.categoryName_FIELDNAME, "分类名称")
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.categoryAlias_FIELDNAME, "分类别名")
            .put(Indexs.PREFIX_FIELD_CMS_STRING + Document.description_FIELDNAME, "说明")
            .put(Indexs.PREFIX_FIELD_CMS_DATE + Document.publishTime_FIELDNAME, "发布时间")
            .put(Indexs.PREFIX_FIELD_CMS_DATE + Document.modifyTime_FIELDNAME, "修改时间")
            .build();

    protected static final WoField IDWOFIELD = new WoField(Indexs.FIELD_ID, "标识", Indexs.FIELD_TYPE_STRING);
    protected static final CacheCategory cacheCategory = new CacheCategory(Application.class, AppInfo.class);

    protected static final List<WoField> FIXEDFIELD_APPLICATION = ListUtils
            .unmodifiableList(Arrays.asList(new WoField(Indexs.FIELD_TITLE, "标题", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATORPERSON, "创建者", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATORUNIT, "部门", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_CREATETIME, "创建时间", Indexs.FIELD_TYPE_DATE),
                    new WoField(Indexs.FIELD_UPDATETIME, "更新时间", Indexs.FIELD_TYPE_DATE),
                    new WoField(Indexs.FIELD_SERIAL, "文号", Indexs.FIELD_TYPE_STRING),
                    new WoField(Indexs.FIELD_PROCESSNAME, "流程", Indexs.FIELD_TYPE_STRING)));

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
                    Indexs.FIELD_CREATORUNIT, Indexs.FIELD_COMPLETED));

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

    protected List<WoField> getDynamicFieldList(List<String> categories, IndexReader reader) {
        String[] names = listFieldNamesWithCategory(categories);
        List<WoField> list = org.apache.lucene.luke.models.util.IndexUtils.getFieldNames(reader).stream()
                .filter(o -> StringUtils.startsWithAny(o, names))
                .distinct().map(o -> org.apache.lucene.luke.models.util.IndexUtils.getFieldInfo(reader, o)).map(o -> {
                    // LegacyNumericUtils.
                    WoField woField = new WoField();
                    woField.setField(o.getName());
                    setDynamicFieldType(woField);
                    return woField;
                }).collect(Collectors.toList());
        List<WoField> os = new ArrayList<>();
        list.stream().filter(o -> StringUtils.equalsAny(o.getFieldType(), Indexs.FIELD_TYPE_NUMBER,
                Indexs.FIELD_TYPE_DATE)).forEach(o -> setMinMax(reader, o));
        list.stream().map(o -> {
            Integer order = null;
            if (StringUtils.startsWith(o.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM)) {
                order = 0;
            } else {
                order = StringUtils.startsWith(o.getField(), Indexs.PREFIX_FIELD_CMS) ? 1 : 2;
            }
            return Pair.of(order, o);
        }).collect(Collectors.groupingBy(Pair::first)).entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(o -> o.getValue().stream().map(Pair::second).map(p -> Pair.of(p.getName().charAt(0), p))
                        .map(p -> {
                            if (Character.UnicodeBlock
                                    .of(p.first()) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                                return Pair.of(0, p.second());
                            } else {
                                return Pair.of(1, p.second());
                            }
                        }).collect(Collectors.groupingBy(Pair::first)).entrySet().stream()
                        .sorted(Comparator.comparing(Map.Entry::getKey))
                        .forEach(p -> p.getValue().stream().map(Pair::second)
                                .sorted(Comparator.nullsLast(Comparator.comparing(WoField::getName)))
                                .forEach(os::add)));
        return os;
    }

    private void setDynamicFieldType(WoField woField) {
        if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA)) {
            setDynamicFieldTypeData(woField);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM)) {
            setDynamicFieldTypeProcessPlatform(woField);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS)) {
            setDynamicFieldTypeCms(woField);
        }
    }

    private void setMinMax(IndexReader reader, WoField o) {
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
                    setMinMaxNumber(o, maxLong, minLong);
                }
                if (StringUtils.equalsIgnoreCase(o.getFieldType(), Indexs.FIELD_TYPE_DATE)) {
                    setMinMaxDate(o, maxLong, minLong);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void setMinMaxDate(WoField o, Long maxLong, Long minLong) {
        if (null != maxLong) {
            o.setMax(DateTools.format(new Date(maxLong)));
        }
        if (null != minLong) {
            o.setMin(DateTools.format(new Date(minLong)));
        }
    }

    private void setMinMaxNumber(WoField o, Long maxLong, Long minLong) {
        if (null != maxLong) {
            o.setMax(NumericUtils.sortableLongToDouble(maxLong));
        }
        if (null != minLong) {
            o.setMin(NumericUtils.sortableLongToDouble(minLong));
        }
    }

    private void setDynamicFieldTypeData(WoField woField) {
        if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRING)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRING));
            woField.setFieldType(Indexs.FIELD_TYPE_STRING);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRINGS)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRINGS));
            woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEAN)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEAN));
            woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEANS)) {
            woField.setName(
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEANS));
            woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBER)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBER));
            woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBERS)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBERS));
            woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATE)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATE));
            woField.setFieldType(Indexs.FIELD_TYPE_DATE);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATES)) {
            woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATES));
            woField.setFieldType(Indexs.FIELD_TYPE_DATES);
        }
    }

    private void setDynamicFieldTypeProcessPlatform(WoField woField) {
        if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING)));
            woField.setFieldType(Indexs.FIELD_TYPE_STRING);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS)) {
            woField.setName(
                    PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(), StringUtils
                            .substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS)));
            woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN)));
            woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS)));
            woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER)));
            woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS)));
            woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE)));
            woField.setFieldType(Indexs.FIELD_TYPE_DATE);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES)) {
            woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES)));
            woField.setFieldType(Indexs.FIELD_TYPE_DATES);
        }
    }

    private void setDynamicFieldTypeCms(WoField woField) {
        if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRING)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRING)));
            woField.setFieldType(Indexs.FIELD_TYPE_STRING);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRINGS)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRINGS)));
            woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEAN)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEAN)));
            woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEANS)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEANS)));
            woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBER)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBER)));
            woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBERS)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBERS)));
            woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATE)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATE)));
            woField.setFieldType(Indexs.FIELD_TYPE_DATE);
        } else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATES)) {
            woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
                    StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATES)));
            woField.setFieldType(Indexs.FIELD_TYPE_DATES);
        }
    }

    private String[] listFieldNamesWithCategory(List<String> categories) {
        List<String> names = new ArrayList<>();
        if (categories.contains(Indexs.CATEGORY_PROCESSPLATFORM)) {
            names.addAll(Arrays.asList(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING,
                    Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS, Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN,
                    Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS, Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER,
                    Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS, Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE,
                    Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES));
        }
        if (categories.contains(Indexs.CATEGORY_CMS)) {
            names.addAll(Arrays.asList(Indexs.PREFIX_FIELD_CMS_STRING,
                    Indexs.PREFIX_FIELD_CMS_STRINGS, Indexs.PREFIX_FIELD_CMS_BOOLEAN,
                    Indexs.PREFIX_FIELD_CMS_BOOLEANS, Indexs.PREFIX_FIELD_CMS_NUMBER,
                    Indexs.PREFIX_FIELD_CMS_NUMBERS, Indexs.PREFIX_FIELD_CMS_DATE,
                    Indexs.PREFIX_FIELD_CMS_DATES));
        }
        names.addAll(Arrays.asList(Indexs.PREFIX_FIELD_DATA_STRING,
                Indexs.PREFIX_FIELD_DATA_STRINGS, Indexs.PREFIX_FIELD_DATA_BOOLEAN,
                Indexs.PREFIX_FIELD_DATA_BOOLEANS, Indexs.PREFIX_FIELD_DATA_NUMBER,
                Indexs.PREFIX_FIELD_DATA_NUMBERS, Indexs.PREFIX_FIELD_DATA_DATE,
                Indexs.PREFIX_FIELD_DATA_DATES));
        return names.toArray(new String[] {});
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
