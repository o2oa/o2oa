package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskView;
import net.sf.ehcache.Element;

public class ActionStatisticMyTaskViews extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionStatisticMyTaskViews.class );

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<TaskGroup>  taskGroupList = null;
		List<TaskView> taskViewList = null;
		List<WoTaskGroup> woGroupList = null;
		List<WoTaskView> woViewList = null;
		Boolean check = true;

		String cacheKey = ApplicationCache.concreteCacheKey( "ActionStatisticMyTaskViews", projectId, effectivePerson.getDistinguishedName() );
		Element element = taskCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = ( Wo ) element.getObjectValue();
			result.setData( wo );
		} else {
			if (check) {
				try {
					//查询用户在该项目中所有的视图信息
					taskViewList = taskViewQueryService.listViewWithPersonAndProject( effectivePerson, projectId );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException( e, "查询用户在该项目中的所有视图信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				if( ListTools.isNotEmpty( taskGroupList )) {
					woGroupList = WoTaskGroup.copier.copy( taskGroupList );
					SortTools.asc( woGroupList, "createTime");
				}
			}
			
			if (check) {
				if( ListTools.isNotEmpty( taskViewList )) {
					woViewList = WoTaskView.copier.copy( taskViewList );
					SortTools.asc( woViewList, "createTime");
				}
			}	
			
			
			if (check) {
				try {
					wo.setViews( woViewList );
					taskCache.put( new Element( cacheKey, wo) );
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new TaskQueryException(e, "将查询出来的工作任务组和视图列表信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo{
		
		@FieldDescribe("工作任务视图")
		private List<WoTaskView> views = null;

		public List<WoTaskView> getViews() {
			return views;
		}

		public void setViews(List<WoTaskView> views) {
			this.views = views;
		}		
	}
	
	public static class WoTaskGroup extends TaskGroup{		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();	
		static WrapCopier<TaskGroup, WoTaskGroup> copier = WrapCopierFactory.wo( TaskGroup.class, WoTaskGroup.class, null, Excludes);
	}
	
	public static class WoTaskView extends TaskView{		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();	
		static WrapCopier<TaskView, WoTaskView> copier = WrapCopierFactory.wo( TaskView.class, WoTaskView.class, null, Excludes);
	}
}