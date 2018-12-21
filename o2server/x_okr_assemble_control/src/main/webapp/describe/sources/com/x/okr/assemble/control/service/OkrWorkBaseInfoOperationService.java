package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkDynamics;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class OkrWorkBaseInfoOperationService {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkBaseInfoOperationService.class);
	private OkrWorkBaseInfoExcuteArchive okrWorkBaseInfoExcuteArchive = new OkrWorkBaseInfoExcuteArchive();
	private OkrWorkBaseInfoExcuteProgressAdjust okrWorkBaseInfoExcuteProgressAdjust = new OkrWorkBaseInfoExcuteProgressAdjust();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrSendNotifyService okrNotifyService = new OkrSendNotifyService();
	private OkrStatisticReportStatusService okrStatisticReportStatusService = new OkrStatisticReportStatusService();
	private OkrTaskService okrTaskService = new OkrTaskService();

	/**
	 * 
	 * 向数据库保存OkrWorkBaseInfo对象, 第一次保存或者是继续拆解工作
	 * 
	 * @param wrapIn
	 * @param workDetail
	 * @param dutyDescription
	 * @param landmarkDescription
	 * @param majorIssuesDescription
	 * @param progressAction
	 * @param progressPlan
	 * @param resultDescription
	 * @return
	 * @throws Exception
	 */
	public OkrWorkBaseInfo save(OkrWorkBaseInfo wrapIn, String workDetail, String dutyDescription,
			String landmarkDescription, String majorIssuesDescription, String progressAction, String progressPlan,
			String resultDescription) throws Exception {
		OkrWorkPerson okrWorkPerson_tmp = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<String> ids = null;
		List<String> statuses = new ArrayList<String>();
		Business business = null;
		int shortChartCount = 30;
		statuses.add("正常");

		// 根据ID查询信息是否存在，如果存在就update，如果不存在就create
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction(OkrWorkBaseInfo.class);
			emc.beginTransaction(OkrWorkDetailInfo.class);
			emc.beginTransaction(OkrWorkPerson.class);
			
			if (StringUtils.isNotEmpty( wrapIn.getId() )) {
				okrWorkBaseInfo = emc.find(wrapIn.getId(), OkrWorkBaseInfo.class); // 查询基础信息
				okrWorkDetailInfo = emc.find(wrapIn.getId(), OkrWorkDetailInfo.class); // 查询详细信息
			}
			// 保存工作基础内容
			if (okrWorkBaseInfo == null) {
				okrWorkBaseInfo = new OkrWorkBaseInfo();
				
				wrapIn.copyTo(okrWorkBaseInfo);
				
				okrWorkBaseInfo.setId(wrapIn.getId());// 使用参数传入的ID作为记录的ID
				if (workDetail != null && workDetail.length() > shortChartCount) {
					okrWorkBaseInfo.setTitle(workDetail.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setTitle(workDetail);
				}
				if (workDetail != null && workDetail.length() > shortChartCount) {
					okrWorkBaseInfo.setShortWorkDetail(workDetail.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortWorkDetail(workDetail);
				}
				if (dutyDescription != null && dutyDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortDutyDescription(dutyDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortDutyDescription(dutyDescription);
				}
				if (landmarkDescription != null && landmarkDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortLandmarkDescription(landmarkDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortLandmarkDescription(landmarkDescription);
				}
				if (majorIssuesDescription != null && majorIssuesDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortMajorIssuesDescription(majorIssuesDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortMajorIssuesDescription(majorIssuesDescription);
				}
				if (progressAction != null && progressAction.length() > shortChartCount) {
					okrWorkBaseInfo.setShortProgressAction(progressAction.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortProgressAction(progressAction);
				}
				if (progressPlan != null && progressPlan.length() > shortChartCount) {
					okrWorkBaseInfo.setShortProgressPlan(progressPlan.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortProgressPlan(progressPlan);
				}
				if (resultDescription != null && resultDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortResultDescription(resultDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortResultDescription(resultDescription);
				}
				okrWorkBaseInfo.setUpdateTime(okrWorkBaseInfo.getCreateTime());
				emc.persist(okrWorkBaseInfo, CheckPersistType.all);
			} else {// 更新
				//附件信息

				List<String> attachmemnts = okrWorkBaseInfo.getAttachmentList();
				
				wrapIn.copyTo( okrWorkBaseInfo, JpaObject.FieldsUnmodify );
				okrWorkBaseInfo.setAttachmentList(attachmemnts);

				if (workDetail != null && workDetail.length() > shortChartCount) {
					okrWorkBaseInfo.setTitle(workDetail.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setTitle(workDetail);
				}
				if (workDetail != null && workDetail.length() > shortChartCount) {
					okrWorkBaseInfo.setShortWorkDetail(workDetail.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortWorkDetail(workDetail);
				}
				if (dutyDescription != null && dutyDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortDutyDescription(dutyDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortDutyDescription(dutyDescription);
				}
				if (landmarkDescription != null && landmarkDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortLandmarkDescription(landmarkDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortLandmarkDescription(landmarkDescription);
				}
				if (majorIssuesDescription != null && majorIssuesDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortMajorIssuesDescription(majorIssuesDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortMajorIssuesDescription(majorIssuesDescription);
				}
				if (progressAction != null && progressAction.length() > shortChartCount) {
					okrWorkBaseInfo.setShortProgressAction(progressAction.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortProgressAction(progressAction);
				}
				if (progressPlan != null && progressPlan.length() > shortChartCount) {
					okrWorkBaseInfo.setShortProgressPlan(progressPlan.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortProgressPlan(progressPlan);
				}
				if (resultDescription != null && resultDescription.length() > shortChartCount) {
					okrWorkBaseInfo.setShortResultDescription(resultDescription.substring(0, shortChartCount) + "...");
				} else {
					okrWorkBaseInfo.setShortResultDescription(resultDescription);
				}
				emc.check(okrWorkBaseInfo, CheckPersistType.all);
			}
			
			// 保存详细信息数据
			if (okrWorkDetailInfo == null) {
				okrWorkDetailInfo = new OkrWorkDetailInfo();
				okrWorkDetailInfo.setId(wrapIn.getId()); // 详细信息的ID与工作基础信息ID一致
				okrWorkDetailInfo.setCenterId(wrapIn.getCenterId());
				okrWorkDetailInfo.setWorkDetail(workDetail);
				okrWorkDetailInfo.setDutyDescription(dutyDescription);
				okrWorkDetailInfo.setLandmarkDescription(landmarkDescription);
				okrWorkDetailInfo.setMajorIssuesDescription(majorIssuesDescription);
				okrWorkDetailInfo.setProgressAction(progressAction);
				okrWorkDetailInfo.setProgressPlan(progressPlan);
				okrWorkDetailInfo.setResultDescription(resultDescription);
				okrWorkDetailInfo.setUpdateTime(okrWorkDetailInfo.getCreateTime());
				emc.persist(okrWorkDetailInfo, CheckPersistType.all);
			} else {// 更新
				okrWorkDetailInfo.setCenterId(wrapIn.getCenterId());
				okrWorkDetailInfo.setWorkDetail(workDetail);
				okrWorkDetailInfo.setDutyDescription(dutyDescription);
				okrWorkDetailInfo.setLandmarkDescription(landmarkDescription);
				okrWorkDetailInfo.setMajorIssuesDescription(majorIssuesDescription);
				okrWorkDetailInfo.setProgressAction(progressAction);
				okrWorkDetailInfo.setProgressPlan(progressPlan);
				okrWorkDetailInfo.setResultDescription(resultDescription);
				emc.check(okrWorkDetailInfo, CheckPersistType.all);
			}
			// 保存工作的干系人信息，先根据工作基础信息来获取工作所有的干系人对象信息
			okrWorkPersonList = okrWorkPersonService.getWorkPersonListByWorkBaseInfoForWorkSave(okrWorkBaseInfo);
			if (okrWorkPersonList != null && okrWorkPersonList.size() > 0) {
				for (OkrWorkPerson okrWorkPerson : okrWorkPersonList) {
					ids = business.okrWorkPersonFactory().listByWorkAndIdentity(okrWorkPerson.getCenterId(),
							okrWorkPerson.getWorkId(), okrWorkPerson.getEmployeeIdentity(),
							okrWorkPerson.getProcessIdentity(), statuses);
					if (ids != null && ids.size() > 0) {
						for (String id : ids) {
							okrWorkPerson_tmp = emc.find(id, OkrWorkPerson.class);
							if (okrWorkPerson_tmp != null) {
								emc.remove(okrWorkPerson_tmp);
							}
						}
					}
					okrWorkPerson.setUpdateTime(okrWorkPerson.getCreateTime());
					emc.persist(okrWorkPerson, CheckPersistType.all);
				}
			}
			emc.commit();
		} catch (Exception e) {
			logger.warn("OkrWorkBaseInfo update/save get a error!");
			throw e;
		}
		return okrWorkBaseInfo;
	}

	/**
	 * 强制撤回，不管有没有下级工作信息，根据ID从撤回所有的工作以及全部下级工作
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void recycleWorkForce(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, system can not recycle any object.");
		}
		List<String> ids = getSubNormalWorkBaseInfoIds(id);
		if (ids != null && ids.size() > 0) {
			for (String workid : ids) {
				recycleWorkForce(workid);// 处理这个工作的下级工作，完成后再处理该工作
				recycleWork(workid);
			}
		} else {
			// 已经没有下级工作了，可以进行撤回
			recycleWork(id);
		}
	}

	/**
	 * 收回已经部署的工作，如果要收回的工作已经被拆解到下级工作，则不允许收回 收回工作，其实就是将工作置为已撤回，汇报信息，以及问题请示都不需要变更
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void recycleWork(String workId) throws Exception {
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> subWorkIds = null;
		List<String> taskIds = null;
		List<String> ids = null;
		String[] userIdentityArray = null;
		OkrTask okrTask = null;

		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, system can not delete any object.");
		}
		subWorkIds = getSubNormalWorkBaseInfoIds(workId);
		if (subWorkIds != null && subWorkIds.size() > 0) {
			throw new Exception("该工作存在" + subWorkIds.size() + "个下级工作，该工作暂无法收回。");
		} else {// 工作可以被撤消
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
				okrWorkBaseInfo = emc.find(workId, OkrWorkBaseInfo.class);
				okrWorkDetailInfo = emc.find(workId, OkrWorkDetailInfo.class);

				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrWorkDetailInfo.class);

				if (okrWorkBaseInfo != null) {
					okrWorkBaseInfo.setStatus("已撤回");
					emc.check(okrWorkBaseInfo, CheckRemoveType.all);
				} else {
					logger.warn("can not recycle work, okrWorkBaseInfo is not exist {'id':'" + workId + "'}");
				}

				if (okrWorkDetailInfo != null) {
					okrWorkDetailInfo.setStatus("已撤回");
					emc.check(okrWorkDetailInfo, CheckRemoveType.all);
				} else {
					logger.warn("can not recycle work, okrWorkDetailInfo is not exist {'id':'" + workId + "'}");
				}

				if (okrWorkBaseInfo.getResponsibilityEmployeeName() != null
						&& !okrWorkBaseInfo.getResponsibilityEmployeeName().isEmpty()) {
					userIdentityArray = okrWorkBaseInfo.getResponsibilityEmployeeName().split(",");
					for (String identity : userIdentityArray) {
						// 对待办数据进行处理
						taskIds = okrTaskService.listIdsByCenterAndPerson(okrWorkBaseInfo.getCenterId(), identity,
								"中心工作");
						// 查询该工作的负责人是否有待办信息
						if (taskIds != null && taskIds.size() > 0) {
							// 是否在此中心工作下仍有需要确认和部署的工作
							ids = listUnConfirmWorkIdsByCenterAndPerson(okrWorkBaseInfo.getCenterId(), identity);
							if (ids == null || ids.size() == 0) {
								// 删除待办信息
								for (String taskId : taskIds) {
									okrTask = emc.find(taskId, OkrTask.class);
									if (okrTask != null) {
										emc.remove(okrTask);
									}
								}
							}
						}
					}
				}

				if (ListTools.isNotEmpty( okrWorkBaseInfo.getCooperateIdentityList())) {
					for (String identity : okrWorkBaseInfo.getCooperateIdentityList()) {
						// 对待办数据进行处理
						taskIds = okrTaskService.listIdsByCenterAndPerson(okrWorkBaseInfo.getCenterId(), identity, "中心工作");
						// 查询该工作的协助人是否有待阅信息
						if (taskIds != null && taskIds.size() > 0) {
							// 是否在此中心工作下仍有需要确认和部署的工作
							ids = listUnConfirmWorkIdsByCenterAndPerson(okrWorkBaseInfo.getCenterId(), identity);
							if (ids == null || ids.size() == 0) {
								// 删除待办信息
								for (String taskId : taskIds) {
									okrTask = emc.find(taskId, OkrTask.class);
									if (okrTask != null) {
										emc.remove(okrTask);
									}
								}
							}
						}
					}
				}

				if (ListTools.isNotEmpty( okrWorkBaseInfo.getReadLeaderIdentityList() )) {
					for (String identity : okrWorkBaseInfo.getReadLeaderIdentityList()) {
						// 对待办数据进行处理
						taskIds = okrTaskService.listIdsByCenterAndPerson(okrWorkBaseInfo.getCenterId(), identity, "中心工作");
						// 查询该工作的阅知人是否有待阅信息
						if (taskIds != null && taskIds.size() > 0) {
							// 是否在此中心工作下仍有需要确认和部署的工作
							ids = listUnConfirmWorkIdsByCenterAndPerson(okrWorkBaseInfo.getCenterId(), identity);
							if (ids == null || ids.size() == 0) {
								// 删除待办信息
								for (String taskId : taskIds) {
									okrTask = emc.find(taskId, OkrTask.class);
									if (okrTask != null) {
										emc.remove(okrTask);
									}
								}
							}
						}
					}
				}

				emc.commit();
				// 向工作相关干系人发送消息
				notityRecycleMessage(okrWorkBaseInfo);

			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 强制删除，不管有没有下级工作信息，根据ID从数据库中删除OkrWorkBaseInfo对象 递归删除
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteForce(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, system can not delete any object.");
		}
		List<String> ids = getSubNormalWorkBaseInfoIds(id);
		if (ids != null && ids.size() > 0) {
			for (String workid : ids) {
				deleteForce(workid);
				// deleteByWorkId( workid );
			}
		} else {
			// 已经没有下级工作了，可以进行删除
			deleteByWorkId(id);
		}
	}

	/**
	 * 删除工作信息，如果有下级信息则无法进行删除
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteByWorkId(String workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, system can not delete any object.");
		}
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> subWorkIds = null;
		List<String> ids = null;
		List<String> ids_work = null;
		List<String> st_ids = null;
		OkrStatisticReportStatus okrStatisticReportStatus = null;
		List<String> statuses = new ArrayList<String>();
		OkrTask okrTask = null;
		Business business = null;
		boolean excuteSuccess = true; // 判断执行是否正常
		statuses.add("正常");

		subWorkIds = getSubNormalWorkBaseInfoIds(workId);
		if (subWorkIds != null && subWorkIds.size() > 0) {
			throw new Exception("该工作存在" + subWorkIds.size() + "个下级工作，该工作暂无法删除。");
		} else {// 工作可以被删除
				// logger.debug( "开始删除工作......" );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
				// 删除时详细信息也一并删除，还有所有的下级工作，汇报，请示，人员，审批信息等等
				if (excuteSuccess) {
					try {
						okrWorkBaseInfo = emc.find(workId, OkrWorkBaseInfo.class);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system find okrWorkBaseInfo by workid got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						okrWorkDetailInfo = emc.find(workId, OkrWorkDetailInfo.class);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system find okrWorkBaseInfo by workid got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					// 删除工作以及工作相关信息
					emc.beginTransaction(OkrWorkBaseInfo.class);
					emc.beginTransaction(OkrWorkDetailInfo.class);
					emc.beginTransaction(OkrWorkPerson.class);
					emc.beginTransaction(OkrWorkReportBaseInfo.class);
					emc.beginTransaction(OkrWorkReportDetailInfo.class);
					emc.beginTransaction(OkrWorkReportPersonLink.class);
					emc.beginTransaction(OkrWorkReportProcessLog.class);
					emc.beginTransaction(OkrTask.class);
					emc.beginTransaction(OkrTaskHandled.class);
					emc.beginTransaction(OkrWorkDynamics.class);
					emc.beginTransaction(OkrWorkAuthorizeRecord.class);
					emc.beginTransaction(OkrWorkChat.class);
					emc.beginTransaction(OkrStatisticReportStatus.class);
				}

				if (excuteSuccess) {
					try {
						deleteWorkReportByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkReportByWorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteWorkReportDetailByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkReportDetailByWorkId got an exception, workid:"
								+ workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteWorkReportPersonByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkReportPersonByWorkId got an exception, workid:"
								+ workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteWorkReportProcessLogByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkReportProcessLogByWorkId got an exception, workid:"
								+ workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteWorkPersonByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkPersonByWorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteWorkAuthorizeRecordByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkAuthorizeRecordByWorkId got an exception, workid:"
								+ workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteWorkChatByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteWorkChatByWorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteTaskByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system excute method deleteTaskByWorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				if (excuteSuccess) {
					try {
						deleteTaskHandledByWorkId(workId, emc, true);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn(
								"system excute method deleteTaskHandledByWorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}
				if (excuteSuccess) {
					st_ids = okrStatisticReportStatusService.listIds(null, workId, null, null, null);
					if (st_ids != null && !st_ids.isEmpty()) {
						for (String st_id : st_ids) {
							okrStatisticReportStatus = emc.find(st_id, OkrStatisticReportStatus.class);
							if (okrStatisticReportStatus != null) {
								emc.remove(okrStatisticReportStatus, CheckRemoveType.all);
							}
						}
					}
				}
				if (excuteSuccess) {
					try {
						emc.remove(okrWorkBaseInfo, CheckRemoveType.all);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system delete okrWorkBaseInfo By WorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}
				if (excuteSuccess) {
					try {
						emc.remove(okrWorkDetailInfo, CheckRemoveType.all);
					} catch (Exception e1) {
						excuteSuccess = false;
						logger.warn("system delete okrWorkDetailInfo By WorkId got an exception, workid:" + workId);
						logger.error(e1);
					}
				}

				// 不应该删除
				// deleteWorkDynamicsByWorkId( workId, emc, realDelete );

				if (excuteSuccess) {
					emc.commit();
				} else {
					emc.rollback();
				}

				if (excuteSuccess) {
					emc.beginTransaction(OkrTask.class);
					emc.beginTransaction(OkrTaskHandled.class);

					// 如果责任人在该中心工作下面没有其他需要负责的工作了，那么需要删除该责任人中心工作的待办
					if (okrWorkBaseInfo.getResponsibilityIdentity() != null) {
						String[] responsibilityIdentities = okrWorkBaseInfo.getResponsibilityIdentity().split(",");
						for (String responsibilityIdentitiy : responsibilityIdentities) {
							// 待办删除的功能先不管，用户可以主动提交后删除
							// 先看看该授权者是否仍存在该中心工作的待办，本来就没有待办信息，就不管了
							ids = business.okrTaskFactory().listIdsByTargetActivityAndObjId("TASK", "中心工作",
									okrWorkBaseInfo.getCenterId(), null, responsibilityIdentitiy);
							if (ids != null && !ids.isEmpty()) {
								// 判断该中心工作下是否仍有授权者需要部署和拆解的工作，
								// workPerson表，有责任者是授权者记录
								ids_work = business.okrWorkPersonFactory().listWorkByCenterAndIdentity(
										okrWorkBaseInfo.getCenterId(), responsibilityIdentitiy, "责任者", statuses);
								if (ids_work == null || ids_work.isEmpty()) {// 已经没有需要部署的工作了，需要删除待办并且生成一条已办
									// 删除所有的待办信息
									for (String _id : ids) {
										okrTask = emc.find(_id, OkrTask.class);
										if (okrTask != null) {
											emc.remove(okrTask, CheckRemoveType.all);
										}
									}
								}
							}
						}
					}
				}

				if (excuteSuccess) {
					emc.commit();
				} else {
					emc.rollback();
				}

				if (excuteSuccess) {
					notityDeleteMessage(okrWorkBaseInfo);
				}

			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 根据工作信息ID删除所有汇报信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		List<String> ids = business.okrWorkReportBaseInfoFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().list(ids);
			for (OkrWorkReportBaseInfo okrWorkReportBaseInfo : okrWorkReportBaseInfoList) {
				if (realDelete) {
					emc.remove(okrWorkReportBaseInfo, CheckRemoveType.all);
				} else {
					okrWorkReportBaseInfo.setStatus("已删除");
					emc.check(okrWorkReportBaseInfo, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有工作动态信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkDynamicsByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkDynamics> okrWorkDynamicsList = null;
		List<String> ids = business.okrWorkDynamicsFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkDynamicsList = business.okrWorkDynamicsFactory().list(ids);
			for (OkrWorkDynamics okrWorkDynamics : okrWorkDynamicsList) {
				if (realDelete) {
					emc.remove(okrWorkDynamics, CheckRemoveType.all);
				} else {
					okrWorkDynamics.setStatus("已删除");
					emc.check(okrWorkDynamics, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有工作交流信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkChatByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkChat> okrWorkChatList = null;
		List<String> ids = business.okrWorkChatFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkChatList = business.okrWorkChatFactory().list(ids);
			for (OkrWorkChat _okrWorkChat : okrWorkChatList) {
				emc.remove(_okrWorkChat, CheckRemoveType.all);
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有工作待办信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTaskByWorkId(String workId, EntityManagerContainer emc, boolean realDelete) throws Exception {
		Business business = new Business(emc);
		List<OkrTask> okrTaskList = null;
		List<String> ids = business.okrTaskFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrTaskList = business.okrTaskFactory().list(ids);
			for (OkrTask _okrTask : okrTaskList) {
				emc.remove(_okrTask, CheckRemoveType.all);
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有工作已办办信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTaskHandledByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrTaskHandled> okrTaskHandledList = null;
		List<String> ids = business.okrTaskHandledFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrTaskHandledList = business.okrTaskHandledFactory().list(ids);
			for (OkrTaskHandled _okrTaskHandled : okrTaskHandledList) {
				emc.remove(_okrTaskHandled, CheckRemoveType.all);
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有工作授权信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkAuthorizeRecordByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		List<String> ids = business.okrWorkAuthorizeRecordFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkAuthorizeRecordList = business.okrWorkAuthorizeRecordFactory().list(ids);
			for (OkrWorkAuthorizeRecord okrWorkAuthorizeRecord : okrWorkAuthorizeRecordList) {
				if (realDelete) {
					emc.remove(okrWorkAuthorizeRecord, CheckRemoveType.all);
				} else {
					okrWorkAuthorizeRecord.setStatus("已删除");
					emc.check(okrWorkAuthorizeRecord, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有工作干系人信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkPersonByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkPerson> okrWorkPersonList = null;
		List<String> ids = business.okrWorkPersonFactory().listByWorkId(workId, null);
		if (ids != null && ids.size() > 0) {
			okrWorkPersonList = business.okrWorkPersonFactory().list(ids);
			for (OkrWorkPerson okrWorkPerson : okrWorkPersonList) {
				if (realDelete) {
					emc.remove(okrWorkPerson, CheckRemoveType.all);
				} else {
					okrWorkPerson.setStatus("已删除");
					emc.check(okrWorkPerson, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有汇报处理日志信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportProcessLogByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		List<String> ids = business.okrWorkReportProcessLogFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkReportProcessLogList = business.okrWorkReportProcessLogFactory().list(ids);
			for (OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList) {
				if (realDelete) {
					emc.remove(okrWorkReportProcessLog, CheckRemoveType.all);
				} else {
					okrWorkReportProcessLog.setStatus("已删除");
					emc.check(okrWorkReportProcessLog, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有汇报处理人信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportPersonByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList = null;
		List<String> ids = business.okrWorkReportPersonLinkFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().list(ids);
			for (OkrWorkReportPersonLink okrWorkReportPersonLink : okrWorkReportPersonLinkList) {
				if (realDelete) {
					emc.remove(okrWorkReportPersonLink, CheckRemoveType.all);
				} else {
					okrWorkReportPersonLink.setStatus("已删除");
					emc.check(okrWorkReportPersonLink, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 根据工作信息ID删除所有汇报详细信息
	 * 
	 * @param workId
	 *            工作ID
	 * @param emc
	 *            数据源
	 * @param realDelete
	 *            是否真正删除
	 * @return
	 * @throws Exception
	 */
	public boolean deleteWorkReportDetailByWorkId(String workId, EntityManagerContainer emc, boolean realDelete)
			throws Exception {
		Business business = new Business(emc);
		List<OkrWorkReportDetailInfo> okrWorkReportDetailInfoList = null;
		List<String> ids = business.okrWorkReportDetailInfoFactory().listByWorkId(workId);
		if (ids != null && ids.size() > 0) {
			okrWorkReportDetailInfoList = business.okrWorkReportDetailInfoFactory().list(ids);
			for (OkrWorkReportDetailInfo okrWorkReportDetailInfo : okrWorkReportDetailInfoList) {
				if (realDelete) {
					emc.remove(okrWorkReportDetailInfo, CheckRemoveType.all);
				} else {
					okrWorkReportDetailInfo.setStatus("已删除");
					emc.check(okrWorkReportDetailInfo, CheckPersistType.all);
				}
			}
		}
		return true;
	}

	/**
	 * 发送消息通知
	 * 
	 * @param okrWorkBaseInfo
	 */
	private void notityDeleteMessage(OkrWorkBaseInfo okrWorkBaseInfo) {
		try {
			okrNotifyService.notifyCooperaterForWorkDeleted(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.warn("工作删除成功，向协助者发送消息通知发生异常！");
			logger.error(e);
		}
		try {
			okrNotifyService.notifyDeployerForWorkDeletedSuccess(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.warn("工作删除成功，向部署者发送消息通知发生异常！");
			logger.error(e);
		}
		try {
			okrNotifyService.notifyResponsibilityForWorkDeleted(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.warn("工作删除成功，向责任者发送消息通知发生异常！");
			logger.error(e);
		}
	}

	/**
	 * 发送消息通知
	 * 
	 * @param okrWorkBaseInfo
	 */
	private void notityRecycleMessage(OkrWorkBaseInfo okrWorkBaseInfo) {
		// try {
		// okrNotifyService.notifyCooperaterForWorkRecycled(okrWorkBaseInfo);
		// } catch (Exception e) {
		// logger.error( "工作撤消成功，向协助者发送消息通知发生异常！", e );
		// }
		// try {
		// okrNotifyService.notifyDeployerForWorkRecycleSuccess(okrWorkBaseInfo);
		// } catch (Exception e) {
		// logger.error( "工作撤消成功，向部署者发送消息通知发生异常！", e );
		// }
		// try {
		// okrNotifyService.notifyResponsibilityForWorkRecycled(okrWorkBaseInfo);
		// } catch (Exception e) {
		// logger.error( "工作撤消成功，向责任者发送消息通知发生异常！", e );
		// }
	}

	/**
	 * 根据工作ID，获取指定工作的所有下级工作ID列表
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private List<String> getSubNormalWorkBaseInfoIds(String workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, return null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().getSubNormalWorkBaseInfoIds(workId);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据中心工作ID和指定处理人查询是否存在未确认和未拆分并且状态正常的工作基础信息存在，查询ID列表
	 * 
	 * @param centerId
	 * @param responsibilityEmployeeName
	 * @return
	 * @throws Exception
	 */
	private List<String> listUnConfirmWorkIdsByCenterAndPerson(String centerId, String userIdentity) throws Exception {
		if (centerId == null || centerId.isEmpty()) {
			throw new Exception("centerId is null, system can not query!");
		}
		if (userIdentity == null || userIdentity.isEmpty()) {
			throw new Exception("userNameString is null, system can not query!");
		}
		Business business = null;
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 先查询中心工作ID和处理人查询与其有关的所有工作ID
			ids = business.okrWorkPersonFactory().listByCenterAndPerson(centerId, userIdentity, null, null);
			// 在IDS范围内，查询所有状态正常并且待确认的工作
			ids = business.okrWorkBaseInfoFactory().listUnConfirmWorkIdInIds(ids);
			return ids;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 生成待办信息
	 * 
	 * @param okrWorkBaseInfo
	 */
	public void createTasks(List<String> workIds, String userIdentity) throws Exception {
		String splitFlag = ",";
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<String> taskUserIdentityList = new ArrayList<String>();
		List<String> centerWorkIds = new ArrayList<>();
		String[] targetIdentityArray = null;
		String targetIdentities = null;

		if (workIds != null && !workIds.isEmpty()) {
			okrWorkBaseInfoList = listByIds(workIds);
		}

		// 生成新的待办信息
		if (okrWorkBaseInfoList != null && okrWorkBaseInfoList.size() > 0) {
			for (OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList) {
				if (!centerWorkIds.contains(okrWorkBaseInfo.getCenterId())) {
					centerWorkIds.add(okrWorkBaseInfo.getCenterId());
				}
				// 责任者，需要生成待办，有可能多人
				targetIdentities = okrWorkBaseInfo.getResponsibilityIdentity();
				if (targetIdentities != null && !targetIdentities.isEmpty()) {
					targetIdentityArray = targetIdentities.split(splitFlag);
					if (targetIdentityArray != null && targetIdentityArray.length > 0) {
						for (String identity : targetIdentityArray) {
							if (!taskUserIdentityList.contains(identity)) {
								taskUserIdentityList.add(identity);
							}
						}
					}
				} else {
					throw new Exception("getResponsibilityIdentity is null, can not create tasks!");
				}
			}
		}
		if (centerWorkIds != null && !centerWorkIds.isEmpty()) {
			for (String centerId : centerWorkIds) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrCenterWorkInfo = emc.find(centerId, OkrCenterWorkInfo.class);
				} catch (Exception e) {
					logger.warn("okrCenterWorkInfo{'id':'" + centerId + "'} is not exsits!");
					logger.error(e);
				}
				if (okrCenterWorkInfo != null) {
					// 删除当前处理人的待办信息，并且创建已办信息
					okrTaskService.deleteTask(okrCenterWorkInfo, userIdentity);
					// 为责任者生成工作确认的待办信息
					if (taskUserIdentityList != null && !taskUserIdentityList.isEmpty()) {
						// logger.debug( "责任者:" + taskUserNameList );
						okrTaskService.createTaskProcessors(okrCenterWorkInfo, taskUserIdentityList);
					}
				}
			}
		}
	}

	/**
	 * 根据指定的ID列表查询具体工作信息列表
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	private List<OkrWorkBaseInfo> listByIds(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return null;
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkBaseInfoFactory().list(ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据ID从归档OkrWorkBaseInfo对象 同时归档所有的下级工作以及工作的相关汇报，请示等等 并且删除所有待办
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void archive(String workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, system can not archive any object.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrWorkBaseInfoExcuteArchive.excute(emc, workId);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据ID修改工作进展情况，同时修改最后一次生效的汇报进度
	 * 
	 * @param workId
	 * @param percent
	 * 
	 * @throws Exception
	 */
	public void progressAdjust(String workId, Integer percent) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, system can not adjust progress.");
		}
		if (percent == null) {
			throw new Exception("percent is null, system can not adjust progress.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			okrWorkBaseInfoExcuteProgressAdjust.excute(emc, workId, percent);
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateWorkReportTime(String workId, Date nextReportTime, String reportTimeQue) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, system can not update reportTime for work.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkBaseInfo okrWorkBaseInfo = emc.find(workId, OkrWorkBaseInfo.class);
			if (okrWorkBaseInfo != null) {
				emc.beginTransaction(OkrWorkBaseInfo.class);
				okrWorkBaseInfo.setNextReportTime(nextReportTime);
				okrWorkBaseInfo.setReportTimeQue(reportTimeQue);
				emc.check(okrWorkBaseInfo, CheckPersistType.all);
				emc.commit();
			} else {
				throw new Exception("work is not exists.");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}