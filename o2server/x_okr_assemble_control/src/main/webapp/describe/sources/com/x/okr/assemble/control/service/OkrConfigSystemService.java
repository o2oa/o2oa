package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrConfigSystem;

/**
 * 类   名：OkrConfigSystemService<br/>
 * 实体类：OkrConfigSystem<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrConfigSystemService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrConfigSystemService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrConfigSystem对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrConfigSystem get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrConfigSystem.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrConfigSystem对象
	 * @param wrapIn
	 */
	public OkrConfigSystem save( OkrConfigSystem wrapIn ) throws Exception {
		OkrConfigSystem okrConfigSystem = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrConfigSystem =  emc.find( wrapIn.getId(), OkrConfigSystem.class );
				if( okrConfigSystem != null ){
					emc.beginTransaction( OkrConfigSystem.class );
					wrapIn.copyTo( okrConfigSystem, JpaObject.FieldsUnmodify );
					emc.check( okrConfigSystem, CheckPersistType.all );	
					emc.commit();
				}else{
					okrConfigSystem = new OkrConfigSystem();
					emc.beginTransaction( OkrConfigSystem.class );
					wrapIn.copyTo( okrConfigSystem );
					okrConfigSystem.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrConfigSystem, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrConfigSystem update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrConfigSystem = new OkrConfigSystem();
				emc.beginTransaction( OkrConfigSystem.class );
				wrapIn.copyTo( okrConfigSystem );
				emc.persist( okrConfigSystem, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrConfigSystem create got a error!", e);
				throw e;
			}
		}
		return okrConfigSystem;
	}
	
	/**
	 * 根据ID从数据库中删除OkrConfigSystem对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrConfigSystem okrConfigSystem = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrConfigSystem = emc.find(id, OkrConfigSystem.class);
			if (null == okrConfigSystem) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrConfigSystem.class );
				emc.remove( okrConfigSystem, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据传入的ID从数据库查询OkrConfigSystem对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getValueWithConfigCode( String configCode ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrConfigSystemFactory().getValueWithConfigCode( configCode );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据传入的ID从数据库查询OkrConfigSystem对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrConfigSystem getWithConfigCode( String configCode ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrConfigSystemFactory().getWithConfigCode(configCode);
		}catch( Exception e ){
			throw e;
		}
	}

	public List<OkrConfigSystem> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrConfigSystemFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 检查配置项是否存在，如果不存在根据信息创建一个新的配置项
	 * @param configCode
	 * @param configName
	 * @param configValue
	 * @param description
	 * @throws Exception
	 */
	public void checkAndInitSystemConfig( String configCode, String configName, String configValue, String description, String type, String selectContent, Boolean isMultiple, Integer orderNumber ) throws Exception {
		if( configCode  == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null!" );
		}
		if( configName  == null || configName.isEmpty() ){
			throw new Exception( "configName is null!" );
		}
		Business business = null;
		OkrConfigSystem okrConfigSystem = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			okrConfigSystem = business.okrConfigSystemFactory().getWithConfigCode( configCode );
		}catch( Exception e ){
			logger.warn( "system find system config{'configCode':'"+configCode+"'} got an exception. " );
			throw e;
		}
		//如果配置不存在，则新建一个配置记录
		if( okrConfigSystem == null ){
			okrConfigSystem = new OkrConfigSystem();
			okrConfigSystem.setConfigCode( configCode );
			okrConfigSystem.setConfigName( configName );
			okrConfigSystem.setConfigValue( configValue );
			okrConfigSystem.setDescription( description );
			okrConfigSystem.setOrderNumber( orderNumber );
			okrConfigSystem.setValueType( type );
			okrConfigSystem.setSelectContent( selectContent );
			okrConfigSystem.setIsMultiple( isMultiple );
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction( OkrConfigSystem.class );
				emc.persist( okrConfigSystem, CheckPersistType.all );
				//logger.info("系统参数基础信息已经被新增：" + okrConfigSystem.getConfigCode() + "[" + okrConfigSystem.getConfigName()+ "].");
				emc.commit();
			}catch( Exception e ){
				logger.warn("system persist new system config{'configCode':'"+configCode+"'} got an exception. " );
				throw e;
			}
		}else{
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				okrConfigSystem = emc.find( okrConfigSystem.getId(), OkrConfigSystem.class );
				emc.beginTransaction( OkrConfigSystem.class );
				if( !configName.equals( okrConfigSystem.getConfigName() ) ){
					okrConfigSystem.setConfigName( configName );
				}
				if( orderNumber != okrConfigSystem.getOrderNumber() ){
					okrConfigSystem.setOrderNumber(orderNumber);
				}
				if( description != null ){
					okrConfigSystem.setDescription( description );
				}
				if( type != null ){
					okrConfigSystem.setValueType( type );
				}
				if( selectContent != null ){
					okrConfigSystem.setSelectContent( selectContent );
				}
				if( isMultiple != null ){
					okrConfigSystem.setIsMultiple( isMultiple );
				}
				emc.check( okrConfigSystem, CheckPersistType.all );
				emc.commit();
				//logger.info("系统参数基础信息已经被更新：" + okrConfigSystem.getConfigCode() + "[" + okrConfigSystem.getConfigName()+ "].");
			}catch( Exception e ){
				logger.warn("system update system config{'configCode':'"+configCode+"'} got an exception. ");
				throw e;
			}
		}
	}

	/**
	 *  REPORT_WORKFLOW_TYPE	工作汇报工作流方式	ADMIN_AND_ALLLEADER
		REPORT_SUPERVISOR	        汇报督办员身份	       蔡艳红(O2研发团队)
		REPORT_AUDIT_LEADER	        汇报审阅领导	               周睿(O2研发团队),胡起(O2研发团队),刘振兴(O2研发团队)
		REPORT_AUDIT_LEVEL	        汇报审阅控制层级	   1
		TOPUNIT_WORK_ADMIN      顶层组织工作管理员            蔡艳红(O2研发团队)
	 * @throws Exception 
	 */
	public void initAllSystemConfig() throws Exception {
		String value = null, description = null, type = null, selectContent = null;
		Boolean isMultiple = false;
		Integer ordernumber = 0;
		
		value = "";
		type = "identity";
		selectContent = null;
		isMultiple = true;
		description = "顶层组织工作管理员：可选值为指定的人员身份，可多值。顶层组织工作管理可以进行工作部署，其他人员不允许进行工作部署。（暂定配置，后续使用权限设计实现）。";
		try {
			checkAndInitSystemConfig("TOPUNIT_WORK_ADMIN", "顶层组织工作管理员", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'TOPUNIT_WORK_ADMIN' got an exception." );
			throw e;
		}
		
		/**
		 * 汇报流程方式
		 * reportWorkflowType = ADMIN_AND_ALLLEADER - 经过工作管理员和所有的批示领导
		 * reportWorkflowType = DEPLOYER - 工作部署者审核（默认）
		 */
		value = "DEPLOYER";
		type = "select";
		selectContent = "ADMIN_AND_ALLLEADER|DEPLOYER";
		isMultiple = false;
		description = "汇报流程方式：可选值：ADMIN_AND_ALLLEADER|DEPLOYER。值为ADMIN_AND_ALLLEADER时工作汇报处理需经过汇报督办员身份和所有的批示领导，否则仅需要部署者审核即可。";
		try {
			checkAndInitSystemConfig("REPORT_WORKFLOW_TYPE", "工作汇报工作流方式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_WORKFLOW_TYPE' got an exception." );
			throw e;
		}
		
		value = null;
		type = "identity";
		selectContent = null;
		isMultiple = false;
		description = "汇报督办员身份：可选值为指定的人员身份，单值。该配置与汇报流程方式中的ADMIN_AND_ALLLEADER配合使用";
		try {
			checkAndInitSystemConfig("REPORT_SUPERVISOR", "汇报督办员身份", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_SUPERVISOR' got an exception." );
			throw e;
		}
		
		value = null;
		type = "identity";
		selectContent = null;
		isMultiple = true;
		description = "汇报审阅领导：可选值为指定的人员身份，多值以半角“,”分隔。默认的汇报审阅领导，创建中心工作时取默认领导，编辑时可修改作为具体中心工作的汇报审阅领导。";
		try {
			checkAndInitSystemConfig("REPORT_AUDIT_LEADER", "汇报审阅领导", value, description, type, selectContent, isMultiple , ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_AUDIT_LEADER' got an exception." );
			throw e;
		}
		
		value = "1";
		type = "number";
		selectContent = null;
		isMultiple = false;
		description = "汇报审阅控制层级：可选值为数字。该配置指定配置ADMIN_AND_ALLLEADER控制的工作层级（LEVEL）数，控制之外的工作汇报审批方式指定为DEPLOYER，即审阅人为部署者。";
		try {
			checkAndInitSystemConfig("REPORT_AUDIT_LEVEL", "汇报审阅控制层级", value, description, type, selectContent, isMultiple , ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_AUDIT_LEVEL' got an exception." );
			throw e;
		}
		
		value = "OPEN";
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "定期汇报自动生成：可选值[OPEN|CLOSE]。此配置控制是否在系统中启用定期生成汇报拟稿功能。";
		try {
			checkAndInitSystemConfig("REPORT_AUTOCREATE", "定期汇报自动生成", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_AUTOCREATE' got an exception." );
			throw e;
		}
		
		value = "10:00:00"; //10:00:00
		type = "time";
		selectContent = null;
		isMultiple = false;
		description = "定期汇报生成时间：配置值格式为[hh24:mi:ss]。此配置控制定期汇报的启动时间，值为时分秒格式，默认：10:00:00。";
		try {
			checkAndInitSystemConfig("REPORT_CREATETIME", "定期汇报生成时间", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_CREATETIME' got an exception." );
			throw e;
		}
		
		value = "OPEN"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "用户工作汇报功能：可选值[OPEN|CLOSE]。此配置控制是否在系统中允许用户自主进行工作汇报拟稿功能。";
		try {
			checkAndInitSystemConfig("REPORT_USERCREATE", "用户工作汇报功能", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_USERCREATE' got an exception." );
			throw e;
		}
		
		value = "OPEN"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "汇报工作进度：可选值[OPEN|CLOSE]。此配置控制是否在用户进行工作汇报时要求填写工作的进度百分比以及是否已经完成的选择项，作为后统计依据。";
		try {
			checkAndInitSystemConfig("REPORT_PROGRESS", "汇报工作进度", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_PROGRESS' got an exception." );
			throw e;
		}
		
		value = "ICON"; //LIST|ICON
		type = "select";
		selectContent = "LIST|ICON";
		isMultiple = false;
		description = "脑图工作列表样式：可选值[LIST|ICON]。系统脑图展示中中心工作列表提供两种展现样式，普通列表和工作图标。";
		try {
			checkAndInitSystemConfig("MIND_LISTSTYLE", "脑图工作列表样式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'MIND_LISTSTYLE' got an exception." );
			throw e;
		}
		
		value = "OPEN"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "工作授权功能：可选值[OPEN|CLOSE]。此配置控制是否在系统中启用工作授权功能。";
		try {
			checkAndInitSystemConfig("WORK_AUTHORIZE", "工作授权功能", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'WORK_AUTHORIZE' got an exception." );
			throw e;
		}
		
		value = "NONE"; //NONE|READ|TASK
		type = "select";
		selectContent = "NONE|READ|TASK";
		isMultiple = false;
		description = "工作授权人汇报通知方式：可选值[NONE|READ|TASK]。此配置控制工作负责人提交工作汇报时以何种方式通知授权人，或者不通知授权人。";
		try {
			checkAndInitSystemConfig("REPORT_AUTHOR_NOTICE", "汇报通知授权人方式", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_AUTHOR_NOTICE' got an exception." );
			throw e;
		}
		
		value = "CLOSE"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "汇报审核进展通知：可选值[OPEN|CLOSE]。此配置控制工作汇报被领导审核后是否以待阅方式通知汇报人。";
		try {
			checkAndInitSystemConfig("REPORTOR_AUDIT_NOTICE", "汇报审核进展通知", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORTOR_AUDIT_NOTICE' got an exception." );
			throw e;
		}
		
		value = "OPEN"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "工作拆分功能：可选值[OPEN|CLOSE]。此配置控制系统内工作部署后，是否允许责任者进行下一级的工作拆解。";
		try {
			checkAndInitSystemConfig("WORK_DISMANTLING", "工作拆分功能", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'WORK_DISMANTLING' got an exception." );
			throw e;
		}
		
		value = "OPEN"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "首页工作状态列表：可选值[OPEN|CLOSE]。此配置控制系统首页是否展示工作状态列表的TAB页。";
		try {
			checkAndInitSystemConfig("INDEX_WORK_STATUSLIST", "首页工作状态列表", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'INDEX_WORK_STATUSLIST' got an exception." );
			throw e;
		}
		
		value = "PROMPTNESSRATE"; //PROMPTNESSRATE|COMPLETIONRATE
		type = "select";
		selectContent = "PROMPTNESSRATE|COMPLETIONRATE";
		isMultiple = false;
		description = "首页统计状态类别：可选值[PROMPTNESSRATE|COMPLETIONRATE]。此配置控制系统首页统计显示的内容，及时率统计或完成率统计。";
		try {
			checkAndInitSystemConfig("INDEX_STATISTIC_TYPE", "首页统计状态类别", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'INDEX_STATISTIC_TYPE' got an exception." );
			throw e;
		}
		
		value = ""; //可以执行归档的用户身份列表，多值，可以用,号分隔
		type = "identity";
		selectContent = null;
		isMultiple = true;
		description = "工作归档管理员：可选值为指定的人员身份，多值以半角“,”分隔。此配置控制系统中可以对中心工作进行归档操作的用户。";
		try {
			checkAndInitSystemConfig("ARCHIVEMANAGER", "工作归档管理员", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'ARCHIVEMANAGER' got an exception." );
			throw e;
		}
		
		value = "OPEN"; //OPEN|CLOSE
		type = "select";
		selectContent = "OPEN|CLOSE";
		isMultiple = false;
		description = "工作汇报自动结束：可选值[OPEN|CLOSE]。此配置控制系统是否会在生成新的汇报时自动结束已经存在的工作汇报信息，删除之前的汇报待办信息。";
		try {
			checkAndInitSystemConfig("REPORT_AUTO_OVER", "工作汇报自动结束", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'REPORT_AUTO_OVER' got an exception." );
			throw e;
		}
		
		value = "0";
		type = "number";
		selectContent = "";
		isMultiple = false;
		description = "工作考核流程发起次数：可选值[数值]。此配置控制系统能发起考核流程的次数，如果为0，则不允许发起。";
		try {
			checkAndInitSystemConfig("APPRAISE_MAX_TIMES", "工作考核流程发起次数", value, description, type, selectContent, isMultiple, ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPRAISE_MAX_TIMES' got an exception." );
			throw e;
		}
		
		value = "NONE";
		type = "workflow";
		selectContent = "";
		isMultiple = false;
		description = "工作考核流程ID：指定发起工作考核流程时使用的流程ID.";
		try {
			checkAndInitSystemConfig("APPRAISE_WORKFLOW_ID", "工作考核流程ID", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'APPRAISE_WORKFLOW_ID' got an exception." );
			logger.error(e);
		}
		
		value = "NONE";
		type = "text";
		selectContent = "";
		isMultiple = false;
		description = "短信接口WSDL地址.";
		try {
			checkAndInitSystemConfig("SMS_WSDL", "短信接口WSDL地址", value, description, type, selectContent, isMultiple,  ++ordernumber );
		} catch (Exception e) {
			logger.warn( "system init system config 'SMS_WSDL' got an exception." );
			logger.error(e);
		}
	}
}
