package com.x.query.assemble.designer.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.AbstractFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;

public class QueryFactory extends AbstractFactory {

	public QueryFactory(Business business) throws Exception {
		super(business);
	}

	public <T extends Query> List<T> sort(List<T> list) {
		if (null == list) {
			return null;
		}
		list = list.stream()
				.sorted(Comparator.comparing(Query::getAlias, StringTools.emptyLastComparator())
						.thenComparing(Comparator.comparing(Query::getName, StringTools.emptyLastComparator())))
				.collect(Collectors.toList());
		return list;

	}

}