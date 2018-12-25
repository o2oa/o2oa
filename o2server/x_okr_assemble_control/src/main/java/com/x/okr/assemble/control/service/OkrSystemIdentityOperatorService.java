package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.identity.entity.ErrorIdentityRecord;
import com.x.okr.assemble.control.jaxrs.identity.entity.ErrorIdentityRecords;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrConfigSecretary;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrErrorIdentityRecords;
import com.x.okr.entity.OkrErrorSystemIdentityInfo;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;

/**
 * 全系统范围内处理人员身份问题
 *  1、全系统身份替换功能
	2、无效身份检查功能
	   1)无效身份涉及的数据展现出来
	   2)无效身份的替换
	   
 * @author 李义
 *
 */
public class OkrSystemIdentityOperatorService {
	private static  Logger logger = LoggerFactory.getLogger( OkrSystemIdentityOperatorService.class );
	OkrSystemIdentityQueryService systemIdentityQueryService = new OkrSystemIdentityQueryService();
	/**
	 * 在系统中分析所有的不正常身份，并且统计到数据库中
	 * @throws Exception
	 */
	public void checkAllAbnormalIdentityInSystem() throws Exception {
		logger.info("系统开始尝试分析不正常的系统身份以及所涉及的信息......" );
		String flag = new DateOperation().getNowTimeChar();
		
		List<String> identities_ok = new ArrayList<>();
		List<String> identities_error = new ArrayList<>();
		
		checkIdentityInWork( identities_ok, identities_error );
		checkIdentityInReport( identities_ok, identities_error );
		checkIdentityInDynamics( identities_ok, identities_error );
		checkIdentityInTask( identities_ok, identities_error );
		checkIdentityInConfig( identities_ok, identities_error );
		checkIdentityInStatistic( identities_ok, identities_error );
		
		int number = 0;
		int total = identities_error.size();
		
		//处理所有的非正常人员身份信息，查询所有的人员涉及到的信息列表，并且存储为JSON
		for( String identity : identities_error ){
			number++;
			logger.info("正在汇总不正常的系统身份("+number+"/"+total+")[" + identity + "]所涉及的信息......" );
			//查询该身份所有涉及的数据信息列表
			String content = null;			
			List<ErrorIdentityRecords> errorRecordsList = new ArrayList<>();
			ErrorIdentityRecords errorRecords = null;			
			try {
				errorRecords = getErrorRecordsInWork( identity );
				if ( errorRecords != null ){
					errorRecordsList.add( errorRecords );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				errorRecords = getErrorRecordsInReport( identity );
				if ( errorRecords != null ){
					errorRecordsList.add( errorRecords );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				errorRecords = getErrorRecordsInDynamics( identity );
				if ( errorRecords != null ){
					errorRecordsList.add( errorRecords );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				errorRecords = getErrorRecordsInTask( identity );
				if ( errorRecords != null ){
					errorRecordsList.add( errorRecords );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				errorRecords = getErrorRecordsInConfig( identity );
				if ( errorRecords != null ){
					errorRecordsList.add( errorRecords );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				errorRecords = getErrorRecordsInStatistic( identity );
				if ( errorRecords != null ){
					errorRecordsList.add( errorRecords );
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			Gson gson = XGsonBuilder.instance();
			if( errorRecordsList != null && !errorRecordsList.isEmpty() ){
				content = gson.toJson( errorRecordsList );
			}else{
				content = "{}";
			}
			
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				
				OkrErrorSystemIdentityInfo okrErrorSystemIdentityInfo = null;
				OkrErrorIdentityRecords okrErrorIdentityRecords = null;
				List<OkrErrorSystemIdentityInfo> errorSystemIdentityInfoList = null;
				List<OkrErrorIdentityRecords> errorIdentityRecordsList = null;
				
				Business business = new Business(emc);
				
				//根据identity查询指定的数据记录是否存在
				errorSystemIdentityInfoList = business.okrErrorSystemIdentityInfoFactory().listByIdentityName( identity );
				errorIdentityRecordsList = business.okrErrorSystemIdentityInfoFactory().listRecordsByIdentityName( identity );
				
				emc.beginTransaction( OkrErrorSystemIdentityInfo.class );
				emc.beginTransaction( OkrErrorIdentityRecords.class );
				if( errorSystemIdentityInfoList != null && !errorSystemIdentityInfoList.isEmpty() ){
					for( int i=0; i<errorSystemIdentityInfoList.size(); i++ ){
						if( i == 0 ){
							okrErrorSystemIdentityInfo = errorSystemIdentityInfoList.get( i );
						}else{
							emc.remove( errorSystemIdentityInfoList.get( i ), CheckRemoveType.all );
						}
					}
				}
				if( errorIdentityRecordsList != null && !errorIdentityRecordsList.isEmpty() ){
					for( int i=0; i<errorIdentityRecordsList.size(); i++ ){
						if( i == 0 ){
							okrErrorIdentityRecords = errorIdentityRecordsList.get( i );
						}else{
							emc.remove( errorIdentityRecordsList.get( i ), CheckRemoveType.all );
						}
					}
				}
				if( okrErrorSystemIdentityInfo == null ){
					if( okrErrorIdentityRecords != null ){
						emc.remove( okrErrorIdentityRecords, CheckRemoveType.all );
					}
					//重新保存新的对象
					okrErrorSystemIdentityInfo = new OkrErrorSystemIdentityInfo();
					okrErrorSystemIdentityInfo.setIdentity(identity);
					okrErrorSystemIdentityInfo.setFlag(flag);
					
					okrErrorIdentityRecords =  new OkrErrorIdentityRecords();
					okrErrorIdentityRecords.setId( okrErrorSystemIdentityInfo.getId() );
					okrErrorIdentityRecords.setRecordsJson( content );
					okrErrorIdentityRecords.setIdentity(identity);
					okrErrorIdentityRecords.setFlag(flag);
					
					emc.persist( okrErrorSystemIdentityInfo, CheckPersistType.all );
					emc.persist( okrErrorIdentityRecords, CheckPersistType.all );
				}else{
					//更新原有对象到数据库中
					okrErrorSystemIdentityInfo.setFlag( flag );
					okrErrorIdentityRecords.setFlag( flag );
					okrErrorIdentityRecords.setRecordsJson( content );
					
					emc.check( okrErrorSystemIdentityInfo, CheckPersistType.all );
					emc.check( okrErrorIdentityRecords, CheckPersistType.all );
				}
				emc.commit();
			}catch( Exception e ){
				e.printStackTrace();
			}
		}
		
		//删除所有未更新的数据
		List<OkrErrorSystemIdentityInfo> errorSystemIdentityInfoList = null;
		List<OkrErrorIdentityRecords> errorIdentityRecordsList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc);
			errorSystemIdentityInfoList = business.okrErrorSystemIdentityInfoFactory().listNotFlag(flag);
			errorIdentityRecordsList = business.okrErrorSystemIdentityInfoFactory().listRecordNotFlag(flag);
			emc.beginTransaction( OkrErrorSystemIdentityInfo.class );
			emc.beginTransaction( OkrErrorIdentityRecords.class );
			for( OkrErrorSystemIdentityInfo okrErrorSystemIdentityInfo : errorSystemIdentityInfoList ){
				emc.remove( okrErrorSystemIdentityInfo, CheckRemoveType.all );
			}
			for( OkrErrorIdentityRecords okrErrorIdentityRecords : errorIdentityRecordsList ){
				emc.remove( okrErrorIdentityRecords, CheckRemoveType.all );
			}
			emc.commit();
			
		}catch( Exception e ){
			e.printStackTrace();
		}
		logger.info("系统开始尝试分析不正常的系统身份以及所涉及的信息执行完成。" );
	}
	
	public void checkAllAbnormalIdentityInSystem( String targetIdentity, String flag ) throws Exception {
		logger.info("系统开始尝试分析不正常的系统身份["+targetIdentity+"]以及该身份所涉及的信息......" );
		if( flag == null || flag.isEmpty() ){
			flag = new DateOperation().getNowTimeChar();
		}
		//处理所有的非正常人员身份信息，查询所有的人员涉及到的信息列表，并且存储为JSON
		//查询该身份所有涉及的数据信息列表
		String content = null;
		Boolean hasErrorRecords = false;
		List<ErrorIdentityRecords> errorRecordsList = new ArrayList<>();
		ErrorIdentityRecords errorRecords = null;
		try {
			errorRecords = getErrorRecordsInWork( targetIdentity );
			if ( errorRecords != null ){
				errorRecordsList.add( errorRecords );
				if( errorRecords.getErrorRecords() != null && !errorRecords.getErrorRecords().isEmpty() ){
					hasErrorRecords = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			errorRecords = getErrorRecordsInReport( targetIdentity );
			if ( errorRecords != null ){
				errorRecordsList.add( errorRecords );
				if( errorRecords.getErrorRecords() != null && !errorRecords.getErrorRecords().isEmpty() ){
					hasErrorRecords = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			errorRecords = getErrorRecordsInDynamics( targetIdentity );
			if ( errorRecords != null ){
				errorRecordsList.add( errorRecords );
				if( errorRecords.getErrorRecords() != null && !errorRecords.getErrorRecords().isEmpty() ){
					hasErrorRecords = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			errorRecords = getErrorRecordsInTask( targetIdentity );
			if ( errorRecords != null ){
				errorRecordsList.add( errorRecords );
				if( errorRecords.getErrorRecords() != null && !errorRecords.getErrorRecords().isEmpty() ){
					hasErrorRecords = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			errorRecords = getErrorRecordsInConfig( targetIdentity );
			if ( errorRecords != null ){
				errorRecordsList.add( errorRecords );
				if( errorRecords.getErrorRecords() != null && !errorRecords.getErrorRecords().isEmpty() ){
					hasErrorRecords = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			errorRecords = getErrorRecordsInStatistic( targetIdentity );
			if ( errorRecords != null ){
				errorRecordsList.add( errorRecords );
				if( errorRecords.getErrorRecords() != null && !errorRecords.getErrorRecords().isEmpty() ){
					hasErrorRecords = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Gson gson = XGsonBuilder.instance();
		if( errorRecordsList != null && !errorRecordsList.isEmpty() ){
			content = gson.toJson( errorRecordsList );
		}else{
			content = "{}";
		}
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			OkrErrorSystemIdentityInfo okrErrorSystemIdentityInfo = null;
			OkrErrorIdentityRecords okrErrorIdentityRecords = null;
			List<OkrErrorSystemIdentityInfo> errorSystemIdentityInfoList = null;
			List<OkrErrorIdentityRecords> errorIdentityRecordsList = null;
			Business business = new Business(emc);
			//根据identity查询指定的数据记录是否存在
			errorSystemIdentityInfoList = business.okrErrorSystemIdentityInfoFactory().listByIdentityName( targetIdentity );
			errorIdentityRecordsList = business.okrErrorSystemIdentityInfoFactory().listRecordsByIdentityName( targetIdentity );
			
			emc.beginTransaction( OkrErrorSystemIdentityInfo.class );
			emc.beginTransaction( OkrErrorIdentityRecords.class );
			if( errorSystemIdentityInfoList != null && !errorSystemIdentityInfoList.isEmpty() ){
				for( int i=0; i < errorSystemIdentityInfoList.size(); i++ ){
					if( i == 0 ){
						okrErrorSystemIdentityInfo = errorSystemIdentityInfoList.get( i );
					}else{
						emc.remove( errorSystemIdentityInfoList.get( i ), CheckRemoveType.all );
					}
				}
			}
			
			if( errorIdentityRecordsList != null && !errorIdentityRecordsList.isEmpty() ){
				for( int i=0; i<errorIdentityRecordsList.size(); i++ ){
					if( i == 0 ){
						okrErrorIdentityRecords = errorIdentityRecordsList.get( i );
					}else{
						emc.remove( errorIdentityRecordsList.get( i ), CheckRemoveType.all );
					}
				}
			}
			if( okrErrorSystemIdentityInfo == null ){
				if( okrErrorIdentityRecords != null ){
					emc.remove( okrErrorIdentityRecords, CheckRemoveType.all );
				}
				if( hasErrorRecords ){
					//重新保存新的对象
					okrErrorSystemIdentityInfo = new OkrErrorSystemIdentityInfo();
					okrErrorSystemIdentityInfo.setIdentity( targetIdentity );
					okrErrorSystemIdentityInfo.setFlag(flag);
					
					okrErrorIdentityRecords =  new OkrErrorIdentityRecords();
					okrErrorIdentityRecords.setId( okrErrorSystemIdentityInfo.getId() );
					okrErrorIdentityRecords.setRecordsJson( content );
					okrErrorIdentityRecords.setIdentity( targetIdentity );
					okrErrorIdentityRecords.setFlag(flag);
					
					emc.persist( okrErrorSystemIdentityInfo, CheckPersistType.all );
					emc.persist( okrErrorIdentityRecords, CheckPersistType.all );
				}
			}else{
				//更新原有对象到数据库中
				okrErrorSystemIdentityInfo.setFlag( flag );
				okrErrorIdentityRecords.setFlag( flag );
				okrErrorIdentityRecords.setRecordsJson( content );
				if( hasErrorRecords ){
					emc.check( okrErrorSystemIdentityInfo, CheckPersistType.all );
					emc.check( okrErrorIdentityRecords, CheckPersistType.all );
				}else{
					logger.info("身份["+targetIdentity+"]没有涉及的信息，删除错误身份记录......" );
					emc.remove( okrErrorSystemIdentityInfo, CheckRemoveType.all );
					emc.remove( okrErrorIdentityRecords, CheckRemoveType.all );
				}
			}
			emc.commit();
		}catch( Exception e ){
			e.printStackTrace();
		}
		logger.info("系统开始尝试分析不正常的系统身份以及所涉及的信息执行完成。" );
	}
	/**
	 * TODO:从数据统计信息中获取跟非正常身份有关的信息记录
	 * 
	 * OKR_STATISTIC_REPORT_CONTENT
	 * OKR_STATISTIC_REPORT_STATUS
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	private ErrorIdentityRecords getErrorRecordsInStatistic(String identity) throws Exception {
		Date now = new Date();
		ErrorIdentityRecord errorIdentityRecord = null;
		List<ErrorIdentityRecord> errorIdentityRecordList = new ArrayList<>();
		
		// OKR_STATISTIC_REPORT_CONTENT
		List<OkrStatisticReportContent> statisticReportContentRecords = systemIdentityQueryService.listErrorIdentitiesInStReportContent( identity, "all" );
		for( OkrStatisticReportContent okrStatisticReportContent : statisticReportContentRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrStatisticReportContent.getId(), identity, "OKR_STATISTIC_REPORT_CONTENT", okrStatisticReportContent.getWorkTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_STATISTIC_REPORT_STATUS
		List<OkrStatisticReportStatus> statisticReportStatusRecords = systemIdentityQueryService.listErrorIdentitiesInStReportStatus( identity, "all" );
		for( OkrStatisticReportStatus okrStatisticReportStatus : statisticReportStatusRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrStatisticReportStatus.getId(), identity, "OKR_STATISTIC_REPORT_STATUS", okrStatisticReportStatus.getWorkTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		
		ErrorIdentityRecords errorIdentityRecords = new ErrorIdentityRecords();
		errorIdentityRecords.setIdentity(identity);
		errorIdentityRecords.setRecordType( "数据统计" );
		errorIdentityRecords.setErrorRecords( errorIdentityRecordList );
		return errorIdentityRecords;
	}
	
	/**
	 * TODO:从系统配置信息中获取跟非正常身份有关的信息记录
	 * 
	 * OKR_CONFIG_SECRETARY
	 * OKR_CONFIG_SYSTEM
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	private ErrorIdentityRecords getErrorRecordsInConfig(String identity) throws Exception {
		Date now = new Date();
		ErrorIdentityRecord errorIdentityRecord = null;
		List<ErrorIdentityRecord> errorIdentityRecordList = new ArrayList<>();
		
		// OKR_CONFIG_SECRETARY
		List<OkrConfigSecretary> configSecretaryRecords = systemIdentityQueryService.listErrorIdentitiesInConfigSecretary( identity, "all" );
		for( OkrConfigSecretary okrConfigSecretary : configSecretaryRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrConfigSecretary.getId(), identity, "OKR_CONFIG_SECRETARY", okrConfigSecretary.getSecretaryIdentity() + "代理领导：" + okrConfigSecretary.getLeaderIdentity(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_CONFIG_SYSTEM
		List<OkrConfigSystem> configSystemRecords = systemIdentityQueryService.listErrorIdentitiesInConfigSystem( identity, "all" );
		for( OkrConfigSystem okrConfigSystem : configSystemRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrConfigSystem.getId(), identity, "OKR_CONFIG_SYSTEM", okrConfigSystem.getConfigValue(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		
		ErrorIdentityRecords errorIdentityRecords = new ErrorIdentityRecords();
		errorIdentityRecords.setIdentity(identity);
		errorIdentityRecords.setRecordType( "系统配置" );
		errorIdentityRecords.setErrorRecords( errorIdentityRecordList );
		return errorIdentityRecords;
	}
	
	/**
	 * TODO:从工作待办已办信息中获取跟非正常身份有关的信息记录
	 * 
	 * OKR_TASK
	 * OKR_TASKHANDLED
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	private ErrorIdentityRecords getErrorRecordsInTask(String identity) throws Exception {
		Date now = new Date();
		ErrorIdentityRecord errorIdentityRecord = null;
		List<ErrorIdentityRecord> errorIdentityRecordList = new ArrayList<>();
		
		// OKR_TASK
		List<OkrTask> taskRecords = systemIdentityQueryService.listErrorIdentitiesInTask( identity, "all" );
		for( OkrTask okrTask : taskRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrTask.getId(), identity, "OKR_TASK", okrTask.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_TASKHANDLED
		List<OkrTaskHandled> taskHandledRecords = systemIdentityQueryService.listErrorIdentitiesInTaskhandled( identity, "all" );
		for( OkrTaskHandled okrTaskHandled : taskHandledRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrTaskHandled.getId(), identity, "OKR_TASKHANDLED", okrTaskHandled.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		
		ErrorIdentityRecords errorIdentityRecords = new ErrorIdentityRecords();
		errorIdentityRecords.setIdentity(identity);
		errorIdentityRecords.setRecordType( "待办已办" );
		errorIdentityRecords.setErrorRecords( errorIdentityRecordList );
		return errorIdentityRecords;
	}
	
	/**
	 * TODO:从工作交流动态信息中获取跟非正常身份有关的信息记录
	 * 
	 * OKR_WORKCHAT
	 * OKR_WORKDYNAMICS
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	private ErrorIdentityRecords getErrorRecordsInDynamics(String identity) throws Exception {
		Date now = new Date();
		ErrorIdentityRecord errorIdentityRecord = null;
		List<ErrorIdentityRecord> errorIdentityRecordList = new ArrayList<>();
		
		// OKR_WORKCHAT
		List<OkrWorkChat> workChatRecords = systemIdentityQueryService.listErrorIdentitiesInWorkChat( identity, "all" );
		for( OkrWorkChat okrWorkChat : workChatRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrWorkChat.getId(), identity, "OKR_WORKCHAT", okrWorkChat.getWorkTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_WORKDYNAMICS
//		List<OkrWorkDynamics> workDynamicsRecords = systemIdentityQueryService.listErrorIdentitiesInDynamics( identity, "all" );
//		for( OkrWorkDynamics workDynamics : workDynamicsRecords ){
//			errorIdentityRecord = new ErrorIdentityRecord( workDynamics.getId(), identity, "OKR_WORKDYNAMICS", workDynamics.getDynamicObjectTitle(), now );
//			errorIdentityRecordList.add( errorIdentityRecord );
//		}
		
		ErrorIdentityRecords errorIdentityRecords = new ErrorIdentityRecords();
		errorIdentityRecords.setIdentity(identity);
		errorIdentityRecords.setRecordType( "交流动态" );
		errorIdentityRecords.setErrorRecords( errorIdentityRecordList );
		return errorIdentityRecords;
	}
	
	/**
	 * TODO:从工作汇报信息中获取跟非正常身份有关的信息记录
	 * 
	 * OKR_WORK_REPORTBASEINFO
	 * OKR_WORK_REPORT_PERSONLINK
	 * OKR_WORK_REPORT_PROCESSLOG
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	private ErrorIdentityRecords getErrorRecordsInReport(String identity) throws Exception {
		Date now = new Date();
		ErrorIdentityRecord errorIdentityRecord = null;
		List<ErrorIdentityRecord> errorIdentityRecordList = new ArrayList<>();
		
		// OKR_WORK_REPORTBASEINFO
		List<OkrWorkReportBaseInfo> reportInfoRecords = systemIdentityQueryService.listErrorIdentitiesInReportBaseInfo( identity, "all" );
		for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : reportInfoRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrWorkReportBaseInfo.getId(), identity, "OKR_WORK_REPORTBASEINFO", okrWorkReportBaseInfo.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_WORK_REPORT_PERSONLINK
		List<OkrWorkReportPersonLink> workReportPersonLinkRecords = systemIdentityQueryService.listErrorIdentitiesInReportPersonInfo( identity, "all" );
		for( OkrWorkReportPersonLink okrWorkReportPersonLink : workReportPersonLinkRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrWorkReportPersonLink.getId(), identity, "OKR_WORK_REPORT_PERSONLINK", okrWorkReportPersonLink.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_WORK_REPORT_PROCESSLOG 处理日志不检查
//		List<OkrWorkReportProcessLog> workReportProcessLogRecords = systemIdentityQueryService.listErrorIdentitiesInReportProcessLog( identity );
//		for( OkrWorkReportProcessLog okrWorkReportProcessLog : workReportProcessLogRecords ){
//			errorIdentityRecord = new ErrorIdentityRecord( okrWorkReportProcessLog.getId(), identity, "OKR_WORK_REPORT_PROCESSLOG", okrWorkReportProcessLog.getTitle(), now );
//			errorIdentityRecordList.add( errorIdentityRecord );
//		}
		
		ErrorIdentityRecords errorIdentityRecords = new ErrorIdentityRecords();
		errorIdentityRecords.setIdentity(identity);
		errorIdentityRecords.setRecordType( "工作汇报" );
		errorIdentityRecords.setErrorRecords( errorIdentityRecordList );
		return errorIdentityRecords;
	}
	
	/**
	 * TODO:从工作信息中获取跟非正常身份有关的信息记录
	 * 
	 * OKR_CENTERWORKINFO
	 * OKR_WORKBASEINFO
	 * OKR_WORK_AUTHORIZE_RECORD
	 * OKR_WORK_PERSON
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	private ErrorIdentityRecords getErrorRecordsInWork( String identity ) throws Exception {
		Date now = new Date();
		ErrorIdentityRecord errorIdentityRecord = null;
		List<ErrorIdentityRecord> errorIdentityRecordList = new ArrayList<>();
		
		// OKR_CENTERWORKINFO
		List<OkrCenterWorkInfo> centerRecords = systemIdentityQueryService.listErrorIdentitiesInCenterInfo( identity, "all" );
		for( OkrCenterWorkInfo okrCenterWorkInfo : centerRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrCenterWorkInfo.getId(), identity, "OKR_CENTERWORKINFO", okrCenterWorkInfo.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_WORKBASEINFO
		List<OkrWorkBaseInfo> workBaseRecords = systemIdentityQueryService.listErrorIdentitiesInWorkBaseInfo( identity, "all" );
		for( OkrWorkBaseInfo okrWorkBaseInfo : workBaseRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( okrWorkBaseInfo.getId(), identity, "OKR_WORKBASEINFO", okrWorkBaseInfo.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_WORK_AUTHORIZE_RECORD
		List<OkrWorkAuthorizeRecord> workAuthorizeRecords = systemIdentityQueryService.listErrorIdentitiesInAuthorizeRecord( identity, "all" );
		for( OkrWorkAuthorizeRecord workAuthorizeRecord : workAuthorizeRecords ){
			errorIdentityRecord = new ErrorIdentityRecord( workAuthorizeRecord.getId(), identity, "OKR_WORK_AUTHORIZE_RECORD", workAuthorizeRecord.getTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		// OKR_WORK_PERSON
		List<OkrWorkPerson> workPersons = systemIdentityQueryService.listErrorIdentitiesInWorkPerson( identity, "all" );
		for( OkrWorkPerson workPerson : workPersons ){
			errorIdentityRecord = new ErrorIdentityRecord( workPerson.getId(), identity, "OKR_WORK_PERSON", workPerson.getProcessIdentity() + ":" + workPerson.getWorkTitle(), now );
			errorIdentityRecordList.add( errorIdentityRecord );
		}
		
		ErrorIdentityRecords errorIdentityRecords = new ErrorIdentityRecords();
		errorIdentityRecords.setIdentity(identity);
		errorIdentityRecords.setRecordType( "工作信息" );
		errorIdentityRecords.setErrorRecords( errorIdentityRecordList );
		return errorIdentityRecords;
	}
	
	/**
	 * TODO:检查统计信息中所有的身份信息
	 * 
	 * OKR_STATISTIC_REPORT_CONTENT
	 * OKR_STATISTIC_REPORT_STATUS
	 * 
	 * @return
	 * @throws Exception
	 */
	private void checkIdentityInStatistic(List<String> identities_ok, List<String> identities_error) throws Exception {
		List<String> identities = null;	
		//OKR_STATISTIC_REPORT_CONTENT
		identities = systemIdentityQueryService.listIdentitiesInStReportContent( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );
		//OKR_STATISTIC_REPORT_STATUS
		identities = systemIdentityQueryService.listIdentitiesInStReportStatus( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );
	}

	/**
	 * TODO:检查配置信息中所有的身份信息
	 * 
	 * OKR_CONFIG_SECRETARY
	 * OKR_CONFIG_SYSTEM
	 * 
	 * @return
	 * @throws Exception
	 */
	private void checkIdentityInConfig(List<String> identities_ok, List<String> identities_error) throws Exception {
		List<String> identities = null;
		//OKR_CONFIG_SECRETARY
		identities = systemIdentityQueryService.listIdentitiesInSecretaryConfig( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_CONFIG_SYSTEM
		identities = systemIdentityQueryService.listIdentitiesInSystemConfig( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );
	}
	
	/**
	 * TODO:检查待办已办信息中所有的身份信息
	 * 
	 * OKR_TASK
	 * OKR_TASKHANDLED
	 * 
	 * @return
	 * @throws Exception
	 */
	private void checkIdentityInTask(List<String> identities_ok, List<String> identities_error) throws Exception {
		List<String> identities = null;
		//OKR_TASK
		identities = systemIdentityQueryService.listIdentitiesInTask( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_TASKHANDLED
		identities = systemIdentityQueryService.listIdentitiesInTaskHandled( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );
	}
	
	/**
	 * TODO:检查交流动态信息中所有的身份信息
	 * 
	 * OKR_WORKCHAT
	 * OKR_WORKDYNAMICS
	 * 
	 * @return
	 * @throws Exception
	 */
	private void checkIdentityInDynamics(List<String> identities_ok, List<String> identities_error) throws Exception {
		List<String> identities = null;
		//OKR_WORKCHAT
		identities = systemIdentityQueryService.listIdentitiesInWorkChat( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_WORKDYNAMICS
//		identities = systemIdentityQueryService.listIdentitiesInWorkDynamics( identities_ok, identities_error );
//		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );
	}
	
	/**
	 * TODO:检查工作信息中所有的身份信息
	 * 
	 * OKR_CENTERWORKINFO
	 * OKR_WORKBASEINFO
	 * OKR_WORK_AUTHORIZE_RECORD
	 * OKR_WORK_PERSON
	 * 
	 * @param identities_ok
	 * @param identities_error
	 * @throws Exception
	 */
	private void checkIdentityInWork( List<String> identities_ok, List<String> identities_error ) throws Exception {
		List<String> identities = null;
		//OKR_CENTERWORKINFO
		identities = listIdentitiesInCenterInfo( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_WORKBASEINFO
		identities = listIdentitiesInWorkBaseInfo( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_WORK_AUTHORIZE_RECORD
		identities = systemIdentityQueryService.listIdentitiesInWorkAuthorizeRecord( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_WORK_PERSON
		identities = systemIdentityQueryService.listIdentitiesInWorkPerson( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
	}

	/**
	 * TODO:查询表OKR_WORKBASEINFO中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	private List<String> listIdentitiesInWorkBaseInfo(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//cooperateIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctCooperateIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//creatorIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctCreatorIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//deployerIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctDeployerIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//reportLeaderIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctReportLeaderIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//responsibilityIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctResponsibilityIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * 查询表OKR_CENTERWORKINFO中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	private List<String> listIdentitiesInCenterInfo( List<String> identities_ok, List<String> identities_error ) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//auditLeaderIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctAuditLeaderIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//creatorIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctCreatorIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//deployerIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctDeployerIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
			//reportAuditLeaderIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctReportAuditLeaderIdentity( identities_ok, identities_error );
			result = systemIdentityQueryService.addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * TODO:检查汇报信息中所有的身份信息
	 * 
	 * OKR_WORK_REPORTBASEINFO
	 * OKR_WORK_REPORT_PERSONLINK
	 * OKR_WORK_REPORT_PROCESSLOG
	 * 
	 * @return
	 * @throws Exception
	 */
	private void checkIdentityInReport( List<String> identities_ok, List<String> identities_error ) throws Exception {
		List<String> identities = null;
		//OKR_WORK_REPORTBASEINFO
		identities = systemIdentityQueryService.listIdentitiesInReportBaseInfo( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_WORK_REPORT_PERSONLINK
		identities = systemIdentityQueryService.listIdentitiesInReportPersonInfo( identities_ok, identities_error );
		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );		
		//OKR_WORK_REPORT_PROCESSLOG
//		identities = systemIdentityQueryService.listIdentitiesInReportProcessLog( identities_ok, identities_error );
//		systemIdentityQueryService.checkIdentities( identities, identities_ok, identities_error );			
	}
	
	/**
	 * 将所有的数据中相关的身份修改为指定的身份，并且修改相应的姓名，组织以及顶层组织信息，如果需要的话
	 * 
	 * @param fromIdentityName
	 * @param toIdentityName
	 * @param recordType  数据类别
	 * @param tableName   数据表名
	 * @param recordId    数据记录ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public Boolean changeUserIdentity( String fromIdentityName, String toIdentityName, String recordType, String tableName, String recordId ) throws Exception{
		logger.info( "系统尝试将所有的数据中涉及["+fromIdentityName+"]修改为["+toIdentityName+"]......" );
		if( fromIdentityName == null ){
			throw new Exception("fromIdentityName 不允许为空！");
		}
		if( toIdentityName == null ){
			throw new Exception("toIdentityName 不允许为空！");
		}
		Boolean check = true;
		Boolean isSameUser = false;
		OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
		String personName = okrUserManagerService.getPersonNameByIdentity( toIdentityName );
		
		OkrUserCache toUserCache = new OkrUserCache();
		toUserCache.setLoginUserName( personName );
		toUserCache.setLoginIdentityName( toIdentityName );
		toUserCache.setLoginUserUnitName( okrUserManagerService.getUnitNameByIdentity( toIdentityName ) );
		toUserCache.setLoginUserTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( toIdentityName ) );
		
		//判断是不是由于组织调整导致身份无效，如果是同一个人，那么是所有记录的身份都可以转过来的，除了处理日志
		//如果不是同一个人，那么部分身份是不应该转到新的员工身份的，如已办
		if( fromIdentityName.split("\\(") != null && fromIdentityName.split("\\(").length > 0 && fromIdentityName.split("\\(")[0].trim().equalsIgnoreCase( personName )){
			isSameUser = true;
		}
		
		//处理所有fromIdentityName涉及的数据信息
		if( check ){
			if( "工作信息".equals( recordType ) || "all".equalsIgnoreCase( recordType )){
				try {
					logger.info("系统正在修改工作信息相关数据：将["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]");
					check = changeUserIdentityInWork( fromIdentityName, toUserCache, isSameUser, tableName, recordId );
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		if( check ){
			if( "工作汇报".equals( recordType ) || "all".equalsIgnoreCase( recordType )){
				try {
					logger.info("系统正在修改工作汇报相关数据：将["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]");
					check = changeUserIdentityInReport( fromIdentityName, toUserCache, isSameUser, tableName, recordId );
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		if( check ){
			if( "交流动态".equals( recordType ) || "all".equalsIgnoreCase( recordType )){
				try {
					logger.info("系统正在修改交流动态相关数据：将["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]");
					check = changeUserIdentityInDynamics( fromIdentityName, toUserCache, isSameUser, tableName, recordId );
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		if( check ){
			if( "待办已办".equals( recordType ) || "all".equalsIgnoreCase( recordType )){
				try {
					logger.info("系统正在修改待办已办相关数据：将["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]");
					check = changeUserIdentityInTask( fromIdentityName, toUserCache, isSameUser, tableName, recordId );
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		if( check ){
			if( "系统配置".equals( recordType ) || "all".equalsIgnoreCase( recordType )){
				try {
					logger.info("系统正在修改系统配置相关数据：将["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]");
					check = changeUserIdentityInConfig( fromIdentityName, toUserCache, isSameUser, tableName, recordId );
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		if( check ){
			if( "数据统计".equals( recordType ) || "all".equalsIgnoreCase( recordType )){
				try {
					logger.info("系统正在修改统计数据相关数据：将["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]");
					check = changeUserIdentityInStatistic( fromIdentityName, toUserCache, isSameUser, tableName, recordId );
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		logger.info( "将系统中所有的数据中涉及["+fromIdentityName+"]修改为["+toUserCache.getLoginIdentityName()+"]处理完成。" );
		return true;
	}
	/**
	 * TODO:修改数据表中交流和动态相关的信息<br/><br/>
	 * 
	 * 涉及数据表：<br/>OKR_STATISTIC_REPORT_CONTENT,OKR_STATISTIC_REPORT_STATUS
	 * 
	 * @param fromIdentityName
	 * @param isSameUser 
	 * @param recordId 
	 * @param tableName 
	 * @param okrUserCache
	 * @return
	 * @throws Exception 
	 */
	private Boolean changeUserIdentityInStatistic(String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser, String tableName, String recordId) throws Exception {
		// OKR_STATISTIC_REPORT_CONTENT
		if( "all".equalsIgnoreCase( tableName ) || "OKR_STATISTIC_REPORT_CONTENT".equalsIgnoreCase( tableName )){
			List<OkrStatisticReportContent> statisticReportContentRecords = systemIdentityQueryService.listErrorIdentitiesInStReportContent( fromIdentityName, recordId );
			if( statisticReportContentRecords != null && !statisticReportContentRecords.isEmpty() ){
				for( OkrStatisticReportContent okrStatisticReportContent : statisticReportContentRecords ){
					systemIdentityQueryService.changeUserIdentityInStReportContent( okrStatisticReportContent.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		// OKR_STATISTIC_REPORT_STATUS
		if( "all".equalsIgnoreCase( tableName ) || "OKR_STATISTIC_REPORT_STATUS".equalsIgnoreCase( tableName )){
			List<OkrStatisticReportStatus> statisticReportStatusRecords = systemIdentityQueryService.listErrorIdentitiesInStReportStatus( fromIdentityName, recordId );
			if( statisticReportStatusRecords != null && !statisticReportStatusRecords.isEmpty() ){
				for( OkrStatisticReportStatus okrStatisticReportStatus : statisticReportStatusRecords ){
					systemIdentityQueryService.changeUserIdentityInStReportStatus( okrStatisticReportStatus.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		
		return true;
	}
	/**
	 * TODO:修改数据表中交流和动态相关的信息<br/><br/>
	 * 
	 * 涉及数据表：<br/>OKR_CONFIG_SECRETARY,OKR_CONFIG_SYSTEM
	 * 
	 * @param fromIdentityName
	 * @param isSameUser 
	 * @param recordId 
	 * @param tableName 
	 * @param okrUserCache
	 * @return
	 * @throws Exception 
	 */
	private Boolean changeUserIdentityInConfig( String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser, String tableName, String recordId ) throws Exception {
		// OKR_CONFIG_SECRETARY
		if( "all".equalsIgnoreCase( tableName ) || "OKR_CONFIG_SECRETARY".equalsIgnoreCase( tableName )){
			List<OkrConfigSecretary> configSecretaryRecords = systemIdentityQueryService.listErrorIdentitiesInConfigSecretary( fromIdentityName, recordId );
			if( configSecretaryRecords != null && !configSecretaryRecords.isEmpty() ){
				for( OkrConfigSecretary okrConfigSecretary : configSecretaryRecords ){
					systemIdentityQueryService.changeUserIdentityInConfigSecretary( okrConfigSecretary.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		// OKR_CONFIG_SYSTEM
		if( "all".equalsIgnoreCase( tableName ) || "OKR_CONFIG_SYSTEM".equalsIgnoreCase( tableName )){
			List<OkrConfigSystem> configSystemRecords = systemIdentityQueryService.listErrorIdentitiesInConfigSystem( fromIdentityName, recordId );
			if( configSystemRecords != null && !configSystemRecords.isEmpty() ){
				for( OkrConfigSystem okrConfigSystem : configSystemRecords ){
					systemIdentityQueryService.changeUserIdentityInConfigSystem( okrConfigSystem.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
			
		}
		return true;
	}
	/**
	 * TODO:修改数据表中交流和动态相关的信息<br/><br/>
	 * 
	 * 涉及数据表：<br/>OKR_TASK,OKR_TASKHANDLED
	 * 
	 * @param fromIdentityName
	 * @param isSameUser 
	 * @param recordId 
	 * @param tableName 
	 * @param okrUserCache
	 * @return
	 * @throws Exception 
	 */
	private Boolean changeUserIdentityInTask( String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser, String tableName, String recordId ) throws Exception {
		// OKR_TASK
		if( "all".equalsIgnoreCase( tableName ) || "OKR_TASK".equalsIgnoreCase( tableName )){
			List<OkrTask> taskRecords = systemIdentityQueryService.listErrorIdentitiesInTask( fromIdentityName, recordId );
			if( taskRecords != null && !taskRecords.isEmpty() ){
				for( OkrTask okrTask : taskRecords ){
					systemIdentityQueryService.changeUserIdentityInTask( okrTask.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
			
		}
		// OKR_TASKHANDLED
		if( "all".equalsIgnoreCase( tableName ) || "OKR_TASKHANDLED".equalsIgnoreCase( tableName )){
			List<OkrTaskHandled> taskHandledRecords = systemIdentityQueryService.listErrorIdentitiesInTaskhandled( fromIdentityName, recordId );
			if( taskHandledRecords != null && !taskHandledRecords.isEmpty() ){
				for( OkrTaskHandled okrTaskHandled : taskHandledRecords ){
					systemIdentityQueryService.changeUserIdentityInTaskhandled( okrTaskHandled.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		return true;
	}
	/**
	 * TODO:修改数据表中交流和动态相关的信息<br/><br/>
	 * 
	 * 涉及数据表：<br/>OKR_WORKCHAT,OKR_WORKDYNAMICS
	 * 
	 * @param fromIdentityName
	 * @param isSameUser 
	 * @param okrUserCache
	 * @return
	 * @throws Exception 
	 */
	private Boolean changeUserIdentityInDynamics( String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser, String tableName, String recordId ) throws Exception {
		// OKR_WORKCHAT		
		if( "all".equalsIgnoreCase( tableName ) || "OKR_WORKCHAT".equalsIgnoreCase( tableName )){
			List<OkrWorkChat> workChatRecords = systemIdentityQueryService.listErrorIdentitiesInWorkChat( fromIdentityName, recordId );
			if( workChatRecords != null && !workChatRecords.isEmpty() ){
				for( OkrWorkChat okrWorkChat : workChatRecords ){
					systemIdentityQueryService.changeUserIdentityInWorkChat( okrWorkChat.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		// OKR_WORKDYNAMICS
//		List<OkrWorkDynamics> workDynamicsRecords = systemIdentityQueryService.listErrorIdentitiesInDynamics( fromIdentityName );
//		for( OkrWorkDynamics okrWorkDynamics : workDynamicsRecords ){
//			systemIdentityQueryService.changeUserIdentityInDynamics( okrWorkDynamics.getId(), fromIdentityName, toUserCache, isSameUser );
//		}
		return true;
	}
	
	/**
	 * TODO:修改数据表中汇报相关的信息<br/><br/>
	 * 
	 * 涉及数据表：<br/>OKR_WORK_REPORTBASEINFO,OKR_WORK_REPORT_PERSONLINK,OKR_WORK_REPORT_PROCESSLOG
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser
	 * @param tableName
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	private Boolean changeUserIdentityInReport(String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser, String tableName, String recordId) throws Exception {
		// OKR_WORK_REPORTBASEINFO
		if( "all".equalsIgnoreCase( tableName ) || "OKR_WORK_REPORTBASEINFO".equalsIgnoreCase( tableName )){
			List<OkrWorkReportBaseInfo> reportInfoRecords = systemIdentityQueryService.listErrorIdentitiesInReportBaseInfo( fromIdentityName, recordId );
			if( reportInfoRecords != null && !reportInfoRecords.isEmpty() ){
				for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : reportInfoRecords ){
					systemIdentityQueryService.changeUserIdentityInReportBaseInfo( okrWorkReportBaseInfo.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		// OKR_WORK_REPORT_PERSONLINK
		if( "all".equalsIgnoreCase( tableName ) || "OKR_WORK_REPORT_PERSONLINK".equalsIgnoreCase( tableName )){
			List<OkrWorkReportPersonLink> workReportPersonLinkRecords = systemIdentityQueryService.listErrorIdentitiesInReportPersonInfo( fromIdentityName, recordId );
			if( workReportPersonLinkRecords != null && !workReportPersonLinkRecords.isEmpty() ){
				for( OkrWorkReportPersonLink okrWorkReportPersonLink : workReportPersonLinkRecords ){
					systemIdentityQueryService.changeUserIdentityInReportPersonLink( okrWorkReportPersonLink.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		
		// OKR_WORK_REPORT_PROCESSLOG 处理日志不用修改
//		List<OkrWorkReportProcessLog> workReportProcessLogRecords = systemIdentityQueryService.listErrorIdentitiesInReportProcessLog( fromIdentityName );
//		for( OkrWorkReportProcessLog okrWorkReportProcessLog : workReportProcessLogRecords ){
//			systemIdentityQueryService.changeUserIdentityInReportProcessLog( okrWorkReportProcessLog.getId(), fromIdentityName, toUserCache, isSameUser );
//		}
		return true;
	}

	/**
	 * TODO:修改数据表中工作相关的信息<br/><br/>
	 * 
	 * 涉及数据表：<br/>OKR_CENTERWORKINFO,OKR_WORKBASEINFO,OKR_WORK_AUTHORIZE_RECORD,OKR_WORK_PERSON
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser
	 * @param tableName
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	private Boolean changeUserIdentityInWork(String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser, String tableName, String recordId ) throws Exception {
		// OKR_CENTERWORKINFO
		if( "all".equalsIgnoreCase( tableName ) || "OKR_CENTERWORKINFO".equalsIgnoreCase( tableName )){
			List<OkrCenterWorkInfo> centerRecords = systemIdentityQueryService.listErrorIdentitiesInCenterInfo( fromIdentityName, recordId );
			if( centerRecords != null && !centerRecords.isEmpty() ){
				for( OkrCenterWorkInfo okrCenterWorkInfo : centerRecords ){
					systemIdentityQueryService.changeUserIdentityInCenterInfo( okrCenterWorkInfo.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}		
		// OKR_WORKBASEINFO
		if( "all".equalsIgnoreCase( tableName ) || "OKR_WORKBASEINFO".equalsIgnoreCase( tableName )){
			List<OkrWorkBaseInfo> workBaseRecords = systemIdentityQueryService.listErrorIdentitiesInWorkBaseInfo( fromIdentityName, recordId );
			if( workBaseRecords != null && !workBaseRecords.isEmpty() ){
				for( OkrWorkBaseInfo okrWorkBaseInfo : workBaseRecords ){
					systemIdentityQueryService.changeUserIdentityInWorkBaseInfo( okrWorkBaseInfo.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		// OKR_WORK_AUTHORIZE_RECORD
		if( "all".equalsIgnoreCase( tableName ) || "OKR_WORK_AUTHORIZE_RECORD".equalsIgnoreCase( tableName )){
			List<OkrWorkAuthorizeRecord> workAuthorizeRecords = systemIdentityQueryService.listErrorIdentitiesInAuthorizeRecord( fromIdentityName, recordId );
			if( workAuthorizeRecords != null && !workAuthorizeRecords.isEmpty() ){
				for( OkrWorkAuthorizeRecord okrWorkAuthorizeRecord : workAuthorizeRecords ){
					systemIdentityQueryService.changeUserIdentityInAuthorizeRecord( okrWorkAuthorizeRecord.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}
		// OKR_WORK_PERSON
		if( "all".equalsIgnoreCase( tableName ) || "OKR_WORK_AUTHORIZE_RECORD".equalsIgnoreCase( tableName )){
			List<OkrWorkPerson> workPersons = systemIdentityQueryService.listErrorIdentitiesInWorkPerson( fromIdentityName, recordId );
			if( workPersons != null && !workPersons.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : workPersons ){
					systemIdentityQueryService.changeUserIdentityInWorkPerson( okrWorkPerson.getId(), fromIdentityName, toUserCache, isSameUser );
				}
			}
		}		
		return true;
	}
}