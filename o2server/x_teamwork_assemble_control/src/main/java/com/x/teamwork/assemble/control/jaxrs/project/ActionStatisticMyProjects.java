package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.ProjectStatusEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionStatisticMyProjects extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStatisticMyProjects.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<WoGroup> woGroupList = new ArrayList<>();

		Long unGroupCount = 0L;

		String person = this.isSysManager(effectivePerson) ? "" : effectivePerson.getDistinguishedName();

		WrapInQueryProject wi = new WrapInQueryProject();
		Long allCount = projectQueryService.countWithCondition(person, wi.getQueryFilter(effectivePerson));

		wi.setExecutor(effectivePerson.getDistinguishedName());
		Long myCount = projectQueryService.countWithCondition("", wi.getQueryFilter(effectivePerson));

		wi = new WrapInQueryProject();
		wi.setIsStar(true);
		Long starCount = projectQueryService.countWithCondition(person, wi.getQueryFilter(effectivePerson));

		wi = new WrapInQueryProject();
		wi.setStatusList(ListTools.toList(ProjectStatusEnum.ARCHIVED.getValue()));
		Long archiveCount = projectQueryService.countWithCondition(person, wi.getQueryFilter(effectivePerson));

		wi = new WrapInQueryProject();
		wi.setStatusList(ListTools.toList(ProjectStatusEnum.COMPLETED.getValue()));
		Long completedCount = projectQueryService.countWithCondition(person, wi.getQueryFilter(effectivePerson));

		wi = new WrapInQueryProject();
		wi.setStatusList(ListTools.toList(ProjectStatusEnum.CANCELED.getValue()));
		Long deleteCount = projectQueryService.countWithCondition(person, wi.getQueryFilter(effectivePerson));

		wi = new WrapInQueryProject();
		wi.setStatusList(ListTools.toList(ProjectStatusEnum.DELAY.getValue()));
		Long delayCount = projectQueryService.countWithCondition(person, wi.getQueryFilter(effectivePerson));

		Long processingCount = allCount - archiveCount - completedCount - deleteCount - delayCount;

		List<ProjectGroup> projectGroupList = projectGroupQueryService.listGroupByPerson( effectivePerson.getDistinguishedName() );
		if( ListTools.isNotEmpty( projectGroupList )) {
			woGroupList = WoGroup.copier.copy( projectGroupList );
			SortTools.asc( woGroupList, JpaObject.createTime_FIELDNAME);
		}
		Wo wo = new Wo();
		wo.setAllCount( allCount );
		wo.setMyCount(myCount);
		wo.setStarCount(starCount);
		wo.setUnGroupCount(unGroupCount);
		wo.setCompletedCount(completedCount);
		wo.setArchiveCount(archiveCount);
		wo.setDelayCount(delayCount);
		wo.setProcessingCount(processingCount);
		wo.setDeleteCount(deleteCount);
		wo.setGroups( woGroupList );
		result.setData(wo);
		return result;
	}

	public static class Wo{

		@FieldDescribe("所有项目数量")
		private Long allCount = 0L;

		@FieldDescribe("标星的项目数量")
		private Long starCount = 0L;

		@FieldDescribe("我的项目数量")
		private Long myCount = 0L;

		@FieldDescribe("所有项目数量")
		private Long unGroupCount = 0L;

		@FieldDescribe("已完成项目数量")
		private Long completedCount = 0L;

		@FieldDescribe("进行中的项目数量")
		private Long processingCount = 0L;

		@FieldDescribe("已搁置的项目数量")
		private Long delayCount = 0L;

		@FieldDescribe("已归档的项目数量")
		private Long archiveCount = 0L;

		@FieldDescribe("已取消的项目数量")
		private Long deleteCount = 0L;

		@FieldDescribe("所有分组信息")
		private List<WoGroup> groups = null;

		public List<WoGroup> getGroups() {
			return groups;
		}

		public void setGroups(List<WoGroup> groups) {
			this.groups = groups;
		}

		public Long getAllCount() {
			return allCount;
		}

		public void setAllCount(Long allCount) {
			this.allCount = allCount;
		}

		public Long getStarCount() {
			return starCount;
		}

		public void setStarCount(Long starCount) {
			this.starCount = starCount;
		}

		public Long getMyCount() {
			return myCount;
		}

		public void setMyCount(Long myCount) {
			this.myCount = myCount;
		}

		public Long getUnGroupCount() {
			return unGroupCount;
		}

		public void setUnGroupCount(Long unGroupCount) {
			this.unGroupCount = unGroupCount;
		}

		public Long getCompletedCount() {
			return completedCount;
		}

		public void setCompletedCount(Long completedCount) {
			this.completedCount = completedCount;
		}

		public Long getProcessingCount() {
			return processingCount;
		}

		public void setProcessingCount(Long processingCount) {
			this.processingCount = processingCount;
		}

		public Long getDelayCount() {
			return delayCount;
		}

		public void setDelayCount(Long delayCount) {
			this.delayCount = delayCount;
		}

		public Long getArchiveCount() {
			return archiveCount;
		}

		public void setArchiveCount(Long archiveCount) {
			this.archiveCount = archiveCount;
		}

		public Long getDeleteCount() {
			return deleteCount;
		}

		public void setDeleteCount(Long deleteCount) {
			this.deleteCount = deleteCount;
		}
	}

	public static class WoGroup extends ProjectGroup{

		@FieldDescribe("分组项目数量")
		private Integer projectCount = 0;

		public Integer getProjectCount() {
			return projectCount;
		}

		public void setProjectCount(Integer projectCount) {
			this.projectCount = projectCount;
		}

		public void addProjectCount( Integer count ) {
			if( this.projectCount == null ) {
				this.projectCount =0;
			}
			this.projectCount += count;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static {
			Excludes.add("creatorPerson");
			Excludes.add("owner");
			Excludes.add("updateTime");
			Excludes.add("distributeFactor");
			Excludes.add("sequence");
		}

		static WrapCopier<ProjectGroup, WoGroup> copier = WrapCopierFactory.wo( ProjectGroup.class, WoGroup.class, null, Excludes);
	}
}
