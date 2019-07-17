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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;

public class ActionTagUpdate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionTagUpdate.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,  String taskId,  JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Wi wi = null;
		Boolean check = true;
		List<String> old_tags = null;
		List<String> new_tags = null;
		List<String> addTags = new ArrayList<>();
		List<String> removeTags = new ArrayList<>();
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if ( StringUtils.isEmpty( taskId ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}
		
		if (check) {	
			task = taskQueryService.get( taskId );
			if( task == null ) {
				check = false;
				Exception exception = new TaskNotExistsException( taskId );
				result.error(exception);
			}
		}
		
		if (check) {
			//从数据库中取出关联信息
			old_tags = taskTagQueryService.listTagsWithTask( effectivePerson, taskId );
			new_tags = wi.getTags();
			if( old_tags == null ) { old_tags = new ArrayList<>(); }
			if( new_tags == null ) { old_tags = new ArrayList<>(); }
			for( String manager : old_tags ) {
				if( !new_tags.contains( manager )) {
					removeTags.add( manager );
				}
			}
			for( String manager : new_tags ) {
				if( !old_tags.contains( manager )) {
					addTags.add( manager );
				}
			}
		}
		
		if (check) {
			try {					
				new_tags = taskPersistService.updateTag( taskId, new_tags, addTags, removeTags, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( Review.class );	
				
				Wo wo = new Wo();
				wo.setTags( new_tags );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "添加工作任务管理者时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			
			
			if (check) {
				try {					
					new BatchOperationPersistService().addOperation( 
							BatchOperationProcessService.OPT_OBJ_TASK, 
							BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "变更标签，刷新文档REVIEW信息：ID=" +   task.getId() );
				} catch (Exception e) {
					logger.error(e, effectivePerson, request, null);
				}	
			}
			
			try {//记录工作任务信息变化记录
				dynamicPersistService.taskTagUpdateDynamic(task, addTags, removeTags, effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("工作所属标签列表")
		private List<String> tags;

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}
	}

	public static class Wo{
		
		@FieldDescribe("工作所属标签列表")
		private List<String> tags;

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}
	}
	
}