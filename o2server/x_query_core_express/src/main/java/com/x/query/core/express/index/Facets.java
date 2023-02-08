package com.x.query.core.express.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.grouping.TopGroupsCollector;
import org.apache.lucene.util.BytesRef;

import com.x.base.core.project.bean.ValueCountPair;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;

public class Facets {

    public static List<WoFacet> topGroupsCollector(
            List<Pair<String, TopGroupsCollector<BytesRef>>> topGroupsCollectorPairs)
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

}
