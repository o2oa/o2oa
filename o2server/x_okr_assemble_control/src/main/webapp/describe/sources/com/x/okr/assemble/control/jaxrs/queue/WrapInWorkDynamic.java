package com.x.okr.assemble.control.jaxrs.queue;

import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrConfigSecretary;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrConfigWorkLevel;
import com.x.okr.entity.OkrConfigWorkType;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class WrapInWorkDynamic extends OkrWorkDynamics {
	
	private static final long serialVersionUID = 1L;
	
	private String dynamicInfoType = null;

	public String getDynamicInfoType() {
		return dynamicInfoType;
	}

	public void setDynamicInfoType(String dynamicInfoType) {
		this.dynamicInfoType = dynamicInfoType;
	}
	
	public static void sendWithCenterWorkInfo( OkrCenterWorkInfo okrCenterWorkInfo, 
			String distinguishedName, String loginUserName, String loginIdentityName, String dynamicType, String description) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setDynamicObjectId( okrCenterWorkInfo.getId() );
		o.setDynamicObjectTitle( okrCenterWorkInfo.getTitle() );
		o.setDynamicObjectType( "中心工作" );
		o.setCenterId( okrCenterWorkInfo.getId() );
		o.setCenterTitle( okrCenterWorkInfo.getTitle() );
		o.setWorkId( null );
		o.setWorkTitle( null );
		o.setContent( dynamicType + ":" + okrCenterWorkInfo.getTitle() );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDynamicType( dynamicType );
		o.setDescription( description );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithWorkInfo( OkrWorkBaseInfo okrWorkBaseInfo, 
			String distinguishedName, String loginUserName, String loginIdentityName, String dynamicType, String description ) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setDynamicObjectId( okrWorkBaseInfo.getId() );
		o.setDynamicObjectTitle( okrWorkBaseInfo.getTitle() );
		o.setDynamicObjectType( "具体工作信息" );
		o.setCenterId( okrWorkBaseInfo.getCenterId() );
		o.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
		o.setWorkId( okrWorkBaseInfo.getId() );
		o.setWorkTitle( okrWorkBaseInfo.getTitle() );
		o.setContent( dynamicType + ":" + okrWorkBaseInfo.getTitle() );
		o.setDynamicType( dynamicType );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithSecretaryConfig(OkrConfigSecretary okrConfigSecretary,
			String distinguishedName, String loginUserName, String loginIdentityName, String dynamicType, String description ) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setDynamicObjectId( okrConfigSecretary.getId() );
		o.setDynamicObjectTitle( okrConfigSecretary.getLeaderName() );
		o.setDynamicObjectType( "领导秘书配置" );
		o.setContent( dynamicType + ":设置领导[" + okrConfigSecretary.getLeaderIdentity() + "]秘书为：" + okrConfigSecretary.getSecretaryIdentity() );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );		
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithSystemConfig( OkrConfigSystem okrConfigSystem, String distinguishedName,
			String loginUserName, String loginIdentityName, String dynamicType, String description ) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setDynamicObjectId( okrConfigSystem.getId() );
		o.setDynamicObjectTitle( okrConfigSystem.getConfigCode() );
		o.setDynamicObjectType( "系统配置" );
		o.setContent( dynamicType + ":" + okrConfigSystem.getConfigCode() + "["+ okrConfigSystem.getConfigName()+"] = " + okrConfigSystem.getConfigValue() );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithConfigWorkLevel( OkrConfigWorkLevel okrConfigWorkLevel, String distinguishedName,
			String loginUserName, String loginIdentityName, String dynamicType, String description ) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setDynamicObjectId( okrConfigWorkLevel.getId() );
		o.setDynamicObjectTitle( okrConfigWorkLevel.getWorkLevelName() );
		o.setDynamicObjectType( "工作级别配置" );
		o.setContent( dynamicType + ":" + okrConfigWorkLevel.getWorkLevelName() );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithConfigWorkType(OkrConfigWorkType okrConfigWorkType, String distinguishedName,
			String loginUserName, String loginIdentityName, String dynamicType, String description ) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setDynamicObjectId( okrConfigWorkType.getId() );
		o.setDynamicObjectTitle( okrConfigWorkType.getWorkTypeName() );
		o.setDynamicObjectType( "工作类别配置" );
		o.setContent( dynamicType + ":" + okrConfigWorkType.getWorkTypeName() );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithWorkChat(OkrWorkChat okrWorkChat, String distinguishedName, String loginUserName,
			String loginIdentityName, String dynamicType, String description) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setCenterId( okrWorkChat.getCenterId() );
		o.setCenterTitle( okrWorkChat.getCenterTitle() );
		o.setWorkId( okrWorkChat.getWorkId() );
		o.setWorkTitle( okrWorkChat.getWorkTitle() );
		o.setDynamicObjectId( okrWorkChat.getId() );
		o.setDynamicObjectTitle( okrWorkChat.getTargetIdentity() );
		o.setDynamicObjectType( "工作交流信息" );
		o.setContent( dynamicType + ":[" + okrWorkChat.getSenderIdentity() + "]发送给[" + okrWorkChat.getTargetIdentity() + "]:“" + okrWorkChat.getContent() +"”" );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithWorkReport(OkrWorkReportBaseInfo okrWorkReportBaseInfo, String distinguishedName,
			String loginUserName, String loginIdentityName, String dynamicType, String description) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setCenterId( okrWorkReportBaseInfo.getCenterId() );
		o.setCenterTitle( okrWorkReportBaseInfo.getCenterTitle() );
		o.setWorkId( okrWorkReportBaseInfo.getWorkId() );
		o.setWorkTitle( okrWorkReportBaseInfo.getWorkTitle() );
		o.setDynamicObjectId( okrWorkReportBaseInfo.getId() );
		o.setDynamicObjectTitle( okrWorkReportBaseInfo.getTitle() );
		o.setDynamicObjectType( "工作汇报信息" );
		o.setContent( dynamicType + ":" + okrWorkReportBaseInfo.getTitle() + "[" +okrWorkReportBaseInfo.getActivityName()+ "]" );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithTask(OkrTask okrTask, String distinguishedName, String loginUserName,
			String loginIdentityName, String dynamicType, String description) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setCenterId( okrTask.getCenterId() );
		o.setCenterTitle( okrTask.getCenterTitle() );
		o.setWorkId( okrTask.getWorkId() );
		o.setWorkTitle( okrTask.getWorkTitle() );
		o.setDynamicObjectId( okrTask.getId() );
		o.setDynamicObjectTitle( okrTask.getTitle() );
		o.setDynamicObjectType( "工作汇报信息" );
		o.setContent( dynamicType + ":" + okrTask.getTitle() + "[" +okrTask.getActivityName()+ "]" );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}

	public static void sendWithTaskHandled(OkrTaskHandled okrTaskHandled, String distinguishedName,
			String loginUserName, String loginIdentityName, String dynamicType, String description) throws Exception {
		WrapInWorkDynamic o = new WrapInWorkDynamic();
		o.setCenterId( okrTaskHandled.getCenterId() );
		o.setCenterTitle( okrTaskHandled.getCenterTitle() );
		o.setWorkId( okrTaskHandled.getWorkId() );
		o.setWorkTitle( okrTaskHandled.getWorkTitle() );
		o.setDynamicObjectId( okrTaskHandled.getId() );
		o.setDynamicObjectTitle( okrTaskHandled.getTitle() );
		o.setDynamicObjectType( "工作汇报信息" );
		o.setContent( dynamicType + ":" + okrTaskHandled.getTitle() + "[" +okrTaskHandled.getActivityName()+ "]" );
		o.setOperatorName( distinguishedName );
		o.setTargetName( loginUserName );
		o.setTargetIdentity( loginIdentityName );
		o.setDescription( description );
		o.setDynamicType( dynamicType );
		ThisApplication.queueWorkDynamicRecord.send( o );
	}
	
}
