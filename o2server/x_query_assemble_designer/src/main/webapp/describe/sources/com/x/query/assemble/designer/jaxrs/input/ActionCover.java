package com.x.query.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.wrap.*;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;

class ActionCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCover.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Query query = business.entityManagerContainer().find(wi.getId(), Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(wi.getId());
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getName(), query.getName(), query.getId());
			}
			this.cover(business, wi, query);
			wo.setId(query.getId());
			result.setData(wo);
			return result;
		}
	}

	private void cover(Business business, Wi wi, Query query) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		for (WrapView _o : wi.getViewList()) {
			View obj = business.entityManagerContainer().find(_o.getId(), View.class);
			if (null != obj) {
				WrapView.inCopier.copy(_o, obj);
			} else {
				obj = WrapView.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), View.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), View.class, obj.getId()));
			}
			obj.setQuery(query.getId());
		}
		for (WrapStat _o : wi.getStatList()) {
			Stat obj = business.entityManagerContainer().find(_o.getId(), Stat.class);
			if (null != obj) {
				WrapStat.inCopier.copy(_o, obj);
			} else {
				obj = WrapStat.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), Stat.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), Stat.class, obj.getId()));
			}
			obj.setQuery(query.getId());
		}
		for (WrapReveal _o : wi.getRevealList()) {
			Reveal obj = business.entityManagerContainer().find(_o.getId(), Reveal.class);
			if (null != obj) {
				WrapReveal.inCopier.copy(_o, obj);
			} else {
				obj = WrapReveal.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), Reveal.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), Reveal.class, obj.getId()));
			}
			obj.setQuery(query.getId());
		}
		for (WrapTable _o : wi.getTableList()) {
			Table obj = business.entityManagerContainer().find(_o.getId(), Table.class);
			if (null != obj) {
				WrapTable.inCopier.copy(_o, obj);
			} else {
				obj = WrapTable.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), Table.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), Table.class, obj.getId()));
			}
			obj.setQuery(query.getId());
		}
		for (WrapStatement _o : wi.getStatementList()) {
			Statement obj = business.entityManagerContainer().find(_o.getId(), Statement.class);
			if (null != obj) {
				WrapStatement.inCopier.copy(_o, obj);
			} else {
				obj = WrapStatement.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(
						this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), Statement.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), Statement.class, obj.getId()));
			}
			obj.setQuery(query.getId());
		}
		business.entityManagerContainer().beginTransaction(Query.class);
		business.entityManagerContainer().beginTransaction(View.class);
		business.entityManagerContainer().beginTransaction(Stat.class);
		business.entityManagerContainer().beginTransaction(Reveal.class);
		business.entityManagerContainer().beginTransaction(Table.class);
		business.entityManagerContainer().beginTransaction(Statement.class);
		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		business.entityManagerContainer().commit();
		if(!wi.getTableList().isEmpty()){
			ApplicationCache.notify(Table.class);
			ApplicationCache.notify(Statement.class);

			business.buildAllTable();
		}else if(!wi.getStatementList().isEmpty()){
			ApplicationCache.notify(Statement.class);
		}
		if(!wi.getViewList().isEmpty()){
			ApplicationCache.notify(View.class);
		}
		if(!wi.getStatList().isEmpty()){
			ApplicationCache.notify(Stat.class);
		}
		if(!wi.getRevealList().isEmpty()){
			ApplicationCache.notify(Reveal.class);
		}
	}

	private <T extends JpaObject> String idleNameWithQuery(Business business, String queryId, String name, Class<T> cls,
			String excludeId) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(name);
		for (int i = 1; i < 99; i++) {
			list.add(name + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("name").in(list);
		p = cb.and(p, cb.equal(root.get("query"), queryId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("name")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	private <T extends JpaObject> String idleAliasWithQuery(Business business, String queryId, String alias,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(alias);
		for (int i = 1; i < 99; i++) {
			list.add(alias + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("alias").in(list);
		p = cb.and(p, cb.equal(root.get("query"), queryId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("alias")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public static class Wi extends WrapQuery {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WoId {

	}

}