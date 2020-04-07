package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 将指定的任务移动到其他的泳道里
 */
public class ActionMoveToList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionMoveToList.class);

	/**
	 * 将指定的任务移动到其他的泳道（TaskList）里
	 * 1、查询 参数是否合法
	 * 2、转移Task的泳道关联
	 * 3、调整TaskList和TaskGroup的相关统计数据
	 * 4、记录动态信息
	 *
	 * @param request
	 * @param effectivePerson
	 * @param sourceTaskId
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String sourceTaskId, String targetListId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Task sourceTask = null;
		TaskList targetTaskList = null;
		TaskDetail taskDetail = null;
		TaskExtField taskExtField = null;
		List<Dynamic> dynamics  = new ArrayList<>();
		Boolean check = true;

		if ( StringUtils.isEmpty( sourceTaskId ) ) {
			check = false;
			Exception exception = new TaskTransformException("需要复制的工作任务ID不允许为空！");
			result.error( exception );
		}

		if ( StringUtils.isEmpty( targetListId ) ) {
			check = false;
			Exception exception = new TaskTransformException("需要复制到的目标任务列表ID不允许为空！");
			result.error( exception );
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				sourceTask = taskQueryService.get( sourceTaskId );
				if ( sourceTask == null) {
					check = false;
					Exception exception = new TaskNotExistsException(sourceTaskId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。ID:" + sourceTaskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				targetTaskList = taskListQueryService.get( targetListId );
				if ( targetTaskList == null) {
					check = false;
					Exception exception = new TaskListNotExistsException(targetListId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException( e, "根据指定ID查询工作任务列表信息对象时发生异常。ID:" + targetListId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		Task newTask = new Task();
		TaskDetail newTaskDetail = new TaskDetail();
		TaskExtField newTaskExtField = new TaskExtField();
		if( Boolean.TRUE.equals( check ) ){
			//COPY对象
			sourceTask.copyTo( newTask );
			taskDetail.copyTo( newTaskDetail );
			taskExtField.copyTo( newTaskExtField );

			//重新命名
			newTask.setName( sourceTask.getName() + " - 副本");

			//调整ID
			newTask.setId( Task.createId() );
			newTaskDetail.setId( newTask.getId() );
			newTaskExtField.setId( newTask.getId() );

			try {
				newTask = taskPersistService.save( newTask, newTaskDetail, newTaskExtField, effectivePerson );
				wo.setId( newTask.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "工作上级任务ID信息更新时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				dynamics = dynamicPersistService.taskCopyDynamic( sourceTask, newTask, effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				new BatchOperationPersistService().addOperation(
						BatchOperationProcessService.OPT_OBJ_TASK,
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  newTask.getId(),  newTask.getId(), "刷新文档权限：ID=" +   sourceTask.getId() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( ListTools.isNotEmpty( dynamics ) ) {
			wo.setDynamics( WoDynamic.copier.copy( dynamics ) );
		}

		// 更新缓存
		ApplicationCache.notify( Task.class );
		ApplicationCache.notify( TaskList.class );
		ApplicationCache.notify( TaskView.class );
		ApplicationCache.notify( Review.class );
		ApplicationCache.notify( TaskGroup.class );
		ApplicationCache.notify( Dynamic.class );

		result.setData( wo );
		return result;
	}


	public static class Wo extends WoId {

		@FieldDescribe("操作引起的动态内容")
		List<ActionSave.WoDynamic> dynamics = new ArrayList<>();

		public List<ActionSave.WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<ActionSave.WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}

	}

	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, ActionSave.WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, ActionSave.WoDynamic.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}