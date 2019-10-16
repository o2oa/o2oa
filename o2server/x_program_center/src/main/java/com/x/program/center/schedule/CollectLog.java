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

	private static Logger logger = LoggerFactory.getLogger(CollectLog.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					if (BooleanUtils.isTrue(Config.collect().getEnable())) {
						Business business = new Business(emc);
						if (business.validateCollect()) {
							List<PromptErrorLog> os_promptErrorLog = this.list_promptErrorLog(emc);
							List<UnexpectedErrorLog> os_unexpectedErrorLog = this.list_unexpectedErrorLog(emc);
							List<WarnLog> os_warnLog = this.list_warnLog(emc);
							if (!os_promptErrorLog.isEmpty()) {
								Req req = new Req();
								req.setName(Config.collect().getName());
								req.setPassword(Config.collect().getPassword());
								req.setPromptErrorLogList(os_promptErrorLog);
								try {
									ActionResponse response = ConnectionAction
											.put(Config.collect().url(ADDRESS_COLLECT_PROMPTERRORLOG), null, req);
									response.getData(WrapOutBoolean.class);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if (!os_unexpectedErrorLog.isEmpty()) {
								Req req = new Req();
								req.setName(Config.collect().getName());
								req.setPassword(Config.collect().getPassword());
								req.setUnexceptedErrorLog(os_unexpectedErrorLog);
								try {
									ActionResponse response = ConnectionAction
											.put(Config.collect().url(ADDRESS_COLLECT_UNEXPECTEDERRORLOG), null, req);
									response.getData(WrapOutBoolean.class);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if (!os_warnLog.isEmpty()) {
								Req req = new Req();
								req.setName(Config.collect().getName());
								req.setPassword(Config.collect().getPassword());
								req.setWarnLogList(os_warnLog);
								try {
									ActionResponse response = ConnectionAction
											.put(Config.collect().url(ADDRESS_COLLECT_WARNLOG), null, req);
									response.getData(WrapOutBoolean.class);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							logger.info("无法登录到云服务器.");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	public static class Req extends GsonPropertyObject {

		private String name;
		private String password;
		private List<PromptErrorLog> promptErrorLogList = new ArrayList<>();
		private List<UnexpectedErrorLog> unexceptedErrorLog = new ArrayList<>();
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

		public List<UnexpectedErrorLog> getUnexceptedErrorLog() {
			return unexceptedErrorLog;
		}

		public void setUnexceptedErrorLog(List<UnexpectedErrorLog> unexceptedErrorLog) {
			this.unexceptedErrorLog = unexceptedErrorLog;
		}

	}

	private List<PromptErrorLog> list_promptErrorLog(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(PromptErrorLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PromptErrorLog> cq = cb.createQuery(PromptErrorLog.class);
		Root<PromptErrorLog> root = cq.from(PromptErrorLog.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Predicate p = cb.greaterThan(root.get(PromptErrorLog_.createTime), cal.getTime());
		p = cb.and(p, cb.notEqual(root.get(PromptErrorLog_.collected), true));
		cq.select(root).where(p).orderBy(cb.desc(root.get(PromptErrorLog_.createTime)));
		List<PromptErrorLog> list = em.createQuery(cq).setMaxResults(10).getResultList();
		if (!list.isEmpty()) {
			emc.beginTransaction(PromptErrorLog.class);
			for (PromptErrorLog o : list) {
				o.setCollected(true);
			}
			emc.commit();
		}
		return list;
	}

	private List<UnexpectedErrorLog> list_unexpectedErrorLog(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(UnexpectedErrorLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnexpectedErrorLog> cq = cb.createQuery(UnexpectedErrorLog.class);
		Root<UnexpectedErrorLog> root = cq.from(UnexpectedErrorLog.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Predicate p = cb.greaterThan(root.get(UnexpectedErrorLog_.createTime), cal.getTime());
		p = cb.and(p, cb.notEqual(root.get(UnexpectedErrorLog_.collected), true));
		cq.select(root).where(p).orderBy(cb.desc(root.get(UnexpectedErrorLog_.createTime)));
		List<UnexpectedErrorLog> list = em.createQuery(cq).setMaxResults(10).getResultList();
		if (!list.isEmpty()) {
			emc.beginTransaction(UnexpectedErrorLog.class);
			for (UnexpectedErrorLog o : list) {
				o.setCollected(true);
			}
			emc.commit();
		}
		return list;
	}

	private List<WarnLog> list_warnLog(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(WarnLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarnLog> cq = cb.createQuery(WarnLog.class);
		Root<WarnLog> root = cq.from(WarnLog.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Predicate p = cb.greaterThan(root.get(WarnLog_.createTime), cal.getTime());
		p = cb.and(p, cb.notEqual(root.get(WarnLog_.collected), true));
		cq.select(root).where(p).orderBy(cb.desc(root.get(WarnLog_.createTime)));
		List<WarnLog> list = em.createQuery(cq).setMaxResults(10).getResultList();
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