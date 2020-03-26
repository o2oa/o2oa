package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.DynamicDetail;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;

/**
 * 对项目信息查询的服务
 * 
 * @author O2LEE
 */
class DynamicService {

	/**
	 * 根据项目的标识查询项目的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected Dynamic get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.dynamicFactory().get( flag );
	}
	
	/**
	 * 根据项目的标识查询项目的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected DynamicDetail getDetail(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.dynamicFactory().getDetail( flag );
	}

	/**
	 * 根据过滤条件查询符合要求的项目信息数量
	 * @param emc
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	protected Long countWithFilter( EntityManagerContainer emc, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.dynamicFactory().countWithFilter( queryFilter );
	}
	
	/**
	 * 根据过滤条件查询符合要求的项目信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param projectIds
	 * @param taskIds
	 * @return
	 * @throws Exception
	 */
	protected List<Dynamic> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, List<String> projectIds, List<String> taskIds ) throws Exception {
		Business business = new Business( emc );
		
		//组织查询条件对象
		QueryFilter  queryFilter = new QueryFilter();
		if( ListTools.isNotEmpty( projectIds )) {
			queryFilter.addInTerm( new InTerm( "projectId", new ArrayList<Object>(projectIds) ) );
		}
		if( ListTools.isNotEmpty( taskIds )) {
			queryFilter.addInTerm( new InTerm( "taskIds", new ArrayList<Object>(taskIds) ) );
		}
		
		return business.dynamicFactory().listWithFilter(maxCount, orderField, orderType, queryFilter);
	}
	
	/**
	 * 根据过滤条件查询符合要求的项目信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param projectIds
	 * @param taskIds
	 * @return
	 * @throws Exception
	 */
	protected List<Dynamic> listWithFilterNext( EntityManagerContainer emc, Integer maxCount, String sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );		
		return business.dynamicFactory().listWithFilter( maxCount, sequenceFieldValue, orderField, orderType, queryFilter );
	}

	/**
	 * 向数据库持久化动态信息
	 * @param emc
	 * @param dynamic
	 * @return
	 * @throws Exception 
	 */
	protected Dynamic save( EntityManagerContainer emc, Dynamic object, String content ) throws Exception {
		Dynamic dynamic = null;
		DynamicDetail dynamicDetail = null;
		Project project = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Dynamic.createId() );
		}
		dynamic = emc.find( object.getId(), Dynamic.class );
		dynamicDetail = emc.find( object.getId(), DynamicDetail.class );
		project = emc.find( object.getProjectId() , Project.class );
		
		emc.beginTransaction( Dynamic.class );
		emc.beginTransaction( DynamicDetail.class );
		if( project != null && StringUtils.isEmpty( object.getProjectTitle() ) ) {
			object.setProjectTitle( project.getTitle() );
		}
		String lobValue = null;
		if( object.getCreateTime() == null ) {
			object.setCreateTime( new Date());
		}
		if( object.getUpdateTime() == null ) {
			object.setUpdateTime( new Date());
		}
		if( dynamic == null ){ // 保存一个新的对象
			dynamic = new Dynamic();
			object.copyTo( dynamic );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				dynamic.setId( object.getId() );
			}
			lobValue = dynamic.getDescription();
			if( dynamic.getDescription().length() > 80 ) {
				dynamic.setDescription( dynamic.getDescription().substring(0, 80) + "...");
			}
			emc.persist( dynamic, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			object.copyTo( dynamic, JpaObject.FieldsUnmodify  );
			emc.check( dynamic, CheckPersistType.all );	
		}		
		if( dynamicDetail == null ){ 
			dynamicDetail = new DynamicDetail();
			dynamicDetail.setId( dynamic.getId() );
			dynamicDetail.setContent(content);
			emc.persist( dynamicDetail, CheckPersistType.all);
		}else {
			dynamicDetail.setContent(content);
			if( StringUtils.isEmpty( dynamicDetail.getContent() )) {
				dynamicDetail.setContent( lobValue );
			}
			emc.check( dynamicDetail, CheckPersistType.all );	
		}		
		emc.commit();
		return dynamic;
	}

	/**
	 * 根据项目标识删除项目信息
	 * @param emc
	 * @param flag 主要是ID
	 * @throws Exception 
	 */
	protected void delete(EntityManagerContainer emc, String flag) throws Exception {
		Dynamic dynamic = emc.find( flag, Dynamic.class );
		if( dynamic != null ) {
			//这里要先递归删除所有的任务信息
			emc.beginTransaction( Dynamic.class );
			emc.remove( dynamic , CheckRemoveType.all );
			emc.commit();
		}
	}

	/**
	 * 根据参数组织一个简单的通用操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewSimpleDynamic( String objectType, String title, String description, String viewUrl, String optType, EffectivePerson effectivePerson, Boolean personal) {
		if( StringUtils.isNotEmpty(description) && description.length() > 70 ) {
			description = description.substring( 0 , 70 ) + "..." ;
		}
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType( objectType );
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		dynamic.setTitle( title );		
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		dynamic.setPersonal( personal );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的Project操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, Project object,  EffectivePerson effectivePerson, Boolean personal) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( object.getTitle() );
		dynamic.setTaskId( ""  );
		dynamic.setTaskTitle( null );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( object.getExecutor() );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的ProjectExtFieldRele操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, ProjectExtFieldRele object,  EffectivePerson effectivePerson, Boolean personal ) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( object.getDisplayName() + "(" + object.getExtFieldName() + ")" );
		dynamic.setTaskId( ""  );
		dynamic.setTaskTitle( null );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( effectivePerson.getDistinguishedName() );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的TaskList操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, TaskList object,  EffectivePerson effectivePerson, Boolean personal ) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( null );
		dynamic.setTaskId( ""  );
		dynamic.setTaskTitle( null );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( effectivePerson.getDistinguishedName() );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的Task操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, Task object,  EffectivePerson effectivePerson, Boolean personal ) {
		if( description.length() > 70 ) {
			description = description.substring( 0, 60 )+ "...";
		}
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( object.getProjectName() );
		dynamic.setTaskId( object.getId()  );
		dynamic.setTaskTitle( object.getName() );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( object.getExecutor() );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的ProjectGroup操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, ProjectGroup object,  EffectivePerson effectivePerson, Boolean personal ) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( "" );
		dynamic.setProjectTitle(null );
		dynamic.setTaskId( ""  );
		dynamic.setTaskTitle( null );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( effectivePerson.getDistinguishedName() );
		return dynamic;
	}

	/**
	 * 根据参数组织一个新的Attachment操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, Attachment object,  EffectivePerson effectivePerson, Boolean personal) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getProjectId() );
		dynamic.setProjectTitle( null );
		dynamic.setTaskId( object.getTaskId()  );
		dynamic.setTaskTitle( null );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( effectivePerson.getDistinguishedName() );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的TaskTag操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, TaskTag object,  EffectivePerson effectivePerson, Boolean personal) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getProject() );
		dynamic.setProjectTitle( null );
		dynamic.setTaskId( ""  );
		dynamic.setTaskTitle( null );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( effectivePerson.getDistinguishedName() );
		return dynamic;
	}
	
	/**
	 * 根据参数组织一个新的Chat操作动态信息
	 * @param objectType
	 * @param title
	 * @param description
	 * @param viewUrl
	 * @param optType
	 * @param object
	 * @param effectivePerson
	 * @param personal
	 * @return
	 */
	private Dynamic composeNewDynamic( String objectType, String title, String description, String viewUrl, String optType, Chat object,  EffectivePerson effectivePerson, Boolean personal) {
		Dynamic dynamic = composeNewSimpleDynamic(objectType, title, description, viewUrl, optType, effectivePerson, personal );
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( "" );
		dynamic.setTaskId( object.getTaskId()  );
		dynamic.setTaskTitle( "" );
		dynamic.setBundle( object.getId() );
		dynamic.setTarget( object.getTarget() );
		return dynamic;
	}
	
	/**
	 * 保存项目创建或者更新动态信息
	 * @param object_old
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected List<Dynamic> getProjectSaveDynamic( Project object_old, Project object, EffectivePerson effectivePerson ) {
		List<Dynamic> dynamics = new ArrayList<>();
		String objectType =  "PROJECT";
		String viewUrl = null;
		String title =  null;
		String optType = "UPDATE_PROJECT";
		String description = null;
		if( object_old != null ) {
			if( !object_old.getTitle().equalsIgnoreCase( object.getTitle() )) { //变更了名称
				title =  "项目信息标题变更";
				optType = "UPDATE_TITLE";
				description = effectivePerson.getName() + "变更了项目信息的标题为：" + object.getTitle();
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
			if( !object_old.getExecutor().equalsIgnoreCase( object.getExecutor() )) {//变更了负责人
				title =  "项目负责人变更";
				optType = "UPDATE_EXECUTOR";
				if( StringUtils.isNotEmpty(  object.getExecutor() ) &&  object.getExecutor().split( "@" ).length > 1 ) {
					description = effectivePerson.getName() + "变更了项目负责人为：" + object.getExecutor().split( "@" )[0] + "。";					
				}else {
					description = effectivePerson.getName() + "变更了项目负责人为：" + object.getExecutor() + "。";
				}
				
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
		}else {//创建项目
			title =  "项目信息创建";
			optType = "CREATE";
			description = effectivePerson.getName() + "创建了新的项目信息：" + object.getTitle() + "。";
			dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
		}
		return dynamics;
	}
	
	/**
	 * 组织一个更新项目图片操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getProjectIconSaveDynamic( Project object, EffectivePerson effectivePerson ) {
		String objectType =  "PROJECT";
		String title =  "项目信息图标更新";
		String viewUrl = null;
		String optType =  "UPDATE_ICON";
		String description = effectivePerson.getName() +"更新了项目信息图标。";
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}
	
	/**
	 * 组织一个项目删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getProjectDeleteDynamic( Project object, EffectivePerson effectivePerson ) {
		String objectType =  "PROJECT";
		String title =  "项目信息删除";
		String viewUrl = null;
		String optType =  "DELETE";
		String description = effectivePerson.getName() +"删除了项目信息：" + object.getTitle();
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}
	
	/**
	 * 保存和根据项目组信息操作动态
	 * @param object_old
	 * @param object
	 * @param optType
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getProjectGroupSaveDynamic( ProjectGroup object_old, ProjectGroup object, EffectivePerson effectivePerson ) {
		String objectType =  "PROJECT_GROUP";
		String optType =  "UPDATE_PROJECT_GROUP";
		String viewUrl = null;
		String title =  "项目组信息" + optType.toUpperCase();
		String description = effectivePerson.getDistinguishedName() + "添加了了一个项目组信息：" + object.getName();
		if( object_old != null ) {
			if( !object_old.getName().equalsIgnoreCase( object.getName() )) { //变更了显示名称
				title =  "变更项目组名称";
				optType = "UPDATE_NAME";
				description = effectivePerson.getName() + "变更了项目组名称为：" + object.getName();
				return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true );
			}
		}else {
			title =  "添加项目组信息";
			optType = "CREATE";
			description = effectivePerson.getName() + "添加了新的项目组：" + object.getName();
			return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true );
		}
		return null;
	}
	
	/**
	 * 组织一个项目组删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getProjectGroupDeleteDynamic( ProjectGroup object, EffectivePerson effectivePerson ) {
		String objectType =  "PROJECT_GROUP";
		String title =  "项目组信息删除";
		String viewUrl = null;
		String optType =  "DELETE";
		String description = effectivePerson.getName() +"删除了项目组信息：" + object.getName();
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true );
	}
	
	/**
	 * 组织项目扩展信息配置保存操作动态
	 * @param object_old
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getProjectSaveExtFieldReleDynamic( ProjectExtFieldRele object_old, ProjectExtFieldRele object, EffectivePerson effectivePerson ) {
		String objectType =  "PROJECT_EXTFIELD_RELE";
		String optType =  "UPDATE_EXTFIELD_RELE";
		String title =  "保存项目扩展属性";
		String viewUrl = null;
		String description = null;
		if( object_old != null ) {
			if( !object_old.getDisplayName().equalsIgnoreCase( object.getDisplayName() )) { //变更了显示名称
				title =  "变更项目扩展属性显示名称";
				optType = "UPDATE_DISPLAYNAME";
				description = effectivePerson.getName() + "变更了项目扩展属性"+object.getExtFieldName()+"的显示名称为：" + object.getDisplayName();
				return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
			}
		}else {
			title =  "添加项目扩展属性信息";
			optType = "CREATE";
			description = effectivePerson.getName() + "添加了新的项目扩展属性：" + object.getDisplayName() + "("+object.getExtFieldName()+")。";
			return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
		}
		return null;
	}
	
	/**
	 * 组织项目扩展信息配置删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getProjectDeleteExtFieldReleDynamic( ProjectExtFieldRele object, EffectivePerson effectivePerson ) {
		String objectType =  "PROJECT_EXTFIELD_RELE";
		String title =  "项目扩展属性信息删除";
		String viewUrl = null;
		String optType =  "DELETE";
		String description = effectivePerson.getName() +"删除了项目扩展属性：" + object.getDisplayName() + "("+object.getExtFieldName()+")。";
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}
	
	/**
	 * 组织项目工作任务列表保存操作动态
	 * @param object_old
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getTaskListSaveDynamic( TaskList object_old, TaskList object, EffectivePerson effectivePerson ) {
		String objectType =  "TASK_LIST";
		String optType =  "TASK_LIST";
		String viewUrl = null;
		String title =  "保存工作任务列表信息：" + object.getName();
		String description = effectivePerson.getDistinguishedName() + "保存了一个工作任务列表信息：" + object.getName();
		
		if( object_old != null ) {
			if( !object_old.getName().equalsIgnoreCase( object.getName() )) { //变更了列表名称
				title =  "变更工作任务列表名称";
				optType = "UPDATE_NAME";
				description = effectivePerson.getName() + "变更了工作任务列表名称为："+object.getName();
				return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true );
			}
		}else {
			title =  "添加工作任务列表信息";
			optType = "CREATE";
			description = effectivePerson.getName() + "添加了新的工作任务列表：" + object.getName() ;
			return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true );
		}
		return null;
	}
	
	/**
	 * 组织项目工作任务列表删除操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getTaskListDeleteDynamic( TaskList object, EffectivePerson effectivePerson ) {
		String objectType =  "TASK_LIST";
		String title =  "工作任务列表信息删除";
		String viewUrl = null;
		String optType =  "DELETE";
		String description = effectivePerson.getName() +"删除了工作任务列表：" + object.getName();
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true );
	}
	
	/**
	 * 删除工作任务信息操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getTaskDeleteDynamic( Task object, EffectivePerson effectivePerson ) {
		String objectType =  "TASK";
		String title =  "工作任务信息删除";
		String viewUrl = null;
		String optType =  "DELETE";
		String description = effectivePerson.getName() +"删除了工作任务信息：" + object.getName();
		Dynamic dynamic =  composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
		dynamic.setTarget( object.getExecutor() );		
		return dynamic;
	}
	
	/**
	 * 保存和更新任务信息操作动态
	 * @param object_old
	 * @param object
	 * @param newDetail 
	 * @param oldDetail 
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	protected List<Dynamic> getTaskDynamic( Task object_old, Task object,  TaskDetail oldDetail, TaskDetail newDetail, EffectivePerson effectivePerson ) throws Exception {
		String objectType =  "TASK";
		String optType =  "TASK_INFO";
		String title =  "保存工作任务信息";
		List<Dynamic> dynamics = new ArrayList<>();
		String viewUrl = null;
		String description = null;
		
		if( object_old != null ) {
			if( !object_old.getName().equalsIgnoreCase( object.getName() )) {
				optType =  "UPDATE_NAME";
				title =  "工作任务标题变更";
				description = effectivePerson.getName() + "变更了任务信息：" + object.getName() + "的标题。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
			if( !object_old.getExecutor().equalsIgnoreCase( object.getExecutor() )) {
				optType =  "UPDATE_EXECUTOR";
				title =  "工作任务负责人变更";
				description = effectivePerson.getName() + "变更了任务信息的负责人为：" + object.getExecutor() + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
			if( !object_old.getWorkStatus().equalsIgnoreCase( object.getWorkStatus() )) {
				optType =  "UPDATE_STATUS";
				title =  "工作任务状态变更";
				description = effectivePerson.getName() + "变更了任务信息的状态为：" + object.getWorkStatus() + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
			if( object_old.getStartTime().getTime() != object.getStartTime().getTime() 
					|| object_old.getEndTime().getTime() != object.getEndTime().getTime()  ) {
				optType =  "UPDATE_TIME";
				title =  "工作任务启始时间变更";
				description = effectivePerson.getName() + "变更了任务信息的启始时间为：" + 
				DateOperation.getDateStringFromDate( object_old.getStartTime(), "yyyy-MM-dd HH:mm:ss") + "到" + 
				DateOperation.getDateStringFromDate( object_old.getEndTime(), "yyyy-MM-dd HH:mm:ss") ;
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl,optType, object, effectivePerson, false ) );
			}
			if( object_old.getProgress() != object.getProgress()) {
				optType =  "UPDATE_PROGRESS";
				title =  "工作任务进度变更";
				description = effectivePerson.getName() + "变更了任务信息的工作进度为：" + object.getProgress() + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
			if( !StringUtils.equals( object_old.getPriority(), object.getPriority())) {
				optType =  "UPDATE_PRIORITY";
				title =  "工作任务紧急程度变更";
				description = effectivePerson.getName() + "变更了任务信息的紧急程度为：" + object.getPriority() + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
			if(  !StringUtils.equals( oldDetail.getDescription(), newDetail.getDescription()) ) {
				if( StringUtils.isEmpty( oldDetail.getDescription() ) && StringUtils.isNotEmpty( newDetail.getDescription() )) {
					optType =  "ADD_DESCRIPTION";
					title =  "工作任务备注信息添加";
					description = effectivePerson.getName() + "添加了任务的备注信息。";
				}else {
					if( StringUtils.isNotEmpty( newDetail.getDescription() ) ) {
						title =  "工作任务备注信息变更";
						optType =  "UPDATE_DESCRIPTION";
						description = effectivePerson.getName() + "变更了任务的备注信息。";
					}else {
						title =  "工作任务备注信息清空";
						optType =  "REMOVE_DESCRIPTION";
						description = effectivePerson.getName() + "清空了任务的备注信息。";
					}
				}
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
		}else {
			optType =  "CREATE";
			title =  "工作任务信息新增";
			description = effectivePerson.getName() + "新增了新的任务信息：" + object.getName() + "。";
			dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
		}
		return dynamics;
	}
	
	public List<Dynamic> getTaskPropertyUpdateDynamic( Task task, String dynamicTitle, String dynamicOptType, String dynamicDescription, EffectivePerson effectivePerson) {
		List<Dynamic> dynamics = new ArrayList<>();
		String objectType =  "TASK";
		String viewUrl = null;
		if( StringUtils.isEmpty( dynamicOptType )) {
			dynamicOptType =  "TASK_INFO";
		}
		if( StringUtils.isEmpty( dynamicTitle )) {
			dynamicTitle =  "工作任务信息更新";
		}
		if( StringUtils.isEmpty( dynamicDescription )) {
			dynamicDescription = effectivePerson.getName() + "变更了任务信息：" + task.getName() + "的信息。";
		}
		if( StringUtils.isNotEmpty( dynamicTitle ) && task != null ) {
			dynamics.add( composeNewDynamic( objectType, dynamicTitle, dynamicDescription, viewUrl, dynamicOptType, task, effectivePerson, false ) );
		}
		
		return dynamics;
	}
	
	/**
	 * 更新工作任务管理者信息操作动态
	 * @param task
	 * @param addManagers
	 * @param removeManagers
	 * @param effectivePerson
	 * @return
	 */
	public List<Dynamic> getTaskManagerDynamic( Task object, List<String> addManagers, List<String> removeManagers, EffectivePerson effectivePerson ) {
		String objectType =  "TASK";
		String optType =  "UPDATE_MANAGER";
		String title =  "更新工作任务管理者信息";
		List<Dynamic> dynamics = new ArrayList<>();
		String viewUrl = null;
		String description = null;
		if( ListTools.isNotEmpty( addManagers )) {
			for( String manager : addManagers ) {
				optType =  "ADD_MANAGER";
				title =  "添加工作任务管理者";
				description = effectivePerson.getName() + "为工作" +object.getName() + "添加了管理者：" + manager.split("@")[0] + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
		}
		if( ListTools.isNotEmpty( removeManagers )) {
			for( String manager : removeManagers ) {
				optType =  "REMOVE_MANAGER";
				title =  "删除工作任务管理者";
				description = effectivePerson.getName() + "从工作" +object.getName() + "的管理者中删除了：" + manager.split("@")[0] + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
		}		
		return dynamics;
	}
	
	/**
	 * 更新工作任务参与者操作动态
	 * @param task
	 * @param addParticipants
	 * @param removeParticipants
	 * @param effectivePerson
	 * @return
	 */
	public List<Dynamic> getTaskParticipantsDynamic( Task object, List<String> addParticipants, List<String> removeParticipants, EffectivePerson effectivePerson ) {
		String objectType =  "TASK";
		String optType =  "UPDATE_PARTICIPANTS";
		String title =  "更新工作任务参与者信息";
		List<Dynamic> dynamics = new ArrayList<>();
		String viewUrl = null;
		String description = null;
		if( ListTools.isNotEmpty( addParticipants )) {
			for( String participant : addParticipants ) {
				optType =  "ADD_PARTICIPANTS";
				title =  "添加工作任务参与者";
				description = effectivePerson.getName() + "为工作" +object.getName() + "添加了参与者：" + participant.split("@")[0] + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
		}
		if( ListTools.isNotEmpty( removeParticipants)) {
			for( String participant : removeParticipants ) {
				optType =  "REMOVE_PARTICIPANTS";
				title =  "删除工作任务参与者";
				description = effectivePerson.getName() + "从工作" +object.getName() + "的参与者中删除了：" + participant.split("@")[0] + "。";
				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false ) );
			}
		}
		return dynamics;
	}
	
	public Dynamic getTaskSplitDynamic(Task parentTask, Task task, EffectivePerson effectivePerson) {
		String objectType =  "TASK";
		String title =  "工作任务分解";
		String viewUrl = task.getId();
		String optType =  "SPLIT";
		String description = effectivePerson.getName() +"为工作添加了一个子任务：" + task.getName();
		Dynamic dynamic =  composeNewDynamic( objectType, title, description, viewUrl, optType, parentTask, effectivePerson, false );
		dynamic.setTarget( parentTask.getExecutor() );		
		return dynamic;
	}
	
	public Dynamic subTaskDeleteDynamic(Task parentTask, Task task, EffectivePerson effectivePerson) {
		String objectType =  "TASK";
		String title =  "删除下级工作";
		String viewUrl = task.getId();
		String optType =  "DELETE_SUBTASK";
		String description = effectivePerson.getName() +"删除了一个子任务：" + task.getName();
		Dynamic dynamic =  composeNewDynamic( objectType, title, description, viewUrl, optType, parentTask, effectivePerson, false );
		dynamic.setTarget( parentTask.getExecutor() );
		return dynamic;
	}
	
//	/**
//	 * 更新任务标签信息操作动态
//	 * @param task
//	 * @param addTags
//	 * @param removeTags
//	 * @param effectivePerson
//	 * @return
//	 */
//	public List<Dynamic> getTaskTagsDynamic( Task object, List<String> addTags, List<String> removeTags, EffectivePerson effectivePerson ) {
//		String objectType =  "TASK";
//		String optType =  "UPDATE_TAGS";
//		String title =  "更新工作任务参与者信息";
//		List<Dynamic> dynamics = new ArrayList<>();
//		String viewUrl = null;
//		String description = null;
//		if( ListTools.isNotEmpty( addTags )) {
//			for( String tag : addTags ) {
//				optType =  "ADD_TAGS";
//				title =  "添加工作任务标签";
//				description = effectivePerson.getName() + "为工作" +object.getName() + "添加了标签：" + tag + "。";
//				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true ) );
//			}
//		}
//		if( ListTools.isNotEmpty( removeTags )) {
//			for( String tag : removeTags ) {
//				optType =  "REMOVE_TAGS";
//				title =  "删除工作任务标签";
//				description = effectivePerson.getName() + "从工作" +object.getName() + "的标签中删除了：" + tag + "。";
//				dynamics.add( composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, true ) );
//			}
//		}		
//		return dynamics;
//	}
	
	/**
	 * 创建工作任务标签信息操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getTagCreateDynamic( TaskTag object, EffectivePerson effectivePerson ) {
		String objectType =  "TAG";
		String optType =  "CREATE";
		String title =  "创建工作标签信息";
		String viewUrl = null;		
		String description = effectivePerson.getName() +"创建了工作任务标签信息：" + object.getTag();
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}
	
	/**
	 * 删除工作任务标签信息操作动态
	 * @param object
	 * @param effectivePerson
	 * @return
	 */
	protected Dynamic getTagDeleteDynamic( TaskTag object, EffectivePerson effectivePerson ) {
		String objectType =  "TAG";
		String optType =  "DELETE";
		String title =  "工作标签信息删除";
		String viewUrl = null;		
		String description = effectivePerson.getName() +"删除了工作任务标签信息：" + object.getTag();
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}
	
	/**
	 * 工作任务附件上传操作动态信息
	 * @param attachment
	 * @param effectivePerson
	 * @return
	 */
	public Dynamic getAttachmentUploadDynamic( Attachment object, EffectivePerson effectivePerson) {
		String objectType =  "ATTACHMENT";
		String optType =  "UPLOAD";
		String viewUrl = null;
		String title =  "上传附件";
		String description = effectivePerson.getName() + "上传了附件：" + object.getName() ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}
	
	/**
	 * 工作任务附件下载操作动态信息
	 * @param attachment
	 * @param effectivePerson
	 * @return
	 */
	public Dynamic getAttachmentDownloadDynamic( Attachment object, EffectivePerson effectivePerson) {
		String objectType =  "ATTACHMENT";
		String optType =  "DOWNLOAD";
		String viewUrl = null;
		String title =  "下载附件";
		String description = effectivePerson.getName() + "下载了附件：" + object.getName() ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}

	/**
	 * 工作任务附件删除操作动态信息
	 * @param attachment
	 * @param effectivePerson
	 * @return
	 */
	public Dynamic getAttachmentDeleteDynamic( Attachment object, EffectivePerson effectivePerson) {
		String objectType =  "ATTACHMENT";
		String optType =  "DELETE";
		String viewUrl = null;
		String title =  "删除附件";
		String description = effectivePerson.getName() + "删除了附件：" + object.getName() ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}

	public Dynamic getChatPublishDynamic(Chat object, EffectivePerson effectivePerson) {
		String objectType =  "CHAT";
		String optType =  "PUBLISH";
		String title =  "工作任务评论信息发布";
		String viewUrl = null;
		String description = effectivePerson.getName() +"发表了工作任务评论信息。" ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}

	public Dynamic getChatDeleteDynamic( Chat object, EffectivePerson effectivePerson) {
		String objectType =  "CHAT";
		String optType =  "DELETE";
		String title =  "工作任务评论信息删除";
		String viewUrl = null;
		String description = effectivePerson.getName() +"删除了工作任务评论信息。" ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, object, effectivePerson, false );
	}

	public Dynamic getAddTaskTagReleDynamic(Task task, TaskTag taskTag, EffectivePerson effectivePerson) {
		String objectType =  "TASK_TAG";
		String optType =  "ADD";
		String title =  "工作任务添加标签";
		String viewUrl = null;
		String description = effectivePerson.getName() +"为工作任务添加了标签：" + taskTag.getTag() ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, task, effectivePerson, false );
	}
	
	public Dynamic geRemoveTaskTagReleDynamic(Task task, TaskTag taskTag, EffectivePerson effectivePerson) {
		String objectType =  "TASK_TAG";
		String optType =  "REMOVE";
		String title =  "工作任务移除标签";
		String viewUrl = null;
		String description = effectivePerson.getName() +"为工作任务移除了标签：" + taskTag.getTag() ;
		return composeNewDynamic( objectType, title, description, viewUrl, optType, task, effectivePerson, false );
	}
}
