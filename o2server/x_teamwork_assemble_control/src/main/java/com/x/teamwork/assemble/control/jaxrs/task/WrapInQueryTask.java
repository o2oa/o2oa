package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.tools.filter.term.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sword
 */
public class WrapInQueryTask {

	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = JpaObject.sequence_FIELDNAME;

	@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
	private String orderType = "DESC";

	@FieldDescribe("用于搜索的标题，单值，非必填.")
	private String title = null;

	@FieldDescribe("用于搜索的项目ID，单值，非必填.")
	private String project = null;

	private List<String> projectList;

	@FieldDescribe("用于搜索的上级工作任务ID，单值，非必填.")
	private String parentId = null;

	private String tag = null;

	@FieldDescribe("工作等级：普通-normal | 紧急-urgent | 特急-extraurgent，单值，非必填")
	private String priority = null;

	@FieldDescribe("重要程度，单值，非必填")
	private String important;

	@FieldDescribe("任务来源")
	private String source;

	@FieldDescribe("紧急程度，单值，非必填")
	private String urgency;

	@FieldDescribe("用于搜索的工作状态：进行中-processing|已完成-completed|已搁置-delay|已取消(已删除)-canceled，单值，非必填")
	private String workStatus = null;

	@FieldDescribe( "同workStatus，workStatus优先, 可多个，非必填" )
	private List<String> workStatusList;

	@FieldDescribe("执行者或者负责人，单值，非必填")
	private String executor = null;

	@FieldDescribe( "发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> publishDateList;

	/**
	 * 查询管理权限的任务
	 */
	private Boolean queryManager = null;

	/**
	 * 查询过期任务
	 */
	private Boolean overTime = null;

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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public List<String> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<String> projectList) {
		this.projectList = projectList;
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

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public List<String> getPublishDateList() {
		return publishDateList;
	}

	public void setPublishDateList(List<String> publishDateList) {
		this.publishDateList = publishDateList;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<String> getWorkStatusList() {
		return workStatusList;
	}

	public void setWorkStatusList(List<String> workStatusList) {
		this.workStatusList = workStatusList;
	}

	public Boolean getQueryManager() {
		return queryManager;
	}

	public void setQueryManager(Boolean queryManager) {
		this.queryManager = queryManager;
	}

	public Boolean getOverTime() {
		return overTime;
	}

	public void setOverTime(Boolean overTime) {
		this.overTime = overTime;
	}

	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @return
	 */
	public QueryFilter getQueryFilter() throws Exception {
		QueryFilter queryFilter = new QueryFilter();
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( Task.name_FIELDNAME, this.getTitle() ) );
		}
		if( StringUtils.isNotEmpty( this.getProject() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.project_FIELDNAME, this.getProject() ) );
		}else if(ListTools.isNotEmpty( this.getProjectList())){
			if( this.getProjectList().size() == 1 ) {
				queryFilter.addEqualsTerm( new EqualsTerm( Task.project_FIELDNAME, this.getProjectList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( Task.project_FIELDNAME, new ArrayList<>( this.getProjectList())) );
			}
		}
		if( StringUtils.isNotEmpty( this.getParentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.parent_FIELDNAME, this.getParentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getSource() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.source_FIELDNAME, this.getSource() ) );
		}
		if( StringUtils.isNotEmpty( this.getPriority())) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.priority_FIELDNAME, this.getPriority() ) );
		}
		if( StringUtils.isNotEmpty( this.getImportant())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Task.important_FIELDNAME, this.getImportant() ) );
		}
		if( StringUtils.isNotEmpty( this.getUrgency())) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.urgency_FIELDNAME, this.getUrgency() ) );
		}
		if( StringUtils.isNotEmpty( this.getWorkStatus() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.workStatus_FIELDNAME, this.getWorkStatus() ) );
		} else if(ListTools.isNotEmpty( this.getWorkStatusList())){
			if( this.getWorkStatusList().size() == 1 ) {
				queryFilter.addEqualsTerm( new EqualsTerm( Task.workStatus_FIELDNAME, this.getWorkStatusList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( Task.workStatus_FIELDNAME, new ArrayList<>( this.getWorkStatusList())) );
			}
		}
		if( StringUtils.isNotEmpty( this.getExecutor())) {
			queryFilter.addEqualsTerm( new EqualsTerm( Task.executor_FIELDNAME, this.getExecutor() ) );
		}
		if( ListTools.isNotEmpty( this.getPublishDateList())) {
			Date startDate = DateTools.parse(this.getPublishDateList().get(0));
			Date endDate = new Date();
			if(this.getPublishDateList().size() > 1){
				endDate = DateTools.parse(this.getPublishDateList().get(1));
			}
			queryFilter.addDateBetweenTerm( Project.publishTime_FIELDNAME, startDate, endDate );
		}
		if(this.getOverTime() != null){
			queryFilter.addEqualsTerm( new EqualsTerm(Task.overtime_FIELDNAME, this.getOverTime() ) );
		}
		queryFilter.setQueryManager(this.getQueryManager());
		return queryFilter;
	}
}
