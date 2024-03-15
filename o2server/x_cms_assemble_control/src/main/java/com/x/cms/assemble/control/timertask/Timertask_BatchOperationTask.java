package com.x.cms.assemble.control.timertask;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.CmsBatchOperationQueryService;
import com.x.cms.assemble.control.service.DocumentInfoService;
import com.x.cms.core.entity.CmsBatchOperation;

/**
 * 定时代理: 定期执行批处理，或者将批处理信息压入处理队列并且待久化（如果队列是空的话）
 *
 */
public class Timertask_BatchOperationTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Timertask_BatchOperationTask.class);
	private CmsBatchOperationQueryService cmsBatchOperationQueryService = new CmsBatchOperationQueryService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		if (ThisApplication.queueBatchOperation.isEmpty()) {
			List<CmsBatchOperation> operations = null;
			try {
				logger.debug("Timertask_BatchOperationTask ->  query 1000 cms batch operation in database.");
				operations = cmsBatchOperationQueryService.list(1000, 8);
			} catch (Exception e) {
				logger.error(e);
			}

			if (ListTools.isNotEmpty(operations)) {
				for (CmsBatchOperation operation : operations) {
					try {
						logger.debug(
								"Timertask_BatchOperationTask -> send operation to queue[queueBatchOperation].");
						ThisApplication.queueBatchOperation.send(operation);
					} catch (Exception e) {
						logger.error(e);
					}
				}
			} else {
				logger.info(
						"Timertask_BatchOperationTask -> not found any cms batch operation, try to check unreview document in database.");
				// 如果队列里已经没有任务了，那么检查一下是否还有未revieiw的文档，添加到队列
				DocumentInfoService documentInfoService = new DocumentInfoService();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					List<String> ids = documentInfoService.listUnReviewIds(emc, 1000, 5);
					if (ListTools.isNotEmpty(ids)) {
						CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
						for (String docId : ids) {
							cmsBatchOperationPersistService.addOperation(
									CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT,
									CmsBatchOperationProcessService.OPT_TYPE_PERMISSION, docId, docId,
									"刷新文档权限：ID=" + docId);
						}
					} else {
						logger.debug("Timertask_BatchOperationTask -> not found any unreview document in database.");
						// 也没有需要review的文档了，那么检查一下最近变更过的身份，组织，群组，人员等信息
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.info("Timertask_BatchOperationTask -> queueBatchOperation is processing, wait to next excute point.");
		}
		logger.info("Timertask_BatchOperationTask -> batch operations timer task excute completed.");
	}


}