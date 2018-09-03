package com.x.processplatform.assemble.designer.jaxrs.querystat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.query.Calculate;
import com.x.processplatform.core.entity.query.DateRangeEntry;
import com.x.processplatform.core.entity.query.FilterEntry;
import com.x.processplatform.core.entity.query.Query;
import com.x.processplatform.core.entity.query.SelectEntry;
import com.x.processplatform.core.entity.query.WhereEntry;

class ActionSimulate extends BaseAction {

	private static Type filterEntryCollectionType = new TypeToken<List<FilterEntry>>() {
	}.getType();

	private static Type stringCollectionType = new TypeToken<List<String>>() {
	}.getType();

	private Gson gson = XGsonBuilder.instance();

	public ActionResult<Query> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		/* 前台通过wrapIn将Query的可选择部分FilterEntryList和WhereEntry进行输入 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			QueryStat queryStat = emc.find(id, QueryStat.class);
			if (null == queryStat) {
				throw new ExceptionQueryStatNotExist(id);
			}
			QueryView queryView = emc.find(queryStat.getQueryView(), QueryView.class);
			if (null == queryView) {
				throw new ExceptionQueryViewNotExist(queryStat.getQueryView());
			}
			ActionResult<Query> result = new ActionResult<>();
			Query query = gson.fromJson(queryView.getData(), Query.class);
			/* 写入统计条件,统计条件和一些其他前端值都放在data中,先将calculate分离出来 */
			if (StringUtils.isNotBlank(queryStat.getData())) {
				JsonElement element = gson.fromJson(queryStat.getData(), JsonElement.class);
				if (element.isJsonObject()) {
					JsonObject jsonObject = element.getAsJsonObject();
					if (jsonObject.has("calculate")) {
						query.setCalculate(gson.fromJson(jsonObject.get("calculate"), Calculate.class));
					}
				}
			}
			/* 写入动态条件值 */
			if (null != wi) {
				if (null != wi.getDate()) {
					query.setDateRangeEntry(this.readDateRangeEntry(wi.getDate()));
				}
				if ((null != wi.getFilterList()) && (!wi.getFilterList().isJsonNull())) {
					query.setFilterEntryList(this.readFilterEntryList(wi.getFilterList()));
				}
				if ((null != wi.getColumn()) && (!wi.getColumn().isJsonNull())) {
					query.setColumnList(this.readColumnList(wi.getColumn(), query.getSelectEntryList()));
				}
				WhereEntry whereEntry = this.readWhereEntry(wi.getApplication(), wi.getProcess(), wi.getUnit(),
						wi.getPerson(), wi.getIdentity());
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

	public static class Wi extends GsonPropertyObject {

		private DateRangeEntry date;

		private JsonElement filterList;

		private JsonElement column;

		private JsonElement application;

		private JsonElement process;

		private JsonElement unit;

		private JsonElement person;

		private JsonElement identity;

		public DateRangeEntry getDate() {
			return date;
		}

		public void setDate(DateRangeEntry date) {
			this.date = date;
		}

		public JsonElement getColumn() {
			return column;
		}

		public void setColumn(JsonElement column) {
			this.column = column;
		}

		public JsonElement getApplication() {
			return application;
		}

		public void setApplication(JsonElement application) {
			this.application = application;
		}

		public JsonElement getProcess() {
			return process;
		}

		public void setProcess(JsonElement process) {
			this.process = process;
		}

		public JsonElement getPerson() {
			return person;
		}

		public void setPerson(JsonElement person) {
			this.person = person;
		}

		public JsonElement getIdentity() {
			return identity;
		}

		public void setIdentity(JsonElement identity) {
			this.identity = identity;
		}

		public JsonElement getUnit() {
			return unit;
		}

		public void setUnit(JsonElement unit) {
			this.unit = unit;
		}

		public JsonElement getFilterList() {
			return filterList;
		}

		public void setFilterList(JsonElement filterList) {
			this.filterList = filterList;
		}

	}

	private WhereEntry readWhereEntry(JsonElement application, JsonElement process, JsonElement unit,
			JsonElement person, JsonElement identity) throws Exception {
		WhereEntry whereEntry = new WhereEntry();
		if (null != application && (!application.isJsonNull())) {
			whereEntry.setApplicationList(this.readApplication(application));
		}
		if (null != process && (!process.isJsonNull())) {
			whereEntry.setProcessList(this.readProcess(process));
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

	private List<NameIdPair> readProcess(JsonElement o) throws Exception {
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

}