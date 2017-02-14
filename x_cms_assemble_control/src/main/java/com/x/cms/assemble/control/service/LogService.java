package com.x.cms.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.cms.core.entity.Log;

public class LogService {

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
	 * @throws Exception 
	 */
	public boolean log( EntityManagerContainer emc, String person, String description, String appId, String catagoryId, String documentId, String fileId, String operationLevel, String operationType ) throws Exception {
		String operatorUid = person;
		Log log = new Log();
		log.setAppId(appId);
		log.setCatagoryId(catagoryId);
		log.setDescription(description);
		log.setDocumentId(documentId);
		log.setFileId(fileId);
		log.setOperatorName(operatorUid);
		log.setOperatorUid(operatorUid);
		log.setOperationType(operationType);
		log.setOperationLevel(operationLevel);
		try {
			emc.beginTransaction(Log.class);
			emc.persist( log, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

}
