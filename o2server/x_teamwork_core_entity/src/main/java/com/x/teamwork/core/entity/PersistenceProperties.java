package com.x.teamwork.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Attachment {
		public static final String table = "TEW_ATTACHMENT";
	}
	
	public static class Project {
		public static final String table = "TEW_PROJECT";
	}

	public static class ProjectDetail {
		public static final String table = "TEW_PROJECTDETAIL";
	}
	
	public static class TaskExtField {
		public static final String table = "TEW_TASKEXTFIELD";
	}

	public static class ProjectGroup {
		public static final String table = "TEW_PROJECTGROUP";
	}

	public static class Task {
		public static final String table = "TEW_TASK";
	}
	
	public static class Review {
		public static final String table = "TEW_REVIEW";
	}
	
	public static class TaskTag {
		public static final String table = "TEW_TAG";
	}

	public static class TaskDetail {
		public static final String table = "TEW_TASKDETAIL";
	}

	public static class TodoList {
		public static final String table = "TEW_TASKVIEW";
	}

	public static class TaskGroup {
		public static final String table = "TEW_TASKGROUP";
	}
	
	public static class TaskList {
		public static final String table = "TEW_TASKLIST";
	}
	
	public static class TaskRelevance {
		public static final String table = "TEW_TASKRELEVANCE";
	}	

	public static class Dynamic {
		public static final String table = "TEW_DYNAMIC";
	}

	public static class DynamicDetail {
		public static final String table = "TEW_DYNAMIC_DETAIL";
	}

	public static class Chat {
		public static final String table = "TEW_CHAT";
	}

	public static class ChatContent {
		public static final String table = "TEW_CHATCONTENT";
	}

	public static class SystemConfig {
		public static final String table = "TEW_SYSTEMCONFIG";
	}

	public static class SystemConfigLobValue {
		public static final String table = "TEW_SYSTEMCONFIG_LOB";
	}
	
	
	
	public static class BatchOperation {
		public static final String table = "TEW_BATCHOPERATION";
	}
	
	public static class ProjectGroupRele {
		public static final String table = "TEW_PROJECTGROUP_RELE";
	}
	
	public static class ProjectExtFieldRele {
		public static final String table = "TEW_PROJECTEXTFIELD_RELE";
	}
	
	public static class TaskGroupRele {
		public static final String table = "TEW_TASKGROUP_RELE";
	}

	public static class TaskListRele {
		public static final String table = "TEW_TASKLIST_RELE";
	}
	
	public static class TaskTagRele {
		public static final String table = "TEW_TASKTAG_RELE";
	}
}
