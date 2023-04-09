package com.x.query.core.express.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.query.Comparison;
import com.x.query.core.express.plan.FilterEntry;

public class Runtime extends GsonPropertyObject {

	private static final long serialVersionUID = 2119629142378875023L;

	public static final String PARAMETER_PERSON = "person";
	public static final String PARAMETER_IDENTITYLIST = "identityList";
	public static final String PARAMETER_UNITLIST = "unitList";
	public static final String PARAMETER_UNITALLLIST = "unitAllList";
	public static final String PARAMETER_GROUPLIST = "groupList";
	public static final String PARAMETER_ROLELIST = "roleList";

	public static final List<String> ALL_BUILT_IN_PARAMETER = ListUtils
			.unmodifiableList(Arrays.asList(PARAMETER_PERSON, PARAMETER_IDENTITYLIST, PARAMETER_UNITLIST,
					PARAMETER_UNITALLLIST, PARAMETER_GROUPLIST, PARAMETER_ROLELIST));

	@FieldDescribe("参数.")
	public Map<String, Object> parameters = new HashMap<>();

	@FieldDescribe("过滤条件.")
	public List<FilterEntry> filterList = new ArrayList<>();

	public Map<String, Object> getParameters() {
		return parameters;
	}

	@FieldDescribe("页码")
	public Integer page = 0;

	@FieldDescribe("每页大小")
	public Integer size = 20;

	public boolean hasParameter(String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}

		return this.parameters.containsKey(name);
	}

	public void setParameter(String name, Object obj) {
		this.parameters.put(name, obj);
	}

	public Object getParameter(String name) {
		return this.parameters.get(name);
	}

	public List<FilterEntry> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<FilterEntry> filterList) {
		this.filterList = filterList;
	}

	public Optional<String> additionFilter() throws Exception {
		if (ListTools.isNotEmpty(getFilterList())) {
			StringBuilder builder = new StringBuilder();
			builder.append("(");
			for (FilterEntry entry : this.getFilterList()) {
				if (builder.length() > 1) {
					builder.append(" ").append(entry.logic).append(" ");
				}
				builder.append(entry.path).append(" ").append(comparison(entry)).append("").append(entry.value);
			}
			builder.append(")");
			return Optional.of(builder.toString());
		}
		return Optional.empty();
	}

	private String comparison(FilterEntry entry) throws Exception {
		if (Comparison.isNotEquals(entry.comparison)) {
			return "!=";
		}

		if (Comparison.isGreaterThan(entry.comparison)) {
			return ">";
		}

		if (Comparison.isGreaterThanOrEqualTo(entry.comparison)) {
			return ">=";
		}

		if (Comparison.isLessThan(entry.comparison)) {
			return "<";
		}

		if (Comparison.isLessThanOrEqualTo(entry.comparison)) {
			return "<=";
		}

		if (Comparison.isLike(entry.comparison)) {
			return "like";
		}
		return "=";
	}

}
