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
 * 将任务转为子任务
 */
public class ActionTransformAsSubTask extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTransformAsSubTask.class);

	/**
	 * 将任务转换为子任务的服务
	 * 1、确认ID是否合法
	 * 2、将任务的parentId设置为parentId
	 * 3、保存任务信息
	 * 4、记录动态信息
	 *
	 * @param request
	 * @param effectivePerson
	 * @param sourceTaskId
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String sourceTaskId, String parentId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Task sourceTask = null;
		Task parentTask = null;
		List<Dynamic> dynamics  = new ArrayList<>();
		Boolean check = true;

		if ( StringUtils.isEmpty( sourceTaskId ) ) {
			check = false;
			Exception exception = new TaskTransformException("需要转换的工作任务ID不允许为空！");
			result.error( exception );
		}

		if ( StringUtils.isEmpty( parentId ) ) {
			check = false;
			Exception exception = new TaskTransformException("上级任务ID不允许为空！");
			result.error( exception );
		}

		if( Boolean.TRUE.equals( check ) ){
			//查询需要转换为子任务的任务是否存在
			try {
				sourceTask = taskQueryService.get( sourceTaskId );
				if ( sourceTask == null) {
					check = false;
					Exception exception = new TaskNotExistsException(sourceTaskId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。sourceTaskId:" + sourceTaskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			//查询上级任务是否存在
			try {
				parentTask = taskQueryService.get( parentId );
				if ( parentTask == null) {
					check = false;
					Exception exception = new TaskNotExistsException( parentId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。parentId:" + parentId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		try {
			taskPersistService.updateParentId( sourceTask.getId(), parentTask.getId(), effectivePerson );
			wo.setId( sourceTask.getId() );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskPersistException(e, "工作上级任务ID信息更新时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}


		if( Boolean.TRUE.equals( check ) ){
			//记录工作任务信息变化记录
			try {
				dynamics = dynamicPersistService.subTaskTransformDynamic( sourceTask, parentTask, effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				new BatchOperationPersistService().addOperation(
						BatchOperationProcessService.OPT_OBJ_TASK,
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  sourceTask.getId(),  sourceTask.getId(), "刷新文档权限：ID=" +   sourceTask.getId() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				new BatchOperationPersistService().addOperation(
						BatchOperationProcessService.OPT_OBJ_TASK,
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  parentTask.getId(),  parentTask.getId(), "刷新文档权限：ID=" +   parentTask.getId() );
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