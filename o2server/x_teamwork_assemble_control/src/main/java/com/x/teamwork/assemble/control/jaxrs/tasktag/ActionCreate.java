package com.x.teamwork.assemble.control.jaxrs.tasktag;

import java.util.ArrayList;
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
import com.x.teamwork.core.entity.TaskTag;

public class ActionCreate extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionCreate.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskTag taskTag = null;
		Project project = null;
		Wi wi = null;
		Boolean check = true;
		Dynamic dynamic = null;
		Wo wo = new Wo();

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskTagPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
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
			if( StringUtils.isEmpty( wi.getTag() )) {
				check = false;
				Exception exception = new TagEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			if( StringUtils.isEmpty( wi.getTagColor() )) {
				check = false;
				Exception exception = new TagColorEmptyException();
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
				Exception exception = new TaskTagPersistException(e, "根据ID查询项目信息对象时发生异常。ID:" + wi.getProject());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskTag = taskTagPersistService.save( Wi.copier.copy( wi ), effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( TaskTag.class );
				
				wo.setId( taskTag.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagPersistException(e, "标签信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		
		if (check) {
			try {					
				dynamic = dynamicPersistService.taskTagCreateDynamic(taskTag, effectivePerson);
				if( dynamic != null ) {
					List<WoDynamic> dynamics = new ArrayList<>();
					dynamics.add( WoDynamic.copier.copy( dynamic ) );
					wo.setDynamics(dynamics);
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		public static WrapCopier<Wi, TaskTag> copier = WrapCopierFactory.wi( Wi.class, TaskTag.class, null, null );

		@FieldDescribe("可以为空，系统自动生成，非必填。")
		private String id;
		
		@FieldDescribe("所属项目ID，<font style='color:red'>必填</font>。")
		private String project;

		@FieldDescribe("标签文字，<font style='color:red'>必填</font>")
		private String tag;

		@FieldDescribe("标签颜色，<font style='color:red'>必填</font>")
		private String tagColor;
		
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

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getTagColor() {
			return tagColor;
		}

		public void setTagColor(String tagColor) {
			this.tagColor = tagColor;
		}		
	}

	public static class Wo extends WoId {
		
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
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