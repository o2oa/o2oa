package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectExtFieldReleQueryService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskGroupPersistService;
import com.x.teamwork.assemble.control.service.TaskGroupQueryService;
import com.x.teamwork.assemble.control.service.TaskListPersistService;
import com.x.teamwork.assemble.control.service.TaskPersistService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.assemble.control.service.TaskTagPersistService;
import com.x.teamwork.assemble.control.service.TaskTagQueryService;
import com.x.teamwork.assemble.control.service.TaskViewQueryService;
import com.x.teamwork.core.entity.Task;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Ehcache taskCache = ApplicationCache.instance().getCache( Task.class );
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
	
	protected 	TaskTagQueryService taskTagQueryService = new TaskTagQueryService();
	
	protected 	TaskTagPersistService taskTagPersistService = new TaskTagPersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	ProjectExtFieldReleQueryService projectExtFieldReleQueryService = new ProjectExtFieldReleQueryService();
	
	protected 	TaskPersistService taskPersistService = new TaskPersistService();
	
	protected TaskListPersistService taskListPersistService = new TaskListPersistService();
	
	protected TaskGroupQueryService taskGroupQueryService = new TaskGroupQueryService();
	
	protected TaskGroupPersistService taskGroupPersistService = new TaskGroupPersistService();
	
	protected TaskViewQueryService taskViewQueryService = new TaskViewQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	public static class TaskListChange {
		
		@FieldDescribe("转移前的列表ID")
		private String source;
		
		@FieldDescribe("转移后的列表ID")
		private String target;
		
		public TaskListChange() {}
		
		public TaskListChange( String _source, String _target ) {
			this.source = _source;
			this.target = _target;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}
	}
	
}
