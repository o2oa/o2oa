package com.x.processplatform.assemble.designer.schedule;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.FormVersion;
import com.x.processplatform.core.entity.element.FormVersion_;
import com.x.processplatform.core.entity.element.ProcessVersion;
import com.x.processplatform.core.entity.element.ProcessVersion_;
import com.x.processplatform.core.entity.element.ScriptVersion;
import com.x.processplatform.core.entity.element.ScriptVersion_;

public class CleanElementVersion extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CleanElementVersion.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);

			int formVersionCount = cleanFormVersion(business);
			int processVersionCount = cleanProcessVersion(business);
			int scriptVersionCount = cleanScriptVersion(business);

			logger.print("清理历史表单 {} 个, 历史流程{} 个, 历史脚本{} 个, 耗时:{}.", formVersionCount, processVersionCount,
					scriptVersionCount, stamp.consumingMilliseconds());
		
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private Integer cleanScriptVersion(Business business) throws Exception {
		List<String> ids;
		ScriptVersion scriptVersion;
		int count = 0;
		do {
			ids = this.listScriptVersion(business);
			if (ListTools.isNotEmpty(ids)) {
				business.entityManagerContainer().beginTransaction(ScriptVersion.class);
				for (String id : ids) {
					scriptVersion = business.entityManagerContainer().find(id, ScriptVersion.class);
					business.entityManagerContainer().remove(scriptVersion, CheckRemoveType.all);
					count++;
				}
				business.entityManagerContainer().commit();
			}
		} while (ListTools.isNotEmpty(ids));
		return count;
	}

	private Integer cleanProcessVersion(Business business) throws Exception {
		List<String> ids;
		ProcessVersion processVersion;
		int count = 0;
		do {
			ids = this.listProcessVersion(business);
			if (ListTools.isNotEmpty(ids)) {
				business.entityManagerContainer().beginTransaction(ProcessVersion.class);
				for (String id : ids) {
					processVersion = business.entityManagerContainer().find(id, ProcessVersion.class);
					business.entityManagerContainer().remove(processVersion, CheckRemoveType.all);
					count++;
				}
				business.entityManagerContainer().commit();
			}
		} while (ListTools.isNotEmpty(ids));
		return count;
	}

	private Integer cleanFormVersion(Business business) throws Exception {
		List<String> ids;
		FormVersion formVersion;
		int count = 0;
		do {
			ids = this.listFormVersion(business);
			if (ListTools.isNotEmpty(ids)) {
				business.entityManagerContainer().beginTransaction(FormVersion.class);
				for (String id : ids) {
					formVersion = business.entityManagerContainer().find(id, FormVersion.class);
					business.entityManagerContainer().remove(formVersion, CheckRemoveType.all);
					count++;
				}
				business.entityManagerContainer().commit();
			}
		} while (ListTools.isNotEmpty(ids));
		return count;
	}

	private List<String> listFormVersion(Business business) throws Exception {
		Date date = new Date();
		date = DateUtils.addDays(date, 0 - Config.processPlatform().getFormVersionPeriod());
		EntityManager em = business.entityManagerContainer().get(FormVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormVersion> root = cq.from(FormVersion.class);
		Predicate p = cb.lessThan(root.get(FormVersion_.createTime), date);
		cq.select(root.get(FormVersion_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq.distinct(true)).setMaxResults(100).getResultList();
		return os;
	}

	private List<String> listProcessVersion(Business business) throws Exception {
		Date date = new Date();
		date = DateUtils.addDays(date, 0 - Config.processPlatform().getProcessVersionPeriod());
		EntityManager em = business.entityManagerContainer().get(ProcessVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProcessVersion> root = cq.from(ProcessVersion.class);
		Predicate p = cb.lessThan(root.get(ProcessVersion_.createTime), date);
		cq.select(root.get(ProcessVersion_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq.distinct(true)).setMaxResults(100).getResultList();
		return os;
	}

	private List<String> listScriptVersion(Business business) throws Exception {
		Date date = new Date();
		date = DateUtils.addDays(date, 0 - Config.processPlatform().getScriptVersionPeriod());
		EntityManager em = business.entityManagerContainer().get(ScriptVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ScriptVersion> root = cq.from(ScriptVersion.class);
		Predicate p = cb.lessThan(root.get(ScriptVersion_.createTime), date);
		cq.select(root.get(ScriptVersion_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq.distinct(true)).setMaxResults(100).getResultList();
		return os;
	}

}