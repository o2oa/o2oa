package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import com.x.teamwork.core.entity.tools.filter.term.MemberTerm;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sword
 */
public class WrapInQueryProject {
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = JpaObject.sequence_FIELDNAME;

	@FieldDescribe("排序方式：DESC | ASC，非必填， 默认为DESC.")
	private String orderType = "DESC";

	@FieldDescribe("用于搜索的标题，非必填.")
	private String title = null;

	@FieldDescribe("用于搜索的项目类型，非必填.")
	private String type = null;

	@FieldDescribe("执行者和负责人：单值，非必填")
	private String executor;

	@FieldDescribe("项目来源")
	private String source;

	@FieldDescribe("是否标星，非必填")
	private Boolean isStar = null;

	@FieldDescribe( "作为过滤条件的状态列表, 可多个, String数组，值：进行中-processing|已完成-completed|已归档-archived|已搁置-delay|已取消(已删除)-canceled，非必填" )
	private List<String> statusList;

	@FieldDescribe( "发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> publishDateList;

	@FieldDescribe("用于搜索的项目分组标识：单值，非必填.")
	private String group = null;

	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public Boolean getIsStar() {
		return isStar;
	}

	public void setIsStar(Boolean isStar) {
		this.isStar = isStar;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
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

	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @return
	 */
	public QueryFilter getQueryFilter(EffectivePerson effectivePerson) throws Exception {
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.setJoinType( "and" );
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( Project.title_FIELDNAME, this.getTitle() ) );
		}
		if( StringUtils.isNotEmpty( this.getExecutor())) {
			queryFilter.addMemberTerm(new MemberTerm(Project.manageablePersonList_FIELDNAME, this.getExecutor()));
		}

		if (BooleanUtils.isTrue(this.getIsStar())){
			queryFilter.addMemberTerm(new MemberTerm(Project.starPersonList_FIELDNAME, effectivePerson.getDistinguishedName()));
		}

		if( StringUtils.isNotEmpty( this.getSource())) {
			queryFilter.addEqualsTerm( new EqualsTerm( Project.source_FIELDNAME, this.getSource() ) );
		}
		if( ListTools.isNotEmpty( this.getStatusList())) {
			if( this.getStatusList().size() == 1 ) {
				queryFilter.addEqualsTerm( new EqualsTerm( Project.workStatus_FIELDNAME, this.getStatusList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( Project.workStatus_FIELDNAME, new ArrayList<>( this.getStatusList())) );
			}
		}else{
			queryFilter.addInTerm( new InTerm( Project.workStatus_FIELDNAME,
					ListTools.toList(ProjectStatusEnum.PROCESSING.getValue(), ProjectStatusEnum.DELAY.getValue(), ProjectStatusEnum.COMPLETED.getValue()) ));
		}
		if( ListTools.isNotEmpty( this.getPublishDateList())) {
			Date startDate = DateTools.parse(this.getPublishDateList().get(0));
			Date endDate = new Date();
			if(this.getPublishDateList().size() > 1){
				endDate = DateTools.parse(this.getPublishDateList().get(1));
			}
			queryFilter.addDateBetweenTerm( Project.publishTime_FIELDNAME, startDate, endDate );
		}

		return queryFilter;
	}

	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @return
	 */
	public QueryFilter getQueryFilter() throws Exception {
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.setJoinType( "and" );
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( Project.title_FIELDNAME, this.getTitle() ) );
		}
		if( StringUtils.isNotEmpty( this.getExecutor())) {
			queryFilter.addEqualsTerm( new EqualsTerm( Project.executor_FIELDNAME, this.getExecutor() ) );
		}
		if( StringUtils.isNotEmpty( this.getSource())) {
			queryFilter.addEqualsTerm( new EqualsTerm( Project.source_FIELDNAME, this.getSource() ) );
		}
		if( ListTools.isNotEmpty( this.getStatusList())) {
			if( this.getStatusList().size() == 1 ) {
				queryFilter.addEqualsTerm( new EqualsTerm( Project.workStatus_FIELDNAME, this.getStatusList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( Project.workStatus_FIELDNAME, new ArrayList<>( this.getStatusList())) );
			}
		}else{
			queryFilter.addInTerm( new InTerm( Project.workStatus_FIELDNAME,
					ListTools.toList(ProjectStatusEnum.PROCESSING.getValue(), ProjectStatusEnum.DELAY.getValue(), ProjectStatusEnum.COMPLETED.getValue()) ));
		}
		if( ListTools.isNotEmpty( this.getPublishDateList())) {
			Date startDate = DateTools.parse(this.getPublishDateList().get(0));
			Date endDate = new Date();
			if(this.getPublishDateList().size() > 1){
				endDate = DateTools.parse(this.getPublishDateList().get(1));
			}
			queryFilter.addDateBetweenTerm( Project.publishTime_FIELDNAME, startDate, endDate );
		}

		return queryFilter;
	}
}
