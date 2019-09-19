package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskView;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Boolean check = true;
		Wo wo = new Wo();

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				task = taskQueryService.get(flag);
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException(flag);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskPersistService.delete(flag, effectivePerson );
				//taskGroupPersistService.refreshTaskCountInTaskGroupWithTaskId( effectivePerson.getDistinguishedName(), flag );
				
				// 更新缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( TaskView.class );
				ApplicationCache.notify( TaskGroup.class );	
				ApplicationCache.notify( TaskList.class );
				
				wo.setId( task.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag删除工作任务信息对象时发生异常。flag:" + flag);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {					
				new BatchOperationPersistService().addOperation( 
						BatchOperationProcessService.OPT_OBJ_TASK, 
						BatchOperationProcessService.OPT_TYPE_DELETE,  flag,  flag, "删除文档：ID=" +   flag );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		
		if (check) {
			if( StringUtils.isNotEmpty( task.getParent() )) {
				Task parentTask = taskQueryService.get( task.getParent() );
				if( parentTask != null ) {
					try {					
						dynamicPersistService.subTaskDeleteDynamic( parentTask, task, effectivePerson );
					} catch (Exception e) {
						logger.error(e, effectivePerson, request, null);
					}	
				}
			}
		}
		
		if (check) {
			try {
				MessageFactory.message_to_teamWorkDelete(task);
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {					
				Dynamic dynamic = dynamicPersistService.taskDeleteDynamic( task, effectivePerson );
				if( dynamic != null ) {
					List<WoDynamic> dynamics = new ArrayList<>();
					dynamics.add( WoDynamic.copier.copy( dynamic ) );
					if( wo != null ) {
						wo.setDynamics(dynamics);
					}
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		result.setData( wo );
		return result;
	}
	
	public static class Wo extends WoId {
		
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
		
	}
	
	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}		
	}
}