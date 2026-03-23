package com.x.teamwork.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Attachment {
		public static final String table = "TEW2_ATTACHMENT";
	}

	public static class Project {
		public static final String table = "TEW2_PROJECT";
	}

	public static class ProjectPermission {
		public static final String table = "TEW2_PROJECT_PERMISSION";
	}

	public static class ProjectDetail {
		public static final String table = "TEW2_PROJECTDETAIL";
	}

	public static class ProjectTemplate {
		public static final String table = "TEW2_PROJECTTEMPLATE";
	}

	public static class TaskExtField {
		public static final String table = "TEW2_TASKEXTFIELD";
	}

	public static class ProjectGroup {
		public static final String table = "TEW2_PROJECTGROUP";
	}

	public static class Task {
		public static final String table = "TEW2_TASK";
	}

	public static class Review {
		public static final String table = "TEW2_REVIEW";
	}

	public static class TaskTag {
		public static final String table = "TEW2_TAG";
	}

	public static class Priority {
		public static final String table = "TEW2_PIORITY";
	}

	public static class ProjectConfig {
		public static final String table = "TEW2_PROJECTCONFIG";
	}

	public static class Config {
		public static final String table = "TEW2_CONFIG";
	}

	public static class TaskDetail {
		public static final String table = "TEW2_TASKDETAIL";
	}

	public static class TodoList {
		public static final String table = "TEW2_TASKVIEW";
	}

	public static class TaskGroup {
		public static final String table = "TEW2_TASKGROUP";
	}

	public static class TaskList {
		public static final String table = "TEW2_TASKLIST";
	}

	public static class TaskListTemplate {
		public static final String table = "TEW2_TASKLISTTEMPLATE";
	}

	public static class TaskRelevance {
		public static final String table = "TEW2_TASKRELEVANCE";
	}

	public static class Dynamic {
		public static final String table = "TEW2_DYNAMIC";
	}

	public static class DynamicDetail {
		public static final String table = "TEW2_DYNAMIC_DETAIL";
	}

	public static class Chat {
		public static final String table = "TEW2_CHAT";
	}

	public static class ChatContent {
		public static final String table = "TEW2_CHATCONTENT";
	}

	public static class SystemConfig {
		public static final String table = "TEW2_SYSTEMCONFIG";
	}

	public static class SystemConfigLobValue {
		public static final String table = "TEW2_SYSTEMCONFIG_LOB";
	}



	public static class BatchOperation {
		public static final String table = "TEW2_BATCHOPERATION";
	}

	public static class ProjectGroupRele {
		public static final String table = "TEW2_PROJECTGROUP_RELE";
	}

	public static class CustomExtFieldRele {
		public static final String table = "TEW2_CUSTOMEXTFIELD_RELE";
	}

	public static class TaskGroupRele {
		public static final String table = "TEW2_TASKGROUP_RELE";
	}

	public static class TaskListRele {
		public static final String table = "TEW2_TASKLIST_RELE";
	}

	public static class TaskTagRele {
		public static final String table = "TEW2_TASKTAG_RELE";
	}
}
