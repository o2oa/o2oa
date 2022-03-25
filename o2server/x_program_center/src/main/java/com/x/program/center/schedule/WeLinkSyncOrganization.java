package com.x.program.center.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.welink.SyncOrganization;



public class WeLinkSyncOrganization implements Job {

	private static Logger logger = LoggerFactory.getLogger(WeLinkSyncOrganization.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			if (!ThisApplication.weLinkSyncOrganizationCallbackRequest.isEmpty()) {
				ThisApplication.weLinkSyncOrganizationCallbackRequest.clear();
				Business business = new Business(emc);
				SyncOrganization o = new SyncOrganization();
				o.execute(business);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}
