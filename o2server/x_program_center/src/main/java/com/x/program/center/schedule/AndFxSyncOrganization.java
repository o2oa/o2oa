package com.x.program.center.schedule;

import org.apache.commons.lang3.BooleanUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import com.x.program.center.andfx.SyncOrganization;


/**
 * @author sword
 */
public class AndFxSyncOrganization extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(AndFxSyncOrganization.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			if (pirmaryCenter() && BooleanUtils.isTrue(Config.andFx().getForceSyncEnable())) {
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
