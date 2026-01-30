package com.x.teamwork.assemble.control;

import java.util.List;

import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.factory.*;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;
	private ProjectFactory projectFactory;
	private TaskFactory taskFactory;
	private ProjectTemplateFactory projectTemplateFactory;
	private TaskListTemplateFactory taskListTemplateFactory;
	private DynamicFactory dynamicFactory;
	private SystemConfigFactory systemConfigFactory;
	private ProjectGroupFactory projectGroupFactory;
	private ProjectGroupReleFactory projectGroupReleFactory;
	private TaskListFactory taskListFactory;
	private ChatFactory chatFactory;
	private TaskGroupFactory taskGroupFactory;
	private TaskGroupReleFactory taskGroupReleFactory;
	private TaskViewFactory taskViewFactory;
	private CustomExtFieldReleFactory customExtFieldReleFactory;
	private ReviewFactory reviewFactory;
	private BatchOperationFactory batchOperationFactory;
	private TaskTagFactory taskTagFactory;
	private AttachmentFactory attachmentFactory;
	private PriorityFactory priorityFactory;
	private ProjectConfigFactory projectConfigFactory;
	private PermissionFactory permissionFactory;

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
	public CustomExtFieldReleFactory customExtFieldReleFactory() throws Exception {
		if (null == this.customExtFieldReleFactory) {
			this.customExtFieldReleFactory = new CustomExtFieldReleFactory( this );
		}
		return customExtFieldReleFactory;
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
	 * 获取优先级数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ProjectConfigFactory projectConfigFactory() throws Exception {
		if (null == this.projectConfigFactory) {
			this.projectConfigFactory = new ProjectConfigFactory( this );
		}
		return projectConfigFactory;
	}

	/**
	 * 获取优先级数据库访问类
	 * @return
	 * @throws Exception
	 */
	public PriorityFactory priorityFactory() throws Exception {
		if (null == this.priorityFactory) {
			this.priorityFactory = new PriorityFactory( this );
		}
		return priorityFactory;
	}

	/**
	 * 获取项目模板数据库访问类
	 * @return
	 * @throws Exception
	 */
	public ProjectTemplateFactory projectTemplateFactory() throws Exception {
		if (null == this.projectTemplateFactory) {
			this.projectTemplateFactory = new ProjectTemplateFactory( this );
		}
		return projectTemplateFactory;
	}

	/**
	 * 获取项目模板对应的泳道数据库访问类
	 * @return
	 * @throws Exception
	 */
	public TaskListTemplateFactory taskListTemplateFactory() throws Exception {
		if (null == this.taskListTemplateFactory) {
			this.taskListTemplateFactory = new TaskListTemplateFactory( this );
		}
		return taskListTemplateFactory;
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

	/**
	 * 获取项目权限信息数据库访问类
	 * @return
	 * @throws Exception
	 */
	public PermissionFactory permissionFactory() throws Exception {
		if (null == this.permissionFactory) {
			this.permissionFactory = new PermissionFactory( this );
		}
		return permissionFactory;
	}

	/**
	 * TODO 判断用户是否管理员权限 1、person.isManager() 2、xadmin 3、CRMManager
	 *
	 * @param person
	 * @return
	 * @throws Exception
	 */

	public boolean isManager(EffectivePerson person) throws Exception {
		// 如果用户的身份是平台的超级管理员，那么就是超级管理员权限
		if (person.isManager()) {
			return true;
		}

		if (organization().person().hasRole(person, OrganizationDefinition.Manager,
				OrganizationDefinition.TeamWorkManager)) {
			return true;
		}
		return false;
	}

	public boolean isHasPlatformRole(String personName, String roleName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is null!");
		}
		if (StringUtils.isEmpty(roleName)) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		roleList = organization().role().listWithPerson(personName);
		if (ListTools.isNotEmpty(roleList) ) {
			if (roleList.stream().filter(r -> roleName.equalsIgnoreCase(r)).count() > 0) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}
}
