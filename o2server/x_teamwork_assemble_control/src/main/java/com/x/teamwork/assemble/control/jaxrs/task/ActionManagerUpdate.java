package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.Task;

public class ActionManagerUpdate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionManagerUpdate.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,  String id,  JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Wi wi = null;
		Boolean check = true;
		List<String> old_managers = null;
		List<String> new_managers = null;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}
		
		if (check) {	
			task = taskQueryService.get( id );
			if( task == null ) {
				check = false;
				Exception exception = new TaskNotExistsException( id );
				result.error(exception);
			}
		}
		
		if (check) {
			old_managers = task.getManageablePersonList();
			new_managers = wi.getManagers();
			if( ListTools.isNotEmpty( wi.getManagers() ) ) {
				try {					
					task = taskPersistService.addManager( id, wi.getManagers(), effectivePerson );
					
					// 更新缓存
					ApplicationCache.notify( Task.class );					
					Wo wo = new Wo();
					wo.setId( task.getId() );			
					result.setData( wo );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskPersistException(e, "添加工作任务管理者时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			List<String> addManagers = new ArrayList<>();
			List<String> removeManagers = new ArrayList<>();
			if( old_managers == null ) { old_managers = new ArrayList<>(); }
			if( new_managers == null ) { old_managers = new ArrayList<>(); }			
			for( String manager : old_managers ) {
				if( !new_managers.contains( manager )) {
					removeManagers.add( manager );
				}
			}
			for( String manager : new_managers ) {
				if( !old_managers.contains( manager )) {
					addManagers.add( manager );
				}
			}
			
			if (check) {
				try {					
					new BatchOperationPersistService().addOperation( 
							BatchOperationProcessService.OPT_OBJ_TASK, 
							BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "变更管理员，刷新文档权限：ID=" +   task.getId() );
				} catch (Exception e) {
					logger.error(e, effectivePerson, request, null);
				}	
			}
			
			try {//记录工作任务信息变化记录
				dynamicPersistService.taskManagerUpdateDynamic( task, addManagers, removeManagers, effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("管理者标识列表")
		private List<String> managers;

		public List<String> getManagers() {
			return managers;
		}

		public void setManagers(List<String> managers) {
			this.managers = managers;
		}		
	}

	public static class Wo extends WoId {
	}
	
}