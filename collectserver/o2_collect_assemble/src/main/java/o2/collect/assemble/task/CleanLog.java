package o2.collect.assemble.task;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.Business;
import o2.collect.core.entity.log.AppLog;
import o2.collect.core.entity.log.PromptErrorLog;
import o2.collect.core.entity.log.UnexpectedErrorLog;
import o2.collect.core.entity.log.WarnLog;

public class CleanLog implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(CleanLog.class);

	public void run() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Date start = new Date();

			Business business = new Business(emc);
			Date date = new Date();
			DateUtils.addDays(date, -14);
			Long cleanWarnLogCount = this.cleanLog(business, date, WarnLog.class);
			Long cleanPromptErrorLogCount = this.cleanLog(business, date, PromptErrorLog.class);
			Long cleanUnexpectedErrorLogCount = this.cleanLog(business, date, UnexpectedErrorLog.class);
			Long cleanAppLogCount = this.cleanLog(business, date, AppLog.class);

			logger.print("删除过期日志 WarnLog:{}, PromptErrorLog:{}, UnexpectedErrorLog:{}, AppLog:{}, 耗时:{}毫秒.",
					cleanWarnLogCount, cleanPromptErrorLogCount, cleanUnexpectedErrorLogCount, cleanAppLogCount,
					(new Date()).getTime() - start.getTime());

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private <T extends JpaObject> long cleanLog(Business business, Date date, Class<T> cls) throws Exception {
		Long count = 0L;
		for (int i = 0; i < 1000; i++) {
			EntityManager em = business.entityManagerContainer().get(cls);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(cls);
			Root<T> root = cq.from(cls);
			Predicate p = cb.lessThan(root.get(JpaObject.createTime_FIELDNAME), date);
			List<T> list = em.createQuery(cq.where(p)).setMaxResults(1000).getResultList();
			if (list.isEmpty()) {
				break;
			} else {
				business.entityManagerContainer().beginTransaction(cls);
				count += list.size();
				for (T t : list) {
					business.entityManagerContainer().remove(t);
				}
				business.entityManagerContainer().commit();
			}
		}
		return count;
	}

}