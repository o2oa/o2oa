package com.x.okr.assemble.control.service.update;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrConfigSecretary;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrUserInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class UpdateOldUnitToNewUnit {
	
	private static  Logger logger = LoggerFactory.getLogger( UpdateOldUnitToNewUnit.class );
	
	/**
	 * 根据对照表，替换相关数据中的人员、身份、组织信息
	 * @throws Exception
	 */
	public void processReplace() throws Exception {
		process_table_OKR_ATTACHMENTFILEINFO();
		process_table_OKR_CENTERWORKINFO();
		process_table_OKR_CONFIG_SECRETARY();
		process_table_OKR_STATISTIC_REPORT_CONTENT();
		process_table_OKR_STATISTIC_REPORT_STATUS();
		process_table_OKR_TASK();
		process_table_OKR_TASKHANDLED();
		process_table_OKR_USERINFO();
		process_table_OKR_WORK_AUTHORIZE_RECORD();
		process_table_OKR_WORK_PERSON();
		process_table_OKR_WORK_REPORT_PERSONLINK();
		process_table_OKR_WORK_REPORT_PROCESSLOG();
		process_table_OKR_WORK_REPORTBASEINFO();
		process_table_OKR_WORKBASEINFO();
		process_table_OKR_WORKCHAT();
		process_table_OKR_WORKDYNAMICS();
	}
	
	public void process_table_OKR_ATTACHMENTFILEINFO() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrAttachmentFileInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrAttachmentFileInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrAttachmentFileInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrAttachmentFileInfo.class );
					if( entity != null ) {
						changePropertyValue( entity, "creatorUid", "person" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_CENTERWORKINFO() throws Exception {
		List<String> all_center_ids = null;
		Business business = null;
		OkrCenterWorkInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_center_ids = business.okrCenterWorkInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_center_ids )) {
				emc.beginTransaction( OkrCenterWorkInfo.class );
				for( String _id : all_center_ids ) {
					entity = emc.find( _id, OkrCenterWorkInfo.class );
					if( entity != null ) {
						changePropertyValue( entity, "creatorIdentity", "identity" );
						changePropertyValue( entity, "deployerIdentity", "identity" );
						changePropertyValue( entity, "reportAuditLeaderIdentity", "identity" );
						changePropertyValue( entity, "creatorName", "person" );
						changePropertyValue( entity, "deployerName", "person" );
						changePropertyValue( entity, "reportAuditLeaderName", "person" );
						changePropertyValue( entity, "creatorTopUnitName", "unit" );
						changePropertyValue( entity, "creatorUnitName", "unit" );
						changePropertyValue( entity, "deployerTopUnitName", "unit" );
						changePropertyValue( entity, "deployerUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	//OKR_CONFIG_SECRETARY
	public void process_table_OKR_CONFIG_SECRETARY() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrConfigSecretary entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrConfigSecretaryFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrConfigSecretary.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrConfigSecretary.class );
					if( entity != null ) {
						changePropertyValue( entity, "leaderIdentity", "identity" );
						changePropertyValue( entity, "secretaryIdentity", "identity" );
						changePropertyValue( entity, "leaderName", "person" );
						changePropertyValue( entity, "secretaryName", "person" );
						changePropertyValue( entity, "leaderTopUnitName", "unit" );
						changePropertyValue( entity, "leaderUnitName", "unit" );
						changePropertyValue( entity, "secretaryTopUnitName", "unit" );
						changePropertyValue( entity, "secretaryUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_STATISTIC_REPORT_CONTENT() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrStatisticReportContent entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrStatisticReportContentFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrStatisticReportContent.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrStatisticReportContent.class );
					if( entity != null ) {
						changePropertyValue( entity, "responsibilityIdentity", "identity" );
						changePropertyValue( entity, "responsibilityEmployeeName", "person" );
						changePropertyValue( entity, "responsibilityTopUnitName", "unit" );
						changePropertyValue( entity, "responsibilityUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_STATISTIC_REPORT_STATUS() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrStatisticReportStatus entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrStatisticReportStatusFactory().listAllIds();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrStatisticReportStatus.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrStatisticReportStatus.class );
					if( entity != null ) {
						changePropertyValue( entity, "responsibilityIdentity", "identity" );
						changePropertyValue( entity, "responsibilityEmployeeName", "person" );
						changePropertyValue( entity, "responsibilityTopUnitName", "unit" );
						changePropertyValue( entity, "responsibilityUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_TASK() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrTask entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrTaskFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrTask.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrTask.class );
					if( entity != null ) {
						changePropertyValue( entity, "targetIdentity", "identity" );
						changePropertyValue( entity, "targetName", "person" );
						changePropertyValue( entity, "targetTopUnitName", "unit" );
						changePropertyValue( entity, "targetUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_TASKHANDLED() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrTaskHandled entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrTaskHandledFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrTaskHandled.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrTaskHandled.class );
					if( entity != null ) {
						changePropertyValue( entity, "targetIdentity", "identity" );
						changePropertyValue( entity, "targetName", "person" );
						changePropertyValue( entity, "targetTopUnitName", "unit" );
						changePropertyValue( entity, "targetUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_USERINFO() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrUserInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrUserInfoFactory().listAllIds();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrUserInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrUserInfo.class );
					if( entity != null ) {
						changePropertyValue( entity, "userName", "person" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORK_AUTHORIZE_RECORD() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkAuthorizeRecord entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkAuthorizeRecordFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkAuthorizeRecord.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkAuthorizeRecord.class );
					if( entity != null ) {
						changePropertyValue( entity, "delegatorIdentity", "identity" );
						changePropertyValue( entity, "targetIdentity", "identity" );
						changePropertyValue( entity, "delegatorName", "person" );
						changePropertyValue( entity, "targetName", "person" );
						changePropertyValue( entity, "delegatorTopUnitName", "unit" );
						changePropertyValue( entity, "targetTopUnitName", "unit" );
						changePropertyValue( entity, "delegatorUnitName", "unit" );
						changePropertyValue( entity, "targetUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORK_PERSON() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkPerson entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkPersonFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkPerson.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkPerson.class );
					if( entity != null ) {
						changePropertyValue( entity, "employeeIdentity", "identity" );
						changePropertyValue( entity, "processIdentity", "identity" );
						changePropertyValue( entity, "employeeName", "person" );
						changePropertyValue( entity, "topUnitName", "unit" );
						changePropertyValue( entity, "unitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORK_REPORT_PERSONLINK() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkReportPersonLink entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkReportPersonLinkFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkReportPersonLink.class );
					if( entity != null ) {
						changePropertyValue( entity, "processorIdentity", "identity" );
						changePropertyValue( entity, "processorName", "person" );
						changePropertyValue( entity, "processorTopUnitName", "unit" );
						changePropertyValue( entity, "processorUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORK_REPORT_PROCESSLOG() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkReportProcessLog entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkReportProcessLogFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkReportProcessLog.class );
					if( entity != null ) {
						changePropertyValue( entity, "processorIdentity", "identity" );
						changePropertyValue( entity, "processorName", "person" );
						changePropertyValue( entity, "processorTopUnitName", "unit" );
						changePropertyValue( entity, "processorUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORK_REPORTBASEINFO() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkReportBaseInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkReportBaseInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkReportBaseInfo.class );
					if( entity != null ) {
						changePropertyValue( entity, "creatorIdentity", "identity" );
						changePropertyValue( entity, "currentProcessorIdentity", "identity" );
						changePropertyValue( entity, "readLeadersIdentity", "identity" );
						changePropertyValue( entity, "reporterIdentity", "identity" );
						changePropertyValue( entity, "workAdminIdentity", "identity" );						
						changePropertyValue( entity, "creatorName", "identity" );
						changePropertyValue( entity, "currentProcessorName", "person" );
						changePropertyValue( entity, "readLeadersName", "person" );
						changePropertyValue( entity, "reporterName", "person" );
						changePropertyValue( entity, "workAdminName", "person" );						
						changePropertyValue( entity, "creatorTopUnitName", "unit" );
						changePropertyValue( entity, "currentProcessorTopUnitName", "unit" );
						changePropertyValue( entity, "reporterTopUnitName", "unit" );
						changePropertyValue( entity, "creatorUnitName", "unit" );
						changePropertyValue( entity, "currentProcessorUnitName", "unit" );
						changePropertyValue( entity, "reporterUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORKBASEINFO() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkBaseInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkBaseInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkBaseInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkBaseInfo.class );
					if( entity != null ) {
						changePropertyValue( entity, "cooperateIdentity", "identity" );
						changePropertyValue( entity, "creatorIdentity", "identity" );
						changePropertyValue( entity, "deployerIdentity", "identity" );
						changePropertyValue( entity, "readLeaderIdentity", "identity" );
						changePropertyValue( entity, "reportAdminIdentity", "identity" );
						changePropertyValue( entity, "responsibilityIdentity", "identity" );						
						changePropertyValue( entity, "cooperateEmployeeName", "person" );
						changePropertyValue( entity, "creatorName", "person" );
						changePropertyValue( entity, "deployerName", "person" );
						changePropertyValue( entity, "readLeaderName", "person" );
						changePropertyValue( entity, "reportAdminName", "person" );
						changePropertyValue( entity, "responsibilityEmployeeName", "person" );
						changePropertyValue( entity, "cooperateTopUnitName", "unit" );
						changePropertyValue( entity, "creatorTopUnitName", "unit" );
						changePropertyValue( entity, "deployerTopUnitName", "unit" );
						changePropertyValue( entity, "readLeaderTopUnitName", "unit" );
						changePropertyValue( entity, "responsibilityTopUnitName", "unit" );						
						changePropertyValue( entity, "cooperateUnitName", "unit" );
						changePropertyValue( entity, "creatorUnitName", "unit" );
						changePropertyValue( entity, "deployerUnitName", "unit" );
						changePropertyValue( entity, "readLeaderUnitName", "unit" );
						changePropertyValue( entity, "responsibilityUnitName", "unit" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORKCHAT() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkChat entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkChatFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkChat.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkChat.class );
					if( entity != null ) {
						changePropertyValue( entity, "senderIdentity", "identity" );
						changePropertyValue( entity, "targetIdentity", "identity" );
						changePropertyValue( entity, "senderName", "person" );
						changePropertyValue( entity, "targetName", "person" );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void process_table_OKR_WORKDYNAMICS() throws Exception {
		List<String> all_entity_ids = null;
		Business business = null;
		OkrWorkDynamics entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkDynamicsFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkDynamics.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkDynamics.class );
					if( entity != null ) {
						changePropertyValue( entity, "targetIdentity", "identity" );
						changePropertyValue( entity, "operatorName", "person" );
						changePropertyValue( entity, "targetName", "person" );
						emc.check(entity, CheckPersistType.all );
					}
				}
				emc.commit();
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	private void changePropertyValue(Object entity,  String propertyName,  String propertyType) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String old_name = null;
		String new_name = null;
		String get_methodName = "get"+propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		String set_methodName = "set"+propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		Object result = entity.getClass().getMethod(get_methodName, null).invoke(entity, null);
		if( result != null && StringUtils.isNotEmpty(old_name)) {
			old_name = (String)result;
			if("identity".equals(propertyType)) {
				new_name = UpdateIdentityMap.getIdentityWithOldName( old_name );
			}else if ("person".equals(propertyType)) {
				new_name = UpdatePersonMap.getPersonWithOldName( old_name );
			}else if ("unit".equals(propertyType)) {
				new_name = UpdateUnitMap.getUnitWithOldName( old_name );
			}			
			if( StringUtils.isNotEmpty(old_name) ) {
				entity.getClass().getMethod(set_methodName, null).invoke(entity, new_name);
			}
		}
	}
}