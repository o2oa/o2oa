package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
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
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import net.sf.ehcache.Element;

public class ActionListPageWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPageWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer pageNum, Integer pageSize, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();		
		List<Wo> wos = new ArrayList<>();
		ResultObject resultObject = null;
		Wi wrapIn = null;
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		QueryFilter  queryFilter = null;
		List<TaskTag> tags = null;
		
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskQueryException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if( check ) {
			if( check ) {
				queryFilter = wrapIn.getQueryFilter();
			}
		}
		
		if( check ) {
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListPage", effectivePerson.getDistinguishedName(), 
					pageNum, pageSize, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter.getContentSHA1() );
			element = taskCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {				
				try {
					Long total = taskQueryService.countWithFilter( effectivePerson, queryFilter );
					List<Task> taskList = taskQueryService.listWithFilter( effectivePerson, pageSize, pageNum, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter );
					
					if( ListTools.isNotEmpty( taskList )) {
						wos = Wo.copier.copy(taskList);
						for( Wo wo : wos ) {
							tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, wo );
							if( ListTools.isNotEmpty( tags )) {
								wo.setTags( WoTaskTag.copier.copy( tags ));
							}
						}
					}

					resultObject = new ResultObject( total, wos );
					taskCache.put(new Element( cacheKey, resultObject ));
					
					result.setCount( resultObject.getTotal() );
					result.setData( resultObject.getWos() );
				} catch (Exception e) {
					check = false;
					logger.warn("系统查询工作任务信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}
	
	public static class Wi extends WrapInQueryTask{
	}
	
	public static class Wo extends Task {

		@FieldDescribe("任务标签")
		private List<WoTaskTag> tags = null;
		
		public List<WoTaskTag> getTags() {
			return tags;
		}

		public void setTags(List<WoTaskTag> tags) {
			this.tags = tags;
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
	
	public static class ResultObject {

		private Long total;
		
		private List<Wo> wos;

		public ResultObject() {}
		
		public ResultObject(Long count, List<Wo> data) {
			this.total = count;
			this.wos = data;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getWos() {
			return wos;
		}

		public void setWos(List<Wo> wos) {
			this.wos = wos;
		}
	}
}