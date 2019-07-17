package com.x.teamwork.assemble.control.jaxrs.dynamic;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;

import net.sf.ehcache.Element;

public class ActionListNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		ResultObject resultObject = null;
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = null;		
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		QueryFilter  queryFilter = null;
		
		if ( StringUtils.isEmpty( flag ) || "(0)".equals(flag)) {
			flag = null;
		}
		
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new DynamicQueryException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( check ) {
			queryFilter = wrapIn.getQueryFilter();
		}
		
		if( check ) {
			cacheKey = ApplicationCache.concreteCacheKey( "ActionListNext", effectivePerson.getDistinguishedName(),  
					flag, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter.getContentSHA1() );
			element = dynamicCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {
				try {
					List<Dynamic>  dynamicList = null;
					long total = dynamicQueryService.countWithFilter( queryFilter );
					
					if( total > 0 ) {
						dynamicList = dynamicQueryService.listWithFilter( effectivePerson, count, flag, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter );			
					}else {
						total = 0;
					}
					
					if( ListTools.isNotEmpty( dynamicList )) {
						wos = Wo.copier.copy(dynamicList);
					}else {
						wos = new ArrayList<>();
					}					
					resultObject = new ResultObject( total, wos );
					dynamicCache.put(new Element( cacheKey, resultObject ));
					result.setCount( resultObject.getTotal() );
					result.setData( resultObject.getWos() );
				} catch (Exception e) {
					check = false;
					logger.warn("系统根据条件查询工作动态信息列表时发生异常!");
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}		
		}
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
		private String orderField = "createTime";

		@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
		private String orderType = "DESC";
		
		@FieldDescribe("用于搜索的项目ID，<font style='color:red'>必填</font>")
		private String projectId = null;
		
		@FieldDescribe("用于搜索的工作任务ID，非必填.")
		private String taskId = null;
		
		@FieldDescribe("用于搜索的对象类型：PROJECT、TASK、TASKGROUP、TASKLIST、TASKVIEW、CHAT等，非必填.")
		private String objectType = null;
		
		@FieldDescribe("用于搜索的项目、工作或者工作组、列表、视图等对象的ID，非必填.")
		private String bundle = null;
		
		@FieldDescribe("用于搜索的操作类别，非必填.")
		private String optType = null;
		
		private Long rank = 0L;

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getObjectType() {
			return objectType;
		}

		public void setObjectType(String objectType) {
			this.objectType = objectType;
		}

		public String getBundle() {
			return bundle;
		}

		public void setBundle(String bundle) {
			this.bundle = bundle;
		}

		public String getOptType() {
			return optType;
		}

		public void setOptType(String optType) {
			this.optType = optType;
		}
		
		public String getOrderField() {
			return orderField;
		}

		public void setOrderField(String orderField) {
			this.orderField = orderField;
		}

		public String getOrderType() {
			return orderType;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		/**
		 * 根据传入的查询参数，组织一个完整的QueryFilter对象
		 * @return
		 */
		public QueryFilter getQueryFilter() {
			QueryFilter queryFilter = new QueryFilter();
			//组织查询条件对象
			if( StringUtils.isNotEmpty( this.getProjectId() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "projectId", this.getProjectId() ) );
			}
			if( StringUtils.isNotEmpty( this.getTaskId() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "taskId", this.getTaskId() ) );
			}
			if( StringUtils.isNotEmpty( this.getObjectType())) {
				queryFilter.addEqualsTerm( new EqualsTerm( "objectType", this.getObjectType() ) );
			}
			if( StringUtils.isNotEmpty( this.getOptType() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "optType", this.getOptType() ) );
			}
			if( StringUtils.isNotEmpty( this.getBundle() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "bundle", this.getBundle() ) );
			}
			return queryFilter;
		}
	}
	
	public static class Wo extends Dynamic {

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Dynamic, Wo> copier = WrapCopierFactory.wo( Dynamic.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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