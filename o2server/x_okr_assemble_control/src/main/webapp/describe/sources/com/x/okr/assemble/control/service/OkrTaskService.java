package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;

/**
 * 类   名：OkrTaskService<br/>
 * 实体类：OkrTask<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrTaskService{
	private static  Logger logger = LoggerFactory.getLogger( OkrTaskService.class );
	private DateOperation dateOperation = new DateOperation();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	
	/**
	 * 根据传入的ID从数据库查询OkrTask对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrTask get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrTask.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据ID从数据库中删除OkrTask对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrTask okrTask = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrTask = emc.find(id, OkrTask.class);
			if (null == okrTask) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrTask.class );
				emc.remove( okrTask, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 为责任者生成工作确认的待办信息
	 * @param okrWorkBaseInfo
	 * @throws Exception 
	 */
	public void createTaskForResponsibility( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception( "工作信息对象为空，无法生成待办信息！" );
		}
		Business business = null;
		OkrTask okrTask_tmp = null;
		List<String> ids = null;		
		List<OkrTask> okrTaskList = getResponsibilityTask( okrWorkBaseInfo );		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			for( OkrTask okrTask : okrTaskList ){
				//根据工作ID，处理人，处理环节名称查询一下是否存在待办信息，如果存在则进行删除
				ids = business.okrTaskFactory().listIdsByWorkAndTarget( okrTask.getWorkId(), okrTask.getTargetName(), okrTask.getActivityName() );
				emc.beginTransaction( OkrTask.class );
				if( ids != null && ids.size() > 0 ){
					for( String id : ids ){
						okrTask_tmp = emc.find( id, OkrTask.class );
						if( okrTask_tmp != null ){
							emc.remove( okrTask_tmp );
						}
					}
				}
				emc.persist( okrTask, CheckPersistType.all );
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 为责任者生成待办信息
	 * @param okrWorkBaseInfo
	 * @return
	 * @throws Exception 
	 */
	private List<OkrTask> getResponsibilityTask( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception( "okrWorkBaseInfo is null, can not create any task!" );
		}
		Date now = new Date();
		List<OkrTask> okrTaskList = new ArrayList<OkrTask>();
		
		OkrTask okrTask = null;
		String splitFlag = ",";
		String[] targetNameArray = null;
		String[] targetUnitNameArray = null;
		String[] targetTopUnitNameArray = null;
		String targetName = okrWorkBaseInfo.getResponsibilityEmployeeName();
		String targetUnitName = okrWorkBaseInfo.getResponsibilityUnitName();
		String targetTopUnitName = okrWorkBaseInfo.getResponsibilityTopUnitName();
		if( targetName != null ){
			if( targetUnitName == null ){
				throw new Exception( "ResponsibilityUnitName is null, can not create any task!" );
			}
			if( targetTopUnitName == null ){
				throw new Exception( "ResponsibilityTopUnitName is null, can not create any task!" );
			}			
			targetNameArray = targetName.split( splitFlag );
			targetUnitNameArray = targetUnitName.split( splitFlag );
			targetTopUnitNameArray = targetTopUnitName.split( splitFlag );
			if( targetNameArray.length == targetUnitNameArray.length ){
				if( targetNameArray.length == targetTopUnitNameArray.length ){
					for( int i=0; i<targetNameArray.length; i++ ){
						okrTask = new OkrTask();
						okrTask.setTitle( okrWorkBaseInfo.getTitle() );
						okrTask.setWorkId( okrWorkBaseInfo.getId() );
						okrTask.setWorkTitle( okrWorkBaseInfo.getTitle() );
						okrTask.setWorkType( okrWorkBaseInfo.getWorkType() );
						okrTask.setCenterId( okrWorkBaseInfo.getCenterId() );
						okrTask.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );						
						okrTask.setTargetName( targetNameArray[i] );
						okrTask.setTargetUnitName( targetUnitNameArray[i] );
						okrTask.setTargetTopUnitName( targetTopUnitNameArray[i] );						
						okrTask.setActivityName( "工作确认" );
						okrTask.setArriveDateTime( now );
						okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss" ) );						
						okrTask.setDynamicObjectId( okrWorkBaseInfo.getId() );
						okrTask.setDynamicObjectTitle( okrWorkBaseInfo.getTitle() );
						okrTask.setDynamicObjectType( "具体工作" );
						okrTask.setProcessType( "TASK" );
						okrTask.setStatus( "正常" );	
						okrTask.setViewUrl( "" );
						okrTaskList.add( okrTask );
					}
				}else{
					throw new Exception( "Responsibility name( "+targetName+" ), unitName( "+targetUnitName+" ), topUnit( "+targetUnitNameArray+" ) split by ',', array length is not same, can not create tasks!" );
				}
			}else{
				throw new Exception( "Responsibility name( "+targetName+" ), unitName( "+targetUnitName+" ), topUnit( "+targetUnitNameArray+" ) split by ',', array length is not same, can not create tasks!" );
			}
		}else{
			throw new Exception( "ResponsibilityEmployeeName is null, can not create any task!" );
		}
		return okrTaskList;
	}
	

	public void deleteTask( OkrCenterWorkInfo okrCenterWorkInfo, String userIdentity ) throws Exception {
		if( okrCenterWorkInfo == null ){
			throw new Exception( "okrCenterWorkInfo is null, can not delete task for user!" );
		}
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userIdentity is null, can not delete task for user!" );
		}
		OkrTask okrTask = null;
		OkrTaskHandled okrTaskHandled = new OkrTaskHandled();
		List<String> ids = null;
		Business business = null;
		String personName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			personName =  okrUserManagerService.getPersonNameByIdentity( userIdentity );
			//查询该用户在指定的中心工作中的待办信息
			ids = business.okrTaskFactory().listIdsByCenterAndPerson( okrCenterWorkInfo.getId(), userIdentity, "中心工作" );
			emc.beginTransaction( OkrTask.class );
			emc.beginTransaction( OkrTaskHandled.class );
			
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					okrTask = emc.find( id, OkrTask.class );
					if( personName != null ){
						okrTaskHandled = new OkrTaskHandled();
						okrTaskHandled.setActivityName( "工作确认" );
						if( okrTask != null ){
							okrTaskHandled.setArriveDateTime( okrTask.getArriveDateTime() );
							okrTaskHandled.setArriveDateTimeStr( okrTask.getArriveDateTimeStr() );
						}
						okrTaskHandled.setCenterId( okrCenterWorkInfo.getId() );
						okrTaskHandled.setCenterTitle( okrCenterWorkInfo.getTitle() );
						okrTaskHandled.setDynamicObjectId( okrCenterWorkInfo.getId() );
						okrTaskHandled.setDynamicObjectTitle( okrCenterWorkInfo.getTitle() );
						okrTaskHandled.setDynamicObjectType( "中心工作" );
						okrTaskHandled.setProcessDateTime( new Date() );
						okrTaskHandled.setProcessDateTimeStr( dateOperation.getNowDateTime() );
						okrTaskHandled.setTargetTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( userIdentity ));
						okrTaskHandled.setTargetIdentity( userIdentity );
						okrTaskHandled.setTargetName( personName );
						okrTaskHandled.setTargetUnitName( okrUserManagerService.getUnitNameByIdentity( userIdentity ) );
						okrTaskHandled.setTitle( okrCenterWorkInfo.getTitle() );
						okrTaskHandled.setViewUrl( "" );
						okrTaskHandled.setWorkId( null );
						okrTaskHandled.setWorkTitle( null );
						okrTaskHandled.setWorkType( okrCenterWorkInfo.getDefaultWorkType() );
						emc.persist( okrTaskHandled, CheckPersistType.all );
					}
					if( okrTask != null ){
						emc.remove( okrTask, CheckRemoveType.all );
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "create task info got a error!", e);
			throw e;
		}
	}
	
	public void createTaskProcessors( OkrCenterWorkInfo okrCenterWorkInfo, List<String> taskUserIdentityList ) throws Exception {
		if( okrCenterWorkInfo == null ){
			throw new Exception( "okrCenterWorkInfo is null, can not create task for user!" );
		}
		if( taskUserIdentityList == null || taskUserIdentityList.size() == 0 ){
			throw new Exception( "taskUserIdentityList is null, can not create task for user!" );
		}
		OkrTask okrTask = null;
		Date now = new Date();
		List<String> ids = null;
		Business business = null;
		String personName = null;
		String unitName  = null;
		String topUnitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( OkrTask.class );
			for( String identity : taskUserIdentityList ){
				//要先查询一下该员工在该中心工作 相同环节上有没有待办，如果有，则不新增
				ids = business.okrTaskFactory().listIdsByCenterAndIdentityActivity( "中心工作", okrCenterWorkInfo.getId(), identity, "TASK", "工作确认" );
				if( ids == null || ids.size() == 0 ){
					//根据身份查询用户的组织和顶层组织
					personName = okrUserManagerService.getPersonNameByIdentity( identity );
					if( personName != null ){
						unitName = okrUserManagerService.getUnitNameByIdentity( identity );
						topUnitName = okrUserManagerService.getTopUnitNameByIdentity( identity );
						okrTask = new OkrTask();
						okrTask.setTitle( okrCenterWorkInfo.getTitle() );
						okrTask.setCenterId( okrCenterWorkInfo.getId() );
						okrTask.setCenterTitle( okrCenterWorkInfo.getTitle() );
						okrTask.setWorkType( okrCenterWorkInfo.getDefaultWorkType() );
						okrTask.setTargetIdentity( identity );
						okrTask.setTargetName( personName );
						if( unitName != null ){
							okrTask.setTargetUnitName( unitName );
						}
						if( topUnitName != null ){
							okrTask.setTargetTopUnitName( topUnitName );	
						}			
						okrTask.setActivityName( "工作确认" );
						okrTask.setArriveDateTime( now );
						okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss" ) );						
						okrTask.setDynamicObjectId( okrCenterWorkInfo.getId() );
						okrTask.setDynamicObjectTitle( okrCenterWorkInfo.getTitle() );
						okrTask.setDynamicObjectType( "中心工作" );
						okrTask.setProcessType( "TASK" );
						okrTask.setStatus( "正常" );	
						okrTask.setViewUrl( "" );
						emc.persist( okrTask, CheckPersistType.all );
					}else{
						
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "create task info got a error!", e);
			throw e;
		}
	}

	public void createTaskForReaders(OkrCenterWorkInfo okrCenterWorkInfo, List<String> readUserIdentityList ) throws Exception {
		if( okrCenterWorkInfo == null ){
			throw new Exception( "okrCenterWorkInfo is null, can not create task for user!" );
		}
		if( readUserIdentityList == null || readUserIdentityList.size() == 0 ){
			throw new Exception( "readUserIdentityList is null, can not create task for user!" );
		}
		OkrTask okrTask = null;
		Date now = new Date();
		List<String> ids = null;
		Business business = null;
		String personName = null;
		String unitName  = null;
		String topUnitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( OkrTask.class );
			for( String identity : readUserIdentityList ){
				//要先查询一下该员工在该中心工作 相同环节上有没有待办，如果有，则不新增
				ids = business.okrTaskFactory().listIdsByCenterAndIdentityActivity( "中心工作", okrCenterWorkInfo.getId(), identity, "TASK", "工作阅知" );
				if( ids == null || ids.size() == 0 ){
					//根据身份查询用户的组织和顶层组织
					personName = okrUserManagerService.getPersonNameByIdentity( identity );
					if( personName != null ){
						unitName = okrUserManagerService.getUnitNameByIdentity( identity );
						topUnitName = okrUserManagerService.getTopUnitNameByIdentity( identity );
						okrTask = new OkrTask();
						okrTask.setTitle( okrCenterWorkInfo.getTitle() );
						okrTask.setCenterId( okrCenterWorkInfo.getId() );
						okrTask.setCenterTitle( okrCenterWorkInfo.getTitle() );
						okrTask.setWorkType( okrCenterWorkInfo.getDefaultWorkType() );
						okrTask.setTargetIdentity( identity );
						okrTask.setTargetName( personName );
						if( unitName != null ){
							okrTask.setTargetUnitName( unitName );
						}
						if( topUnitName != null ){
							okrTask.setTargetTopUnitName( topUnitName );	
						}			
						okrTask.setActivityName( "工作阅知" );
						okrTask.setArriveDateTime( now );
						okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss" ) );						
						okrTask.setDynamicObjectId( okrCenterWorkInfo.getId() );
						okrTask.setDynamicObjectTitle( okrCenterWorkInfo.getTitle() );
						okrTask.setDynamicObjectType( "中心工作" );
						okrTask.setProcessType( "READ" );
						okrTask.setStatus( "正常" );	
						okrTask.setViewUrl( "" );
						emc.persist( okrTask, CheckPersistType.all );
					}else{
						
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "create read info got a error!", e);
			throw e;
		}
	}

	/**
	 * 查询在中心中工作是否有指定员工的待办信息
	 * @param centerId
	 * @param userName 可能多值
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsByCenterAndPerson( String centerId, String identity, String dynamicObjectType ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not excute query." );
		}
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null, system can not excute query." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().listIdsByCenterAndPerson( centerId, identity, dynamicObjectType );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listIdsByTargetActivityAndObjId( String dynamicObjectType, String objectId, String activityName, String processorIdentity ) throws Exception {
		if( dynamicObjectType == null || dynamicObjectType.isEmpty() ){
			throw new Exception( "dynamicObjectType is null, system can not excute query." );
		}
		if( objectId == null || objectId.isEmpty() ){
			throw new Exception( "objectId is null, system can not excute query." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().listIdsByTargetActivityAndObjId( null, dynamicObjectType, objectId, activityName, processorIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrTask> list(List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception( "ids is null, system can not excute query." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据待办各类获取人员身份列表
	 * @param taskTypeList
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctIdentity( List<String> taskTypeList ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().listDistinctIdentity( taskTypeList );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据待办类别和用户身份，获取用户的待办数量
	 * @param taskTypeList
	 * @param userIdentity
	 * @return
	 * @throws Exception 
	 */
	public Long getTaskCount( List<String> taskTypeList, String userIdentity, String workTypeName ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().getTaskCount( taskTypeList, userIdentity, workTypeName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long getNotReportConfirmTaskCount( List<String> taskTypeList, String userIdentity, String workTypeName ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().getNotReportConfirmTaskCount( taskTypeList, userIdentity, workTypeName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<OkrTask> listTaskByTaskType(List<String> taskTypeList, String userIdentity, String workTypeName) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().listTaskByTaskType( taskTypeList, userIdentity, workTypeName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<OkrTask> listReadByTaskType(List<String> taskTypeList, String userIdentity, String workTypeName) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().listReadByTaskType( taskTypeList, userIdentity, workTypeName );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 处理待阅信息
	 * @param okrTask
	 * @throws Exception 
	 */
	public void processRead( OkrTask okrTask ) throws Exception {		
		if( okrTask == null ){
			return;
		}		
		if( "中心工作".equals( okrTask.getDynamicObjectType() )){
			//说明是工作部署的阅知信息，一般是协助者和工作阅知者有该信息
			processWorkDeployRead( okrTask );
		}else if( "汇报确认".equals( okrTask.getDynamicObjectType() )){
			//说明是工作汇报的阅知信息，一般是工作汇报审批完成后，以阅知形式返回汇报人
			processWorkReportRead( okrTask );
		}
	}

	private void processWorkReportRead( OkrTask okrTask ) throws Exception {
		//1、根据待阅信息生成已阅信息
		OkrTaskHandled okrTaskHandled = getTaskHandledByTask( okrTask );
		//2、删除待阅信息，保存已阅信息
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrTask = emc.find( okrTask.getId(), OkrTask.class );
			emc.beginTransaction( OkrTask.class );
			emc.beginTransaction( OkrTaskHandled.class );
			emc.persist(okrTaskHandled, CheckPersistType.all );
			emc.remove( okrTask, CheckRemoveType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	private void processWorkDeployRead( OkrTask okrTask ) throws Exception {
		List<String> ids = null;
		List<String> status = new ArrayList<String>();
		List<OkrWorkPerson> personList = null;
		//1、根据待阅信息生成已阅信息
		OkrTaskHandled okrTaskHandled = getTaskHandledByTask( okrTask );
		//2、删除待阅信息, 保存已阅信息
		//3、如果是工作部署待阅，记录一下该用户的访问时间
		Business business = null;
		Date now = new Date();
		status.add( "正常" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrWorkPersonFactory().listByCenterAndPerson( 
					okrTask.getCenterId(), 
					okrTask.getTargetIdentity(), 
					"阅知者", 
					status );
			if( ids != null && !ids.isEmpty()){
				personList = business.okrWorkPersonFactory().list(ids);
			}
			
			//查询该用户身份需要处理的WorkPerson信息
			okrTask = emc.find( okrTask.getId(), OkrTask.class );
			emc.beginTransaction( OkrTask.class );
			emc.beginTransaction( OkrTaskHandled.class );
			emc.beginTransaction( OkrWorkPerson.class );
			if( personList != null && !personList.isEmpty()){
				for( OkrWorkPerson okrWorkPerson : personList ){
					okrWorkPerson.setViewTime( dateOperation.getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss" ) );
					emc.check( okrWorkPerson, CheckPersistType.all );
				}
			}
			emc.persist(okrTaskHandled, CheckPersistType.all );
			emc.remove( okrTask, CheckRemoveType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据待办待阅信息，创建一个新的已办或者已阅信息对象
	 * @param okrTask
	 * @return
	 */
	public OkrTaskHandled getTaskHandledByTask(OkrTask okrTask) {
		OkrTaskHandled okrTaskHandled = new OkrTaskHandled();
		okrTaskHandled.setTitle( okrTask.getTitle() );
		okrTaskHandled.setCenterId( okrTask.getCenterId() );
		okrTaskHandled.setCenterTitle( okrTask.getCenterTitle() );
		okrTaskHandled.setWorkId( okrTask.getWorkId() );
		okrTaskHandled.setWorkTitle( okrTask.getWorkTitle() );
		okrTaskHandled.setWorkType( okrTask.getWorkType() );
		okrTaskHandled.setDynamicObjectId( okrTask.getDynamicObjectId() );
		okrTaskHandled.setDynamicObjectTitle( okrTask.getDynamicObjectTitle() );
		okrTaskHandled.setDynamicObjectType( okrTask.getDynamicObjectType() );
		okrTaskHandled.setActivityName( okrTask.getActivityName() );
		okrTaskHandled.setTargetTopUnitName( okrTask.getTargetTopUnitName() );
		okrTaskHandled.setTargetIdentity( okrTask.getTargetIdentity() );
		okrTaskHandled.setTargetName( okrTask.getTargetName() );
		okrTaskHandled.setTargetUnitName( okrTask.getTargetUnitName() );
		okrTaskHandled.setArriveDateTime( okrTask.getArriveDateTime() );
		okrTaskHandled.setArriveDateTimeStr( okrTask.getArriveDateTimeStr() );
		okrTaskHandled.setProcessDateTime( new Date() );
		okrTaskHandled.setProcessDateTimeStr( dateOperation.getNowDateTime() );
		okrTaskHandled.setViewUrl( "" );
		return okrTaskHandled;
	}

	/**
	 * 根据用户唯一标识来查询用户信息
	 * @param taskTypeList
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public Long getTaskCountByUserName( List<String> taskTypeList, List<String> notInTaskTypeList, String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null, system can not excute query." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrTaskFactory().getTaskCountByUserName( taskTypeList, notInTaskTypeList, name );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrTask> listIdsByReportId(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not excute query." );
		}
		List<String> ids = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			ids = business.okrTaskFactory().listIdsByReportId( id );
			return business.okrTaskFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
//	/**
//	 * 为协助者生成待办信息
//	 * @param okrWorkBaseInfo
//	 * @return
//	 * @throws Exception 
//	 */
//	private List<OkrTask> getCooperateTask( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
//		if( okrWorkBaseInfo == null ){
//			throw new Exception( "okrWorkBaseInfo is null, can not create any task!" );
//		}
//		Date now = new Date();
//		List<OkrTask> okrTaskList = new ArrayList<OkrTask>();
//		
//		OkrTask okrTask = null;
//		String splitFlag = ",";
//		String[] targetNameArray = null;
//		String[] targetUnitNameArray = null;
//		String[] targetTopUnitNameArray = null;
//		String targetName = okrWorkBaseInfo.getCooperateEmployeeName();
//		String targetUnitName = okrWorkBaseInfo.getCooperateUnitName();
//		String targetTopUnitName = okrWorkBaseInfo.getCooperateTopUnitName();
//		if( targetName != null ){
//			if( targetUnitName == null ){
//				throw new Exception( "CooperateUnitName is null, can not create any task!" );
//			}
//			if( targetTopUnitName == null ){
//				throw new Exception( "CooperateTopUnitName is null, can not create any task!" );
//			}			
//			targetNameArray = targetName.split( splitFlag );
//			targetUnitNameArray = targetUnitName.split( splitFlag );
//			targetTopUnitNameArray = targetTopUnitName.split( splitFlag );
//			if( targetNameArray.length == targetUnitNameArray.length ){
//				if( targetNameArray.length == targetTopUnitNameArray.length ){
//					for( int i=0; i<targetNameArray.length; i++ ){
//						okrTask = new OkrTask();
//						okrTask.setTitle( okrWorkBaseInfo.getTitle() );
//						okrTask.setWorkId( okrWorkBaseInfo.getId() );
//						okrTask.setWorkTitle( okrWorkBaseInfo.getTitle() );
//						okrTask.setWorkType( okrWorkBaseInfo.getWorkType() );
//						okrTask.setCenterId( okrWorkBaseInfo.getCenterId() );
//						okrTask.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );						
//						okrTask.setTargetName( targetNameArray[i] );
//						okrTask.setTargetUnitName( targetUnitNameArray[i] );
//						okrTask.setTargetTopUnitName( targetTopUnitNameArray[i] );						
//						okrTask.setActivityName( "工作阅知" );
//						okrTask.setArriveDateTime( now );
//						okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss" ) );						
//						okrTask.setDynamicObjectId( okrWorkBaseInfo.getId() );
//						okrTask.setDynamicObjectTitle( okrWorkBaseInfo.getTitle() );
//						okrTask.setDynamicObjectType( "具体工作" );
//						okrTask.setProcessType( "READ" );
//						okrTask.setStatus( "正常" );	
//						okrTask.setViewUrl( "" );
//						okrTaskList.add( okrTask );
//					}
//				}else{
//					throw new Exception( "Cooperate name( "+targetName+" ), unitName( "+targetUnitName+" ), topUnit( "+targetUnitNameArray+" ) split by ',', array length is not same, can not create tasks!" );
//				}
//			}else{
//				throw new Exception( "Cooperate name( "+targetName+" ), unitName( "+targetUnitName+" ), topUnit( "+targetUnitNameArray+" ) split by ',', array length is not same, can not create tasks!" );
//			}
//		}else{
//			throw new Exception( "CooperateEmployeeName is null, can not create any task!" );
//		}
//		return okrTaskList;
//	}
//	
//	/**
//	 * 为阅知领导生成阅知待办信息
//	 * @param okrWorkBaseInfo
//	 * @return
//	 * @throws Exception
//	 */
//	private List<OkrTask> getLeaderReadTask( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
//		if( okrWorkBaseInfo == null ){
//			throw new Exception( "okrWorkBaseInfo is null, can not create any task!" );
//		}
//		Date now = new Date();
//		List<OkrTask> okrTaskList = new ArrayList<OkrTask>();
//		
//		OkrTask okrTask = null;
//		String splitFlag = ",";
//		String[] targetNameArray = null;
//		String[] targetUnitNameArray = null;
//		String[] targetTopUnitNameArray = null;
//		String targetName = okrWorkBaseInfo.getReadLeaderName();
//		String targetUnitName = okrWorkBaseInfo.getReadLeaderUnitName();
//		String targetTopUnitName = okrWorkBaseInfo.getReadLeaderTopUnitName();
//		if( targetName != null ){
//			if( targetUnitName == null ){
//				throw new Exception( "ReadLeaderUnitName is null, can not create any task!" );
//			}
//			if( targetTopUnitName == null ){
//				throw new Exception( "ReadLeaderTopUnitName is null, can not create any task!" );
//			}			
//			targetNameArray = targetName.split( splitFlag );
//			targetUnitNameArray = targetUnitName.split( splitFlag );
//			targetTopUnitNameArray = targetTopUnitName.split( splitFlag );
//			if( targetNameArray.length == targetUnitNameArray.length ){
//				if( targetNameArray.length == targetTopUnitNameArray.length ){
//					for( int i=0; i<targetNameArray.length; i++ ){
//						okrTask = new OkrTask();
//						okrTask.setTitle( okrWorkBaseInfo.getTitle() );
//						okrTask.setWorkId( okrWorkBaseInfo.getId() );
//						okrTask.setWorkTitle( okrWorkBaseInfo.getTitle() );
//						okrTask.setWorkType( okrWorkBaseInfo.getWorkType() );
//						okrTask.setCenterId( okrWorkBaseInfo.getCenterId() );
//						okrTask.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );						
//						okrTask.setTargetName( targetNameArray[i] );
//						okrTask.setTargetUnitName( targetUnitNameArray[i] );
//						okrTask.setTargetTopUnitName( targetTopUnitNameArray[i] );						
//						okrTask.setActivityName( "工作阅知" );
//						okrTask.setArriveDateTime( now );
//						okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate( now, "yyyy-MM-dd HH:mm:ss" ) );						
//						okrTask.setDynamicObjectId( okrWorkBaseInfo.getId() );
//						okrTask.setDynamicObjectTitle( okrWorkBaseInfo.getTitle() );
//						okrTask.setDynamicObjectType( "具体工作" );
//						okrTask.setProcessType( "READ" );
//						okrTask.setStatus( "正常" );	
//						okrTask.setViewUrl( "" );
//						okrTaskList.add( okrTask );
//					}
//				}else{
//					throw new Exception( "ReadLeader name( "+targetName+" ), unitName( "+targetUnitName+" ), topUnit( "+targetUnitNameArray+" ) split by ',', array length is not same, can not create tasks!" );
//				}
//			}else{
//				throw new Exception( "ReadLeader name( "+targetName+" ), unitName( "+targetUnitName+" ), topUnit( "+targetUnitNameArray+" ) split by ',', array length is not same, can not create tasks!" );
//			}
//		}else{
//			throw new Exception( "ReadLeaderName is null, can not create any task!" );
//		}
//		return okrTaskList;
//	}

}
