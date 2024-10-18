package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.express.tools.DateOperation;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.cms.core.express.tools.filter.term.EqualsTerm;
import com.x.cms.core.express.tools.filter.term.InTerm;
import com.x.cms.core.express.tools.filter.term.IsFalseTerm;
import com.x.cms.core.express.tools.filter.term.IsTrueTerm;
import com.x.cms.core.express.tools.filter.term.LikeTerm;

public class WrapInDocumentFilter {

	public static final String READ_FLAG_READ = "READ";
	public static final String READ_FLAG_UNREAD = "UNREAD";

	@FieldDescribe( "排序列名" )
	private String orderField = "publishTime";

	@FieldDescribe( "排序方式：ASC|DESC." )
	private String orderType = "DESC";

	@FieldDescribe( "是否需要查询数据，默认不查询." )
	private Boolean needData = false;

	@FieldDescribe( "是否已读：ALL|READ|UNREAD." )
	private String readFlag = "ALL";

	@FieldDescribe( "是否置顶：ALL|TOP|UNTOP." )
	private String topFlag = "ALL";

	@FieldDescribe( "只查询minutes分钟之类发布的文档，值为null或者为0时不作限制" )
	private Integer minutes = null;

	@FieldDescribe( "作为过滤条件的CMS应用ID列表, 可多个, String数组." )
	private List<String> appIdList;

	@FieldDescribe( "作为过滤条件的CMS应用名称列表, 可多个, String数组." )
	private List<String> appNameList;

	@FieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
	private List<String> appAliasList;

	@FieldDescribe( "作为过滤条件的CMS分类ID列表, 可多个, String数组." )
	private List<String> categoryIdList;

	@FieldDescribe( "作为过滤条件的CMS分类名称列表, 可多个, String数组." )
	private List<String> categoryNameList;

	@FieldDescribe( "作为过滤条件的CMS分类别名列表, 可多个, String数组." )
	private List<String> categoryAliasList;

	@FieldDescribe( "作为过滤条件的创建者姓名列表, 可多个, String数组." )
	private List<String> creatorList;

	@FieldDescribe( "作为过滤条件的文档状态列表, 可多个, String数组，值：published(默认值) | waitPublish(待发布) | draft(草稿) | archived(归档)" )
	private List<String> statusList;

