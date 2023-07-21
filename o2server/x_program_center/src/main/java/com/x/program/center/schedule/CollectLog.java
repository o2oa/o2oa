package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.PromptErrorLog_;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.UnexpectedErrorLog_;
import com.x.program.center.core.entity.WarnLog;
import com.x.program.center.core.entity.WarnLog_;

public class CollectLog extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectLog.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter() && BooleanUtils.isTrue(Config.collect().getEnable())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					if (BooleanUtils.isNotTrue(business.validateCollect())) {
						LOGGER.warn("login cloud server failure.");
					}
				}
				this.collectPromptErrorLog();
				this.collectUnexpectedErrorLog();
				this.collectWarnLog();
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void collectPromptErrorLog() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<PromptErrorLog> list = this.listPromptErrorLog(emc);
			if (!list.isEmpty()) {
				Req req = new Req();
				req.setName(Config.collect().getName());
				req.setPassword(Config.collect().getPassword());
				req.setPromptErrorLogList(list);
				try {
					ActionResponse response = ConnectionAction.put(Config.collect().url(ADDRESS_COLLECT_PROMPTERRORLOG),
							null, req);
					response.getData(WrapOutBoolean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void collectUnexpectedErrorLog() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<UnexpectedErrorLog> list = this.listUnexpectedErrorLog(emc);
			if (!list.isEmpty()) {
				Req req = new Req();
				req.setName(Config.collect().getName());
				req.setPassword(Config.collect().getPassword());
				req.setUnexpectedErrorLogList(list);
				try {
					ActionResponse response = ConnectionAction
							.put(Config.collect().url(ADDRESS_COLLECT_UNEXPECTEDERRORLOG), null, req);
					response.getData(WrapOutBoolean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void collectWarnLog() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<WarnLog> list = this.listWarnLog(emc);
			if (!list.isEmpty()) {
				Req req = new Req();
				req.setName(Config.collect().getName());
				req.setPassword(Config.collect().getPassword());
				req.setWarnLogList(list);
				try {
					ActionResponse response = ConnectionAction.put(Config.collect().url(ADDRESS_COLLECT_WARNLOG), null,
							req);
					response.getData(WrapOutBoolean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static class Req extends GsonPropertyObject {

		private static final long serialVersionUID = 2018703062822498687L;
		private String name;
		private String password;
		private List<PromptErrorLog> promptErrorLogList = new ArrayList<>();
		private List<UnexpectedErrorLog> unexpectedErrorLogList = new ArrayList<>();
		private List<WarnLog> warnLogList = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public List<PromptErrorLog> getPromptErrorLogList() {
			return promptErrorLogList;
		}

		public void setPromptErrorLogList(List<PromptErrorLog> promptErrorLogList) {
			this.promptErrorLogList = promptErrorLogList;
		}

		public List<WarnLog> getWarnLogList() {
			return warnLogList;
		}

		public void setWarnLogList(List<WarnLog> warnLogList) {
			this.warnLogList = warnLogList;
		}

		public List<UnexpectedErrorLog> getUnexpectedErrorLogList() {
			return unexpectedErrorLogList;
		}

		public void setUnexpectedErrorLogList(List<UnexpectedErrorLog> unexpectedErrorLogList) {
			this.unexpectedErrorLogList = unexpectedErrorLogList;
		}

	}

	private List<PromptErrorLog> listPromptErrorLog(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(PromptErrorLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PromptErrorLog> cq = cb.createQuery(PromptErrorLog.class);
		Root<PromptErrorLog> root = cq.from(PromptErrorLog.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Predicate p = cb.greaterThan(root.get(JpaObject_.createTime), cal.getTime());
		p = cb.and(p, cb.or(cb.notEqual(root.get(PromptErrorLog_.collected), true),
				cb.isNull(root.get(PromptErrorLog_.collected))));
		cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.createTime)));
		List<PromptErrorLog> list = em.createQuery(cq).setMaxResults(20).getResultList();
		if (!list.isEmpty()) {
			emc.beginTransaction(PromptErrorLog.class);
			for (PromptErrorLog o : list) {
				o.setCollected(true);
			}
			emc.commit();
		}
		return list;
	}

	private List<UnexpectedErrorLog> listUnexpectedErrorLog(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(UnexpectedErrorLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnexpectedErrorLog> cq = cb.createQuery(UnexpectedErrorLog.class);
		Root<UnexpectedErrorLog> root = cq.from(UnexpectedErrorLog.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Predicate p = cb.greaterThan(root.get(JpaObject_.createTime), cal.getTime());
		p = cb.and(p, cb.or(cb.notEqual(root.get(UnexpectedErrorLog_.collected), true),
				cb.isNull(root.get(UnexpectedErrorLog_.collected))));
		cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.createTime)));
		List<UnexpectedErrorLog> list = em.createQuery(cq).setMaxResults(20).getResultList();
		if (!list.isEmpty()) {
			emc.beginTransaction(UnexpectedErrorLog.class);
			for (UnexpectedErrorLog o : list) {
				o.setCollected(true);
			}
			emc.commit();
		}
		return list;
	}

	private List<WarnLog> listWarnLog(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(WarnLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarnLog> cq = cb.createQuery(WarnLog.class);
		Root<WarnLog> root = cq.from(WarnLog.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Predicate p = cb.greaterThan(root.get(JpaObject_.createTime), cal.getTime());
		p = cb.and(p, cb.or(cb.notEqual(root.get(WarnLog_.collected), true), cb.isNull(root.get(WarnLog_.collected))));
		cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.createTime)));
		List<WarnLog> list = em.createQuery(cq).setMaxResults(20).getResultList();
		if (!list.isEmpty()) {
			emc.beginTransaction(WarnLog.class);
			for (WarnLog o : list) {
				o.setCollected(true);
			}
			emc.commit();
		}
		return list;
	}

}