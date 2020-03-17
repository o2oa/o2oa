package com.x.teamwork.assemble.control.jaxrs.tasktag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskTagRele;

public class ActionAddTagRele extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAddTagRele.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String taskId, String tagId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskTag taskTag = null;
		TaskTagRele rele = null;
		Task task = null;
		Dynamic dynamic = null;
		Wo wo = new Wo();
		Boolean check = true;
		
		if (check) {
			if( StringUtils.isEmpty( taskId )) {
				check = false;
				Exception exception = new TaskIdEmptyException();
				result.error(exception);
			}
		}		
		if (check) {
			if( StringUtils.isEmpty( tagId )) {
				check = false;
				Exception exception = new TagIdEmptyException();
				result.error(exception);
			}
		}
		
		if (check) {
			try {
				task = taskQueryService.get( taskId );
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException( taskId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagPersistException(e, "根据ID查询任务信息对象时发生异常。ID:" + taskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskTag = taskTagQueryService.get( tagId );
				if ( taskTag == null) {
					check = false;
					Exception exception = new TaskTagNotExistsException( tagId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagPersistException(e, "根据ID查询标签信息对象时发生异常。ID:" + tagId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				
				rele = taskTagPersistService.addTagRele( task, taskTag, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( TaskTag.class );
				ApplicationCache.notify( Task.class );
				
				if( rele != null ) {
					result.setData( Wo.copier.copy( rele ) );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskTagPersistException(e, "关联标签信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		
		if (check) {
			try {					
				dynamic = dynamicPersistService.addTaskTagReleDynamic( task, taskTag, effectivePerson );
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

	public static class Wo extends TaskTagRele {
		
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskTagRele, Wo> copier = WrapCopierFactory.wo( TaskTagRele.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		
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