	@FieldDescribe( "创建日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> createDateList;

	@FieldDescribe( "发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> publishDateList;

	@FieldDescribe( "作为过滤条件的发布者所属组织, 可多个, String数组." )
	private List<String> creatorUnitNameList;

	@FieldDescribe( "文档类型：全部 | 信息(默认值) | 数据" )
	private String documentType = "信息";

	@FieldDescribe( "作为过滤条件的CMS文档关键字, 通常是标题, String, 模糊查询." )
	private String title;

	@FieldDescribe("文件导入的批次号：一般是分类ID+时间缀")
	private String importBatchName;

	@FieldDescribe("业务数据String值01.")
	private String stringValue01;

	@FieldDescribe("业务数据String值02.")
	private String stringValue02;

	@FieldDescribe("业务数据String值03.")
	private String stringValue03;

	@FieldDescribe("业务数据String值04.")
	private String stringValue04;

	@FieldDescribe("业务数据String值05.")
	private String stringValue05;

	@FieldDescribe("业务数据String值06.")
	private String stringValue06;

	@FieldDescribe("业务数据String值07.")
	private String stringValue07;

	@FieldDescribe("业务数据String值08.")
	private String stringValue08;

	@FieldDescribe("业务数据String值09.")
	private String stringValue09;

	@FieldDescribe("业务数据String值10.")
	private String stringValue10;

	@FieldDescribe("业务数据Long值01.")
	private Long longValue01;

	@FieldDescribe("业务数据Long值02.")
	private Long longValue02;

	@FieldDescribe("业务数据Double值01.")
	private Double doubleValue01;

	@FieldDescribe("业务数据Double值02.")
	private Double doubleValue02;

	@FieldDescribe( "业务数据DateTime值01，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> dateTimeValue01List;

	@FieldDescribe( "业务数据DateTime值02，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> dateTimeValue02List;

	@FieldDescribe( "业务数据DateTime值03，可以传入1个(开始时间)或者2个(开始和结束时间), 格式：yyyy-MM-dd HH:mm:ss或者yyyy-mm-dd." )
	private List<String> dateTimeValue03List;

	public String getTopFlag() {
		return topFlag;
	}

	public void setTopFlag(String topFlag) {
		this.topFlag = topFlag;
	}

	public String getImportBatchName() {
		return importBatchName;
	}

	public void setImportBatchName(String importBatchName) {
		this.importBatchName = importBatchName;
	}

	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public List<String> getCreatorUnitNameList() {
		return creatorUnitNameList;
	}

	public void setCreatorUnitNameList(List<String> creatorUnitNameList) {
		this.creatorUnitNameList = creatorUnitNameList;
	}

	public List<String> getAppIdList() {
		return appIdList == null?new ArrayList<>():appIdList;
	}

	public void setAppIdList(List<String> appIdList) {
		this.appIdList = appIdList;
	}

	public List<String> getCategoryIdList() {
		return categoryIdList == null?new ArrayList<>():categoryIdList;
	}

	public void setCategoryIdList(List<String> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}

	public List<String> getCreatorList() {
		return creatorList == null?new ArrayList<>():creatorList;
	}

	public void setCreatorList(List<String> creatorList) {
		this.creatorList = creatorList;
	}

	public List<String> getStatusList() {
		return statusList == null?new ArrayList<>():statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public List<String> getCreateDateList() {
		return createDateList == null?new ArrayList<>():createDateList;
	}

	public void setCreateDateList(List<String> createDateList) {
		this.createDateList = createDateList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getPublishDateList() {
		return publishDateList == null?new ArrayList<>():publishDateList;
	}

	public void setPublishDateList(List<String> publishDateList) {
		this.publishDateList = publishDateList;
	}

	public String getOrderField() {
		return orderField;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public List<String> getAppAliasList() {
		return appAliasList == null?new ArrayList<>():appAliasList;
	}

	public List<String> getCategoryAliasList() {
		return categoryAliasList == null?new ArrayList<>():categoryAliasList;
	}

	public void setAppAliasList(List<String> appAliasList) {
		this.appAliasList = appAliasList;
	}

	public void setCategoryAliasList(List<String> categoryAliasList) {
		this.categoryAliasList = categoryAliasList;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Boolean getNeedData() {
		return needData;
	}

	public void setNeedData(Boolean needData) {
		this.needData = needData;
	}

	public List<String> getAppNameList() {
		return appNameList;
	}

	public void setAppNameList(List<String> appNameList) {
		this.appNameList = appNameList;
	}

	public List<String> getCategoryNameList() {
		return categoryNameList;
	}

	public void setCategoryNameList(List<String> categoryNameList) {
		this.categoryNameList = categoryNameList;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}

	public String getStringValue04() {
		return stringValue04;
	}

	public void setStringValue04(String stringValue04) {
		this.stringValue04 = stringValue04;
	}

	public Long getLongValue01() {
		return longValue01;
	}

	public void setLongValue01(Long longValue01) {
		this.longValue01 = longValue01;
	}

	public Long getLongValue02() {
		return longValue02;
	}

	public void setLongValue02(Long longValue02) {
		this.longValue02 = longValue02;
	}

	public Double getDoubleValue01() {
		return doubleValue01;
	}

	public void setDoubleValue01(Double doubleValue01) {
		this.doubleValue01 = doubleValue01;
	}

	public Double getDoubleValue02() {
		return doubleValue02;
	}

	public void setDoubleValue02(Double doubleValue02) {
		this.doubleValue02 = doubleValue02;
	}

	public List<String> getDateTimeValue01List() {
		return dateTimeValue01List;
	}

	public void setDateTimeValue01List(List<String> dateTimeValue01List) {
		this.dateTimeValue01List = dateTimeValue01List;
	}

	public List<String> getDateTimeValue02List() {
		return dateTimeValue02List;
	}

	public void setDateTimeValue02List(List<String> dateTimeValue02List) {
		this.dateTimeValue02List = dateTimeValue02List;
	}

	public List<String> getDateTimeValue03List() {
		return dateTimeValue03List;
	}

	public void setDateTimeValue03List(List<String> dateTimeValue03List) {
		this.dateTimeValue03List = dateTimeValue03List;
	}

	public String getStringValue05() {
		return stringValue05;
	}

	public void setStringValue05(String stringValue05) {
		this.stringValue05 = stringValue05;
	}

	public String getStringValue06() {
		return stringValue06;
	}

	public void setStringValue06(String stringValue06) {
		this.stringValue06 = stringValue06;
	}

	public String getStringValue07() {
		return stringValue07;
	}

	public void setStringValue07(String stringValue07) {
		this.stringValue07 = stringValue07;
	}

	public String getStringValue08() {
		return stringValue08;
	}

	public void setStringValue08(String stringValue08) {
		this.stringValue08 = stringValue08;
	}

	public String getStringValue09() {
		return stringValue09;
	}

	public void setStringValue09(String stringValue09) {
		this.stringValue09 = stringValue09;
	}

	public String getStringValue10() {
		return stringValue10;
	}

	public void setStringValue10(String stringValue10) {
		this.stringValue10 = stringValue10;
	}

	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @return
	 * @throws Exception
	 */
	public QueryFilter getQueryFilter() throws Exception {
		QueryFilter queryFilter = new QueryFilter();
		queryFilter.setJoinType( "and" );

		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( "title", this.getTitle() ) );
		}

		//文档类型：全部 | 信息 | 数据
		if( StringUtils.isNotEmpty( this.getDocumentType())) {
			if( "信息".equals( this.getDocumentType() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "documentType", this.getDocumentType() ) );
			}else if( "数据".equals( this.getDocumentType() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "documentType", this.getDocumentType() ) );
			}
		}

		//是否置顶：ALL|TOP|UNTOP
		if( StringUtils.isNotEmpty( this.getTopFlag())) {
			if( "TOP".equals( this.getTopFlag() )) {
				queryFilter.addIsTrueTerm( new IsTrueTerm( "isTop" ) );
			}else if( "UNTOP".equals( this.getTopFlag() )) {
				queryFilter.addIsFalseTerm( new IsFalseTerm( "isTop" ) );
			}
		}

		if( StringUtils.isNotEmpty( this.getImportBatchName())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "importBatchName", this.getImportBatchName() ) );
		}

		if( ListTools.isNotEmpty( this.getAppAliasList())) {
			if( this.getAppAliasList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "appAlias", this.getAppAliasList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "appAlias", new ArrayList<>( this.getAppAliasList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getAppNameList())) {
			if( this.getAppNameList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "appName", this.getAppNameList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "appName", new ArrayList<>( this.getAppNameList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getAppIdList())) {
			if( this.getAppIdList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "appId", this.getAppIdList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "appId", new ArrayList<>( this.getAppIdList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getCategoryAliasList())) {
			if( this.getCategoryAliasList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "categoryAlias", this.getCategoryAliasList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "categoryAlias", new ArrayList<>( this.getCategoryAliasList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getCategoryNameList())) {
			if( this.getCategoryNameList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "categoryName", this.getCategoryNameList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "categoryName", new ArrayList<>( this.getCategoryNameList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getCategoryIdList())) {
			if( this.getCategoryIdList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "categoryId", this.getCategoryIdList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "categoryId", new ArrayList<>( this.getCategoryIdList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getCreatorList())) {
			if( this.getCreatorList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "creatorPerson", this.getCreatorList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "creatorPerson", new ArrayList<>( this.getCreatorList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getCreatorUnitNameList())) {
			if( this.getCreatorUnitNameList().size() == 1 ) { //如果只有一个值，就不要用IN，直接使用equals
				queryFilter.addEqualsTerm( new EqualsTerm( "creatorUnitName", this.getCreatorUnitNameList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "creatorUnitName", new ArrayList<>( this.getCreatorUnitNameList() ) ) );
			}
		}

		if( ListTools.isNotEmpty( this.getStatusList())) {
			if( this.getStatusList().size() == 1 ) {
				queryFilter.addEqualsTerm( new EqualsTerm( "docStatus", this.getStatusList().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "docStatus", new ArrayList<>( this.getStatusList())) );
			}
		}else {
			queryFilter.addEqualsTerm( new EqualsTerm( "docStatus", "published" ) );
		}

		if( ListTools.isNotEmpty( this.getCreateDateList())) {
			Date startDate = DateTools.parse(this.getCreateDateList().get(0));
			Date endDate = new Date();
			if(this.getCreateDateList().size() > 1){
				endDate = DateTools.parse(this.getCreateDateList().get(1));
			}
			queryFilter.addDateBetweenTerm( Document.createTime_FIELDNAME, startDate, endDate );
		}

		if( ListTools.isNotEmpty( this.getPublishDateList())) {
			Date startDate = DateTools.parse(this.getPublishDateList().get(0));
			Date endDate = new Date();
			if(this.getPublishDateList().size() > 1){
				endDate = DateTools.parse(this.getPublishDateList().get(1));
			}
			queryFilter.addDateBetweenTerm( Document.publishTime_FIELDNAME, startDate, endDate );
		}

		if( this.getMinutes() != null && this.getMinutes() > 0 ) {
			queryFilter.addDateBetweenTerm( "publishTime", new Date ( System.currentTimeMillis() - minutes*60*1000L ), new Date() );
		}

		if( StringUtils.isNotEmpty( this.getStringValue01())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue01_FIELDNAME, this.getStringValue01() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue02())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue02_FIELDNAME, this.getStringValue02() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue03())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue03_FIELDNAME, this.getStringValue03() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue04())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue04_FIELDNAME, this.getStringValue04() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue05())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue05_FIELDNAME, this.getStringValue05() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue06())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue06_FIELDNAME, this.getStringValue06() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue07())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue07_FIELDNAME, this.getStringValue07() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue08())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue08_FIELDNAME, this.getStringValue08() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue09())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue09_FIELDNAME, this.getStringValue09() ) );
		}

