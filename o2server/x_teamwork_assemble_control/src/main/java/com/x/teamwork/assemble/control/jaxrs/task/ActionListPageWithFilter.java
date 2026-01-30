package com.x.teamwork.assemble.control.jaxrs.task;

import com.google.gson.JsonElement;
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
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionListPageWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPageWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer pageNum, Integer pageSize, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();

		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		QueryFilter  queryFilter = wrapIn.getQueryFilter();
		String person = this.isSysManager(effectivePerson) ? "" : effectivePerson.getDistinguishedName();
		Long total = taskQueryService.countWithCondition(person, queryFilter);
		if(total > 0) {
			List<Task> taskList = taskQueryService.listPagingWithCondition(person,
					wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter, pageNum, pageSize);
			for(Task task : taskList){
				Wo wo = Wo.copier.copy(taskQueryService.getFromCache(task.getId()));
				List<TaskTag> tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, wo );
				if( ListTools.isNotEmpty( tags )) {
					wo.setTags( WoTaskTag.copier.copy( tags ));
				}
				Project project = projectQueryService.getFromCache(task.getProject());
				if(project!=null) {
					wo.setProjectName(project.getTitle());
				}
				wo.setSubTaskCount(taskQueryService.countTaskWithParentId( wo.getId()));
				wo.setAttCount(taskQueryService.countAttachmentWithTask(wo.getId()));

				WrapOutControl control = new WrapOutControl();
				if( this.canEdit(task, effectivePerson)){
					control.setDelete( true );
					control.setSortable( true );
					control.setChangeExecutor(true);
					control.setEdit( true );
				}
				control.setFounder(this.isParentManager(task, effectivePerson));
				wo.setControl(control);
				wos.add(wo);
			}
		}
		result.setCount(total);
		result.setData(wos);
		return result;
	}

	public static class Wi extends WrapInQueryTask{
	}

	public static class Wo extends Task {

		@FieldDescribe("任务标签")
		private List<WoTaskTag> tags = null;

		@FieldDescribe("任务权限")
		private WrapOutControl control = null;

		@FieldDescribe("下级子任务数")
		private Long subTaskCount = 0L;

		@FieldDescribe("任务附件数")
		private Long attCount = 0L;

		public List<WoTaskTag> getTags() {
			return tags;
		}

		public void setTags(List<WoTaskTag> tags) {
			this.tags = tags;
		}

		public Long getSubTaskCount() {
			return subTaskCount;
		}

		public void setSubTaskCount(Long subTaskCount) {
			this.subTaskCount = subTaskCount;
		}

		public Long getAttCount() {
			return attCount;
		}

		public void setAttCount(Long attCount) {
			this.attCount = attCount;
		}

		public WrapOutControl getControl() {
			return control;
		}

		public void setControl(WrapOutControl control) {
			this.control = control;
		}

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo( Task.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}

	public static class WoTaskTag extends TaskTag {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskTag, WoTaskTag> copier = WrapCopierFactory.wo( TaskTag.class, WoTaskTag.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}
