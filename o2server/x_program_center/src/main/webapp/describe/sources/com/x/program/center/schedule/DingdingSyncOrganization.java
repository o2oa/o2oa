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
//2019-11-06 应该使用 com.x.program.center.dingding.SyncOrganization
//import com.x.program.center.zhengwudingding.SyncOrganization;
import com.x.program.center.dingding.SyncOrganization;

public class DingdingSyncOrganization implements Job {

	private static Logger logger = LoggerFactory.getLogger(DingdingSyncOrganization.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//2019-11-06 应该使用 ThisApplication.dingdingSyncOrganizationCallbackRequest 作判断
//			if (!ThisApplication.zhengwuDingdingSyncOrganizationCallbackRequest.isEmpty()) {
//				ThisApplication.zhengwuDingdingSyncOrganizationCallbackRequest.clear();
			if (!ThisApplication.dingdingSyncOrganizationCallbackRequest.isEmpty()) {
				ThisApplication.dingdingSyncOrganizationCallbackRequest.clear();
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