		if( StringUtils.isNotEmpty( this.getStringValue10())) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.stringValue10_FIELDNAME, this.getStringValue10() ) );
		}

		if( this.getLongValue01() != null) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.longValue01_FIELDNAME, this.getLongValue01() ) );
		}

		if( this.getLongValue02() != null) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.longValue02_FIELDNAME, this.getLongValue02() ) );
		}

		if( this.getDoubleValue01() != null) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.doubleValue01_FIELDNAME, this.getDoubleValue01() ) );
		}

		if( this.getDoubleValue02() != null) {
			queryFilter.addEqualsTerm( new EqualsTerm(Document.doubleValue02_FIELDNAME, this.getDoubleValue02() ) );
		}

		if( ListTools.isNotEmpty( this.getDateTimeValue01List())) {
			Date startDate = DateTools.parse(this.getDateTimeValue01List().get(0));
			Date endDate = new Date();
			if(this.getDateTimeValue01List().size() > 1){
				endDate = DateTools.parse(this.getDateTimeValue01List().get(1));
			}
			queryFilter.addDateBetweenTerm( Document.dateTimeValue01_FIELDNAME, startDate, endDate );
		}

		if( ListTools.isNotEmpty( this.getDateTimeValue02List())) {
			Date startDate = DateTools.parse(this.getDateTimeValue02List().get(0));
			Date endDate = new Date();
			if(this.getDateTimeValue02List().size() > 1){
				endDate = DateTools.parse(this.getDateTimeValue02List().get(1));
			}
			queryFilter.addDateBetweenTerm( Document.dateTimeValue02_FIELDNAME, startDate, endDate );
		}

		if( ListTools.isNotEmpty( this.getDateTimeValue03List())) {
			Date startDate = DateTools.parse(this.getDateTimeValue03List().get(0));
			Date endDate = new Date();
			if(this.getDateTimeValue03List().size() > 1){
				endDate = DateTools.parse(this.getDateTimeValue03List().get(1));
			}
			queryFilter.addDateBetweenTerm( Document.dateTimeValue03_FIELDNAME, startDate, endDate );
		}

		return queryFilter;
	}

}
