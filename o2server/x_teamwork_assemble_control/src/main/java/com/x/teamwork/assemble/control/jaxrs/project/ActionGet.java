package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * 获取项目详细信息
 * @author sword
 */
public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectDetail projectDetail = null;
		List<String> groupIds = null;
		List<ProjectGroup> groups = null;
		List<ProjectConfig>  projectConfigs = null;
		WrapOutControl control = null;

		Integer taskTotal = 0;
		Integer progressTotal = 0;
		Integer completedTotal = 0;
		Integer overtimeTotal = 0;

		Project project = projectQueryService.get(flag);
		if(project == null){
			throw new ProjectNotExistsException(flag);
		}
		if(!this.isReader(project.getId(), effectivePerson)){
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}

		List<Task> taskList = projectQueryService.listAllTasks(flag , true);
		if( ListTools.isNotEmpty( taskList )) {
			for( Task task : taskList ) {
				taskTotal ++;
				if( "completed".equalsIgnoreCase(task.getWorkStatus()) ) {
					completedTotal++;
				}
				if( "processing".equalsIgnoreCase(task.getWorkStatus()) ) {
					progressTotal++;
				}
				if(task.getOvertime() ) {
					overtimeTotal++;
				}
			}
		}

		Wo wo = Wo.copier.copy( project );
		if( wo.getStarPersonList().contains( effectivePerson.getDistinguishedName() )) {
			wo.setStar( true );
		}

		//查询项目配置信息
		projectConfigs = projectConfigQueryService.getProjectConfigByProject( project.getId() );

		control = new WrapOutControl();
		if(ListTools.isNotEmpty(projectConfigs)){
			ProjectConfig projectConfig = projectConfigs.get(0);
			control.setTaskCreate(projectConfig.getTaskCreate());
			control.setTaskCopy(projectConfig.getTaskCopy());
			control.setTaskRemove(projectConfig.getTaskRemove());
			control.setLaneCreate(projectConfig.getLaneCreate());
			control.setLaneEdit(projectConfig.getLaneEdit());
			control.setLaneRemove(projectConfig.getLaneRemove());
			control.setAttachmentUpload(projectConfig.getAttachmentUpload());
			control.setComment(projectConfig.getComment());
		}else{
			control.setTaskCreate(true);
		}

		if(this.canEdit(project, effectivePerson)) {
			control.setDelete( true );
			control.setEdit( true );
			control.setSortable( true );
			control.setTaskCreate(true);
		}
		if(ProjectStatusEnum.isEndStatus(project.getWorkStatus())){
			control.setTaskCreate(false);
		}
		control.setFounder(control.getEdit() ? true : this.isManager(project.getId(), effectivePerson));
		control.setSysAdmin(this.isSysManager(effectivePerson));
		wo.setControl(control);

		wo.setProgressTotal(progressTotal);
		wo.setCompletedTotal(completedTotal);
		wo.setOvertimeTotal(overtimeTotal);
		wo.setTaskTotal(taskTotal);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapOutProject {

		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe("所有任务数量")
		private Integer taskTotal = 0;

		@FieldDescribe("执行中任务数量")
		private Integer progressTotal = 0;

		@FieldDescribe("已完成任务数量")
		private Integer completedTotal = 0;

		@FieldDescribe("超时任务数量")
		private Integer overtimeTotal = 0;

		public Integer getTaskTotal() {
			return taskTotal;
		}

		public void setTaskTotal(Integer taskTotal) {
			this.taskTotal = taskTotal;
		}

		public Integer getProgressTotal() {
			return progressTotal;
		}

		public void setProgressTotal(Integer progressTotal) {
			this.progressTotal = progressTotal;
		}

		public Integer getCompletedTotal() {
			return completedTotal;
		}

		public void setCompletedTotal( Integer completedTotal ) {
			this.completedTotal = completedTotal;
		}

		public Integer getOvertimeTotal() {
			return overtimeTotal;
		}

		public void setOvertimeTotal( Integer overtimeTotal ) {
			this.overtimeTotal = overtimeTotal;
		}


		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo( Project.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}
