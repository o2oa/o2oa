package com.x.teamwork.assemble.control.jaxrs.taskview;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.TaskView;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskView taskView = null;
		Project project = null;
		Wi wi = null;
		Boolean check = true;
		Wo wo = new Wo();

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskViewPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getProject() )) {
				check = false;
				Exception exception = new ProjectIdEmptyException();
				result.error(exception);
			}
		}		
		if (check) {
			if( StringUtils.isEmpty( wi.getName())) {
				check = false;
				Exception exception = new TaskViewNameEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			if( StringUtils.isEmpty( wi.getProject() )) {
				check = false;
				Exception exception = new ProjectIdEmptyException();
				result.error(exception);
			}
		}
		
		if (check) {
			try {
				project = projectQueryService.get( wi.getProject() );
				if ( project == null) {
					check = false;
					Exception exception = new ProjectNotExistsException( wi.getProject() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskViewPersistException(e, "根据ID查询项目信息对象时发生异常。ID:" + wi.getProject());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskView = taskViewPersistService.save( Wi.copier.copy( wi ), effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( TaskView.class );
				
				wo.setId( taskView.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskViewPersistException(e, "标签信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		public static WrapCopier<Wi, TaskView> copier = WrapCopierFactory.wi( Wi.class, TaskView.class, null, JpaObject.FieldsUnmodifyExcludeId );

		@FieldDescribe("可以为空，系统自动生成，非必填。")
		private String id;
		
		@FieldDescribe("所属项目ID，<font style='color:red'>必填</font>。")
		private String project;
		
		@FieldDescribe("任务视图名称，<font style='color:red'>必填</font>。")
		private String name;

		@FieldDescribe("是否已完成：-1-全部(默认)， 1-是，0-否，非必填。")
		private Integer workCompleted = -1;

		@FieldDescribe("是否已超时：-1-全部(默认)， 1-是，0-否，非必填。")
		private Integer workOverTime = -1;

		@FieldDescribe("查询负责的项目，true(默认)|false。如果为false，则为所有参与的项目范围")
		private Boolean isExcutor = true;

		@FieldDescribe("筛选优先级：普通|紧急|特急，非必填。")
		private List<String> choosePriority;

		@FieldDescribe("筛选标签：自定义标签ID列表，非必填。")
		private List<String> chooseWorkTag;
		
		@FieldDescribe("排序号，非必填。")
		private Integer order;

		@FieldDescribe("列表描述，非必填。")
		private String memo;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(Integer workCompleted) {
			this.workCompleted = workCompleted;
		}

		public Integer getWorkOverTime() {
			return workOverTime;
		}

		public void setWorkOverTime(Integer workOverTime) {
			this.workOverTime = workOverTime;
		}

		public Boolean getIsExcutor() {
			return isExcutor;
		}

		public void setIsExcutor(Boolean isExcutor) {
			this.isExcutor = isExcutor;
		}

		public List<String> getChoosePriority() {
			return choosePriority;
		}

		public void setChoosePriority(List<String> choosePriority) {
			this.choosePriority = choosePriority;
		}

		public List<String> getChooseWorkTag() {
			return chooseWorkTag;
		}

		public void setChooseWorkTag(List<String> chooseWorkTag) {
			this.chooseWorkTag = chooseWorkTag;
		}

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}
	}

	public static class Wo extends WoId {
	}
	
	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}		
	}
}