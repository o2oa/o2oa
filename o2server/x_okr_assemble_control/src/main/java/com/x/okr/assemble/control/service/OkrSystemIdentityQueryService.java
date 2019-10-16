package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrConfigSecretary;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrErrorIdentityRecords;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

/**
 * 全系统范围内处理人员身份问题
 *  1、全系统身份替换功能
	2、无效身份检查功能
	   1)无效身份涉及的数据展现出来
	   2)无效身份的替换
	   
 * @author 李义
 *
 */
public class OkrSystemIdentityQueryService {
	
	private OkrUserManagerService userManagerService = new OkrUserManagerService();
	private static  Logger logger = LoggerFactory.getLogger( OkrSystemIdentityQueryService.class );
	
	/**
	 * 根据身份名称，从工作最新汇报情况统计信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_STATISTIC_REPORT_CONTENT
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrStatisticReportStatus> listErrorIdentitiesInStReportStatus(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrStatisticReportStatusFactory().listErrorIdentitiesInStReportStatus( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}
	/**
	 * 根据身份名称，从工作汇报状态统计信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_STATISTIC_REPORT_STATUS
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrStatisticReportContent> listErrorIdentitiesInStReportContent(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrStatisticReportContentFactory().listErrorIdentitiesInStReportContent( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从系统参数配置信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_CONFIG_SYSTEM
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrConfigSystem> listErrorIdentitiesInConfigSystem(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrConfigSystemFactory().listErrorIdentitiesInConfigSystem( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}
	/**
	 * 根据身份名称，从领导秘书配置信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_CONFIG_SECRETARY
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrConfigSecretary> listErrorIdentitiesInConfigSecretary(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrConfigSecretaryFactory().listErrorIdentitiesInConfigSecretary( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从工作已办已阅信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_TASKHANDLED
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrTaskHandled> listErrorIdentitiesInTaskhandled(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrTaskHandledFactory().listErrorIdentitiesInTaskhandled( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}
	/**
	 * 根据身份名称，从工作待办待阅信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_TASK
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrTask> listErrorIdentitiesInTask(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrTaskFactory().listErrorIdentitiesInWorkTask( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从工作操作动态信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORKDYNAMICS
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkDynamics> listErrorIdentitiesInDynamics(String identity) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkDynamicsFactory().listErrorIdentitiesInDynamics( identity );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从工作交流信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORKCHAT
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkChat> listErrorIdentitiesInWorkChat(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkChatFactory().listErrorIdentitiesInWorkChat( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}


	/**
	 * 根据身份名称，从工作汇报信息处理日志中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORK_REPORT_PROCESSLOG
	 * 
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportProcessLog> listErrorIdentitiesInReportProcessLog(String identity) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkReportProcessLogFactory().listErrorIdentitiesInReportProcessLog( identity );
		}catch( Exception e ){
			throw e;
		}
	}
	/**
	 * 根据身份名称，从工作汇报处理者信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORK_REPORT_PERSONLINK
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportPersonLink> listErrorIdentitiesInReportPersonInfo(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkReportPersonLinkFactory().listErrorIdentitiesInReportPersonInfo( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}
	/**
	 * 根据身份名称，从工作汇报信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORK_REPORTBASEINFO
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listErrorIdentitiesInReportBaseInfo(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().listErrorIdentitiesInReportBaseInfo( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}
	

	/**
	 * 根据身份名称，从工作授权信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORK_PERSON
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkPerson> listErrorIdentitiesInWorkPerson(String identity, String recordId ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listErrorIdentitiesInWorkPerson( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从工作授权信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORK_AUTHORIZE_RECORD
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkAuthorizeRecord> listErrorIdentitiesInAuthorizeRecord(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().listErrorIdentitiesInAuthorizeRecord( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从具体工作信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_WORKBASEINFO
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkBaseInfo> listErrorIdentitiesInWorkBaseInfo(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().listErrorIdentitiesInWorkBaseInfo( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据身份名称，从中心工作信息中查询与该身份有关的所有信息列表
	 * 
	 * OKR_CENTERWORKINFO
	 * 
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrCenterWorkInfo> listErrorIdentitiesInCenterInfo(String identity, String recordId) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrCenterWorkInfoFactory().listErrorIdentitiesInCenterInfo( identity, recordId );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * TODO:查询表OKR_STATISTIC_REPORT_STATUS中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInStReportStatus(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//responsibilityIdentity
			identities = business.okrStatisticReportStatusFactory().listAllDistinctResponsibilityIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	/**
	 * TODO:查询表OKR_STATISTIC_REPORT_CONTENT中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInStReportContent(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//responsibilityIdentity
			identities = business.okrStatisticReportContentFactory().listAllDistinctResponsibilityIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
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
	public void checkIdentityInConfig(List<String> identities_ok, List<String> identities_error) throws Exception {
		List<String> identities = null;
		//OKR_CONFIG_SECRETARY
		identities = listIdentitiesInSecretaryConfig( identities_ok, identities_error );
		checkIdentities( identities, identities_ok, identities_error );		
		//OKR_CONFIG_SYSTEM
		identities = listIdentitiesInSystemConfig( identities_ok, identities_error );
		checkIdentities( identities, identities_ok, identities_error );
	}
	/**
	 * TODO:查询表OKR_CONFIG_SYSTEM中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInSystemConfig(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//value
			identities = business.okrConfigSystemFactory().listAllDistinctValueIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	/**
	 * TODO:查询表OKR_CONFIG_SECRETARY中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInSecretaryConfig(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//leaderIdentity
			identities = business.okrConfigSecretaryFactory().listAllDistinctLeaderIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * TODO:查询表OKR_TASKHANDLED中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInTaskHandled(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//targetIdentity
			identities = business.okrTaskHandledFactory().listAllDistinctTargetIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	/**
	 * TODO:查询表OKR_TASK中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInTask(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//targetIdentity
			identities = business.okrTaskFactory().listAllDistinctTargetIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	
	/**
	 * TODO:查询表OKR_WORKDYNAMICS中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInWorkDynamics(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//targetIdentity
			identities = business.okrWorkDynamicsFactory().listAllDistinctTargetIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	
	/**
	 * TODO:查询表OKR_WORKCHAT中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInWorkChat(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//senderIdentity
			identities = business.okrWorkChatFactory().listAllDistinctSenderIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//targetIdentity
			identities = business.okrWorkChatFactory().listAllDistinctTargetIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	
	/**
	 * TODO:查询表OKR_WORK_PERSON中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInWorkPerson(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//employeeIdentity
			identities = business.okrWorkPersonFactory().listAllDistinctEmployeeIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * TODO:查询表OKR_WORK_AUTHORIZE_RECORD中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInWorkAuthorizeRecord(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//delegatorIdentity
			identities = business.okrWorkAuthorizeRecordFactory().listAllDistinctDelegatorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//targetIdentity
			identities = business.okrWorkAuthorizeRecordFactory().listAllDistinctTargetIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}	

	/**
	 * TODO:查询表OKR_WORKBASEINFO中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInWorkBaseInfo(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//cooperateIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctCooperateIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//creatorIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctCreatorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//deployerIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctDeployerIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//reportLeaderIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctReportLeaderIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//responsibilityIdentity
			identities = business.okrWorkBaseInfoFactory().listAllDistinctResponsibilityIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
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
	public List<String> listIdentitiesInCenterInfo( List<String> identities_ok, List<String> identities_error ) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//auditLeaderIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctAuditLeaderIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//creatorIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctCreatorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//deployerIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctDeployerIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//reportAuditLeaderIdentity
			identities = business.okrCenterWorkInfoFactory().listAllDistinctReportAuditLeaderIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * TODO:查询表OKR_WORK_REPORT_PROCESSLOG中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInReportProcessLog(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//processorIdentity
			identities = business.okrWorkReportProcessLogFactory().listAllDistinctProcessorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * TODO:查询表OKR_WORK_REPORT_PERSONLINK中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInReportPersonInfo(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//processorIdentity
			identities = business.okrWorkReportPersonLinkFactory().listAllDistinctProcessorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}

	/**
	 * TODO:查询表OKR_WORK_REPORTBASEINFO中涉及的所有人员身份列表
	 * @param identities_ok 排除已经OK的人员身份信息
	 * @param identities_error 排除已经有问题的人员身份信息
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdentitiesInReportBaseInfo(List<String> identities_ok, List<String> identities_error) throws Exception {
		Business business = null;
		List<String> result = new ArrayList<>();
		List<String> identities = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//creatorIdentity
			identities = business.okrWorkReportBaseInfoFactory().listAllDistinctCreatorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//reporterIdentity
			identities = business.okrWorkReportBaseInfoFactory().listAllDistinctReporterIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//currentProcessorIdentity
			identities = business.okrWorkReportBaseInfoFactory().listAllDistinctCurrentProcessorIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//readleadersIdentity
			identities = business.okrWorkReportBaseInfoFactory().listAllDistinctReadleadersIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
			//workAdminIdentity
			identities = business.okrWorkReportBaseInfoFactory().listAllDistinctWorkAdminIdentity( identities_ok, identities_error );
			result = addListToResult( identities, result );
		}catch( Exception e ){
			throw e;
		}
		return result;
	}
	
	/**
	 * TODO:检查身份列表的身份是否正常，如果正常则加入到正常身份列表里，如果不正常则加入不正常身份列表里
	 * @param identities 待检查身份列表
	 * @param identities_ok  正常身份列表
	 * @param identities_error  不正常身份列表
	 * @throws Exception
	 */
	public void checkIdentities(List<String> identities, List<String> identities_ok, List<String> identities_error) throws Exception {
		String[] array = null;
		if( identities != null && !identities.isEmpty() ){
			for( String identityString : identities ){
				if( identityString != null && !identityString.isEmpty() ){
					array = identityString.split(",");
					for( String identity : array ){
						if( identity != null && !identity.isEmpty() && !identity.equalsIgnoreCase( "SYSTEM" )&& !identity.equalsIgnoreCase( "xadmin" ) ){
							if( !identities_ok.contains( identity ) ){
								if( !identities_error.contains( identity ) ){
									//检查该身份是否仍存在
									try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
										if( userManagerService.isIdentityExsits( identity ) == null ){
											identities_error.add( identity );
										}else{
											identities_ok.add( identity );
										}
									}catch( Exception e ){
										throw e;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public List<String> addListToResult(List<String> identities, List<String> result) {
		if( identities != null && !identities.isEmpty() ){
			if( result == null ) {
				result = new ArrayList<>();
				return result;
			}
			
			for( String identity : identities ){
				if(identity != null) {
				if( !result.contains( identity) && !identity.equalsIgnoreCase( "SYSTEM" )&& !identity.equalsIgnoreCase( "xadmin" ) ){
					result.add( identity );
				}}else {
					logger.info("identity=" + identity);
					
				}
			}
		}
		return result;
	}
	
	/**
	 * 修改表OKR_CENTERWORKINFO里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: creatorIdentity，deployerIdentity，reportAuditLeaderIdentity，auditLeaderIdentity, defaultLeaderIdentity
	 * 
	 * @param centerRecords
	 * @param fromIdentityName
	 * @param isSameUser 
	 * @param okrUserCache
	 * @throws Exception 
	 */
	public void changeUserIdentityInCenterInfo( String centerId, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser ) throws Exception {
		if( centerId != null && fromIdentityName != null && toUserCache != null ){
			OkrCenterWorkInfo entity = null;
			String personName = null;
			String unitName = null;
			String topUnitName = null;

			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( centerId, OkrCenterWorkInfo.class );
				emc.beginTransaction( OkrCenterWorkInfo.class );
				if( entity != null ){
					//修改creatorIdentity相关的数据
					if( entity.getCreatorIdentity() != null && entity.getCreatorIdentity().equalsIgnoreCase( fromIdentityName ) ){
						entity.setCreatorName( toUserCache.getLoginUserName() );
						entity.setCreatorIdentity( toUserCache.getLoginIdentityName() );
						entity.setCreatorUnitName( toUserCache.getLoginUserUnitName() );
						entity.setCreatorTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改deployerIdentity相关的数据
					if( entity.getDeployerIdentity() != null && entity.getDeployerIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setDeployerName( toUserCache.getLoginUserName() );
						entity.setDeployerIdentity( toUserCache.getLoginIdentityName() );
						entity.setDeployerUnitName( toUserCache.getLoginUserUnitName() );
						entity.setDeployerTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改reportAuditLeaderIdentityList相关的数据, 多值
					if( ListTools.isNotEmpty( entity.getReportAuditLeaderIdentityList() ) &&  entity.getReportAuditLeaderIdentityList().contains(fromIdentityName) ){
						List<String> identities = new ArrayList<>();
						List<String> names = new ArrayList<>();
						List<String> unitNames = new ArrayList<>();
						List<String> topUnitNames = new ArrayList<>();
						for( String identity : entity.getReportAuditLeaderIdentityList() ){
							if( identity.equalsIgnoreCase( fromIdentityName )){
								identities.add( toUserCache.getLoginIdentityName() );
								names.add( toUserCache.getLoginUserName() );
								unitNames.add( toUserCache.getLoginUserUnitName() );
								topUnitNames.add( toUserCache.getLoginUserTopUnitName() );
							}else{
								personName = userManagerService.getPersonNameByIdentity(identity);
								unitName = userManagerService.getUnitNameByIdentity( identity );
								topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
								identities.add( identity );
								if( personName != null ){
									names.add( personName );
								}else{
									names.add( personName );
								}
								if( unitName != null ){
									unitNames.add( unitName );
									topUnitNames.add( topUnitName );
								}else{
									unitNames.add( "未知" );
									topUnitNames.add( "未知" );
								}
							}
						}
						entity.setReportAuditLeaderIdentityList(identities);
						entity.setReportAuditLeaderNameList(names);
						entity.setReportAuditLeaderUnitNameList( unitNames );
						entity.setReportAuditLeaderTopUnitNameList( topUnitNames );
					}
//					//修改auditLeaderIdentity相关的数据, 多值
//					if( ListTools.isNotEmpty(entity.getAuditLeaderIdentityList()) ){
//						List<String> identities = new ArrayList<>();
//						List<String> names = new ArrayList<>();
//						List<String> unitNames = new ArrayList<>();
//						List<String> topUnitNames = new ArrayList<>();
//						for( String identity : entity.getAuditLeaderIdentityList() ){
//							if( identity.equalsIgnoreCase( fromIdentityName )){
//								identities.add( toUserCache.getLoginIdentityName() );
//								names.add( toUserCache.getLoginUserName() );
//								unitNames.add( toUserCache.getLoginUserUnitName() );
//								topUnitNames.add( toUserCache.getLoginUserTopUnitName() );
//							}else{
//								identities.add( identity );
//								personName = userManagerService.getPersonNameByIdentity(identity);
//								unitName = userManagerService.getUnitNameByIdentity( identity );
//								topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
//								if( personName != null ){
//									names.add( personName );
//								}else{
//									names.add( "未知" );
//								}
//								if( unitName != null ){
//									unitNames.add( unitName );
//									topUnitNames.add( topUnitName );
//								}else{
//									unitNames.add( "未知" );
//									topUnitNames.add( "未知" );
//								}
//							}
//						}
//						entity.setAuditLeaderIdentityList( identities );
//						entity.setAuditLeaderNameList( names );
//						entity.setAuditLeaderUnitNameList( unitNames );
//						entity.setAuditLeaderTopUnitNameList( topUnitNames );
//					}
					//修改defaultLeaderIdentityList相关的数据, 多值
					if( ListTools.isNotEmpty( entity.getDefaultLeaderIdentityList() ) && entity.getDefaultLeaderIdentityList().contains( fromIdentityName ) ){
						List<String> identities = new ArrayList<>();
						List<String> names = new ArrayList<>();
						for( String identity : entity.getDefaultLeaderIdentityList() ){
							if( identity.equalsIgnoreCase( fromIdentityName )){
								identities.add( toUserCache.getLoginIdentityName() );
								names.add( toUserCache.getLoginUserName() );
							}else{
								personName = userManagerService.getPersonNameByIdentity(identity);
								identities.add( identity );
								if( personName != null ){
									names.add( personName );
								}else{
									names.add( personName );
								}
							}
						}
						entity.setDefaultLeaderIdentityList( identities );
						entity.setDefaultLeaderList( names );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_CENTERWORKINFO]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_CENTERWORKINFO is not exists, can not change idenity for this center record!id:"+centerId);
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORKBASEINFO里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: creatorIdentity，deployerIdentity，responsibilityIdentity, cooperateIdentity，readLeaderIdentity, reportAdminIdentity
	 * 
	 * @param centerRecords
	 * @param fromIdentityName
	 * @param isSameUser 
	 * @param okrUserCache
	 * @throws Exception
	 */
	public void changeUserIdentityInWorkBaseInfo( String workId, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( workId != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkBaseInfo entity = null;
			String personName = null;
			String unitName = null;
			String topUnitName = null;

			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( workId, OkrWorkBaseInfo.class );
				emc.beginTransaction( OkrWorkBaseInfo.class );
				if( entity != null ){
					//修改creatorIdentity相关的数据
					if( entity.getCreatorIdentity() != null && entity.getCreatorIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setCreatorName( toUserCache.getLoginUserName() );
						entity.setCreatorIdentity( toUserCache.getLoginIdentityName() );
						entity.setCreatorUnitName( toUserCache.getLoginUserUnitName() );
						entity.setCreatorTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改deployerIdentity相关的数据
					if( entity.getDeployerIdentity() != null && entity.getDeployerIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setDeployerName( toUserCache.getLoginUserName() );
						entity.setDeployerIdentity( toUserCache.getLoginIdentityName() );
						entity.setDeployerUnitName( toUserCache.getLoginUserUnitName() );
						entity.setDeployerTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改responsibilityIdentity相关的数据
					if( entity.getResponsibilityIdentity() != null && entity.getResponsibilityIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setResponsibilityEmployeeName( toUserCache.getLoginUserName() );
						entity.setResponsibilityIdentity( toUserCache.getLoginIdentityName() );
						entity.setResponsibilityUnitName( toUserCache.getLoginUserUnitName() );
						entity.setResponsibilityTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改reportAdminIdentity相关的数据
					if( entity.getReportAdminIdentity() != null && entity.getReportAdminIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setReportAdminName( toUserCache.getLoginUserName() );
						entity.setReportAdminIdentity( toUserCache.getLoginIdentityName() );
					}
					//修改cooperateIdentity相关的数据, 多值
					if( ListTools.isNotEmpty(entity.getCooperateIdentityList()) ){
						List<String> identities = new ArrayList<>();
						List<String> names = new ArrayList<>();
						List<String> unitNames = new ArrayList<>();
						List<String> topUnitNames = new ArrayList<>();
						for( String identity : entity.getCooperateIdentityList() ){
							if( identity.equalsIgnoreCase( fromIdentityName )){
								identities.add( toUserCache.getLoginIdentityName() );
								names.add( toUserCache.getLoginUserName() );
								unitNames.add( toUserCache.getLoginUserUnitName() );
								topUnitNames.add( toUserCache.getLoginUserTopUnitName() );
							}else{
								identities.add( identity );								
								personName = userManagerService.getPersonNameByIdentity(identity);
								unitName = userManagerService.getUnitNameByIdentity( identity );
								topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
								if( personName != null ){
									names.add( personName );
								}else{
									names.add( "未知" );
								}
								if( unitName != null ){
									unitNames.add( unitName );
									topUnitNames.add( topUnitName );
								}else{
									unitNames.add( "未知" );
									topUnitNames.add( "未知" );
								}
							}
						}
						entity.setCooperateIdentityList( identities );
						entity.setCooperateEmployeeNameList( names );
						entity.setCooperateUnitNameList( unitNames );
						entity.setCooperateTopUnitNameList( topUnitNames );
					}
					//修改readLeaderIdentity相关的数据, 多值
					if( ListTools.isNotEmpty( entity.getReadLeaderIdentityList())){
						List<String> identities = new ArrayList<>();
						List<String> names = new ArrayList<>();
						List<String> unitNames = new ArrayList<>();
						List<String> topUnitNames = new ArrayList<>();
						for( String identity : entity.getReadLeaderIdentityList() ){
							if( identity.equalsIgnoreCase( fromIdentityName )){
								identities.add( toUserCache.getLoginIdentityName() );
								names.add( toUserCache.getLoginUserName() );
								unitNames.add( toUserCache.getLoginUserUnitName() );
								topUnitNames.add( toUserCache.getLoginUserTopUnitName() );
							}else{
								identities.add( identity );
								personName = userManagerService.getPersonNameByIdentity(identity);
								unitName = userManagerService.getUnitNameByIdentity( identity );
								topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
								if( personName != null ){
									names.add( personName );
								}else{
									names.add( "未知" );
								}
								if( unitName != null ){
									unitNames.add( unitName );
									topUnitNames.add( topUnitName );
								}else{
									unitNames.add( "未知" );
									topUnitNames.add( "未知" );
								}
							}
						}			
						entity.setReadLeaderIdentityList( identities );
						entity.setReadLeaderNameList( names );
						entity.setReadLeaderUnitNameList( unitNames );
						entity.setReadLeaderTopUnitNameList( topUnitNames );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORKBASEINFO]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORKBASEINFO is not exists, can not change idenity for this record!id:"+ workId );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORK_AUTHORIZE_RECORD里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: delegatorIdentity, targetIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInAuthorizeRecord( String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkAuthorizeRecord entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkAuthorizeRecord.class );
				emc.beginTransaction( OkrWorkAuthorizeRecord.class );
				if( entity != null ){
					//修改delegatorIdentity相关的数据
					if( entity.getDelegatorIdentity() != null && entity.getDelegatorIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setDelegatorName( toUserCache.getLoginUserName() );
						entity.setDelegatorIdentity( toUserCache.getLoginIdentityName() );
						entity.setDelegatorUnitName( toUserCache.getLoginUserUnitName() );
						entity.setDelegatorTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改targetIdentity相关的数据
					if( entity.getTargetIdentity() != null && entity.getTargetIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setTargetName( toUserCache.getLoginUserName() );
						entity.setTargetIdentity( toUserCache.getLoginIdentityName() );
						entity.setTargetUnitName( toUserCache.getLoginUserUnitName() );
						entity.setTargetTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORK_AUTHORIZE_RECORD]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORK_AUTHORIZE_RECORD is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORK_PERSON里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: employeeIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInWorkPerson(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkPerson entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkPerson.class );
				emc.beginTransaction( OkrWorkPerson.class );
				if( entity != null ){
					//修改employeeIdentity相关的数据
					if( entity.getEmployeeIdentity() != null && entity.getEmployeeIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setEmployeeName( toUserCache.getLoginUserName() );
						entity.setEmployeeIdentity( toUserCache.getLoginIdentityName() );
						entity.setUnitName( toUserCache.getLoginUserUnitName() );
						entity.setTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORK_PERSON]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORK_PERSON is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORK_REPORTBASEINFO里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: creatorIdentity,workAdminIdentity,reporterIdentity,currentProcessorIdentity,readLeadersIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInReportBaseInfo( String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser ) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkReportBaseInfo entity = null;
			String personName = null;
			String unitName = null;
			String topUnitName = null;

			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkReportBaseInfo.class );
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				if( entity != null ){
					//修改creatorIdentity相关的数据
					if( entity.getCreatorIdentity() != null && entity.getCreatorIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setCreatorName( toUserCache.getLoginUserName() );
						entity.setCreatorIdentity( toUserCache.getLoginIdentityName() );
						entity.setCreatorUnitName( toUserCache.getLoginUserUnitName() );
						entity.setCreatorTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改workAdminIdentity相关的数据
					if( entity.getWorkAdminIdentity() != null && entity.getWorkAdminIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setWorkAdminName( toUserCache.getLoginUserName() );
						entity.setWorkAdminIdentity( toUserCache.getLoginIdentityName() );
					}
					//修改reporterIdentity相关的数据
					if( entity.getReporterIdentity() != null && entity.getReporterIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setReporterName( toUserCache.getLoginUserName() );
						entity.setReporterIdentity( toUserCache.getLoginIdentityName() );
						entity.setReporterUnitName( toUserCache.getLoginUserUnitName() );
						entity.setReporterTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改currentProcessorIdentity相关的数据
					if( ListTools.isNotEmpty( entity.getCurrentProcessorIdentityList() )){
						entity.setCurrentProcessorNameList( new ArrayList<>());
						entity.setCurrentProcessorIdentityList( new ArrayList<>());
						entity.setCurrentProcessorUnitNameList( new ArrayList<>());
						entity.setCurrentProcessorTopUnitNameList( new ArrayList<>());
						for( String identity : entity.getCurrentProcessorIdentityList() ){
							if( identity.equalsIgnoreCase( fromIdentityName )){
								entity.getCurrentProcessorNameList().add(toUserCache.getLoginUserName());
								entity.getCurrentProcessorIdentityList().add(toUserCache.getLoginIdentityName());
								entity.getCurrentProcessorUnitNameList().add(toUserCache.getLoginUserUnitName());
								entity.getCurrentProcessorTopUnitNameList().add(toUserCache.getLoginUserTopUnitName());
							}else{
								personName = userManagerService.getPersonNameByIdentity( identity );
								unitName = userManagerService.getUnitNameByIdentity( identity );
								topUnitName = userManagerService.getTopUnitNameByIdentity( identity );
								if( personName != null ){
									entity.getCurrentProcessorNameList().add(personName);
								}else{
									entity.getCurrentProcessorNameList().add("未知");
								}
								if( unitName != null ){
									entity.getCurrentProcessorUnitNameList().add(unitName);
									entity.getCurrentProcessorTopUnitNameList().add(topUnitName);
								}else{
									entity.getCurrentProcessorUnitNameList().add("未知");
									entity.getCurrentProcessorTopUnitNameList().add("未知");
								}
							}
						}
					}
					//修改readLeadersIdentity相关的数据, 多值
					if( ListTools.isNotEmpty( entity.getReadLeadersIdentityList() ) && entity.getReadLeadersIdentityList().contains( fromIdentityName) ){
						List<String> identities = new ArrayList<>();
						List<String> names = new ArrayList<>();
						for( String identity : entity.getReadLeadersIdentityList() ){
							if( identity.equalsIgnoreCase( fromIdentityName )){
								identities.add(  toUserCache.getLoginIdentityName() );
								names.add( toUserCache.getLoginUserName() );
							}else{
								personName = userManagerService.getPersonNameByIdentity(identity);
								if( personName != null ){
									identities.add(  identity );
									names.add( personName );
								}else{
									identities.add(  identity );
									names.add( "未知" );
								}
							}
						}			
						entity.setReadLeadersIdentityList( identities );
						entity.setReadLeadersNameList( names );
					}
					
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORK_REPORTBASEINFO]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORK_REPORTBASEINFO is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORK_REPORT_PERSONLINK里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: processorIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInReportPersonLink(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkReportPersonLink entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkReportPersonLink.class );
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				if( entity != null ){
					//修改processorIdentity相关的数据
					if( entity.getProcessorIdentity() != null && entity.getProcessorIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setProcessorName( toUserCache.getLoginUserName() );
						entity.setProcessorIdentity( toUserCache.getLoginIdentityName() );
						entity.setProcessorUnitName( toUserCache.getLoginUserUnitName() );
						entity.setProcessorTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORK_REPORT_PERSONLINK]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORK_REPORT_PERSONLINK is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORK_REPORT_PROCESSLOG里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: processorIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInReportProcessLog(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkReportProcessLog entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkReportProcessLog.class );
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				if( entity != null ){
					//修改processorIdentity相关的数据
					if( entity.getProcessorIdentity() != null && entity.getProcessorIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setProcessorName( toUserCache.getLoginUserName() );
						entity.setProcessorIdentity( toUserCache.getLoginIdentityName() );
						entity.setProcessorUnitName( toUserCache.getLoginUserUnitName() );
						entity.setProcessorTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORK_REPORT_PROCESSLOG]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORK_REPORT_PROCESSLOG is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORKCHAT里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: targetIdentity,senderIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInWorkChat(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkChat entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkChat.class );
				emc.beginTransaction( OkrWorkChat.class );
				if( entity != null ){
					//修改senderIdentity相关的数据
					if( entity.getSenderIdentity() != null && entity.getSenderIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setSenderName( toUserCache.getLoginUserName() );
						entity.setSenderIdentity( toUserCache.getLoginIdentityName() );
					}
					//修改targetIdentity相关的数据
					if( entity.getTargetIdentity() != null && entity.getTargetIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setTargetName( toUserCache.getLoginUserName() );
						entity.setTargetIdentity( toUserCache.getLoginIdentityName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORKCHAT]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORKCHAT is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_WORKDYNAMICS里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: targetIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInDynamics(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrWorkDynamics entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrWorkDynamics.class );
				emc.beginTransaction( OkrWorkDynamics.class );
				if( entity != null ){
					//修改targetIdentity相关的数据
					if( entity.getTargetIdentity() != null && entity.getTargetIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setTargetName( toUserCache.getLoginUserName() );
						entity.setTargetIdentity( toUserCache.getLoginIdentityName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_WORKDYNAMICS]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_WORKDYNAMICS is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * 修改表OKR_TASK里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: targetIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInTask(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrTask entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrTask.class );
				emc.beginTransaction( OkrTask.class );
				if( entity != null ){
					//修改targetIdentity相关的数据
					if( entity.getTargetIdentity() != null && entity.getTargetIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setTargetName( toUserCache.getLoginUserName() );
						entity.setTargetIdentity( toUserCache.getLoginIdentityName() );
						entity.setTargetUnitName( toUserCache.getLoginUserUnitName() );
						entity.setTargetTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_TASK]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_TASK is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_TASKHANDLED里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: targetIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInTaskhandled(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrTaskHandled entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrTaskHandled.class );
				emc.beginTransaction( OkrTaskHandled.class );
				if( entity != null ){
					//修改targetIdentity相关的数据
					if( entity.getTargetIdentity() != null && entity.getTargetIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setTargetName( toUserCache.getLoginUserName() );
						entity.setTargetIdentity( toUserCache.getLoginIdentityName() );
						entity.setTargetUnitName( toUserCache.getLoginUserUnitName() );
						entity.setTargetTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_TASKHANDLED]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_TASKHANDLED is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * 修改表OKR_CONFIG_SECRETARY里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: leaderIdentity,secretaryIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInConfigSecretary(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrConfigSecretary entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrConfigSecretary.class );
				emc.beginTransaction( OkrConfigSecretary.class );
				if( entity != null ){
					//修改leaderIdentity相关的数据
					if( entity.getLeaderIdentity() != null && entity.getLeaderIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setLeaderName( toUserCache.getLoginUserName() );
						entity.setLeaderIdentity( toUserCache.getLoginIdentityName() );
						entity.setLeaderUnitName( toUserCache.getLoginUserUnitName() );
						entity.setLeaderTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					//修改secretaryIdentity相关的数据
					if( entity.getSecretaryIdentity() != null && entity.getSecretaryIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setSecretaryName( toUserCache.getLoginUserName() );
						entity.setSecretaryIdentity( toUserCache.getLoginIdentityName() );
						entity.setSecretaryUnitName( toUserCache.getLoginUserUnitName() );
						entity.setSecretaryTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_CONFIG_SECRETARY]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_CONFIG_SECRETARY is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * 修改表OKR_CONFIG_SYSTEM里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: configValue
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInConfigSystem(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrConfigSystem entity = null;
			String identityString = null;
			String[] array = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrConfigSystem.class );
				emc.beginTransaction( OkrConfigSystem.class );
				if( entity != null ){
					//修改configValue相关的数据, 多值
					if( entity.getConfigValue() != null && entity.getConfigValue().indexOf( fromIdentityName ) >=0 ){
						identityString = "";
						array = entity.getConfigValue().split( "," );
						for( String idx : array ){
							if( identityString != null && !identityString.isEmpty() ){
								identityString += ",";
							}
							if( idx.equalsIgnoreCase( fromIdentityName )){
								identityString += toUserCache.getLoginIdentityName();
							}else{
								identityString += idx;
							}
						}			
						entity.setConfigValue(identityString);
					}					
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_CONFIG_SYSTEM]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_CONFIG_SYSTEM is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * 修改表OKR_STATISTIC_REPORT_CONTENT里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: responsibilityIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInStReportContent(String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrStatisticReportContent entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrStatisticReportContent.class );
				emc.beginTransaction( OkrStatisticReportContent.class );
				if( entity != null ){
					//修改responsibilityIdentity相关的数据
					if( entity.getResponsibilityIdentity() != null && entity.getResponsibilityIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setResponsibilityEmployeeName( toUserCache.getLoginUserName() );
						entity.setResponsibilityIdentity( toUserCache.getLoginIdentityName() );
						entity.setResponsibilityUnitName( toUserCache.getLoginUserUnitName() );
						entity.setResponsibilityTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_STATISTIC_REPORT_CONTENT]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_STATISTIC_REPORT_CONTENT is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	/**
	 * TODO:修改表OKR_STATISTIC_REPORT_STATUS里的相关身份以及名称组织信息<br/><br/>
	 * ARRIBUTE: responsibilityIdentity
	 * 
	 * @param id
	 * @param fromIdentityName
	 * @param toUserCache
	 * @param isSameUser 
	 * 
	 * @throws Exception
	 */
	public void changeUserIdentityInStReportStatus( String id, String fromIdentityName, OkrUserCache toUserCache, Boolean isSameUser) throws Exception {
		if( id != null && fromIdentityName != null && toUserCache != null ){
			OkrStatisticReportStatus entity = null;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				entity = emc.find( id, OkrStatisticReportStatus.class );
				emc.beginTransaction( OkrStatisticReportStatus.class );
				if( entity != null ){
					//修改responsibilityIdentity相关的数据
					if( entity.getResponsibilityIdentity() != null && entity.getResponsibilityIdentity().equalsIgnoreCase( fromIdentityName )){
						entity.setResponsibilityEmployeeName( toUserCache.getLoginUserName() );
						entity.setResponsibilityIdentity( toUserCache.getLoginIdentityName() );
						entity.setResponsibilityUnitName( toUserCache.getLoginUserUnitName() );
						entity.setResponsibilityTopUnitName( toUserCache.getLoginUserTopUnitName() );
					}
					emc.check( entity, CheckPersistType.all );
					emc.commit();
					logger.info( "[OKR_STATISTIC_REPORT_STATUS]:" + fromIdentityName + " to "+ toUserCache.getLoginIdentityName() +", 系统数据修改完成," + entity.getId() );
				}else{
					logger.warn("OKR_STATISTIC_REPORT_STATUS is not exists, can not change idenity for this record!id:"+ id );
				}
			}catch( Exception e ){
				throw e;
			}
		}
	}
	public OkrErrorIdentityRecords getErrorIdentityRecords(String identity) throws Exception {
		Business business = null;
		List<OkrErrorIdentityRecords> list = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			list = business.okrErrorSystemIdentityInfoFactory().listRecordsByIdentityName( identity );
			if( list != null && !list.isEmpty() ){
				return list.get(0);
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
}