package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectConfig;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.Task;


public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Project project = null;
		ProjectDetail projectDetail = null;
		List<String> groupIds = null;
		List<ProjectGroup> groups = null;
		List<Task>  taskList = null;
		List<ProjectConfig>  projectConfigs = null;
		WrapOutControl control = null;
		Boolean check = true;
		
		Integer taskTotal = 0;
		Integer progressTotal = 0;
		Integer completedTotal = 0;
		Integer overtimeTotal = 0;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new ProjectFlagForQueryEmptyException();
			result.error( exception );
		}

		/*String cacheKey = ApplicationCache.concreteCacheKey( flag,effectivePerson );
		Element element = projectCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {*/
			if( Boolean.TRUE.equals( check ) ){
				try {
					project = projectQueryService.get(flag);
					if ( project == null) {
						check = false;
						Exception exception = new ProjectNotExistsException(flag);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectQueryException(e, "根据指定flag查询应用项目信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){				
				taskList = projectQueryService.listAllTasks(flag , true);
				if( ListTools.isNotEmpty( taskList )) {
					for( Task task : taskList ) {
						taskTotal ++;
						if( "completed".equalsIgnoreCase(task.getWorkStatus()) ) {
							completedTotal++;
						}
						if( "processing".equalsIgnoreCase(task.getWorkStatus()) ) {
							progressTotal++;
						}
						if( task.getOvertime() ) {
							overtimeTotal++;
						}
					}
				}
				
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					wo = Wo.copier.copy( project );
					if( wo.getStarPersonList().contains( effectivePerson.getDistinguishedName() )) {
						wo.setStar( true );
					}					
					//查询项目详情
					projectDetail = projectQueryService.getDetail( project.getId() );
					if( projectDetail != null ) {
						wo.setDescription( projectDetail.getDescription() );
					}
					
					//查询项目组信息
					groupIds = projectGroupQueryService.listGroupIdByProject( project.getId() );
					groups = projectGroupQueryService.list( groupIds );
					wo.setGroups( groups );	
					
					//查询项目配置信息
					projectConfigs = projectConfigQueryService.getProjectConfigByProject( project.getId() );
					
					Business business = null;
					try (EntityManagerContainer bc = EntityManagerContainerFactory.instance().create()) {
						business = new Business(bc);
					}
					
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
					
					if( business.isManager(effectivePerson)
							|| effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getCreatorPerson() )
							|| project.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )) {
						control.setDelete( true );
						control.setEdit( true );
						control.setSortable( true );
						control.setTaskCreate(true);
						
					}else{
						control.setDelete( false );
						control.setEdit( false );
						control.setSortable( false );
					}
					if(project.getDeleted() || project.getCompleted()){
						control.setTaskCreate(false);
					}
					if(effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getCreatorPerson())){
						control.setFounder( true );
					}else{
						control.setFounder( false );
					}
					wo.setControl(control);
					
					wo.setProgressTotal(progressTotal);
					wo.setCompletedTotal(completedTotal);
					wo.setOvertimeTotal(overtimeTotal);
					wo.setTaskTotal(taskTotal);
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new ProjectQueryException(e, "将查询出来的应用项目信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		//}
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