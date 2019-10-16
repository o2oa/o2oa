package com.x.teamwork.assemble.control.timertask;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.core.entity.Task;

/**
 * 定时代理: 定期将所有的工作任务的截止时间核对一次，标识超时的工作任务
 *
 */
public class Timertask_CheckAllTaskOverTime extends AbstractJob {

	private ProjectQueryService projectQueryService = new ProjectQueryService();
	private TaskQueryService taskQueryService = new TaskQueryService();
	private static Logger logger = LoggerFactory.getLogger( Timertask_CheckAllTaskOverTime.class );

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		Task task = null;
		Date now = new Date();
		List<String> projectIds = null;
		List<String> taskIds = null;
		try {
			projectIds = projectQueryService.listAllProjectIds();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if( ListTools.isNotEmpty( projectIds )) {
			for( String projectId : projectIds ) {
				try {
					taskIds = taskQueryService.listAllTaskIdsWithProject( projectId );
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						if( ListTools.isNotEmpty( taskIds )) {
							for( String taskId : taskIds ) {
								task = emc.find( taskId, Task.class );
								if( task != null ) {
									logger.info("Timertask_CheckAllTaskOverTime check  task:" +  task.getName());									
									
									if( task.getEndTime()  != null ) {
										if( task.getEndTime().before( now ) && !task.getOvertime()) {
											//超时了,打上标识，并且发送提醒
											emc.beginTransaction( Task.class );
											task.setOvertime( true );
											emc.check( task, CheckPersistType.all );
											emc.commit();
											
											try {
												MessageFactory.message_to_teamWorkOverTime( task, true );				
											} catch (Exception e) {
												logger.error(e);
											}																
										}
										
										Date now_30 = DateUtils.addMinutes(task.getEndTime(), -30);
										if( now_30.before( now ) && !task.getOvertime()) {
											//发送工作任务即将超时提醒
											try {
												MessageFactory.message_to_teamWorkOverTime( task, false );				
											} catch (Exception e) {
												logger.error(e);
											}																
										}
									}
								}
							}
						}
					} catch (Exception e) {
						throw e;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("Timertask_BatchOperationTask -> batch operations timer task excute completed.");
	}
}