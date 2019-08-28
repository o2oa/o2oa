package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.BatchOperation;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;

/**
 * 批处理操作执行
 */
public class BatchOperationProcessService {
	
	public static String OPT_OBJ_TASK = "TASK";
	public static String OPT_OBJ_PROJECT = "PROJECT";
	public static String OPT_OBJ_TASKLIST = "TASKLIST";
	public static String OPT_TYPE_PERMISSION = "PERMISSION";
	public static String OPT_TYPE_UPDATENAME = "UPDATENAME";
	public static String OPT_TYPE_DELETE = "DELETE";
	
	private static  Logger logger = LoggerFactory.getLogger( BatchOperationProcessService.class );
	private TaskService taskService = new TaskService();
	private ReviewService reviewService = new ReviewService();
	/**
	 * 批处理操作执行
	 * @param batchOperation
	 * @return
	 * @throws Exception 
	 */
	public String process( BatchOperation batchOperation ) throws Exception {
		logger.info( "process -> Task processing batch operation: " + batchOperation.toString() );
		//先把batchOperation状态修改为执行中
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			batchOperation = emc.find( batchOperation.getId(), BatchOperation.class );
			batchOperation.setIsRunning( true );
			emc.beginTransaction( BatchOperation.class );
			emc.check( batchOperation, CheckPersistType.all );
			logger.info( "process -> Task change batch operation running......: " );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		
		if( "TASK".equalsIgnoreCase( batchOperation.getObjType() )) {
			if( "PERMISSION".equalsIgnoreCase( batchOperation.getOptType() )) {//工作任务处理
				//重新计算指定任务所有的Review信息
				refreshTaskReview( batchOperation.getId(), batchOperation.getBundle() );
			}else if( "DELETE".equalsIgnoreCase( batchOperation.getOptType()  )) {
				//删除工作任务，并且修改分类所属的栏目中的分类数量和工作任务数量
				deleteTaskReview( batchOperation.getId(), batchOperation.getBundle() );
			}
		}else  if( "PROJECT".equalsIgnoreCase( batchOperation.getObjType() )) {
			//重新计算项目涉及的任务所有的Review信息
			if( "PERMISSION".equalsIgnoreCase( batchOperation.getOptType()  )) {
				//分类修改权限
				refreshTaskReviewInProject( batchOperation.getId(), batchOperation.getBundle() );				
			}else if( "DELETE".equalsIgnoreCase( batchOperation.getOptType()  )) {
				//删除工作任务，并且修改分类所属的栏目中的分类数量和工作任务数量
				deleteTaskInProject( batchOperation.getId(), batchOperation.getBundle() );
			}
		}
		logger.info( "task batch operation process completed." );
		return "error";
	}

