package com.x.mail.assemble.control;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.http.EffectivePerson;
import com.x.mail.assemble.control.factory.AccountFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;
	
	private LogUtil logger = new LogUtil( Business.class );

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}
	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private AccountFactory accountFactory;
	private Organization organization;
	
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization();
		}
		return organization;
	}
	
	public AccountFactory getAccountFactory() throws Exception {
		if (null == this.accountFactory) {
			this.accountFactory = new AccountFactory(this);
		}
		return accountFactory;
	}
	
	
	/**
	 * TODO 保存日志信息
	 * @param person  处理人帐号
 	 * @param description   日志描述
	 * @param appId   应用ID
	 * @param catagoryId  分类ID
	 * @param documentId  文档ID
	 * @param fileId      文件附件ID
	 * @param operationLevel  操作级别：应用|文件|分类|文档
	 * @param operationType  操作类别：新增|更新|删除
	 * @return
	 */
//	public boolean log( String person, String description, String appId, String catagoryId, String documentId, String fileId, String operationLevel, String operationType ) {
//		logger.info("[Business.log]用户["+person+"]进行了操作：" + description );
//		//先查询人员信息
//		String operatorUid = person;
//		
//		//再组织日志信息
//		Log log = new Log();
//		log.setAppId(appId);
//		log.setCatagoryId(catagoryId);
//		log.setDescription(description);
//		log.setDocumentId(documentId);
//		log.setFileId(fileId);
//		log.setOperatorName(operatorUid);
//		log.setOperatorUid(operatorUid);
//		log.setOperationType(operationType);
//		log.setOperationLevel(operationLevel);
//		
//		try {
//			//logger.info("System trying save log to database[Business.log], description=" + description );
//			getLogFactory().saveLog(log);
//			logger.info("System save log success[Business.log]......" );
//		} catch (Exception e) {
//			logger.error("System save log error[Business.log]......", e );
//			return false;
//		}
//		return true;
//	}
	
	/**
	 * TODO 判断用户是否管理员权限
	 * @param request
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public boolean isXAdmin( HttpServletRequest request, EffectivePerson person ) throws Exception {		
		//如果用户的身份是平台的超级管理员，那么就是超级管理员权限
		if ( person.isManager() ) {
			return true;
		}		
		
		return false;
	}
	
	/**
	 * TODO 判断应用信息是否可以被删除，查询与其他数据之间的关联信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public boolean accountDeleteAvailable( String appId ) throws Exception {	
		//查询对应账号下里是否有邮件
//		long count = this.getCatagoryInfoFactory().countByAppId(appId);
//		if( count > 0 ){
//			logger.info("There are "+count+" catagories belong to app{'id':'" +appId+ "'}, app can not delete!");
//			return false;
//		}
		return true;
	}
	
	
	
	
}
