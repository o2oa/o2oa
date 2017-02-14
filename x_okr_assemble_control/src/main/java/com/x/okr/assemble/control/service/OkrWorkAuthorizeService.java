package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;
import com.x.organization.core.express.wrap.WrapPerson;

public class OkrWorkAuthorizeService {

	private Logger logger = LoggerFactory.getLogger( OkrWorkAuthorizeService.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	private DateOperation dateOperation = new DateOperation();
	
	/**
	 * 对工作进行授权操作<br/>
	 * 
	 * 1、查询授权者在此工作中的所有处理身份信息 workPerson<br/>
	 * 2、为承担者添加观察者和处理身份相关信息<br/>
	 * 3、删除授权者在工作所有身份中的除观察者身份之外的所有身份，并且添加授权者身份信息<br/>
	 * 4、添加工作授权记录信息<br/>
	 * 5、判断是否需要删除授权者的工作待办，待办所在中心工作中是否还有其他未授权工作需要处理<br/>
	 * 
	 * @param okrWorkBaseInfo  工作信息
	 * @param authorizeIdentity  授权身份
	 * @param undertakerIdentity  承担者身份
	 * @throws Exception 
	 */
	public void authorize( OkrWorkBaseInfo okrWorkBaseInfo, String authorizeIdentity, String undertakerIdentity, String delegateOpinion ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception("okrWorkBaseInfo is null!");
		}
		if( authorizeIdentity == null || authorizeIdentity.isEmpty() ){
			throw new Exception("authorizeIdentity is null!");
		}
		if( undertakerIdentity == null || undertakerIdentity.isEmpty() ){
			throw new Exception("undertakerIdentity is null!");
		}
		if( delegateOpinion == null || delegateOpinion.isEmpty() ){
			throw new Exception("delegateOpinion is null!");
		}
		List<String> statuses = new ArrayList<String>();
		List<String> ids = null;
		List<String> ids_task = null;
		List<String> ids_tmp = null;
		List<OkrWorkPerson> okrWorkPersons = null;
		String undertakerName = null;
		String undertakerOrganizationName = null;
		String undertakerCompanyName = null;
		String authorizeName = null;
		String authorizeOrganizationName = null;
		String authorizeCompanyName = null;
		String authorizeProcessIdentity = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		OkrWorkPerson okrWorkPerson = null;
		OkrWorkPerson okrWorkPerson_new = null;
		WrapPerson wrapPerson = null;
		OkrTask okrTask = null;
		OkrTaskHandled okrTaskHandled = null;
		Integer delegateLevel = 0;
		boolean check = true;
		Business business = null;
		
		statuses.add( "正常" );

		wrapPerson = okrUserManagerService.getUserNameByIdentity( undertakerIdentity );
		if( wrapPerson == null ){
			throw new Exception("person{'identity':'"+undertakerIdentity+"'} not exists.");
		}
		
		undertakerName = wrapPerson.getName();
		undertakerOrganizationName = okrUserManagerService.getDepartmentNameByIdentity( undertakerIdentity );
		undertakerCompanyName = okrUserManagerService.getCompanyNameByIdentity( undertakerIdentity );
		
		logger.debug( ">>>>>>>>>>>>>>>>>>>系统正在进行授权["+authorizeIdentity+" -> "+ undertakerIdentity +"]......" );
		ids = okrWorkPersonService.listIdsByWorkAndUserIdentity( okrWorkBaseInfo.getId(), authorizeIdentity, statuses );
		if( ids != null && !ids.isEmpty() ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				emc.beginTransaction( OkrWorkAuthorizeRecord.class );
				emc.beginTransaction( OkrWorkPerson.class );
				emc.beginTransaction( OkrWorkBaseInfo.class );
				emc.beginTransaction( OkrTask.class );
				emc.beginTransaction( OkrTaskHandled.class );				
				if( check ){
					try{
						delegateLevel = business.okrWorkAuthorizeRecordFactory().getMaxDelegateLevel( okrWorkBaseInfo.getId());
						
						okrWorkAuthorizeRecord = new OkrWorkAuthorizeRecord();
						
						for( String id : ids ){//遍历授权人在此工作中所有的身份
							okrWorkPerson = business.okrWorkPersonFactory().get( id );
							//判断okrWorkPerson是否为空
							if( okrWorkPerson == null ){
								continue;
							}
							
							//logger.debug( ">>>>>>>>>>>>>>>>>>>授权人["+okrWorkPerson.getEmployeeIdentity()+"]拥有工作干系人身份：" + okrWorkPerson.getProcessIdentity() );
							//授权者的观察者身份保留
							if( !"责任者".equals( okrWorkPerson.getProcessIdentity() )){
								//logger.debug( ">>>>>>>>>>>>>>>>>>>跳过......" );
								continue;
							}
							
							authorizeName = okrWorkPerson.getEmployeeName();
							authorizeOrganizationName = okrWorkPerson.getOrganizationName();
							authorizeCompanyName = okrWorkPerson.getCompanyName();
							authorizeProcessIdentity = okrWorkPerson.getProcessIdentity();
							
							//查询授权者的授权身份信息是否已经存在，如果不存在则将当前的身份信息修改为授权信息
							//logger.debug( ">>>>>>>>>>>>>>>>>>>系统正在为授权者转换工作处理身份["+ authorizeProcessIdentity +" -> 授权者]......" );
							ids_tmp = business.okrWorkPersonFactory().listByWorkAndIdentity( null, okrWorkBaseInfo.getId(), authorizeIdentity, "授权者", statuses );
							if( ids_tmp == null || ids_tmp.isEmpty() ){
								okrWorkPerson.setProcessIdentity( "授权者" );
								//logger.debug( ">>>>>>>>>>>>>>>>>>>更新干系人身份信息，转换工作处理["+ authorizeProcessIdentity +" -> 授权者]，id=" + okrWorkPerson.getId() );
								okrWorkPerson.setAuthorizeRecordId( okrWorkAuthorizeRecord.getId() );
								emc.check( okrWorkPerson, CheckPersistType.all );
							}else{
								//logger.debug( ">>>>>>>>>>>>>>>>>>>删除干系人身份信息，id=" + okrWorkPerson.getId() );
								emc.remove( okrWorkPerson, CheckRemoveType.all );
							}
							
							//logger.debug( ">>>>>>>>>>>>>>>>>>>系统正在为承担者添加工作处理身份["+authorizeProcessIdentity+"]......" );
							//为承担者添加相应的身份信息，先查询该员工在该工作下相应的身份是否已经存在，如果存在，则不需要再添加了
							ids_tmp = business.okrWorkPersonFactory().listByWorkAndIdentity( null, okrWorkBaseInfo.getId(), undertakerIdentity, authorizeProcessIdentity, statuses );
							if( ids_tmp == null || ids_tmp.isEmpty() ){
								okrWorkPerson_new = new OkrWorkPerson();
								okrWorkPerson_new.setAuthorizeRecordId( okrWorkAuthorizeRecord.getId() );
								okrWorkPerson_new.setCenterId( okrWorkPerson.getCenterId() );
								okrWorkPerson_new.setCenterTitle( okrWorkPerson.getCenterTitle() );
								okrWorkPerson_new.setWorkId( okrWorkPerson.getWorkId() );
								okrWorkPerson_new.setParentWorkId( okrWorkPerson.getParentWorkId() );
								okrWorkPerson_new.setWorkTitle( okrWorkPerson.getWorkTitle() );
								okrWorkPerson_new.setWorkType( okrWorkPerson.getWorkType() );
								okrWorkPerson_new.setWorkDateTimeType( okrWorkPerson.getWorkDateTimeType() );
								okrWorkPerson_new.setWorkLevel( okrWorkPerson.getWorkLevel() );
								okrWorkPerson_new.setWorkProcessStatus( okrWorkPerson.getWorkProcessStatus() );						
								okrWorkPerson_new.setEmployeeName( undertakerName );
								okrWorkPerson_new.setEmployeeIdentity( undertakerIdentity );
								okrWorkPerson_new.setOrganizationName( undertakerOrganizationName );
								okrWorkPerson_new.setCompanyName( undertakerCompanyName );
								okrWorkPerson_new.setDeployMonth( okrWorkPerson.getDeployMonth() );
								okrWorkPerson_new.setDeployYear( okrWorkPerson.getDeployYear() );						
								okrWorkPerson_new.setIsCompleted( okrWorkPerson.getIsCompleted() );						
								okrWorkPerson_new.setIsOverTime( okrWorkPerson.getIsOverTime() );						
								okrWorkPerson_new.setProcessIdentity( authorizeProcessIdentity );
								okrWorkPerson_new.setIsDelegateTarget( true );
								//logger.debug( ">>>>>>>>>>>>>>>>>>>添加工作处理身份"+undertakerIdentity+"["+authorizeProcessIdentity+"]，id=" + okrWorkPerson_new.getId() );
								emc.persist( okrWorkPerson_new, CheckPersistType.all );
							}
							
							//logger.debug( ">>>>>>>>>>>>>>>>>>>系统正在为承担者添加工作处理身份[观察者]......" );
							ids_tmp = business.okrWorkPersonFactory().listByWorkAndIdentity( null, okrWorkBaseInfo.getId(), undertakerIdentity, "观察者", statuses );
							if( ids_tmp == null || ids_tmp.isEmpty() ){
								okrWorkPerson_new = new OkrWorkPerson();
								okrWorkPerson_new.setAuthorizeRecordId( okrWorkAuthorizeRecord.getId() );
								okrWorkPerson_new.setCenterId( okrWorkPerson.getCenterId() );
								okrWorkPerson_new.setCenterTitle( okrWorkPerson.getCenterTitle() );
								okrWorkPerson_new.setWorkId( okrWorkPerson.getWorkId() );
								okrWorkPerson_new.setParentWorkId( okrWorkPerson.getParentWorkId() );
								okrWorkPerson_new.setWorkTitle( okrWorkPerson.getWorkTitle() );
								okrWorkPerson_new.setWorkType( okrWorkPerson.getWorkType() );
								okrWorkPerson_new.setWorkDateTimeType( okrWorkPerson.getWorkDateTimeType() );
								okrWorkPerson_new.setWorkLevel( okrWorkPerson.getWorkLevel() );
								okrWorkPerson_new.setWorkProcessStatus( okrWorkPerson.getWorkProcessStatus() );						
								okrWorkPerson_new.setEmployeeName( undertakerName );
								okrWorkPerson_new.setEmployeeIdentity( undertakerIdentity );
								okrWorkPerson_new.setOrganizationName( undertakerOrganizationName );
								okrWorkPerson_new.setCompanyName( undertakerCompanyName );
								okrWorkPerson_new.setDeployMonth( okrWorkPerson.getDeployMonth() );
								okrWorkPerson_new.setDeployYear( okrWorkPerson.getDeployYear() );						
								okrWorkPerson_new.setIsCompleted( okrWorkPerson.getIsCompleted() );						
								okrWorkPerson_new.setIsOverTime( okrWorkPerson.getIsOverTime() );						
								okrWorkPerson_new.setProcessIdentity( "观察者" );
								okrWorkPerson_new.setIsDelegateTarget( true );
							//	logger.debug( ">>>>>>>>>>>>>>>>>>>添加工作处理身份"+undertakerIdentity+"[观察者]，id=" + okrWorkPerson_new.getId() );
								emc.persist( okrWorkPerson_new, CheckPersistType.all );
							}
						}
						//logger.debug(">>>>>>>>>>>>>>>>>>>系统正在添加工作授权日志......");
						//logger.debug(">>>>>>>>>>>>>>>>>>>系统正在查询该工作最大的委托级别......");
						okrWorkAuthorizeRecord.setCenterId(okrWorkPerson.getCenterId());
						okrWorkAuthorizeRecord.setCenterTitle(okrWorkPerson.getCenterTitle());
						okrWorkAuthorizeRecord.setTitle(okrWorkBaseInfo.getTitle());
						okrWorkAuthorizeRecord.setWorkId(okrWorkBaseInfo.getId());
						okrWorkAuthorizeRecord.setDelegateDateTime(new Date());
						okrWorkAuthorizeRecord.setDelegateDateTimeStr(dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						okrWorkAuthorizeRecord.setDelegateOpinion(delegateOpinion);
						okrWorkAuthorizeRecord.setDelegatorCompanyName(authorizeCompanyName);
						okrWorkAuthorizeRecord.setDelegatorIdentity(authorizeIdentity);
						okrWorkAuthorizeRecord.setDelegatorName(authorizeName);
						okrWorkAuthorizeRecord.setDelegatorOrganizationName(authorizeOrganizationName);
						okrWorkAuthorizeRecord.setTargetCompanyName(undertakerCompanyName);
						okrWorkAuthorizeRecord.setTargetIdentity(undertakerIdentity);
						okrWorkAuthorizeRecord.setTargetName(undertakerName);
						okrWorkAuthorizeRecord.setTargetOrganizationName(undertakerOrganizationName);
						okrWorkAuthorizeRecord.setDelegateLevel(++delegateLevel);
						emc.persist( okrWorkAuthorizeRecord, CheckPersistType.all);
						emc.commit();
					}catch( Exception e ){
						check = false;
						logger.error( "system authorize work got an exception.", e );
					}					
				}
				
				//重新组织工作的干系人数据
				if( check ){
					emc.beginTransaction( OkrWorkBaseInfo.class );
					
					okrWorkBaseInfo = emc.find( okrWorkBaseInfo.getId(), OkrWorkBaseInfo.class );
					
					//责任者
					composeResponsibilityWorkPersonInfo( okrWorkBaseInfo, null, okrWorkBaseInfo.getId(), undertakerIdentity );
					//协助者
					//composeCooperateWorkPersonInfo( okrWorkBaseInfo, null, okrWorkBaseInfo.getId(), undertakerIdentity );
					//阅知者
					//composeReadLeaderWorkPersonInfo( okrWorkBaseInfo, null, okrWorkBaseInfo.getId(), undertakerIdentity );
					emc.check( okrWorkBaseInfo, CheckPersistType.all);
					emc.commit();
				}
				
				if( check ){
					emc.beginTransaction( OkrWorkAuthorizeRecord.class );
					emc.beginTransaction( OkrWorkPerson.class );
					emc.beginTransaction( OkrWorkBaseInfo.class );
					emc.beginTransaction( OkrTask.class );
					emc.beginTransaction( OkrTaskHandled.class );
					// 处理待办信息
					// 1、判断承担者是否已经存在该中心工作的待办信息，如果不存在，则需要推送待办信息
					//logger.debug(">>>>>>>>>>>>>>>>>>>判断承担者是否已经存在该中心工作的待办信息，如果不存在，则需要推送待办信息......");
					ids_tmp = business.okrTaskFactory().listIdsByCenterAndPerson( okrWorkPerson.getCenterId(), undertakerIdentity, "中心工作" );
					if ( ids_tmp == null || ids_tmp.isEmpty() ) {
						// 添加待办信息
						okrTask = new OkrTask();
						okrTask.setTitle(okrWorkPerson.getCenterTitle());
						okrTask.setCenterId(okrWorkPerson.getCenterId());
						okrTask.setCenterTitle(okrWorkPerson.getCenterTitle());
						okrTask.setWorkType( okrWorkPerson.getWorkType() );
						okrTask.setTargetIdentity(undertakerIdentity);
						okrTask.setTargetName(undertakerName);
						okrTask.setTargetOrganizationName(undertakerOrganizationName);
						okrTask.setTargetCompanyName(undertakerCompanyName);
						okrTask.setActivityName("工作确认");
						okrTask.setArriveDateTime(new Date());
						okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						okrTask.setDynamicObjectId(okrWorkPerson.getCenterId());
						okrTask.setDynamicObjectTitle(okrWorkPerson.getCenterTitle());
						okrTask.setDynamicObjectType("中心工作");
						okrTask.setProcessType("TASK");
						okrTask.setStatus("正常");
						okrTask.setViewUrl("");
						//logger.debug(">>>>>>>>>>>>>>>>>>>为承担者["+undertakerIdentity+"]添加一条待办信息......");
						emc.persist( okrTask, CheckPersistType.all );
					}

					//logger.debug(">>>>>>>>>>>>>>>>>>>处理授权者的待办信息......");
					// 待办删除的功能先不管，用户可以主动提交后删除
					// 先看看该授权者是否仍存在该中心工作的待办，本来就没有待办信息，就不管了
					ids_task = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "中心工作", okrWorkBaseInfo.getCenterId(), null, authorizeIdentity );
					if (ids_task != null && !ids_task.isEmpty()) {
						// 判断该中心工作下是否仍有授权者需要部署和拆解的工作， workPerson表，有责任者是授权者记录
						//logger.debug(">>>>>>>>>>>>>>>>>>>查询工作干系人中是否仍有员工["+authorizeIdentity+"]为责任者的信息......");
						ids_tmp = null;
						ids_tmp = business.okrWorkPersonFactory().listWorkByCenterAndIdentity( 
								okrWorkBaseInfo.getCenterId(),
								authorizeIdentity,
								"责任者",
								statuses
						);
						//logger.debug(">>>>>>>>>>>>>>>>>>>ids_tmp =" + ids_tmp );
						if ( ids_tmp == null || ids_tmp.isEmpty() ) {//已经没有需要部署的工作了，需要删除待办并且生成一条已办
							okrTask = emc.find( ids_task.get(0), OkrTask.class );
							if ( okrTask != null ) {
								//logger.debug(">>>>>>>>>>>>>>>>>>>为授权者新增一条已办信息......");
								okrTaskHandled = new OkrTaskHandled();
								okrTaskHandled.setActivityName(okrTask.getActivityName());
								okrTaskHandled.setArriveDateTime(okrTask.getArriveDateTime());
								okrTaskHandled.setArriveDateTimeStr(okrTask.getArriveDateTimeStr());
								okrTaskHandled.setCenterId(okrTask.getCenterId());
								okrTaskHandled.setCenterTitle(okrTask.getCenterTitle());
								okrTaskHandled.setDynamicObjectId(okrTask.getDynamicObjectId());
								okrTaskHandled.setDynamicObjectTitle(okrTask.getDynamicObjectTitle());
								okrTaskHandled.setDynamicObjectType(okrTask.getDynamicObjectType());
								okrTaskHandled.setProcessDateTime(new Date());
								okrTaskHandled.setProcessDateTimeStr(dateOperation.getNowDateTime());
								okrTaskHandled.setTargetCompanyName(okrTask.getTargetCompanyName());
								okrTaskHandled.setTargetIdentity(okrTask.getTargetIdentity());
								okrTaskHandled.setTargetName(okrTask.getTargetName());
								okrTaskHandled.setTargetOrganizationName(okrTask.getTargetOrganizationName());
								okrTaskHandled.setTitle(okrTask.getTitle());
								okrTaskHandled.setWorkType( okrTask.getWorkType() );
								okrTaskHandled.setViewUrl("");
								okrTaskHandled.setWorkId(okrTask.getWorkId());
								okrTaskHandled.setWorkTitle(okrTask.getWorkTitle());
								// 保存已办
								emc.persist( okrTaskHandled, CheckPersistType.all );
							}
							// 删除所有的待办信息
							for (String _id : ids_task) {
								okrTask = emc.find( _id, OkrTask.class );
								if ( okrTask != null ) {
									//logger.debug(">>>>>>>>>>>>>>>>>>>为授权者删除一条待办信息......");
									emc.remove(okrTask, CheckRemoveType.all);
								}
							}
						}
					}
					emc.commit();
				}
			} catch ( Exception e ) {
				throw e;
			}
		}
	}

	private OkrWorkBaseInfo composeReadLeaderWorkPersonInfo( OkrWorkBaseInfo okrWorkBaseInfo, String centerId, String workId, String employeeIdentity ) throws Exception {
		OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
		String personNames = null;
		String personIdentities = null;
		String personOrganizations = null;
		String personCompanies = null;
		List<String> statuses = new ArrayList<String>();
		List<String> ids_tmp = null;
		List<OkrWorkPerson> okrWorkPersons = null;
		
		statuses.add( "正常" );
		
		ids_tmp = okrWorkPersonService.listByWorkAndIdentity( centerId, workId, employeeIdentity, "阅知者", statuses );
		
		if( ids_tmp != null && !ids_tmp.isEmpty() ){
			okrWorkPersons = okrWorkPersonService.list( ids_tmp );
		}
		
		if( okrWorkPersons != null && !okrWorkPersons.isEmpty() ){
			for( OkrWorkPerson okrWorkPerson_tmp : okrWorkPersons ){
				if( personNames == null || personNames.trim().isEmpty() ){
					personNames = personNames + okrWorkPerson_tmp.getEmployeeName();
				}else{
					personNames = "," + personNames + okrWorkPerson_tmp.getEmployeeName();
				}
				
				if( personIdentities == null || personIdentities.trim().isEmpty() ){
					personIdentities = personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}else{
					personIdentities = "," + personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}
				
				if( personOrganizations == null || personOrganizations.trim().isEmpty() ){
					personOrganizations = personOrganizations + okrWorkPerson_tmp.getOrganizationName();
				}else{
					personOrganizations = "," + personOrganizations + okrWorkPerson_tmp.getOrganizationName();
				}
				
				if( personCompanies == null || personCompanies.trim().isEmpty() ){
					personCompanies = personCompanies + okrWorkPerson_tmp.getCompanyName();
				}else{
					personCompanies = "," + personCompanies + okrWorkPerson_tmp.getCompanyName();
				}				
			}
			okrWorkBaseInfo.setReadLeaderCompanyName( personCompanies );
			okrWorkBaseInfo.setReadLeaderOrganizationName( personOrganizations );
			okrWorkBaseInfo.setReadLeaderIdentity( personIdentities );
			okrWorkBaseInfo.setReadLeaderName( personNames );
		}
		return okrWorkBaseInfo;
		
	}

	private OkrWorkBaseInfo composeCooperateWorkPersonInfo(OkrWorkBaseInfo okrWorkBaseInfo, String centerId, String workId, String employeeIdentity) throws Exception {
		OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
		String personNames = null;
		String personIdentities = null;
		String personOrganizations = null;
		String personCompanies = null;
		List<String> statuses = new ArrayList<String>();
		List<String> ids_tmp = null;
		List<OkrWorkPerson> okrWorkPersons = null;
		
		statuses.add( "正常" );
		
		ids_tmp = okrWorkPersonService.listByWorkAndIdentity( centerId, workId, employeeIdentity, "协助者", statuses );
		
		if( ids_tmp != null && !ids_tmp.isEmpty() ){
			okrWorkPersons = okrWorkPersonService.list( ids_tmp );
		}
		
		if( okrWorkPersons != null && !okrWorkPersons.isEmpty() ){
			for( OkrWorkPerson okrWorkPerson_tmp : okrWorkPersons ){
				if( personNames == null || personNames.trim().isEmpty() ){
					personNames = personNames + okrWorkPerson_tmp.getEmployeeName();
				}else{
					personNames = "," + personNames + okrWorkPerson_tmp.getEmployeeName();
				}
				
				if( personIdentities == null || personIdentities.trim().isEmpty() ){
					personIdentities = personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}else{
					personIdentities = "," + personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}
				
				if( personOrganizations == null || personOrganizations.trim().isEmpty() ){
					personOrganizations = personOrganizations + okrWorkPerson_tmp.getOrganizationName();
				}else{
					personOrganizations = "," + personOrganizations + okrWorkPerson_tmp.getOrganizationName();
				}
				
				if( personCompanies == null || personCompanies.trim().isEmpty() ){
					personCompanies = personCompanies + okrWorkPerson_tmp.getCompanyName();
				}else{
					personCompanies = "," + personCompanies + okrWorkPerson_tmp.getCompanyName();
				}				
			}
			okrWorkBaseInfo.setCooperateCompanyName( personCompanies );
			okrWorkBaseInfo.setCooperateOrganizationName( personOrganizations );
			okrWorkBaseInfo.setCooperateIdentity( personIdentities );
			okrWorkBaseInfo.setCooperateEmployeeName( personNames );
		}
		return okrWorkBaseInfo;
		
	}

	private OkrWorkBaseInfo composeResponsibilityWorkPersonInfo( OkrWorkBaseInfo okrWorkBaseInfo, String centerId, String workId, String employeeIdentity ) throws Exception {
		OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
		String personNames = "";
		String personIdentities = "";
		String personOrganizations = "";
		String personCompanies = "";
		List<String> statuses = new ArrayList<String>();
		List<String> ids_tmp = null;
		List<OkrWorkPerson> okrWorkPersons = null;
		
		statuses.add( "正常" );
		
		ids_tmp = okrWorkPersonService.listByWorkAndIdentity( centerId, workId, employeeIdentity, "责任者", statuses );
		
		if( ids_tmp != null && !ids_tmp.isEmpty() ){
			okrWorkPersons = okrWorkPersonService.list( ids_tmp );
		}
		
		if( okrWorkPersons != null && !okrWorkPersons.isEmpty() ){
			for( OkrWorkPerson okrWorkPerson_tmp : okrWorkPersons ){
				if( personNames == null || personNames.trim().isEmpty() ){
					personNames = personNames + okrWorkPerson_tmp.getEmployeeName();
				}else{
					personNames = "," + personNames + okrWorkPerson_tmp.getEmployeeName();
				}
				
				if( personIdentities == null || personIdentities.trim().isEmpty() ){
					personIdentities = personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}else{
					personIdentities = "," + personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}
				
				if( personOrganizations == null || personOrganizations.trim().isEmpty() ){
					personOrganizations = personOrganizations + okrWorkPerson_tmp.getOrganizationName();
				}else{
					personOrganizations = "," + personOrganizations + okrWorkPerson_tmp.getOrganizationName();
				}
				
				if( personCompanies == null || personCompanies.trim().isEmpty() ){
					personCompanies = personCompanies + okrWorkPerson_tmp.getCompanyName();
				}else{
					personCompanies = "," + personCompanies + okrWorkPerson_tmp.getCompanyName();
				}				
			}
			
			okrWorkBaseInfo.setResponsibilityCompanyName( personCompanies );
			okrWorkBaseInfo.setResponsibilityOrganizationName( personOrganizations );
			okrWorkBaseInfo.setResponsibilityIdentity( personIdentities );
			okrWorkBaseInfo.setResponsibilityEmployeeName( personNames );
		}
		return okrWorkBaseInfo;
	}

	/**
	 * 授权收回服务
	 * 
	 * 1、根据工作ID，授权者身份，查询级别最小的一次授权信息
	 * 2、将该工作的责任者修改为授权者身份
	 * 3、更新工作基础信息
	 * 4、将该授权以及后续授权产生的所有干系人影响删除（已删除，失效或者已回收）
	 * 5、将该授权记录以及后续授权记录进行标识
	 * 6、为授权者生成待办，并且删除该工作最终的责任者的待办
	 * 
	 * @param okrWorkBaseInfo
	 * @param authorizeIdentity
	 * @throws Exception 
	 */
	public void tackback( OkrWorkBaseInfo okrWorkBaseInfo, String authorizeIdentity ) throws Exception {
		
		if( okrWorkBaseInfo == null ){
			throw new Exception("okrWorkBaseInfo is null!");
		}
		
		if( authorizeIdentity == null || authorizeIdentity.isEmpty() ){
			throw new Exception("authorizeIdentity is null!");
		}
		
		//logger.debug( ">>>>>>>>>>>>>>>>>>>系统正在进行授权收回[" + authorizeIdentity + "......" );
		
		List<String> statuses = new ArrayList<String>();
		List<String> ids = null;
		List<String> ids_task = null;
		List<String> ids_workPerson = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord_tmp = null;
		OkrWorkPerson okrWorkPerson = null;
		OkrTask okrTask = null;
		String oldResponsibilityCompanyName = null;
		String oldResponsibilityEmployeeName = null;
		String oldResponsibilityIdentity = null;
		String oldResponsibilityOrganizationName = null;

		Integer delegateLevel = 0;
		boolean check = true;
		Business business = null;
		
		statuses.add( "正常" );

		//logger.debug( ">>>>>>>>>>>>>>>>>>>根据工作ID["+okrWorkBaseInfo.getId()+"]，授权者身份["+authorizeIdentity+"]，查询级别最小的一次授权信息......" );
		okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getFirstAuthorizeRecord( okrWorkBaseInfo.getId(), authorizeIdentity );
		
		if( okrWorkAuthorizeRecord == null ){
			check = false;
			logger.error( "okrWorkAuthorizeRecord{'workId':'"+okrWorkBaseInfo.getId()+"','delegateIdentity':'"+authorizeIdentity+"'} not exists。" );
			throw new Exception("授权信息不存在，无法进行授权收回操作。");
		}
		
		if( check ){
			
			delegateLevel = okrWorkAuthorizeRecord.getDelegateLevel();
			//logger.debug( ">>>>>>>>>>>>>>>>>>>处理授权的级别：" + delegateLevel );
			
			//logger.debug( ">>>>>>>>>>>>>>>>>>>获取后续所有的工作授权记录列表，包括授权者最小一次的授权信息......" );
			try{
				ids = okrWorkAuthorizeRecordService.listByAuthorizor( okrWorkBaseInfo.getId(), authorizeIdentity, delegateLevel );
			}catch(Exception e ){
				check = false;
				throw e;
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////////
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( OkrWorkAuthorizeRecord.class );
			emc.beginTransaction( OkrWorkPerson.class );
			emc.beginTransaction( OkrWorkBaseInfo.class );
			emc.beginTransaction( OkrTask.class );
			emc.beginTransaction( OkrTaskHandled.class );
			
			if (check) {
				if (ids != null && !ids.isEmpty()) {
					//logger.debug(">>>>>>>>>>>>>>>>>>>处理授权记录信息......");
					for (String id : ids) {
						okrWorkAuthorizeRecord_tmp = emc.find(id, OkrWorkAuthorizeRecord.class);
						if ( okrWorkAuthorizeRecord_tmp.getId().equalsIgnoreCase( okrWorkAuthorizeRecord.getId()) ) {
							okrWorkAuthorizeRecord_tmp.setTakebackDateTime(new Date());
							okrWorkAuthorizeRecord_tmp.setStatus("已收回");
							//logger.debug(">>>>>>>>>>>>>>>>>>>修改授权记录为已收回，[" + okrWorkAuthorizeRecord_tmp.getId() + ", "
							//		+ okrWorkAuthorizeRecord_tmp.getDelegatorIdentity() + "]......");
						} else {
							okrWorkAuthorizeRecord_tmp.setStatus("已失效");
							//logger.debug(">>>>>>>>>>>>>>>>>>>修改授权记录为已失效，[" + okrWorkAuthorizeRecord_tmp.getId() + ", "
							//		+ okrWorkAuthorizeRecord_tmp.getDelegatorIdentity() + "]......");
						}
						emc.check(okrWorkAuthorizeRecord_tmp, CheckPersistType.all);
					}
				}
			}

			if (check) {
				if (ids != null && !ids.isEmpty()) {
					//logger.debug(">>>>>>>>>>>>>>>>>>>处理授权记录影响的工作干系人信息......");
					ids_workPerson = okrWorkPersonService.listByAuthorizeRecordIds( ids, statuses );
					if (ids_workPerson != null && !ids_workPerson.isEmpty()) {
						for (String id : ids_workPerson) {
							okrWorkPerson = emc.find(id, OkrWorkPerson.class);
							if( "责任者".equals( okrWorkPerson.getProcessIdentity() )){
								oldResponsibilityCompanyName = okrWorkPerson.getCompanyName();
								oldResponsibilityEmployeeName = okrWorkPerson.getEmployeeName();
								oldResponsibilityIdentity = okrWorkPerson.getEmployeeIdentity();
								oldResponsibilityOrganizationName = okrWorkPerson.getOrganizationName();
							}
							okrWorkPerson.setStatus("已删除");
							emc.check( okrWorkPerson, CheckPersistType.all );
						}
					}
				}
			}

			if ( check ) {
				logger.debug(">>>>>>>>>>>>>>>>>>>为新的责任者添加干系人信息......");
				okrWorkPerson = new OkrWorkPerson();
				okrWorkPerson.setCenterId( okrWorkBaseInfo.getCenterId() );
				okrWorkPerson.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
				okrWorkPerson.setWorkId( okrWorkBaseInfo.getId() );
				okrWorkPerson.setParentWorkId( okrWorkBaseInfo.getParentWorkId() );
				okrWorkPerson.setWorkTitle( okrWorkBaseInfo.getTitle() );
				okrWorkPerson.setWorkType( okrWorkBaseInfo.getWorkType() );
				okrWorkPerson.setWorkDateTimeType( okrWorkBaseInfo.getWorkDateTimeType() );
				okrWorkPerson.setWorkLevel( okrWorkBaseInfo.getWorkLevel() );
				okrWorkPerson.setWorkProcessStatus( okrWorkBaseInfo.getWorkProcessStatus() );						
				okrWorkPerson.setEmployeeName( okrWorkAuthorizeRecord.getDelegatorName() );
				okrWorkPerson.setEmployeeIdentity( okrWorkAuthorizeRecord.getDelegatorIdentity() );
				okrWorkPerson.setOrganizationName( okrWorkAuthorizeRecord.getDelegatorOrganizationName() );
				okrWorkPerson.setCompanyName( okrWorkAuthorizeRecord.getDelegatorCompanyName() );
				okrWorkPerson.setDeployMonth( okrWorkBaseInfo.getDeployMonth() );
				okrWorkPerson.setDeployYear( okrWorkBaseInfo.getDeployYear() );						
				okrWorkPerson.setIsCompleted( okrWorkBaseInfo.getIsCompleted() );						
				okrWorkPerson.setIsOverTime( okrWorkBaseInfo.getIsOverTime() );						
				okrWorkPerson.setProcessIdentity( "责任者" );
				//logger.debug( ">>>>>>>>>>>>>>>>>>>添加工作处理身份" + okrWorkAuthorizeRecord.getDelegatorIdentity() );
				emc.persist( okrWorkPerson, CheckPersistType.all );
			}
			
			//先提交一次
			if ( check ) {
				emc.commit();
			}
			
			//重新组织工作的干系人数据,责任者，更新工作基础信息数据
			if( check ){
				emc.beginTransaction( OkrWorkBaseInfo.class );				
				okrWorkBaseInfo = emc.find( okrWorkBaseInfo.getId(), OkrWorkBaseInfo.class );				
				composeResponsibilityWorkPersonInfo( okrWorkBaseInfo, null, okrWorkBaseInfo.getId(), okrWorkAuthorizeRecord.getDelegatorIdentity() );
				emc.check( okrWorkBaseInfo, CheckPersistType.all);
				emc.commit();
			}
			
			//处理待办信息
			if( check ){
				//为新的责任者新建待办，如果已经存在待办信息，则不需要添加
				ids_task = business.okrTaskFactory().listIdsByCenterAndPerson( okrWorkPerson.getCenterId(), okrWorkAuthorizeRecord.getDelegatorIdentity(), "中心工作" );
				if ( ids_task == null || ids_task.isEmpty() ) {
					//添加待办信息
					okrTask = new OkrTask();
					okrTask.setTitle( okrWorkPerson.getCenterTitle() );
					okrTask.setCenterId( okrWorkPerson.getCenterId() );
					okrTask.setCenterTitle( okrWorkPerson.getCenterTitle() );
					okrTask.setWorkType( okrWorkPerson.getWorkType() );
					okrTask.setTargetIdentity( okrWorkAuthorizeRecord.getDelegatorIdentity() );
					okrTask.setTargetName( okrWorkAuthorizeRecord.getDelegatorName() );
					okrTask.setTargetOrganizationName( okrWorkAuthorizeRecord.getDelegatorOrganizationName() );
					okrTask.setTargetCompanyName( okrWorkAuthorizeRecord.getDelegatorCompanyName() );
					okrTask.setActivityName("工作确认");
					okrTask.setArriveDateTime( new Date() );
					okrTask.setArriveDateTimeStr( dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					okrTask.setDynamicObjectId( okrWorkPerson.getCenterId() );
					okrTask.setDynamicObjectTitle( okrWorkPerson.getCenterTitle() );
					okrTask.setDynamicObjectType("中心工作");
					okrTask.setProcessType("TASK");
					okrTask.setStatus("正常");
					okrTask.setViewUrl("");
					//logger.debug( ">>>>>>>>>>>>>>>>>>>为授权者 - > 责任者[" + oldResponsibilityIdentity + "]添加一条待办信息......" );
					emc.persist( okrTask, CheckPersistType.all );
				}
				
				//删除原责任者的待办，如果没有待办就不需要处理了
				ids_task = business.okrTaskFactory().listIdsByTargetActivityAndObjId( "中心工作", okrWorkBaseInfo.getCenterId(), null, oldResponsibilityIdentity );
				if ( ids_task != null && !ids_task.isEmpty() ) {
					// 判断该中心工作下是否仍有授权者需要部署和拆解的工作， workPerson表，有责任者是旧授权者oldResponsibilityIdentity的有效记录
					//logger.debug(">>>>>>>>>>>>>>>>>>>查询工作干系人中是否仍有员工["+ authorizeIdentity +"]为责任者的信息......");
					ids_workPerson = business.okrWorkPersonFactory().listWorkByCenterAndIdentity( 
							okrWorkBaseInfo.getCenterId(),
							oldResponsibilityIdentity,
							"责任者",
							statuses
					);
					if ( ids_workPerson == null || ids_workPerson.isEmpty() ) {//已经没有需要部署的工作了
						// 删除所有的待办信息
						for ( String _id : ids_task ) {
							okrTask = emc.find( _id, OkrTask.class );
							if ( okrTask != null ) {
								emc.remove( okrTask, CheckRemoveType.all );
							}
						}
					}
				}
				emc.commit();
			}
			//logger.debug( ">>>>>>>>>>>>>>>>>>>工作授权收回完成。" );
		} catch (Exception e) {
			check = false;
			throw e;
		}
	}	
}