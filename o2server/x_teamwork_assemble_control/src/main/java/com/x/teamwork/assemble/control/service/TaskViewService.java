package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.TaskView;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

class TaskViewService {

	/**
	 * 根据工作任务视图的标识查询工作任务视图的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected TaskView get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.taskViewFactory().get( flag );
	}
	
	/**
	 * 根据用户和项目ID查询工作任务视图
	 * @param emc
	 * @param person
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	protected List<TaskView> listViewWithPersonAndProject( EntityManagerContainer emc,  String person, String projectId ) throws Exception {
		Business business = new Business( emc );	
		return business.taskViewFactory().listWithPersonAndProject( person, projectId );
	}

	/**
	 * 向数据库持久化工作任务视图列表信息
	 * @param emc
	 * @param taskView
	 * @return
	 * @throws Exception 
	 */
	protected TaskView save( EntityManagerContainer emc, TaskView object ) throws Exception {
		TaskView taskView = null;

		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( TaskView.createId() );
		}
		taskView = emc.find( object.getId(), TaskView.class );
		emc.beginTransaction( TaskView.class );		
		if( taskView == null ){ // 保存一个新的对象
			taskView = new TaskView();
			object.copyTo( taskView );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				taskView.setId( object.getId() );
			}			
			emc.persist( taskView, CheckPersistType.all);
		}else{ //对象已经存在，更新对象信息
			if( StringUtils.isNotEmpty( taskView.getCreatorPerson() )) {
				object.setCreatorPerson( taskView.getCreatorPerson() );
			}
			object.copyTo( taskView, JpaObject.FieldsUnmodify  );
			emc.check( taskView, CheckPersistType.all );	
		}
		emc.commit();
		return taskView;
	}
	
	/**
	 * 根据工作任务视图标识删除工作任务视图信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete( EntityManagerContainer emc, String id ) throws Exception {
		emc.beginTransaction( TaskView.class );
		TaskView taskView = emc.find( id, TaskView.class );
		if( taskView != null ) {
			emc.remove( taskView , CheckRemoveType.all );
		}
		emc.commit();
	}

	/**
	 * 为用户在当前项目创建一组默认的工作任务视图
	 * 所有任务、我负责的任务、未完成的任务、已完成的任务、逾期的任务
	 * @param person
	 * @param project
	 * @return
	 * @throws Exception 
	 */
	public List<TaskView> createDefaultTaskViewForPerson(EntityManagerContainer emc, String person, String project ) throws Exception {
		List<TaskView> viewList = new ArrayList<>();
		TaskView view = null;		
		
		view = new TaskView();
		view.setId( TaskView.createId() );
		view.setName( "所有工作" );
		view.setProject(project);
		view.setOwner( person );
		view.setCreatorPerson( "System" );		
		view.setOrder(1);
		view.setMemo( "默认视图-所有工作" );
		emc.beginTransaction( TaskView.class );
		emc.persist( view, CheckPersistType.all );
		emc.commit();
		viewList.add(view);
		
		view = new TaskView();		
		view.setId( TaskView.createId() );
		view.setName( "我负责的任务" );
		view.setProject(project);
		view.setOwner( person );
		view.setCreatorPerson( "System" );		
		view.setOrder(2);
		view.setMemo( "默认视图-我负责的任务" );
		view.setIsExcutor( true );
		emc.beginTransaction( TaskView.class );
		emc.persist( view, CheckPersistType.all );
		emc.commit();
		viewList.add(view);
		
		view = new TaskView();		
		view.setId( TaskView.createId() );
		view.setName( "未完成的任务" );
		view.setProject(project);
		view.setOwner( person );
		view.setCreatorPerson( "System" );		
		view.setOrder(3);
		view.setMemo( "默认视图-未完成的任务" );
		view.setWorkCompleted( 0 );
		emc.beginTransaction( TaskView.class );
		emc.persist( view, CheckPersistType.all );
		emc.commit();
		viewList.add(view);
		
		view = new TaskView();		
		view.setId( TaskView.createId() );
		view.setName( "已完成的任务" );
		view.setProject(project);
		view.setOwner( person );
		view.setCreatorPerson( "System" );		
		view.setOrder(4);
		view.setMemo( "默认视图-已完成的任务" );
		view.setWorkCompleted( 1 );
		emc.beginTransaction( TaskView.class );
		emc.persist( view, CheckPersistType.all );
		emc.commit();
		viewList.add(view);
		
		view = new TaskView();		
		view.setId( TaskView.createId() );
		view.setName( "逾期的任务" );
		view.setProject(project);
		view.setOwner( person );
		view.setCreatorPerson( "System" );		
		view.setOrder(5);
		view.setMemo( "默认视图-逾期的任务" );
		view.setWorkOverTime( 1 );
		emc.beginTransaction( TaskView.class );
		emc.persist( view, CheckPersistType.all );
		emc.commit();
		viewList.add(view);
		
		return viewList;
	}
	
	protected Long countWithFilter(EntityManagerContainer emc, QueryFilter queryFilter) throws Exception {
		Business business = new Business(emc);
		return business.taskViewFactory().countWithFilter( queryFilter);
	}
	
	/**
	 * 根据条件查询符合条件的工作任务信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param personName
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	protected List<TaskView> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return  business.taskViewFactory().listWithFilter( maxCount, orderField, orderType, queryFilter);		
	}
}
