package com.x.processplatform.service.processing.schedule;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.message.WorkCompletedEvent;
import com.x.processplatform.core.entity.message.WorkEvent;
import com.x.processplatform.service.processing.Business;

public class CleanEvent extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanEvent.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            TimeStamp stamp = new TimeStamp();
            Date threshold = DateUtils.addDays(new Date(), -7);
            int workEventCount = this.clean(business, WorkEvent.class, threshold);
            int workComnpletedEventCount = this.clean(business, WorkCompletedEvent.class, threshold);
            LOGGER.print("清理{}个工作事件, {}个已完成工作事件, 耗时:{}.", workEventCount, workComnpletedEventCount,
                    stamp.consumingMilliseconds());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private <T extends JpaObject> int clean(Business business, Class<T> clazz, Date threshold) throws Exception {
        EntityManagerContainer emc = business.entityManagerContainer();
        EntityManager em = emc.get(clazz);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.where(cb.lessThan(root.get(JpaObject_.createTime), threshold));
        List<T> os = em.createQuery(cq).setMaxResults(200).getResultList();
        int count = 0;
        while (!os.isEmpty()) {
            emc.beginTransaction(clazz);
            for (T t : os) {
                emc.remove(t, CheckRemoveType.all);
            }
            emc.commit();
            count += os.size();
            os = em.createQuery(cq).setMaxResults(200).getResultList();
        }
        return count;
    }
}