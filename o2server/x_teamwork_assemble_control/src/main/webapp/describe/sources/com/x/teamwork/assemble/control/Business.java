package com.x.teamwork.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.teamwork.assemble.control.factory.AttachmentFactory;
import com.x.teamwork.assemble.control.factory.BatchOperationFactory;
import com.x.teamwork.assemble.control.factory.ChatFactory;
import com.x.teamwork.assemble.control.factory.DynamicFactory;
import com.x.teamwork.assemble.control.factory.ProjectExtFieldReleFactory;
import com.x.teamwork.assemble.control.factory.ProjectFactory;
import com.x.teamwork.assemble.control.factory.ProjectGroupFactory;
import com.x.teamwork.assemble.control.factory.ProjectGroupReleFactory;
import com.x.teamwork.assemble.control.factory.ReviewFactory;
import com.x.teamwork.assemble.control.factory.SystemConfigFactory;
import com.x.teamwork.assemble.control.factory.TaskFactory;
import com.x.teamwork.assemble.control.factory.TaskGroupFactory;
import com.x.teamwork.assemble.control.factory.TaskGroupReleFactory;
import com.x.teamwork.assemble.control.factory.TaskListFactory;
import com.x.teamwork.assemble.control.factory.TaskTagFactory;
import com.x.teamwork.assemble.control.factory.TaskViewFactory;

public class Business {

	private EntityManagerContainer emc;
	
	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}
	
	private Organization organization;
	private ProjectFactory projectFactory;
	private TaskFactory taskFactory;
	private DynamicFactory dynamicFactory;
	private SystemConfigFactory systemConfigFactory;
	private ProjectGroupFactory projectGroupFactory;
	private ProjectGroupReleFactory projectGroupReleFactory;
	private TaskListFactory taskListFactory;
	private ChatFactory chatFactory;
	private TaskGroupFactory taskGroupFactory;
	private TaskGroupReleFactory taskGroupReleFactory;
	private TaskViewFactory taskViewFactory;
	private ProjectExtFieldReleFactory projectExtFieldReleFactory;
	private ReviewFactory reviewFactory;
	private BatchOperationFactory batchOperationFactory;
	private TaskTagFactory taskTagFactory;	
	private AttachmentFactory attachmentFactory;	
	
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	/**
	 * 获取附件信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public AttachmentFactory attachmentFactory() throws Exception {
		if (null == this.attachmentFactory) {
			this.attachmentFactory = new AttachmentFactory( this );
		}
		return attachmentFactory;
	}
	
	/**
	 * 获取工作任务标签数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskTagFactory taskTagFactory() throws Exception {
		if (null == this.taskTagFactory) {
			this.taskTagFactory = new TaskTagFactory( this );
		}
		return taskTagFactory;
	}
	
	/**
	 * 获取批处理任务数据库访问类
	 * @return
	 * @throws Exception
	 */
	public BatchOperationFactory batchOperationFactory() throws Exception {
		if (null == this.batchOperationFactory) {
			this.batchOperationFactory = new BatchOperationFactory( this );
		}
		return batchOperationFactory;
	}
	
	/**
	 * 获取Review信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ReviewFactory reviewFactory() throws Exception {
		if (null == this.reviewFactory) {
			this.reviewFactory = new ReviewFactory( this );
		}
		return reviewFactory;
	}
	
	/**
	 * 获取项目扩展属性关联信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ProjectExtFieldReleFactory projectExtFieldReleFactory() throws Exception {
		if (null == this.projectExtFieldReleFactory) {
			this.projectExtFieldReleFactory = new ProjectExtFieldReleFactory( this );
		}
		return projectExtFieldReleFactory;
	}
	
	/**
	 * 获取任务视图数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskViewFactory taskViewFactory() throws Exception {
		if (null == this.taskViewFactory) {
			this.taskViewFactory = new TaskViewFactory( this );
		}
		return taskViewFactory;
	}
	
	/**
	 * 获取工作任务组关联信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskGroupReleFactory taskGroupReleFactory() throws Exception {
		if (null == this.taskGroupReleFactory) {
			this.taskGroupReleFactory = new TaskGroupReleFactory( this );
		}
		return taskGroupReleFactory;
	}
	
	/**
	 * 获取工作任务组数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskGroupFactory taskGroupFactory() throws Exception {
		if (null == this.taskGroupFactory) {
			this.taskGroupFactory = new TaskGroupFactory( this );
		}
		return taskGroupFactory;
	}
	
	/**
	 * 获取工作交流数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ChatFactory chatFactory() throws Exception {
		if (null == this.chatFactory) {
			this.chatFactory = new ChatFactory( this );
		}
		return chatFactory;
	}
	
	/**
	 * 获取工作任务列表数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskListFactory taskListFactory() throws Exception {
		if (null == this.taskListFactory) {
			this.taskListFactory = new TaskListFactory( this );
		}
		return taskListFactory;
	}
	
	/**
	 * 获取项目组数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ProjectGroupFactory projectGroupFactory() throws Exception {
		if (null == this.projectGroupFactory) {
			this.projectGroupFactory = new ProjectGroupFactory( this );
		}
		return projectGroupFactory;
	}
	
	/**
	 * 获取项目与项目组关联信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ProjectGroupReleFactory projectGroupReleFactory() throws Exception {
		if (null == this.projectGroupReleFactory) {
			this.projectGroupReleFactory = new ProjectGroupReleFactory( this );
		}
		return projectGroupReleFactory;
	}
	
	/**
	 * 获取系统配置信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public SystemConfigFactory systemConfigFactory() throws Exception {
		if (null == this.systemConfigFactory) {
			this.systemConfigFactory = new SystemConfigFactory( this );
		}
		return systemConfigFactory;
	}
	
	/**
	 * 获取工作任务信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskFactory taskFactory() throws Exception {
		if (null == this.taskFactory) {
			this.taskFactory = new TaskFactory( this );
		}
		return taskFactory;
	}
	
	/**
	 * 获取项目信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ProjectFactory projectFactory() throws Exception {
		if (null == this.projectFactory) {
			this.projectFactory = new ProjectFactory( this );
		}
		return projectFactory;
	}
	
	/**
	 * 获取动态信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public DynamicFactory dynamicFactory() throws Exception {
		if (null == this.dynamicFactory) {
			this.dynamicFactory = new DynamicFactory( this );
		}
		return dynamicFactory;
	}
}
