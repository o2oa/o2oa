package com.x.processplatform.assemble.surface.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.core.entity.content.KeyLock;

public class CleanKeyLock extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CleanKeyLock.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			List<KeyLock> targets = new ArrayList<>();
			Integer count = 0;
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					targets = emc.listLessThan(KeyLock.class, JpaObject.createTime_FIELDNAME,
							DateUtils.addMinutes(new Date(), -2));
					if (!targets.isEmpty()) {
						emc.beginTransaction(KeyLock.class);
						for (KeyLock o : targets) {
							emc.remove(o);
							count++;
						}
						emc.commit();
					}
				}
			} while (!targets.isEmpty());
			logger.debug("定时清理值锁定:{}条.", count);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
}