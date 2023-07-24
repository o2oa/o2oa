package com.x.query.assemble.surface.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.surface.AbstractFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;

public class QueryFactory extends AbstractFactory {

	private CacheCategory cache;

	public QueryFactory(Business business) throws Exception {
		super(business);
		this.cache = new CacheCategory(Query.class);
	}

	public List<Query> pick(List<String> flags) throws Exception {
		List<Query> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(str);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				list.add((Query) optional.get());
			} else {
				Query o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cache, cacheKey, o);
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
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			o = (Query) optional.get();
		} else {
			o = this.pickObject(flag);
			if(o != null) {
				CacheManager.put(cache, cacheKey, o);
			}
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
