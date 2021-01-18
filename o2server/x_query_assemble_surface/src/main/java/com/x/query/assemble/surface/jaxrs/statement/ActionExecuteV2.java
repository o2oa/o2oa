package com.x.query.assemble.surface.jaxrs.statement;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicBaseEntity;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.plan.Comparison;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.statement.Runtime;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ActionExecuteV2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecuteV2.class);
	private final static String[] keys = { "group by", "GROUP BY", "order by", "ORDER BY", "limit", "LIMIT" };
	private final static String[] pageKeys = { "GROUP BY", " COUNT(" };
	private final static String JOIN_KEY = " JOIN ";
	private final static String JOIN_ON_KEY = " ON ";

	ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, String mode, Integer page, Integer size,
			JsonElement jsonElement) throws Exception {
		Statement statement = null;
		ActionResult<Object> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			statement = emc.flag(flag, Statement.class);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}
			if (!business.executable(effectivePerson, statement)) {
				throw new ExceptionAccessDenied(effectivePerson, statement);
			}
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Runtime runtime = this.runtime(effectivePerson, wi.getParameter(), page, size);

		Object data = null;
		Object count = null;
		switch (mode) {
		case Statement.MODE_DATA:
			switch (Objects.toString(statement.getFormat(), "")) {
			case Statement.FORMAT_SCRIPT:
				data = this.script(effectivePerson, statement, runtime, mode, wi);
				break;
			default:
				data = this.jpql(effectivePerson, statement, runtime, mode, wi);
				break;
			}
			result.setData(data);
			break;
		case Statement.MODE_COUNT:
			switch (Objects.toString(statement.getFormat(), "")) {
			case Statement.FORMAT_SCRIPT:
				count = this.script(effectivePerson, statement, runtime, mode, wi);
				break;
			default:
				count = this.jpql(effectivePerson, statement, runtime, mode, wi);
				break;
			}
			result.setData(count);
			result.setCount((Long) count);
			break;
		default:
			switch (Objects.toString(statement.getFormat(), "")) {
			case Statement.FORMAT_SCRIPT:
				data = this.script(effectivePerson, statement, runtime, Statement.MODE_DATA, wi);
				count = this.script(effectivePerson, statement, runtime, Statement.MODE_COUNT, wi);
				break;
			default:
				data = this.jpql(effectivePerson, statement, runtime, Statement.MODE_DATA, wi);
				count = this.jpql(effectivePerson, statement, runtime, Statement.MODE_COUNT, wi);
				break;
			}
			result.setData(data);
			result.setCount((Long) count);
		}
		return result;
	}

	private Object script(EffectivePerson effectivePerson, Statement statement, Runtime runtime, String mode, Wi wi)
			throws Exception {
		Object data = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ScriptContext scriptContext = this.scriptContext(effectivePerson, business, runtime);
			ScriptFactory.initialServiceScriptText().eval(scriptContext);
			String scriptText = statement.getScriptText();
			if (Statement.MODE_COUNT.equals(mode)) {
				scriptText = statement.getCountScriptText();
			}
			Object o = ScriptFactory.scriptEngine.eval(ScriptFactory.functionalization(scriptText), scriptContext);
			String jpql = ScriptFactory.asString(o);
			Class<? extends JpaObject> cls = this.clazz(business, statement);
			EntityManager em;
			if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
					&& StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
				em = business.entityManagerContainer().get(DynamicBaseEntity.class);
			} else {
				em = business.entityManagerContainer().get(cls);
			}
			jpql = joinSql(jpql, wi, business);
			logger.info("执行的sql：{}", jpql);
			Query query;
			String upJpql = jpql.toUpperCase();
			if (upJpql.indexOf(JOIN_KEY) > -1 && upJpql.indexOf(JOIN_ON_KEY) > -1) {
				query = em.createNativeQuery(jpql);
			} else {
				query = em.createQuery(jpql);
			}
			for (Parameter<?> p : query.getParameters()) {
				if (runtime.hasParameter(p.getName())) {
					query.setParameter(p.getName(), runtime.getParameter(p.getName()));
				}
			}
			if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
				if (Statement.MODE_COUNT.equals(mode)) {
					data = query.getSingleResult();
				} else {
					if(isPageSql(jpql)) {
						query.setFirstResult((runtime.page - 1) * runtime.size);
						query.setMaxResults(runtime.size);
					}
					data = query.getResultList();
				}
			} else {
				business.entityManagerContainer().beginTransaction(cls);
				data = query.executeUpdate();
				business.entityManagerContainer().commit();
			}
		}
		return data;
	}

	private Object jpql(EffectivePerson effectivePerson, Statement statement, Runtime runtime, String mode, Wi wi)
			throws Exception {
		Object data = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Class<? extends JpaObject> cls = this.clazz(business, statement);
			EntityManager em;
			if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
					&& StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
				em = business.entityManagerContainer().get(DynamicBaseEntity.class);
			} else {
				em = business.entityManagerContainer().get(cls);
			}
			String jpql = statement.getData();
			if (Statement.MODE_COUNT.equals(mode)) {
				jpql = statement.getCountData();
			}
			jpql = joinSql(jpql, wi, business);
			logger.info("执行的sql：{}", jpql);
			Query query;
			String upJpql = jpql.toUpperCase();
			if (upJpql.indexOf(JOIN_KEY) > -1 && upJpql.indexOf(JOIN_ON_KEY) > -1) {
				query = em.createNativeQuery(jpql);
			} else {
				query = em.createQuery(jpql);
			}
			for (Parameter<?> p : query.getParameters()) {
				if (runtime.hasParameter(p.getName())) {
					query.setParameter(p.getName(), runtime.getParameter(p.getName()));
				}
			}
			if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
				if (Statement.MODE_COUNT.equals(mode)) {
					data = query.getSingleResult();
				} else {
					if(isPageSql(jpql)) {
						query.setFirstResult((runtime.page - 1) * runtime.size);
						query.setMaxResults(runtime.size);
					}
					data = query.getResultList();
				}
			} else {
				business.entityManagerContainer().beginTransaction(cls);
				data = Integer.valueOf(query.executeUpdate());
				business.entityManagerContainer().commit();
			}
		}
		return data;
	}

	private boolean isPageSql(String sql){
		sql = sql.toUpperCase().replaceAll("\\s{1,}", " ");
		for (String key : pageKeys) {
			if (sql.indexOf(key) > -1) {
				return false;
			}
		}
		return true;
	}

	private Class<? extends JpaObject> clazz(Business business, Statement statement) throws Exception {
		Class<? extends JpaObject> cls = null;
		if (StringUtils.equals(Statement.ENTITYCATEGORY_OFFICIAL, statement.getEntityCategory())
				|| StringUtils.equals(Statement.ENTITYCATEGORY_CUSTOM, statement.getEntityCategory())) {
			cls = (Class<? extends JpaObject>) Class.forName(statement.getEntityClassName());
		} else {
			Table table = business.entityManagerContainer().flag(statement.getTable(), Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			cls = (Class<? extends JpaObject>) Class.forName(dynamicEntity.className());
		}
		return cls;
	}

	private ScriptContext scriptContext(EffectivePerson effectivePerson, Business business, Runtime runtime)
			throws Exception {
		ScriptContext scriptContext = new SimpleScriptContext();
		Resources resources = new Resources();
		resources.setEntityManagerContainer(business.entityManagerContainer());
		resources.setContext(ThisApplication.context());
		resources.setApplications(ThisApplication.context().applications());
		resources.setWebservicesClient(new WebservicesClient());
		resources.setOrganization(new Organization(ThisApplication.context()));
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(ScriptFactory.BINDING_NAME_RESOURCES, resources);
		bindings.put(ScriptFactory.BINDING_NAME_EFFECTIVEPERSON, effectivePerson);
		bindings.put(ScriptFactory.BINDING_NAME_PARAMETERS, gson.toJson(runtime.getParameters()));
		return scriptContext;
	}

	private String joinSql(String sql, Wi wi, Business business) throws Exception {
		if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
			List<String> list = new ArrayList<>();
			String whereSql = sql.replaceAll("\\s{1,}", " ");
			String rightSql = "";
			String leftSql = "";
			boolean hasWhere = false;
			if (sql.indexOf("where") > -1) {
				whereSql = StringUtils.substringAfter(sql, "where");
				leftSql = StringUtils.substringBefore(sql, "where");
				hasWhere = true;
			} else if (sql.indexOf("WHERE") > -1) {
				whereSql = StringUtils.substringAfter(sql, "WHERE");
				leftSql = StringUtils.substringBefore(sql, "WHERE");
				hasWhere = true;
			}
			String matchKey = "";
			for (String key : keys) {
				if (whereSql.indexOf(key) > -1) {
					matchKey = key;
					rightSql = StringUtils.substringAfter(whereSql, key);
					whereSql = StringUtils.substringBefore(whereSql, key);
					break;
				}
			}
			List<String> filterList = new ArrayList<>();
			for (FilterEntry filterEntry : wi.getFilterList()) {
				if (StringUtils.isNotBlank(filterEntry.path) && StringUtils.isNotBlank(filterEntry.value)) {
					StringBuilder sb = new StringBuilder();
					sb.append(filterEntry.path);
					sb.append(" ");
					sb.append(Comparison.getMatchCom(filterEntry.comparison));
					sb.append(" ");
					sb.append(":" + filterEntry.value);
					filterList.add(sb.toString());
				}
			}
			if (hasWhere) {
				list.add(leftSql);
				list.add("WHERE");
			} else {
				list.add(whereSql);
				if (!filterList.isEmpty()) {
					list.add("WHERE");
				}
			}
			if (!filterList.isEmpty()) {
				list.add("(");
				list.add(StringUtils.join(filterList, " AND "));
				list.add(")");
			}
			if (hasWhere) {
				list.add("AND");
				list.add("(");
				list.add(whereSql);
				list.add(")");
			}
			if (StringUtils.isNotBlank(matchKey)) {
				list.add(matchKey);
				list.add(rightSql);
			}
			sql = StringUtils.join(list, " ");
		}
		String upSql = sql.toUpperCase();
		if (upSql.indexOf(JOIN_KEY) > -1 && upSql.indexOf(JOIN_ON_KEY) > -1) {
			sql = sql.replaceAll("\\.", ".x");
			sql = sql.replaceAll("\\.x\\*", ".*");
			List<Table> tables = business.entityManagerContainer().fetchEqual(Table.class,
					ListTools.toList(Table.name_FIELDNAME), Table.status_FIELDNAME, Table.STATUS_build);
			for (Table table : tables) {
				sql = sql.replaceAll(" " + table.getName() + " ",
						" " + DynamicEntity.TABLE_PREFIX + table.getName().toUpperCase() + " ");
			}
		}
		return sql;
	}

	public static class Resources extends AbstractResources {

		private Organization organization;

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("过滤")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "com.x.query.core.express.plan.FilterEntry", fieldValue = "{\"logic\": \"and\", \"path\": \"o.name\", \"comparison\": \"equals\", \"value\": \"name\", \"formatType\": \"textValue\"}", fieldSample = "{\"logic\":\"逻辑运算:and\",\"path\":\"data数据的路径:o.title\",\"comparison\":\"比较运算符:equals|notEquals|like|notLike|greaterThan|greaterThanOrEqualTo|lessThan|lessThanOrEqualTo\","
				+ "\"value\":\"7月\",\"formatType\":\"textValue|numberValue|dateTimeValue|booleanValue\"}")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("参数")
		private JsonElement parameter;

		public List<FilterEntry> getFilterList() {
			return filterList;
		}

		public void setFilterList(List<FilterEntry> filterList) {
			this.filterList = filterList;
		}

		public JsonElement getParameter() {
			return parameter;
		}

		public void setParameter(JsonElement parameter) {
			this.parameter = parameter;
		}
	}

}
