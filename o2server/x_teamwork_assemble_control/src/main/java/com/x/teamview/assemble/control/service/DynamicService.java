package com.x.teamview.assemble.control.service;

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
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.DynamicDetail;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskList;
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
	 * @param projectIds
	 * @param taskIds
	 * @return
	 * @throws Exception
	 */
	protected Long countWithFilter( EntityManagerContainer emc, List<String> projectIds, List<String> taskIds ) throws Exception {
		Business business = new Business( emc );
		//组织查询条件对象
		QueryFilter  queryFilter = new QueryFilter();
		if( ListTools.isNotEmpty( projectIds )) {
			queryFilter.addInTerm( new InTerm( "projectId", new ArrayList<Object>(projectIds) ) );
		}
		if( ListTools.isNotEmpty( taskIds )) {
			queryFilter.addInTerm( new InTerm( "taskIds", new ArrayList<Object>(taskIds) ) );
		}
		return business.dynamicFactory().countWithFilter(queryFilter);
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
	protected List<Dynamic> listWithFilterNext( EntityManagerContainer emc, Integer maxCount, String sequenceFieldValue, String orderField, String orderType, List<String> projectIds, List<String> taskIds ) throws Exception {
		Business business = new Business( emc );
		
		//组织查询条件对象
		QueryFilter  queryFilter = new QueryFilter();
		if( ListTools.isNotEmpty( projectIds )) {
			queryFilter.addInTerm( new InTerm( "projectId", new ArrayList<Object>(projectIds) ) );
		}
		if( ListTools.isNotEmpty( taskIds )) {
			queryFilter.addInTerm( new InTerm( "taskIds", new ArrayList<Object>(taskIds) ) );
		}
		
		return business.dynamicFactory().listWithFilter(maxCount, sequenceFieldValue, orderField, orderType, queryFilter);
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
		
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( Dynamic.createId() );
		}
		dynamic = emc.find( object.getId(), Dynamic.class );
		dynamicDetail = emc.find( object.getId(), DynamicDetail.class );
		
		emc.beginTransaction( Dynamic.class );
		emc.beginTransaction( DynamicDetail.class );
		
		if( dynamic == null ){ // 保存一个新的对象
			dynamic = new Dynamic();
			object.copyTo( dynamic );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				dynamic.setId( object.getId() );
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

	protected Dynamic getProjectDynamic( Project object, String optType, EffectivePerson effectivePerson ) {
		String viewUrl = null;
		String title =  "项目信息" + optType.toUpperCase();
		String target = null; 
		String description = effectivePerson.getDistinguishedName() + optType.toUpperCase() + "了一个项目信息：" + object.getTitle();
		
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType("PROJECT");
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( object.getTitle() );
		dynamic.setTaskId( null );
		dynamic.setTaskTitle( null );
		
		dynamic.setBundle( object.getId() );
		dynamic.setTitle( title );		
		dynamic.setTarget( target );
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		return dynamic;
	}
	
	protected Dynamic getProjectExtFieldReleDynamic( ProjectExtFieldRele object, String optType, EffectivePerson effectivePerson ) {
		String viewUrl = null;
		String title =  "项目扩展属性" + optType.toUpperCase();
		String target = null; 
		String description = effectivePerson.getDistinguishedName() + optType.toUpperCase() + "了一个项目扩展属性：" + object.getDisplayName();
		
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType("PROJECTEXTFIELD");
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( object.getDisplayName() );
		dynamic.setTaskId( null );
		dynamic.setTaskTitle( null );
		
		dynamic.setBundle( object.getId() );
		dynamic.setTitle( title );		
		dynamic.setTarget( target );
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		return dynamic;
	}
	
	protected Dynamic getProjectDynamic( TaskList object, String optType, EffectivePerson effectivePerson ) {
		String viewUrl = null;
		String title =  "工作任务列表信息" + optType.toUpperCase();
		String target = null; 
		String description = effectivePerson.getDistinguishedName() + optType.toUpperCase() + "了一个工作任务列表信息：" + object.getName();
		
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType("TASKLIST");
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		
		dynamic.setProjectId( object.getProject() );
		dynamic.setProjectTitle( null );
		dynamic.setTaskId( null );
		dynamic.setTaskTitle( null );
		
		dynamic.setBundle( object.getId() );
		dynamic.setTitle( title );		
		dynamic.setTarget( target );
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		return dynamic;
	}

	protected Dynamic getTaskDynamic( Task object, String optType, EffectivePerson effectivePerson ) {
		String viewUrl = null;
		String title =  "工作任务信息" + optType.toUpperCase();
		String target = object.getExecutor(); 
		String description = effectivePerson.getDistinguishedName() + optType.toUpperCase() + "了一个任务信息：" + object.getName();
		
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType("TASK");
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		
		dynamic.setProjectId( object.getProject() );
		dynamic.setProjectTitle( "" );
		dynamic.setTaskId( object.getId() );
		dynamic.setTaskTitle( object.getName() );
		
		dynamic.setBundle( object.getId() );
		dynamic.setTitle( title );
		dynamic.setTarget( target );
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		
		return dynamic;
	}
	protected Dynamic getProjectGroupDynamic( ProjectGroup object, String optType, EffectivePerson effectivePerson ) {
		String viewUrl = null;
		String title =  "项目组信息" + optType.toUpperCase();
		String target = null; 
		String description = effectivePerson.getDistinguishedName() + optType.toUpperCase() + "了一个项目组信息：" + object.getName();
		
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType("PROJECTGROUP");
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		
		dynamic.setProjectId( object.getId() );
		dynamic.setProjectTitle( object.getName() );
		dynamic.setTaskId( null );
		dynamic.setTaskTitle( null );
		
		dynamic.setBundle( object.getId() );
		dynamic.setTitle( title );		
		dynamic.setTarget( target );
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		return dynamic;
	}
	
	protected Dynamic getChatDynamic( Chat object, String optType, EffectivePerson effectivePerson ) {
		String viewUrl = null;
		String title =  "工作交流" + optType.toUpperCase();
		String target = null; 
		String description = effectivePerson.getDistinguishedName() + optType.toUpperCase() + "了一个工作交流信息";
		
		Dynamic dynamic = new Dynamic();
		dynamic.setObjectType("CHAT");
		dynamic.setOperator( effectivePerson.getDistinguishedName() );		
		dynamic.setOptType(optType);
		dynamic.setOptTime( new Date() );
		dynamic.setDateTimeStr( DateOperation.getNowDateTime());		
		
		dynamic.setProjectId( object.getProjectId() );
		dynamic.setProjectTitle( object.getProjectTitle() );
		dynamic.setTaskId( object.getTaskId()  );
		dynamic.setTaskTitle( object.getTaskTitle() );
		
		dynamic.setBundle( object.getId() );
		dynamic.setTitle( title );		
		dynamic.setTarget( target );
		dynamic.setDescription( description );
		dynamic.setViewUrl(viewUrl);
		return dynamic;
	}
}
