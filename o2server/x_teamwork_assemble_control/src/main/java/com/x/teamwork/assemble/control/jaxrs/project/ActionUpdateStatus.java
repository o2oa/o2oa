package com.x.teamwork.assemble.control.jaxrs.project;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author sword
 */
public class ActionUpdateStatus extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionUpdateStatus.class);

    protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectId, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setId(projectId);
        result.setData(wo);
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getWorkStatus()) || ProjectStatusEnum.getByValue(wi.workStatus) == null){
			throw new ProjectUpdateStatusException("错误的状态：" + wi.getWorkStatus());
		}
		Project oldProject = projectQueryService.get(projectId);
		if(oldProject == null){
			throw new ProjectNotExistsException(projectId);
		}
		if(ProjectStatusEnum.ARCHIVED.getValue().equals(oldProject.getWorkStatus()) && !this.isSysManager(effectivePerson)){
			throw new ProjectUpdateStatusException("该项目已归档");
		}
		if(wi.getWorkStatus().equals(oldProject.getWorkStatus())){
			return result;
		}
		if(ProjectStatusEnum.COMPLETED.getValue().equals(wi.getWorkStatus())){
			List<Task> taskList = this.taskQueryService.allUnCompletedTasks(projectId);
			for (Task task : taskList){
				this.changeTaskStatus(effectivePerson, task, wi.getWorkStatus());
			}
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			emc.beginTransaction(Project.class);
			Project project = emc.find(projectId, Project.class);
			project.setWorkStatus(wi.getWorkStatus());
			if(ProjectStatusEnum.COMPLETED.getValue().equals(wi.getWorkStatus())){
				project.setCompletedTime(new Date());
			}
			Boolean archive = null;
			if(ProjectStatusEnum.ARCHIVED.getValue().equals(wi.getWorkStatus())){
				archive = true;
			}else if(ProjectStatusEnum.ARCHIVED.getValue().equals(oldProject.getWorkStatus())){
				archive = false;
			}
			if(archive != null){
				emc.beginTransaction(Task.class);
				List<Task> taskList = business.taskFactory().listObjectByProject(projectId);
				for (Task task : taskList){
					task.setArchive(archive);
				}
			}
			emc.commit();
		}

		dynamicPersistService.projectUpdateStatusDynamic(oldProject, effectivePerson, wi.getWorkStatus());
		CacheManager.notify(Project.class);
		CacheManager.notify(Task.class);
        return result;
    }

	private void changeTaskStatus(EffectivePerson effectivePerson, Task oldTask, String mainValue) throws Exception {
		String property = Task.workStatus_FIELDNAME;
		this.taskPersistService.changeTaskProperty( oldTask.getId(), property, mainValue, "" );
		mainValue = ProjectStatusEnum.getNameByValue(mainValue);

		String dynamicDescription =  effectivePerson.getName() + "将工作任务的[状态]变更为：[" + mainValue + "]。";

		Task newTask = taskQueryService.get( oldTask.getId() );

		String dynamicTitle = "工作任务的状态";
		this.dynamicPersistService.taskUpdatePropertyDynamic( oldTask,  dynamicTitle,
				"UPDATE_WORKSTATUS", dynamicDescription, effectivePerson );

		this.sendMsg(oldTask, newTask);

	}

	private void sendMsg(Task oldTask, Task newTask){
		try {
			MessageFactory.message_to_teamWorkUpdate( oldTask, newTask );
		} catch (Exception e) {
			logger.error(e);
		}
	}

    public static class Wi {

        @FieldDescribe("状态")
        private String workStatus;

		public String getWorkStatus() {
			return workStatus;
		}

		public void setWorkStatus(String workStatus) {
			this.workStatus = workStatus;
		}
	}

    public static class Wo extends WoId {
    }

}
