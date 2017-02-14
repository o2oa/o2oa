package com.x.processplatform.assemble.surface.timer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.gson.XGsonBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryStatTimed;
import com.x.processplatform.core.entity.element.QueryStatTimed_;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.query.Query;

public class QueryStatTask implements Runnable {

	public void run() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (String str : this.list(emc)) {
				QueryStatTimed queryStatTimed = emc.find(str, QueryStatTimed.class);
				if (null != queryStatTimed) {
					QueryStat queryStat = emc.find(queryStatTimed.getQueryStat(), QueryStat.class);
					if (null != queryStat) {
						QueryView queryView = emc.find(queryStat.getQueryView(), QueryView.class);
						if (null != queryView) {
							try {
								Gson gson = XGsonBuilder.instance();
								Query query = gson.fromJson(queryView.getData(), Query.class);
								query.query();
								emc.beginTransaction(QueryStat.class);
								queryStat.setData(gson.toJson(query));
								emc.commit();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					this.clear(emc, queryStatTimed.getQueryStat());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> list(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(QueryStatTimed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryStatTimed> root = cq.from(QueryStatTimed.class);
		Predicate p = cb.equal(root.get(QueryStatTimed_.project), ThisApplication.token);
		cq.select(root.get(QueryStatTimed_.id)).orderBy(cb.asc(root.get(QueryStatTimed_.createTime)));
		return em.createQuery(cq.where(p)).getResultList();
	}

	private void clear(EntityManagerContainer emc, String queryStatId) throws Exception {
		EntityManager em = emc.get(QueryStatTimed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueryStatTimed> cq = cb.createQuery(QueryStatTimed.class);
		Root<QueryStatTimed> root = cq.from(QueryStatTimed.class);
		Predicate p = cb.equal(root.get(QueryStatTimed_.queryStat), queryStatId);
		cq.select(root).where(p);
		emc.beginTransaction(QueryStatTimed.class);
		for (QueryStatTimed o : em.createQuery(cq.where(p)).getResultList()) {
			emc.remove(o);
		}
		emc.commit();
	}

}