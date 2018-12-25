package com.x.query.service.processing.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.tools.StringTools;
import com.x.query.core.entity.Query;
import com.x.query.service.processing.AbstractFactory;
import com.x.query.service.processing.Business;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class QueryFactory extends AbstractFactory {

	private Ehcache cache;

	public QueryFactory(Business business) throws Exception {
		super(business);
		this.cache = ApplicationCache.instance().getCache(Query.class);
	}

	public List<Query> pick(List<String> flags) throws Exception {
		List<Query> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((Query) element.getObjectValue());
				}
			} else {
				Query o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public Query pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Query o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Query) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}

	private Query pickObject(String flag) throws Exception {
		Query o = this.business().entityManagerContainer().flag(flag, Query.class );
		if (o != null) {
			this.entityManagerContainer().get(Query.class).detach(o);
		}
		return o;
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