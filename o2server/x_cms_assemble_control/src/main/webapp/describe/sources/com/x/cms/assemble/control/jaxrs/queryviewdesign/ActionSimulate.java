package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.query.DateRangeEntry;
import com.x.cms.core.entity.query.FilterEntry;
import com.x.cms.core.entity.query.Query;
import com.x.cms.core.entity.query.SelectEntry;
import com.x.cms.core.entity.query.WhereEntry;

class ActionSimulate extends BaseAction {

	private static Type filterEntryCollectionType = new TypeToken<List<FilterEntry>>() {
	}.getType();

	private static Type stringCollectionType = new TypeToken<List<String>>() {
	}.getType();

	public ActionResult<Query> execute(EffectivePerson effectivePerson, String flag, WrapInQueryViewExecute wrapIn)
			throws Exception {
		/* 前台通过wrapIn将Query的可选择部分FilterEntryList和WhereEntry进行输入 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Query> result = new ActionResult<>();
			QueryView queryView = emc.flag(flag, QueryView.class);
			Query query = this.concrete(queryView);

			if (null != wrapIn) {
				if (null != wrapIn.getDate()) {
					query.setDateRangeEntry(this.readDateRangeEntry(wrapIn.getDate()));
				}
				if ((null != wrapIn.getFilter()) && (!wrapIn.getFilter().isJsonNull())) {
					query.setFilterEntryList(this.readFilterEntryList(wrapIn.getFilter()));
				}
				if ((null != wrapIn.getColumn()) && (!wrapIn.getColumn().isJsonNull())) {
					query.setColumnList(this.readColumnList(wrapIn.getColumn(), query.getSelectEntryList()));
				}
				WhereEntry whereEntry = this.readWhereEntry(wrapIn.getApplication(), wrapIn.getCategory(),
						wrapIn.getUnit(), wrapIn.getPerson(), wrapIn.getIdentity());
				if (whereEntry.available()) {
					query.setWhereEntry(whereEntry);
				}
			}

			query.query();

			/* 整理一下输出值 */
			if ((null != query.getGroupEntry()) && query.getGroupEntry().available()) {
				query.setGrid(null);
			}
			if ((null != query.getCalculate()) && (query.getCalculate().available())) {
				query.setGrid(null);
				query.setGroupGrid(null);
			}

			result.setData(query);
			return result;
		}
	}

	private DateRangeEntry readDateRangeEntry(DateRangeEntry o) throws Exception {
		if (!o.available()) {
			throw new Exception("dateRangeEntry not available:" + o);
		}
		return o;
	}

	private List<FilterEntry> readFilterEntryList(JsonElement o) throws Exception {
		List<FilterEntry> list = new ArrayList<>();
		if (o.isJsonArray()) {
			list = XGsonBuilder.instance().fromJson(o, filterEntryCollectionType);
		} else if (o.isJsonObject()) {
			list.add(XGsonBuilder.instance().fromJson(o, FilterEntry.class));
		}
		for (FilterEntry entry : list) {
			if (!entry.available()) {
				throw new Exception("filterEntry not available:" + entry);
			}
		}
		return list;
	}

	private WhereEntry readWhereEntry(JsonElement application, JsonElement process, JsonElement unit,
			JsonElement person, JsonElement identity) throws Exception {
		WhereEntry whereEntry = new WhereEntry();
		if (null != application && (!application.isJsonNull())) {
			whereEntry.setAppInfoList(this.readApplication(application));
		}
		if (null != process && (!process.isJsonNull())) {
			whereEntry.setCategoryList(this.readCategory(process));
		}

		if (null != unit && (!unit.isJsonNull())) {
			whereEntry.setUnitList(this.readUnit(unit));
		}
		if (null != person && (!person.isJsonNull())) {
			whereEntry.setPersonList(this.readPerson(person));
		}
		if (null != identity && (!identity.isJsonNull())) {
			whereEntry.setIdentityList(this.readIdentity(identity));
		}
		return whereEntry;
	}

	private List<String> readColumnList(JsonElement o, List<SelectEntry> selectEntryList) throws Exception {
		List<String> columns = new ArrayList<>();
		if (o.isJsonArray()) {
			/* 多值 */
			for (JsonElement element : o.getAsJsonArray()) {
				String column = this.getColumn(element, selectEntryList);
				if (StringUtils.isNotEmpty(column)) {
					columns.add(column);
				}
			}
		} else if (o.isJsonPrimitive()) {
			/* 单值 */
			String column = this.getColumn(o, selectEntryList);
			if (StringUtils.isNotEmpty(column)) {
				columns.add(column);
			}
		}
		return columns;
	}

	private String getColumn(JsonElement o, List<SelectEntry> list) throws Exception {
		if (null == o || o.isJsonNull() || (!o.isJsonPrimitive()) || ListTools.isEmpty(list)) {
			return null;
		}
		if (o.getAsJsonPrimitive().isNumber()) {
			Integer idx = o.getAsJsonPrimitive().getAsInt();
			if ((idx >= 1) && (idx <= (list.size() + 1))) {
				return list.get(idx - 1).getColumn();
			}
		} else if (o.getAsJsonPrimitive().isString()) {
			String str = o.getAsJsonPrimitive().getAsString();
			for (SelectEntry entry : list) {
				if (StringUtils.equals(entry.getColumn(), str)) {
					return entry.getColumn();
				}
			}
		}
		return null;
	}

	private Query concrete(QueryView queryView) throws Exception {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson(queryView.getData(), Query.class);
		return query;
	}

	private List<NameIdPair> readApplication(JsonElement o) throws Exception {
		List<NameIdPair> results = new ArrayList<>();
		List<String> flags = new ArrayList<>();
		if (o.isJsonArray()) {
			flags = XGsonBuilder.instance().fromJson(o, stringCollectionType);
		} else if (o.isJsonPrimitive() && o.getAsJsonPrimitive().isString()) {
			flags.add(o.getAsJsonPrimitive().getAsString());
		}
		for (String str : flags) {
			NameIdPair p = new NameIdPair(str, str);
			results.add(p);
		}
		return results;
	}

	private List<NameIdPair> readCategory(JsonElement o) throws Exception {
		List<NameIdPair> results = new ArrayList<>();
		List<String> flags = new ArrayList<>();
		if (o.isJsonArray()) {
			flags = XGsonBuilder.instance().fromJson(o, stringCollectionType);
		} else if (o.isJsonPrimitive() && o.getAsJsonPrimitive().isString()) {
			flags.add(o.getAsJsonPrimitive().getAsString());
		}
		for (String str : flags) {
			NameIdPair p = new NameIdPair(str, str);
			results.add(p);
		}
		return results;
	}

	private List<NameIdPair> readUnit(JsonElement o) throws Exception {
		List<NameIdPair> results = new ArrayList<>();
		List<String> flags = new ArrayList<>();
		if (o.isJsonArray()) {
			flags = XGsonBuilder.instance().fromJson(o, stringCollectionType);
		} else if (o.isJsonPrimitive() && o.getAsJsonPrimitive().isString()) {
			flags.add(o.getAsJsonPrimitive().getAsString());
		}
		for (String str : flags) {
			NameIdPair p = new NameIdPair(str, str);
			results.add(p);
		}
		return results;
	}

	private List<NameIdPair> readIdentity(JsonElement o) throws Exception {
		List<NameIdPair> results = new ArrayList<>();
		List<String> flags = new ArrayList<>();
		if (o.isJsonArray()) {
			flags = XGsonBuilder.instance().fromJson(o, stringCollectionType);
		} else if (o.isJsonPrimitive() && o.getAsJsonPrimitive().isString()) {
			flags.add(o.getAsJsonPrimitive().getAsString());
		}
		for (String str : flags) {
			NameIdPair p = new NameIdPair(str, str);
			results.add(p);
		}
		return results;
	}

	private List<NameIdPair> readPerson(JsonElement o) throws Exception {
		List<NameIdPair> results = new ArrayList<>();
		List<String> flags = new ArrayList<>();
		if (o.isJsonArray()) {
			flags = XGsonBuilder.instance().fromJson(o, stringCollectionType);
		} else if (o.isJsonPrimitive() && o.getAsJsonPrimitive().isString()) {
			flags.add(o.getAsJsonPrimitive().getAsString());
		}
		for (String str : flags) {
			NameIdPair p = new NameIdPair(str, str);
			results.add(p);
		}
		return results;
	}

}