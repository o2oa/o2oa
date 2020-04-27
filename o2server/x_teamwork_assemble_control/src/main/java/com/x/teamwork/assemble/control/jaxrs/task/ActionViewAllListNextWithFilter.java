package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.IsFalseTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

import net.sf.ehcache.Element;

public class ActionViewAllListNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionViewAllListNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag, Integer count, String projectId, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();		
		List<Wo> wos = new ArrayList<>();
		ResultObject resultObject = null;
		Wi wrapIn = null;
		Boolean check = true;
		String cacheKey = null;
		Element element = null;
		QueryFilter  queryFilter = null;
		List<TaskTag> tags = null; 
		WrapOutControl control = null;
		
		if ( StringUtils.isEmpty( projectId ) ) {
			check = false;
			Exception exception = new TaskProjectFlagForQueryEmptyException();
			result.error( exception );
		}
		if ( StringUtils.isEmpty( flag ) || "(0)".equals(flag)) {
			flag = null;
		}
		
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskQueryException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if( Boolean.TRUE.equals( check ) ){
			if( Boolean.TRUE.equals( check ) ){
				wrapIn.setProject(projectId);
				wrapIn.setDeleted("false");
				queryFilter = wrapIn.getQueryFilter();
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			cacheKey = ApplicationCache.concreteCacheKey( "ActionAllListNext", effectivePerson.getDistinguishedName(), 
					flag, count, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter.getContentSHA1() );
			element = taskCache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				resultObject = (ResultObject) element.getObjectValue();
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} else {	
				Business business = null;
				try (EntityManagerContainer bc = EntityManagerContainerFactory.instance().create()) {
					business = new Business(bc);
				}
				try {
					
					Long total = taskQueryService.countWithFilter( effectivePerson, queryFilter );
					List<Task> taskList = taskQueryService.listWithFilter( effectivePerson, count, flag, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter );
					
					if( ListTools.isNotEmpty( taskList )) {
						wos = Wo.copier.copy(taskList);
						for( Wo wo : wos ) {
							tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, wo );
							if( ListTools.isNotEmpty( tags )) {
								wo.setTags( WoTaskTag.copier.copy( tags ));
							}
							try {
								control = new WrapOutControl();
								if( business.isManager(effectivePerson) 
										|| effectivePerson.getDistinguishedName().equalsIgnoreCase( wo.getCreatorPerson() )
										|| wo.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )){
									control.setDelete( true );
									control.setEdit( true );
									control.setSortable( true );
									control.setChangeExecutor(true);
								}else{
									control.setDelete( false );
									control.setEdit( false );
									control.setSortable( false );
									control.setChangeExecutor(false);
								}
								if(effectivePerson.getDistinguishedName().equalsIgnoreCase( wo.getExecutor())){
									control.setChangeExecutor( true );
								}
								if(effectivePerson.getDistinguishedName().equalsIgnoreCase( wo.getCreatorPerson())){
									control.setFounder( true );
								}else{
									control.setFounder( false );
								}
								wo.setControl(control);
							} catch (Exception e) {
								check = false;
								Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务权限信息时发生异常。flag:" + wo.getId());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
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
	
	public static class Wi extends  GsonPropertyObject{
		@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
		private String orderField = "createTime";

		@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
		private String orderType = "DESC";
		
		@FieldDescribe("用于搜索的标题，单值，非必填.")
		private String title = null;
		
		private String project = null;
		
		@FieldDescribe("用于搜索的上级工作任务ID，单值，非必填.")
		private String parentId = null;
		
		@FieldDescribe("用于搜索的工作标签：自定义标签，单值，非必填.")
		private String tag = null;
		
		@FieldDescribe("工作等级：普通-normal | 紧急-urgent | 特急-extraurgent，单值，非必填")
		private String priority = null;		
		
		@FieldDescribe("用于搜索的工作状态：草稿- draft  | 执行中- processing | 已完成- completed | 已归档- archived，单值，非必填")
		private String workStatus = null;
		
		@FieldDescribe("是否已完成，true|false，非必填")
		private String completed = null;		

		@FieldDescribe("是否已超时，true|false，非必填")
		private String overtime = null;		
		
		@FieldDescribe("是否已经删除，true|false，非必填")
		private String deleted = null;		
		
		@FieldDescribe("执行者或者负责人，单值，非必填")
		private String executor = null;		

		private Long rank = 0L;

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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public String getWorkStatus() {
			return workStatus;
		}

		public void setWorkStatus(String workStatus) {
			this.workStatus = workStatus;
		}

		public String getCompleted() {
			return completed;
		}

		public void setCompleted(String completed) {
			this.completed = completed;
		}

		public String getOvertime() {
			return overtime;
		}

		public void setOvertime(String overtime) {
			this.overtime = overtime;
		}

		public String getDeleted() {
			return deleted;
		}

		public void setDeleted(String deleted) {
			this.deleted = deleted;
		}

		public String getExecutor() {
			return executor;
		}

		public void setExecutor(String executor) {
			this.executor = executor;
		}
		
		
		/**
		 * 根据传入的查询参数，组织一个完整的QueryFilter对象
		 * @return
		 */
		public QueryFilter getQueryFilter() {
			QueryFilter queryFilter = new QueryFilter();
			//组织查询条件对象
			if( StringUtils.isNotEmpty( this.getTitle() )) {
				queryFilter.addLikeTerm( new LikeTerm( "name", "%" + this.getTitle() + "%" ) );
			}
			if( StringUtils.isNotEmpty( this.getProject() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "project", this.getProject() ) );
			}
			if( StringUtils.isNotEmpty( this.getParentId() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "parent", this.getParentId() ) );
			}
			if( StringUtils.isNotEmpty( this.getPriority())) {
				queryFilter.addEqualsTerm( new EqualsTerm( "priority", this.getPriority() ) );
			}
			if( StringUtils.isNotEmpty( this.getWorkStatus() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "workStatus", this.getWorkStatus() ) );
			}else {
				//默认查询所有的未归档的工作任务
				queryFilter.addIsFalseTerm( new IsFalseTerm("archive"));
			}
			if( StringUtils.isNotEmpty( this.getExecutor())) {
				queryFilter.addEqualsTerm( new EqualsTerm( "executor", this.getExecutor() ) );
			}
			if( StringUtils.isNotEmpty( this.getCompleted() )) {
				if( "true".equalsIgnoreCase( this.getCompleted() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "completed", true ) );
				}else {
					queryFilter.addEqualsTerm( new EqualsTerm( "completed", false ) );
				}
			}
			if( StringUtils.isNotEmpty( this.getDeleted() )) {
				if( "true".equalsIgnoreCase( this.getDeleted() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "deleted", true ) );
				}else {
					queryFilter.addEqualsTerm( new EqualsTerm( "deleted", false ) );
				}
			}
			if( StringUtils.isNotEmpty( this.getOvertime() )) {
				if( "true".equalsIgnoreCase( this.getOvertime() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "overtime", true ) );
				}else {
					queryFilter.addEqualsTerm( new EqualsTerm( "overtime", false ) );
				}
			}
			return queryFilter;
		}
	}
	
	public static class Wo extends Task {

		@FieldDescribe("任务标签")
		private List<WoTaskTag> tags = null;
		
		@FieldDescribe("任务权限")
		private WrapOutControl control = null;	
		
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
		
		public WrapOutControl getControl() {
			return control;
		}

		public void setControl(WrapOutControl control) {
			this.control = control;
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