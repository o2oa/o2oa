package com.x.teamwork.assemble.control.jaxrs.task;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.UserManagerService;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskView;

public class ActionArchive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionArchive.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Task task = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( taskId ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				task = taskQueryService.get( taskId );
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException( taskId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。id:" + taskId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			//工作创建者、工作负责人、管理者和系统管理员有归档权限
			if( !hasArchivePermission( task, effectivePerson )) {
				check = false;
				Exception exception = new TaskPersistException( "用户没有工作任务的归档权限，请联系管理员和工作负责人进行工作归档操作！" );
				result.error(exception);
			}
		}
		
		if (check) {
			try {
				taskPersistService.archiveTask( taskId );
				// 更新缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( TaskView.class );
				ApplicationCache.notify( TaskGroup.class );	
				ApplicationCache.notify( TaskList.class );
				
				wo = new Wo();
				wo.setId( taskId );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "系统在根据指定ID归档工作任务时发生异常。id:" + taskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	/**
	 * 判断用户是否有指定工作的归档权限
	 * @param task
	 * @param effectivePerson
	 * @return
	 * @throws Exception 
	 */
	private boolean hasArchivePermission(Task task, EffectivePerson effectivePerson) throws Exception {
		if( effectivePerson.isManager()) {
			return true;
		}
		if( effectivePerson.getDistinguishedName().equalsIgnoreCase( task.getCreatorPerson() )) {
			return true;
		}
		if( effectivePerson.getDistinguishedName().equalsIgnoreCase( task.getExecutor() )) {
			return true;
		}
		if( ListTools.isNotEmpty(task.getManageablePersonList()) && task.getManageablePersonList().contains(effectivePerson.getDistinguishedName()) ) {
			return true;
		}
		if( new UserManagerService().isHasPlatformRole( effectivePerson.getDistinguishedName(), "TeamWorkManager") ) {
			return true;
		}
		//如果还查不到，那么判断一下用户是否是项目的管理员，创建者和负责人
		Project project = projectQueryService.get( task.getProject()); 
		if( project != null ) {
			if( effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getCreatorPerson() )) {
				return true;
			}
			if( effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getExecutor() )) {
				return true;
			}
			if( ListTools.isNotEmpty( project.getManageablePersonList()) && project.getManageablePersonList().contains(effectivePerson.getDistinguishedName()) ) {
				return true;
			}
		}
		return false;
	}

	public static class Wo extends WoId {
	}
}