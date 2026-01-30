package com.x.teamwork.assemble.control.schedule;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.core.entity.Task;
import org.quartz.JobExecutionContext;

import java.util.List;

/**
 * 定时代理: 定期将所有的工作任务的截止时间核对一次，标识超时的工作任务
 *
 * @author sword
 */
public class CheckAllTaskOverTimeJob extends AbstractJob {

	private ProjectQueryService projectQueryService = new ProjectQueryService();
	private static Logger logger = LoggerFactory.getLogger( CheckAllTaskOverTimeJob.class );

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		List<String> projectIds = projectQueryService.listNotArchiveProject();
		logger.info("start CheckAllTaskOverTime from project {}.", projectIds.size());

		for( String projectId : projectIds ) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business( emc );
				List<Task> taskList = business.taskFactory().listExpireTasks(projectId);
				for(Task task : taskList){
					emc.beginTransaction( Task.class );
					task.setOvertime( true );
					emc.commit();
				}
			} catch (Exception e) {
				throw e;
			}
		}

		logger.info("Check task overtime execute completed.");
	}
}
