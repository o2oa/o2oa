package com.x.processplatform.service.processing.schedule;

import java.util.List;

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
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;

public class Delay extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Delay.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = this.list(business);
			for (String id : ids) {
				Work work = business.entityManagerContainer().find(id, Work.class);
				if (null != work) {
					try {
						logger.debug("触发延时任务流转: {}, id: {}.", work.getTitle(), work.getId());
						ThisApplication.context().applications()
								.putQuery(x_processplatform_service_processing.class,
										Applications.joinQueryUri("work", work.getId(), "processing"),
										new ProcessingAttributes())
								.getData(WoId.class);
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}
			logger.print("触发处于延时活动工作数量: {}, 耗时: {}.", ids.size(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private List<String> list(Business business) throws Exception {
		return business.entityManagerContainer().idsEqual(Work.class, Work.activityType_FIELDNAME, ActivityType.delay);
	}

}