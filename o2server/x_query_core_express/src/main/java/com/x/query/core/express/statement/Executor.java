package com.x.query.core.express.statement;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicBaseEntity;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.NumberTools;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

public class Executor {

	private Executor() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

	public static Object executeData(Statement statement, Runtime runtime, ExecuteTarget executeTarget)
			throws Exception {
		if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL, Statement.FORMAT_SQLSCRIPT)) {
			return executeDataSql(runtime, executeTarget);
		} else {
			return executeDataJpql(statement, runtime, executeTarget);
		}
	}

	private static Object executeDataSql(Runtime runtime, ExecuteTarget executeTarget) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(DynamicBaseEntity.class);
			LOGGER.debug("executeDataSql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
			Query query = em.createNativeQuery(executeTarget.getSql(), LinkedHashMap.class);
			for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
				int idx = Integer.parseInt(entry.getKey().substring(1));
				query.setParameter(idx, entry.getValue());
			}
			if (executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
				if (NumberTools.greaterThan(runtime.page, 0) && NumberTools.greaterThan(runtime.size, 0)) {
					query.setFirstResult((runtime.page - 1) * runtime.size);
					query.setMaxResults(runtime.size);
				}
				return query.getResultList();
			} else {
				emc.beginTransaction(DynamicBaseEntity.class);
				Object data = Integer.valueOf(query.executeUpdate());
				emc.commit();
				return data;
			}
		}
	}

	private static Object executeDataJpql(Statement statement, Runtime runtime, ExecuteTarget executeTarget)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Class<? extends JpaObject> cls = clazz(emc, statement);
			EntityManager em;
			if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
					&& executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
				em = emc.get(DynamicBaseEntity.class);
			} else {
				em = emc.get(cls);
			}
			LOGGER.debug("executeDataJpql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
			Query query = em.createQuery(executeTarget.getSql());
			for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
				int idx = Integer.parseInt(entry.getKey().substring(1));
				query.setParameter(idx, entry.getValue());
			}
			if (executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
				if (NumberTools.greaterThan(runtime.page, 0) && NumberTools.greaterThan(runtime.size, 0)) {
					query.setFirstResult((runtime.page - 1) * runtime.size);
					query.setMaxResults(runtime.size);
				}
				return query.getResultList();
			} else {
				emc.beginTransaction(cls);
				Object data = Integer.valueOf(query.executeUpdate());
				emc.commit();
				return data;
			}
		}
	}

	public static Long executeCount(Statement statement, ExecuteTarget executeTarget) throws Exception {
		if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL, Statement.FORMAT_SQLSCRIPT)) {
			return executeCountSql(executeTarget);
		} else {
			return executeCountJpql(statement, executeTarget);
		}
	}

	private static Long executeCountSql(ExecuteTarget executeTarget) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(DynamicBaseEntity.class);
			LOGGER.debug("executeCountSql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
			Query query = em.createNativeQuery(executeTarget.getSql());
			for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
				int idx = Integer.parseInt(entry.getKey().substring(1));
				query.setParameter(idx, entry.getValue());
			}
			return (Long) query.getSingleResult();
		}
	}

	private static Long executeCountJpql(Statement statement, ExecuteTarget executeTarget) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Class<? extends JpaObject> cls = clazz(emc, statement);
			EntityManager em;
			if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
					&& executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
				em = emc.get(DynamicBaseEntity.class);
			} else {
				em = emc.get(cls);
			}
			LOGGER.debug("executeCountJpql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
			Query query = em.createQuery(executeTarget.getSql());
			for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
				int idx = Integer.parseInt(entry.getKey().substring(1));
				query.setParameter(idx, entry.getValue());
			}
			return (Long) query.getSingleResult();
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends JpaObject> clazz(EntityManagerContainer entityManagerContainer, Statement statement)
			throws Exception {
		Class<? extends JpaObject> cls = null;
		if (StringUtils.equals(Statement.ENTITYCATEGORY_OFFICIAL, statement.getEntityCategory())
				|| StringUtils.equals(Statement.ENTITYCATEGORY_CUSTOM, statement.getEntityCategory())) {
			cls = (Class<? extends JpaObject>) Thread.currentThread().getContextClassLoader()
					.loadClass(statement.getEntityClassName());
		} else {
			Table table = entityManagerContainer.flag(statement.getTable(), Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			cls = (Class<? extends JpaObject>) Thread.currentThread().getContextClassLoader()
					.loadClass(dynamicEntity.className());
		}
		return cls;
	}
}
