package com.x.okr.assemble.control.service.update;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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

public class UpdateOldUnitToNewUnit2 {
	
	private static  Logger logger = LoggerFactory.getLogger( UpdateOldUnitToNewUnit2.class );
	
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
		String old_name = null;
		OkrAttachmentFileInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrAttachmentFileInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrAttachmentFileInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrAttachmentFileInfo.class );
					if( entity != null ) {
						old_name = entity.getCreatorUid();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setCreatorUid( UpdatePersonMap.getPersonWithOldName(old_name));
						}
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
		String old_name = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_center_ids = business.okrCenterWorkInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_center_ids )) {
				emc.beginTransaction( OkrCenterWorkInfo.class );
				for( String _id : all_center_ids ) {
					okrCenterWorkInfo = emc.find( _id, OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null ) {
						old_name = okrCenterWorkInfo.getCreatorIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setCreatorIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}						
						old_name = okrCenterWorkInfo.getDeployerIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setDeployerIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						
						List<String>old_names = okrCenterWorkInfo.getReportAuditLeaderIdentityList();
						List<String> new_names = new ArrayList<>();
						if( ListTools.isNotEmpty(old_names)) {
							for( String name : old_names ) {
								if( UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
									new_names.add( UpdateIdentityMap.getIdentityWithOldName( old_name ) );
								}else {
									new_names.add( name );
								}
							}
							okrCenterWorkInfo.setReportAuditLeaderIdentityList( new_names );
						}	
						
						old_name = okrCenterWorkInfo.getCreatorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setCreatorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = okrCenterWorkInfo.getDeployerName();
						if( StringUtils.isNotEmpty(old_name) &&  UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setDeployerName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						
						old_names = okrCenterWorkInfo.getReportAuditLeaderNameList();
						new_names = new ArrayList<>();
						if( StringUtils.isNotEmpty(old_name) ) {
							for( String name : old_names ) {
								if( UpdatePersonMap.getPersonWithOldName( name ) != null ) {
									new_names.add( UpdatePersonMap.getPersonWithOldName( name ) );
								}else {
									new_names.add( name );
								}
							}
							okrCenterWorkInfo.setReportAuditLeaderNameList( new_names );
						}
						
						old_name = okrCenterWorkInfo.getCreatorTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setCreatorTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}						
						old_name = okrCenterWorkInfo.getCreatorUnitName();
						if( UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setCreatorUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}						
						old_name = okrCenterWorkInfo.getDeployerTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setDeployerTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}						
						old_name = okrCenterWorkInfo.getDeployerUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							okrCenterWorkInfo.setDeployerUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrConfigSecretary entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrConfigSecretaryFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrConfigSecretary.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrConfigSecretary.class );
					if( entity != null ) {
						old_name = entity.getLeaderIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setLeaderIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}						
						old_name = entity.getSecretaryIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setSecretaryIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}						
						old_name = entity.getLeaderName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setLeaderName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = entity.getSecretaryName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setSecretaryName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = entity.getLeaderTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setLeaderTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}						
						old_name = entity.getLeaderUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setLeaderUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}						
						old_name = entity.getSecretaryTopUnitName();
						if( UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setSecretaryTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}						
						old_name = entity.getSecretaryUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setSecretaryUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrStatisticReportContent entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrStatisticReportContentFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrStatisticReportContent.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrStatisticReportContent.class );
					if( entity != null ) {
						old_name = entity.getResponsibilityIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setResponsibilityIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getResponsibilityEmployeeName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setResponsibilityEmployeeName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = entity.getResponsibilityTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setResponsibilityTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getResponsibilityUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setResponsibilityUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrStatisticReportStatus entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrStatisticReportStatusFactory().listAllIds();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrStatisticReportStatus.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrStatisticReportStatus.class );
					if( entity != null ) {
						old_name = entity.getResponsibilityIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setResponsibilityIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getResponsibilityEmployeeName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setResponsibilityEmployeeName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = entity.getResponsibilityTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setResponsibilityTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getResponsibilityUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setResponsibilityUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrTask entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrTaskFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrTask.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrTask.class );
					if( entity != null ) {
						old_name = entity.getTargetIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setTargetIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getTargetName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setTargetName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = entity.getTargetTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTargetTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getTargetUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTargetUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrTaskHandled entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrTaskHandledFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrTaskHandled.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrTaskHandled.class );
					if( entity != null ) {
						old_name = entity.getTargetIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setTargetIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getTargetName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setTargetName( UpdatePersonMap.getPersonWithOldName(old_name));
						}						
						old_name = entity.getTargetTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTargetTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getTargetUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTargetUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrUserInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrUserInfoFactory().listAllIds();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrUserInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrUserInfo.class );
					if( entity != null ) {
						old_name = entity.getUserName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setUserName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkAuthorizeRecord entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkAuthorizeRecordFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkAuthorizeRecord.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkAuthorizeRecord.class );
					if( entity != null ) {
						old_name = entity.getDelegatorIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setDelegatorIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getTargetIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setTargetIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getDelegatorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setDelegatorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getTargetName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setTargetName( UpdatePersonMap.getPersonWithOldName(old_name));
						}	
						old_name = entity.getDelegatorTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setDelegatorTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getTargetTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTargetTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getDelegatorUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setDelegatorUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getTargetUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTargetUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkPerson entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkPersonFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkPerson.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkPerson.class );
					if( entity != null ) {
						old_name = entity.getEmployeeIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setEmployeeIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getProcessIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setProcessIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getEmployeeName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setEmployeeName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkReportPersonLink entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkReportPersonLinkFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkReportPersonLink.class );
					if( entity != null ) {
						old_name = entity.getProcessorIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setProcessorIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getProcessorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setProcessorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getProcessorTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setProcessorTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getProcessorUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setProcessorUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkReportProcessLog entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkReportProcessLogFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkReportProcessLog.class );
					if( entity != null ) {
						old_name = entity.getProcessorIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setProcessorIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getProcessorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setProcessorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getProcessorTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setProcessorTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getProcessorUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setProcessorUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkReportBaseInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkReportBaseInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkReportBaseInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkReportBaseInfo.class );
					if( entity != null ) {
						old_name = entity.getCreatorIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setCreatorIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						
						List<String> old_identities = entity.getCurrentProcessorIdentityList();
						if( ListTools.isNotEmpty( old_identities )){
							List<String> new_identities = new ArrayList<>();
							for( String name : old_identities) {
								if( UpdateIdentityMap.getIdentityWithOldName( name ) != null ) {
									new_identities.add( UpdateIdentityMap.getIdentityWithOldName(name) );
								}else {
									new_identities.add( name );
								}
							}
							entity.setCurrentProcessorIdentityList( new_identities );
						}
						
						old_identities = entity.getReadLeadersIdentityList();
						if( ListTools.isNotEmpty(old_identities) ) {
							List<String> new_identities = new ArrayList<>();
							for( String name : old_identities ) {
								if( UpdateIdentityMap.getIdentityWithOldName( name ) != null ) {
									new_identities.add( UpdateIdentityMap.getIdentityWithOldName( name ) );
								}else {
									new_identities.add( name );
								}
							}
							entity.setReadLeadersIdentityList( new_identities );
						}
						
						old_name = entity.getReporterIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setReporterIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getWorkAdminIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setWorkAdminIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getCreatorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setCreatorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						
						List<String> old_names = entity.getCurrentProcessorNameList();
						if( ListTools.isNotEmpty( old_names )){
							List<String> new_names = new ArrayList<>();
							for( String name : old_names) {
								if( UpdatePersonMap.getPersonWithOldName( name ) != null ) {
									new_names.add( UpdatePersonMap.getPersonWithOldName(name) );
								}else {
									new_names.add( name );
								}
							}
							entity.setCurrentProcessorNameList( new_names );
						}
						
						old_names = entity.getReadLeadersNameList();
						if( ListTools.isNotEmpty( old_names )){
							List<String> new_names = new ArrayList<>();
							for( String name : old_names) {
								if( UpdatePersonMap.getPersonWithOldName( name ) != null ) {
									new_names.add( UpdatePersonMap.getPersonWithOldName(name) );
								}else {
									new_names.add( name );
								}
							}
							entity.setReadLeadersNameList( new_names );
						}
						
						old_name = entity.getReporterName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setReporterName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getWorkAdminName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setWorkAdminName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getCreatorTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setCreatorTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						
						List<String> old_unitNames = entity.getCurrentProcessorTopUnitNameList();
						if( ListTools.isNotEmpty( old_names )){
							List<String> new_unitNames = new ArrayList<>();
							for( String name : old_unitNames) {
								if( UpdateUnitMap.getUnitWithOldName( name ) != null ) {
									new_unitNames.add( UpdateUnitMap.getUnitWithOldName(name) );
								}else {
									new_unitNames.add( name );
								}
							}
							entity.setCurrentProcessorTopUnitNameList( new_unitNames );
						}
						
						old_name = entity.getReporterTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setReporterTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getCreatorUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setCreatorUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						
						old_unitNames = entity.getCurrentProcessorUnitNameList();
						if( ListTools.isNotEmpty( old_names )){
							List<String> new_unitNames = new ArrayList<>();
							for( String name : old_unitNames) {
								if( UpdateUnitMap.getUnitWithOldName( name ) != null ) {
									new_unitNames.add( UpdateUnitMap.getUnitWithOldName(name) );
								}else {
									new_unitNames.add( name );
								}
							}
							entity.setCurrentProcessorUnitNameList( new_unitNames );
						}
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
		String old_name = null;
		List<String> old_names = null;
		List<String> new_names = null;
		OkrWorkBaseInfo entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkBaseInfoFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkBaseInfo.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkBaseInfo.class );
					
					if( entity != null ) {
						
						old_names = entity.getCooperateIdentityList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdateIdentityMap.getIdentityWithOldName( old_name ) != null) {
									new_names.add( UpdateIdentityMap.getIdentityWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setCooperateIdentityList( new_names );
						
						old_names = entity.getReadLeaderIdentityList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdateIdentityMap.getIdentityWithOldName( old_name ) != null) {
									new_names.add( UpdateIdentityMap.getIdentityWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setReadLeaderIdentityList( new_names );
						
						old_names = entity.getCooperateEmployeeNameList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdatePersonMap.getPersonWithOldName( old_name ) != null) {
									new_names.add( UpdatePersonMap.getPersonWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setCooperateEmployeeNameList( new_names );
						
						old_names = entity.getReadLeaderNameList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdatePersonMap.getPersonWithOldName( old_name ) != null) {
									new_names.add( UpdatePersonMap.getPersonWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setReadLeaderNameList( new_names );
						
						old_names = entity.getCooperateTopUnitNameList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdateUnitMap.getUnitWithOldName( old_name ) != null) {
									new_names.add( UpdateUnitMap.getUnitWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setCooperateTopUnitNameList( new_names );
						
						old_names = entity.getReadLeaderTopUnitNameList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdateUnitMap.getUnitWithOldName( old_name ) != null) {
									new_names.add( UpdateUnitMap.getUnitWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setReadLeaderTopUnitNameList( new_names );
						
						old_names = entity.getCooperateUnitNameList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdateUnitMap.getUnitWithOldName( old_name ) != null) {
									new_names.add( UpdateUnitMap.getUnitWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setCooperateUnitNameList( new_names );
						
						old_names = entity.getReadLeaderUnitNameList();
						new_names = new ArrayList<>();
						if( ListTools.isNotEmpty( old_names ) ) {
							for( String identity : old_names ) {
								if( UpdateUnitMap.getUnitWithOldName( old_name ) != null) {
									new_names.add( UpdateUnitMap.getUnitWithOldName(old_name) );
								}else {
									new_names.add( identity );
								}
							}
						}
						entity.setReadLeaderUnitNameList( new_names );
						
						old_name = entity.getCreatorIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setCreatorIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getDeployerIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setDeployerIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getReportAdminIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setReportAdminIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getResponsibilityIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setResponsibilityIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getCreatorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setCreatorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getDeployerName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setDeployerName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getReportAdminName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setReportAdminName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getResponsibilityEmployeeName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setResponsibilityEmployeeName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getCreatorTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setCreatorTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getDeployerTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setDeployerTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getResponsibilityTopUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setResponsibilityTopUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getCreatorUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setCreatorUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getDeployerUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setDeployerUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
						old_name = entity.getResponsibilityUnitName();
						if( StringUtils.isNotEmpty(old_name) && UpdateUnitMap.getUnitWithOldName( old_name ) != null ) {
							entity.setResponsibilityUnitName( UpdateUnitMap.getUnitWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkChat entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkChatFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkChat.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkChat.class );
					if( entity != null ) {
						old_name = entity.getSenderIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setSenderIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getTargetIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setTargetIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getSenderName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setSenderName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getTargetName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setTargetName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
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
		String old_name = null;
		OkrWorkDynamics entity = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			all_entity_ids = business.okrWorkDynamicsFactory().listAll();
			if( ListTools.isNotEmpty(all_entity_ids )) {
				emc.beginTransaction( OkrWorkDynamics.class );
				for( String _id : all_entity_ids ) {
					entity = emc.find( _id, OkrWorkDynamics.class );
					if( entity != null ) {
						old_name = entity.getTargetIdentity();
						if( StringUtils.isNotEmpty(old_name) && UpdateIdentityMap.getIdentityWithOldName( old_name ) != null ) {
							entity.setTargetIdentity( UpdateIdentityMap.getIdentityWithOldName(old_name));
						}
						old_name = entity.getOperatorName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setOperatorName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
						old_name = entity.getTargetName();
						if( StringUtils.isNotEmpty(old_name) && UpdatePersonMap.getPersonWithOldName( old_name ) != null ) {
							entity.setTargetName( UpdatePersonMap.getPersonWithOldName(old_name));
						}
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