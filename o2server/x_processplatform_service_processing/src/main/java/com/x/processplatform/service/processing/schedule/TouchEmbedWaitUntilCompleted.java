package com.x.processplatform.service.processing.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;

public class TouchEmbedWaitUntilCompleted extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(TouchEmbedWaitUntilCompleted.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			AtomicInteger count = new AtomicInteger();
			Map<String, String> idJobs = this.list();
			if (!idJobs.isEmpty()) {
				for (Map.Entry<String, String> en : idJobs.entrySet()) {
					touch(en.getKey(), en.getValue(), count);
				}
			}
			if (count.get() > 0) {
				LOGGER.info("完成触发{}个等待子流程结束工作, 耗时:{}.", count::intValue, stamp::consumingMilliseconds);
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void touch(String id, String job, AtomicInteger count) {
		try {
			ThisApplication.context().applications()
					.putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", id, "processing"), new ProcessingAttributes(), job)
					.getData(WoId.class);
			count.incrementAndGet();
		} catch (Exception e) {
			LOGGER.error(new ExceptionTouchEmbedWaitUntilCompleted(e, id, job));
		}
	}

	private Map<String, String> list() throws Exception {
		Map<String, String> idJobs = new HashMap<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> embeds = emc.idsEqual(Embed.class, Embed.WAITUNTILCOMPLETED_FIELDNAME, true);
			List<String> ids = emc.idsEqualAndIn(Work.class, Work.activityType_FIELDNAME, ActivityType.embed,
					Work.activity_FIELDNAME, embeds);
			for (String id : ids) {
				Work work = emc.find(id, Work.class);
				if (StringUtils.isNotBlank(work.getProperties().getEmbedCompleted())) {
					idJobs.put(work.getId(), work.getJob());
				}
			}
		}
		return idJobs;
	}
}