	/**
	 * 根据数据库中的工作任务的信息，重新计算工作任务的Review信息，全部删除，然后再重新插入Review记录
	 * @param id
	 * @param taskId
	 * @throws Exception 
	 */
	private void refreshTaskReview( String id, String taskId ) throws Exception {
		BatchOperation batchOperation = null;
		Task task = null;
		Project project = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			batchOperation = emc.find( id, BatchOperation.class );
			task = emc.find( taskId, Task.class );				
			if( task != null ) {
				project = emc.find( task.getProject(), Project.class );		
				
				emc.beginTransaction( Task.class );
				task.setReviewed( true );
				if( project != null ) {
					 task.setProjectName( project.getTitle() );
				}
				if( StringUtils.isEmpty( task.getParent() )) {
					task.setParent( "0" );
				}
				emc.check( task, CheckPersistType.all );
				emc.commit();
			}
			
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>reviewService.refreshTaskReview......");
			reviewService.refreshTaskReview( emc, taskId );
			
			if( batchOperation != null ) {
				//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>delete batch operation......");
				emc.beginTransaction( BatchOperation.class );
				emc.remove( batchOperation, CheckRemoveType.all );
				logger.info( "refreshTaskReview -> task delete batch operation: " + batchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( Review.class );
		ApplicationCache.notify( Task.class );
	}
	
	/**
	 * 删除指定工作任务的所有Review信息
	 * @param id
	 * @param taskId
	 * @throws Exception
	 */
	private void deleteTaskReview(String id, String taskId) throws Exception {
		BatchOperation batchOperation = null;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			batchOperation = emc.find( id, BatchOperation.class );
			logger.info( "deleteTaskReview -> delete all reviews for task: " + taskId );
			reviewService.deleteTaskReview( emc, taskId );
			if( batchOperation != null ) {
				emc.beginTransaction( BatchOperation.class );
				emc.remove( batchOperation, CheckRemoveType.all );
				logger.info( "deleteTaskReview -> task delete batch operation: " + batchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( Review.class );
		ApplicationCache.notify( Task.class );
	}
	
	/**
	 * 重新计算指定项目中所有工作任务的Review信息
	 * @param id  batch_id
	 * @param projectId
	 * @throws Exception
	 */
	private void refreshTaskReviewInProject( String id, String projectId ) throws Exception {
		BatchOperation batchOperation = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			batchOperation = emc.find( id, BatchOperation.class );
			//查询项目中所有的工作任务，重发为工作任务Review更新， 增加删除栏目批量操作（对分类和工作任务）的信息
			List<String> ids = business.taskFactory().listByProject( projectId );
			List<Task> taskList = null;
			Long count = business.taskFactory().countWithProject( projectId );
			logger.info( "refreshTaskReviewInProject -> There are : " + count + " tasks need to refresh review......" );
			
			if(ListTools.isNotEmpty( ids )) {
				taskList = emc.list( Task.class, ids);
			}
			if( ListTools.isNotEmpty( taskList )) {
				Integer total = taskList.size();
				Integer current = 0;
				BatchOperationPersistService batchOperationPersistService = new BatchOperationPersistService();
				for( Task task : taskList ) {
					current++;
					emc.beginTransaction(Task.class );
					task.setReviewed( false );
					emc.check( task, CheckPersistType.all );
					emc.commit();

					logger.info( "refreshTaskReviewInProject -> Send ["+ current +"/"+total+"]task permission operation to queue[queueBatchOperation], task:" + task.getName()  );
					batchOperationPersistService.addOperation( 
							BatchOperationProcessService.OPT_OBJ_TASK, 
							BatchOperationProcessService.OPT_TYPE_PERMISSION, task.getId(), task.getId(), "项目变更引起工作任务Review变更：ID=" + task.getId() );
				}
			}
			if( batchOperation != null ) {
				emc.beginTransaction( BatchOperation.class );
				emc.remove( batchOperation, CheckRemoveType.all );
				logger.info( "refreshTaskReviewInProject -> task delete batch operation: " + batchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 将项目下所有的工作任务及Review删除，最后删除当前的批处理信息
	 * @param id
	 * @param projectId
	 * @throws Exception 
	 */
	private void deleteTaskInProject( String id, String projectId ) throws Exception {
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		List<String> taskIds = null;
		BatchOperation batchOperation = null;
		Task task = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			batchOperation = emc.find( id, BatchOperation.class );
			
			taskIds = business.taskFactory().listByProject(projectId);
			
			if( ListTools.isNotEmpty( taskIds )) {
				for( String taskId : taskIds ) {
					task = emc.find( taskId, Task.class );
					if( task != null ) {
						taskService.remove( emc, taskId );
						new BatchOperationPersistService().addOperation(
								BatchOperationProcessService.OPT_OBJ_TASK, 
								BatchOperationProcessService.OPT_TYPE_DELETE, id, id, "项目删除引起工作任务删除：ID=" + id );
						logger.info( "deleteTaskInApp -> task processing batch operation: remove task("+ currenteWhileCount +"/" + totalWhileCount + "): " + taskId );
					}
				}
			}
			if( batchOperation != null ) {
				emc.beginTransaction( BatchOperation.class );
				emc.remove( batchOperation, CheckRemoveType.all );
				logger.info( "deleteTaskInApp -> task delete batch operation: " + batchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( Task.class );
	}
}
