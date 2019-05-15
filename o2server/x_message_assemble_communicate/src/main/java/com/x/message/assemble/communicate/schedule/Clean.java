package com.x.message.assemble.communicate.schedule;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Instant_;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

public class Clean implements Job {

	private static Logger logger = LoggerFactory.getLogger(Clean.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Business business = new Business(emc);
			Long instantCount = this.clearInstant(business);
			Long messageCount = this.clearMessage(business);
			logger.print("清理过期的消息内容,其中主体消息: {} 条, 消息: {} 条, 耗时: {}.", instantCount, messageCount,
					stamp.consumingMilliseconds());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private Long clearInstant(Business business) throws Exception {
		List<Instant> os = null;
		Long count = 0L;
		do {
			os = this.listInstant(business);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Instant.class);
				for (Instant o : os) {
					business.entityManagerContainer().remove(o);
				}
				business.entityManagerContainer().commit();
				count += os.size();
			}
		} while (ListTools.isNotEmpty(os));
		return count;
	}

	private List<Instant> listInstant(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Instant.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Instant> cq = cb.createQuery(Instant.class);
		Root<Instant> root = cq.from(Instant.class);
		Date limit = DateUtils.addDays(new Date(), -7);
		Predicate p = cb.lessThan(root.get(Instant_.createTime), limit);
		return em.createQuery(cq.select(root).where(p)).setMaxResults(2000).getResultList();
	}

	private Long clearMessage(Business business) throws Exception {
		List<Message> os = null;
		Long count = 0L;
		do {
			os = this.listMessage(business);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Message.class);
				for (Message o : os) {
					business.entityManagerContainer().remove(o);
				}
				business.entityManagerContainer().commit();
				count += os.size();
			}

		} while (ListTools.isNotEmpty(os));
		return count;
	}

	private List<Message> listMessage(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> cq = cb.createQuery(Message.class);
		Root<Message> root = cq.from(Message.class);
		Date limit = DateUtils.addDays(new Date(), -7);
		Predicate p = cb.lessThan(root.get(Message_.createTime), limit);
		return em.createQuery(cq.select(root).where(p)).setMaxResults(2000).getResultList();
	}

}