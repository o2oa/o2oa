package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrWorkAuthorizeService {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkAuthorizeService.class);
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 对工作进行授权操作<br/>
	 * 
	 * 1、查询授权者在此工作中的所有处理身份信息 workPerson<br/>
	 * 2、为承担者添加观察者和处理身份相关信息<br/>
	 * 3、删除授权者在工作所有身份中的除观察者身份之外的所有身份，并且添加授权者身份信息<br/>
	 * 4、添加工作授权记录信息<br/>
	 * 5、判断是否需要删除授权者的工作待办，待办所在中心工作中是否还有其他未授权工作需要处理<br/>
	 * 
	 * @param okrWorkBaseInfo
	 *            工作信息
	 * @param authorizeIdentity
	 *            授权身份
	 * @param undertakerIdentity
	 *            承担者身份
	 * @throws Exception
	 */
	public void authorize(OkrWorkBaseInfo okrWorkBaseInfo, String authorizeIdentity, String undertakerIdentity,
			String delegateOpinion) throws Exception {
		if (okrWorkBaseInfo == null) {
			throw new Exception("okrWorkBaseInfo is null!");
		}
		if (authorizeIdentity == null || authorizeIdentity.isEmpty()) {
			throw new Exception("authorizeIdentity is null!");
		}
		if (undertakerIdentity == null || undertakerIdentity.isEmpty()) {
			throw new Exception("undertakerIdentity is null!");
		}
		if (delegateOpinion == null || delegateOpinion.isEmpty()) {
			throw new Exception("delegateOpinion is null!");
		}
		List<String> statuses = new ArrayList<String>();
		List<String> ids = null;
		List<String> ids_task = null;
		List<String> ids_tmp = null;
		String undertakerName = null;
		String undertakerUnitName = null;
		String undertakerTopUnitName = null;
		String authorizeName = null;
		String authorizeUnitName = null;
		String authorizeTopUnitName = null;
		String authorizeProcessIdentity = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		OkrWorkPerson okrWorkPerson = null;
		OkrWorkPerson okrWorkPerson_new = null;
		OkrTask okrTask = null;
		Integer delegateLevel = 0;
		boolean check = true;
		Business business = null;

		// 查询工作授权承担人是否存在，并且获取承担人姓名，组织以及顶层组织名称
		undertakerName = okrUserManagerService.getPersonNameByIdentity(undertakerIdentity);
		if (undertakerName == null) {
			throw new Exception("person{'identity':'" + undertakerIdentity + "'} not exists.");
		}
		undertakerUnitName = okrUserManagerService.getUnitNameByIdentity(undertakerIdentity);
		undertakerTopUnitName = okrUserManagerService.getTopUnitNameByIdentity(undertakerIdentity);

		statuses.add("正常");
		// 根据工作ID查询授权者的工作干系人信息（需要进行授权人身份变更：责任者 -> 授权者 ）
		ids = okrWorkPersonService.listIdsByWorkAndUserIdentity(okrWorkBaseInfo.getId(), authorizeIdentity, statuses);
		if (ids != null && !ids.isEmpty()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				emc.beginTransaction(OkrWorkAuthorizeRecord.class);
				emc.beginTransaction(OkrWorkPerson.class);
				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrTask.class);
				emc.beginTransaction(OkrTaskHandled.class);

				if (check) {
					try {
						delegateLevel = business.okrWorkAuthorizeRecordFactory()
								.getMaxDelegateLevel(okrWorkBaseInfo.getId());

						okrWorkAuthorizeRecord = new OkrWorkAuthorizeRecord();

						for (String id : ids) {

							// 遍历授权人在此工作中所有的工作干系人处理身份
							okrWorkPerson = emc.find(id, OkrWorkPerson.class);

							// 判断okrWorkPerson是否为空
							if (okrWorkPerson == null) {
								continue;
							}

							// 只需要变更责任者身份 为 授权者身份
							if (!"责任者".equals(okrWorkPerson.getProcessIdentity())) {
								continue;
							}
							authorizeName = okrWorkPerson.getEmployeeName();
							authorizeUnitName = okrWorkPerson.getUnitName();
							authorizeTopUnitName = okrWorkPerson.getTopUnitName();
							authorizeProcessIdentity = okrWorkPerson.getProcessIdentity();

							// 查询授权者在该工作的干系人信息中的授权者身份信息是否已经存在，如果不存在则将当前的身份信息修改为授权信息
							// 如果存在则修改授权记录ID为当前的授权记录ID
							ids_tmp = business.okrWorkPersonFactory().listByWorkAndIdentity(null,
									okrWorkBaseInfo.getId(), authorizeIdentity, "授权者", statuses);
							if (ids_tmp == null || ids_tmp.isEmpty()) {
								okrWorkPerson.setProcessIdentity("授权者");
								okrWorkPerson.setAuthorizeRecordId(okrWorkAuthorizeRecord.getId());
								emc.check(okrWorkPerson, CheckPersistType.all);
							} else {
								logger.warn("授权者已经存在工作处理身份:" + authorizeProcessIdentity);
							}

							// 为承担者添加相应的身份信息，先查询该员工在该工作下相应的责任者身份是否已经存在，如果存在，则不需要再添加了
							ids_tmp = business.okrWorkPersonFactory().listByWorkAndIdentity(null,
									okrWorkBaseInfo.getId(), undertakerIdentity, authorizeProcessIdentity, statuses);
							if (ids_tmp == null || ids_tmp.isEmpty()) {
								okrWorkPerson_new = new OkrWorkPerson();
								okrWorkPerson_new.setAuthorizeRecordId(okrWorkAuthorizeRecord.getId());
								okrWorkPerson_new.setCenterId(okrWorkPerson.getCenterId());
								okrWorkPerson_new.setCenterTitle(okrWorkPerson.getCenterTitle());
								okrWorkPerson_new.setWorkId(okrWorkPerson.getWorkId());
								okrWorkPerson_new.setParentWorkId(okrWorkPerson.getParentWorkId());
								okrWorkPerson_new.setWorkTitle(okrWorkPerson.getWorkTitle());
								okrWorkPerson_new.setWorkType(okrWorkPerson.getWorkType());
								okrWorkPerson_new.setWorkDateTimeType(okrWorkPerson.getWorkDateTimeType());
								okrWorkPerson_new.setWorkLevel(okrWorkPerson.getWorkLevel());
								okrWorkPerson_new.setWorkProcessStatus(okrWorkPerson.getWorkProcessStatus());
								okrWorkPerson_new.setEmployeeName(undertakerName);
								okrWorkPerson_new.setEmployeeIdentity(undertakerIdentity);
								okrWorkPerson_new.setUnitName(undertakerUnitName);
								okrWorkPerson_new.setTopUnitName(undertakerTopUnitName);
								okrWorkPerson_new.setDeployMonth(okrWorkPerson.getDeployMonth());
								okrWorkPerson_new.setDeployYear(okrWorkPerson.getDeployYear());
								okrWorkPerson_new.setIsCompleted(okrWorkPerson.getIsCompleted());
								okrWorkPerson_new.setIsOverTime(okrWorkPerson.getIsOverTime());
								okrWorkPerson_new.setProcessIdentity(authorizeProcessIdentity);
								okrWorkPerson_new.setIsDelegateTarget(true);
								emc.persist(okrWorkPerson_new, CheckPersistType.all);
							}

							ids_tmp = business.okrWorkPersonFactory().listByWorkAndIdentity(null,
									okrWorkBaseInfo.getId(), undertakerIdentity, "观察者", statuses);
							if (ids_tmp == null || ids_tmp.isEmpty()) {
								okrWorkPerson_new = new OkrWorkPerson();
								okrWorkPerson_new.setAuthorizeRecordId(okrWorkAuthorizeRecord.getId());
								okrWorkPerson_new.setCenterId(okrWorkPerson.getCenterId());
								okrWorkPerson_new.setCenterTitle(okrWorkPerson.getCenterTitle());
								okrWorkPerson_new.setWorkId(okrWorkPerson.getWorkId());
								okrWorkPerson_new.setParentWorkId(okrWorkPerson.getParentWorkId());
								okrWorkPerson_new.setWorkTitle(okrWorkPerson.getWorkTitle());
								okrWorkPerson_new.setWorkType(okrWorkPerson.getWorkType());
								okrWorkPerson_new.setWorkDateTimeType(okrWorkPerson.getWorkDateTimeType());
								okrWorkPerson_new.setWorkLevel(okrWorkPerson.getWorkLevel());
								okrWorkPerson_new.setWorkProcessStatus(okrWorkPerson.getWorkProcessStatus());
								okrWorkPerson_new.setEmployeeName(undertakerName);
								okrWorkPerson_new.setEmployeeIdentity(undertakerIdentity);
								okrWorkPerson_new.setUnitName(undertakerUnitName);
								okrWorkPerson_new.setTopUnitName(undertakerTopUnitName);
								okrWorkPerson_new.setDeployMonth(okrWorkPerson.getDeployMonth());
								okrWorkPerson_new.setDeployYear(okrWorkPerson.getDeployYear());
								okrWorkPerson_new.setIsCompleted(okrWorkPerson.getIsCompleted());
								okrWorkPerson_new.setIsOverTime(okrWorkPerson.getIsOverTime());
								okrWorkPerson_new.setProcessIdentity("观察者");
								okrWorkPerson_new.setIsDelegateTarget(true);
								okrWorkPerson_new.setRecordType("具体工作");
								okrWorkPerson_new.setDiscription(authorizeIdentity + "进行了工作授权");
								emc.persist(okrWorkPerson_new, CheckPersistType.all);
							}

							ids_tmp = business.okrWorkPersonFactory().listIdsForCenterWorkByCenterId(
									okrWorkBaseInfo.getCenterId(), undertakerIdentity, "观察者", statuses);
							if (ids_tmp == null || ids_tmp.isEmpty()) {
								okrWorkPerson_new = new OkrWorkPerson();
								okrWorkPerson_new.setAuthorizeRecordId(okrWorkAuthorizeRecord.getId());
								okrWorkPerson_new.setCenterId(okrWorkPerson.getCenterId());
								okrWorkPerson_new.setCenterTitle(okrWorkPerson.getCenterTitle());
								okrWorkPerson_new.setWorkId(null);
								okrWorkPerson_new.setParentWorkId(null);
								okrWorkPerson_new.setWorkTitle(null);
								okrWorkPerson_new.setWorkType(okrWorkPerson.getWorkType());
								okrWorkPerson_new.setWorkDateTimeType(okrWorkPerson.getWorkDateTimeType());
								okrWorkPerson_new.setWorkLevel(null);
								okrWorkPerson_new.setWorkProcessStatus(okrWorkPerson.getWorkProcessStatus());
								okrWorkPerson_new.setEmployeeName(undertakerName);
								okrWorkPerson_new.setEmployeeIdentity(undertakerIdentity);
								okrWorkPerson_new.setUnitName(undertakerUnitName);
								okrWorkPerson_new.setTopUnitName(undertakerTopUnitName);
								okrWorkPerson_new.setDeployMonth(okrWorkPerson.getDeployMonth());
								okrWorkPerson_new.setDeployYear(okrWorkPerson.getDeployYear());
								okrWorkPerson_new.setIsCompleted(okrWorkPerson.getIsCompleted());
								okrWorkPerson_new.setIsOverTime(okrWorkPerson.getIsOverTime());
								okrWorkPerson_new.setProcessIdentity("观察者");
								okrWorkPerson_new.setIsDelegateTarget(true);
								okrWorkPerson_new.setRecordType("中心工作");
								okrWorkPerson_new.setDiscription(authorizeIdentity + "对中心工作中所负责的工作进行了工作授权");
								emc.persist(okrWorkPerson_new, CheckPersistType.all);
							}
						}
						okrWorkAuthorizeRecord.setCenterId(okrWorkPerson.getCenterId());
						okrWorkAuthorizeRecord.setCenterTitle(okrWorkPerson.getCenterTitle());
						okrWorkAuthorizeRecord.setTitle(okrWorkBaseInfo.getTitle());
						okrWorkAuthorizeRecord.setWorkId(okrWorkBaseInfo.getId());
						okrWorkAuthorizeRecord.setDelegateDateTime(new Date());
						okrWorkAuthorizeRecord.setDelegateDateTimeStr(
								dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						okrWorkAuthorizeRecord.setDelegateOpinion(delegateOpinion);
						okrWorkAuthorizeRecord.setDelegatorTopUnitName(authorizeTopUnitName);
						okrWorkAuthorizeRecord.setDelegatorIdentity(authorizeIdentity);
						okrWorkAuthorizeRecord.setDelegatorName(authorizeName);
						okrWorkAuthorizeRecord.setDelegatorUnitName(authorizeUnitName);
						okrWorkAuthorizeRecord.setTargetTopUnitName(undertakerTopUnitName);
						okrWorkAuthorizeRecord.setTargetIdentity(undertakerIdentity);
						okrWorkAuthorizeRecord.setTargetName(undertakerName);
						okrWorkAuthorizeRecord.setTargetUnitName(undertakerUnitName);
						okrWorkAuthorizeRecord.setDelegateLevel(++delegateLevel);
						emc.persist(okrWorkAuthorizeRecord, CheckPersistType.all);
						emc.commit();
					} catch (Exception e) {
						check = false;
						logger.warn("system authorize work got an exception.");
						logger.error(e);
					}
				}

				if (check) {// 重新组织工作的责任者数据
					emc.beginTransaction(OkrWorkBaseInfo.class);
					okrWorkBaseInfo = emc.find(okrWorkBaseInfo.getId(), OkrWorkBaseInfo.class);
					composeResponsibilityWorkPersonInfo(okrWorkBaseInfo, null, okrWorkBaseInfo.getId(),
							undertakerIdentity);
					emc.check(okrWorkBaseInfo, CheckPersistType.all);
					emc.commit();
				}

				if (check) {
					emc.beginTransaction(OkrWorkAuthorizeRecord.class);
					emc.beginTransaction(OkrWorkPerson.class);
					emc.beginTransaction(OkrWorkBaseInfo.class);
					emc.beginTransaction(OkrTask.class);
					emc.beginTransaction(OkrTaskHandled.class);
					// 处理待办信息
					// 1、判断承担者是否已经存在该中心工作的待办信息，如果不存在，则需要推送待办信息
					ids_tmp = business.okrTaskFactory().listIdsByCenterAndPerson(okrWorkPerson.getCenterId(),
							undertakerIdentity, "中心工作");
					if (ids_tmp == null || ids_tmp.isEmpty()) {// 添加待办信息
						okrTask = new OkrTask();
						okrTask.setTitle(okrWorkPerson.getCenterTitle());
						okrTask.setCenterId(okrWorkPerson.getCenterId());
						okrTask.setCenterTitle(okrWorkPerson.getCenterTitle());
						okrTask.setWorkType(okrWorkPerson.getWorkType());
						okrTask.setTargetIdentity(undertakerIdentity);
						okrTask.setTargetName(undertakerName);
						okrTask.setTargetUnitName(undertakerUnitName);
						okrTask.setTargetTopUnitName(undertakerTopUnitName);
						okrTask.setActivityName("工作确认");
						okrTask.setArriveDateTime(new Date());
						okrTask.setArriveDateTimeStr(
								dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
						okrTask.setDynamicObjectId(okrWorkPerson.getCenterId());
						okrTask.setDynamicObjectTitle(okrWorkPerson.getCenterTitle());
						okrTask.setDynamicObjectType("中心工作");
						okrTask.setProcessType("TASK");
						okrTask.setStatus("正常");
						okrTask.setViewUrl("");
						emc.persist(okrTask, CheckPersistType.all);
					}

					// 先看看该授权者是否仍存在该中心工作的待办，本来就没有待办信息，就不管了
					ids_task = business.okrTaskFactory().listIdsByTargetActivityAndObjId("TASK", "中心工作",
							okrWorkBaseInfo.getCenterId(), null, authorizeIdentity);
					if (ids_task != null && !ids_task.isEmpty()) {
						// for( String _id : ids_task ){
						// okrTask = emc.find( _id, OkrTask.class );
						// if ( okrTask != null ) {
						// okrTaskHandled = new OkrTaskHandled();
						// okrTaskHandled.setActivityName(okrTask.getActivityName());
						// okrTaskHandled.setArriveDateTime(okrTask.getArriveDateTime());
						// okrTaskHandled.setArriveDateTimeStr(okrTask.getArriveDateTimeStr());
						// okrTaskHandled.setCenterId(okrTask.getCenterId());
						// okrTaskHandled.setCenterTitle(okrTask.getCenterTitle());
						// okrTaskHandled.setDynamicObjectId(okrTask.getDynamicObjectId());
						// okrTaskHandled.setDynamicObjectTitle(okrTask.getDynamicObjectTitle());
						// okrTaskHandled.setDynamicObjectType(okrTask.getDynamicObjectType());
						// okrTaskHandled.setProcessDateTime(new Date());
						// okrTaskHandled.setProcessDateTimeStr(dateOperation.getNowDateTime());
						// okrTaskHandled.setTargetTopUnitName(okrTask.getTargetTopUnitName());
						// okrTaskHandled.setTargetIdentity(okrTask.getTargetIdentity());
						// okrTaskHandled.setTargetName(okrTask.getTargetName());
						// okrTaskHandled.setTargetUnitName(okrTask.getTargetUnitName());
						// okrTaskHandled.setTitle(okrTask.getTitle());
						// okrTaskHandled.setWorkType( okrTask.getWorkType() );
						// okrTaskHandled.setViewUrl("");
						// okrTaskHandled.setWorkId(okrTask.getWorkId());
						// okrTaskHandled.setWorkTitle(okrTask.getWorkTitle());
						// emc.persist( okrTaskHandled, CheckPersistType.all );
						// break;
						// }
						// }
						// 判断该中心工作下是否仍有授权者需要部署和拆解的工作， workPerson表，有责任者是授权者记录
						ids_tmp = null;
						ids_tmp = business.okrWorkPersonFactory().listWorkByCenterAndIdentity(
								okrWorkBaseInfo.getCenterId(), authorizeIdentity, "责任者", statuses);
						if (ids_tmp == null || ids_tmp.isEmpty()) {// 已经没有需要部署的工作了，需要删除待办并且生成一条已办
							okrTask = emc.find(ids_task.get(0), OkrTask.class);
							for (String _id : ids_task) { // 删除所有的待办信息
								okrTask = emc.find(_id, OkrTask.class);
								if (okrTask != null) {
									emc.remove(okrTask, CheckRemoveType.all);
								}
							}
						}
					}
					emc.commit();
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 授权收回服务
	 * 
	 * @param okrWorkBaseInfo
	 * @param authorizeIdentity
	 * @throws Exception
	 */
	public void tackback(OkrWorkBaseInfo okrWorkBaseInfo, String authorizeIdentity) throws Exception {

		if (okrWorkBaseInfo == null) {
			throw new Exception("okrWorkBaseInfo is null!");
		}
		if (authorizeIdentity == null || authorizeIdentity.isEmpty()) {
			throw new Exception("authorizeIdentity is null!");
		}

		Date now = new Date();
		List<String> statuses = new ArrayList<String>();
		List<String> ids = null;
		List<String> subWorkIds = null;
		List<String> ids_task = null;
		List<String> ids_workPerson = null;
		OkrWorkBaseInfo okrWorkBaseInfo_sub = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord_tmp = null;
		OkrWorkPerson okrWorkPerson = null;
		OkrWorkPerson okrWorkPerson_sub = null;
		OkrTask okrTask = null;
		List<String> responsibilityIdentities = new ArrayList<>();
		Integer delegateLevel = 0;
		boolean check = true;
		Business business = null;

		statuses.add("正常");

		okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getFirstAuthorizeRecord(okrWorkBaseInfo.getId(),
				authorizeIdentity);
		if (okrWorkAuthorizeRecord == null) {
			check = false;
			logger.warn("okrWorkAuthorizeRecord{'workId':'" + okrWorkBaseInfo.getId() + "','delegateIdentity':'"
					+ authorizeIdentity + "'} not exists。");
			throw new Exception("授权信息不存在，无法进行授权收回操作。");
		}

		if (check) {
			delegateLevel = okrWorkAuthorizeRecord.getDelegateLevel();
			try {
				ids = okrWorkAuthorizeRecordService.listByAuthorizor(okrWorkBaseInfo.getId(), null, delegateLevel);
			} catch (Exception e) {
				check = false;
				throw e;
			}
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction(OkrWorkAuthorizeRecord.class);
			emc.beginTransaction(OkrWorkPerson.class);
			emc.beginTransaction(OkrWorkBaseInfo.class);
			emc.beginTransaction(OkrTask.class);
			emc.beginTransaction(OkrTaskHandled.class);
			if (check) {
				if (ids != null && !ids.isEmpty()) {
					for (String id : ids) {
						okrWorkAuthorizeRecord_tmp = emc.find(id, OkrWorkAuthorizeRecord.class);
						if (okrWorkAuthorizeRecord_tmp != null) {
							responsibilityIdentities.add(okrWorkAuthorizeRecord_tmp.getTargetIdentity());
							// 如果是最小的那一次,就是最早的一次授权, 则改为已收回，其他因为该授权产生的下级授权修改为已生效
							if (okrWorkAuthorizeRecord_tmp.getId().equalsIgnoreCase(okrWorkAuthorizeRecord.getId())) {
								okrWorkAuthorizeRecord_tmp.setTakebackDateTime(now);
								okrWorkAuthorizeRecord_tmp.setStatus("已收回");
							} else {
								if ("正常".equals(okrWorkAuthorizeRecord_tmp.getStatus())) {
									okrWorkAuthorizeRecord_tmp.setTakebackDateTime(now);
									okrWorkAuthorizeRecord_tmp.setStatus("已失效");
								}
							}
							emc.check(okrWorkAuthorizeRecord_tmp, CheckPersistType.all);
						}
					}
				}
			}
			if (check) {
				if (ids != null && !ids.isEmpty()) {
					ids_workPerson = okrWorkPersonService.listByAuthorizeRecordIds(ids, statuses);
					if (ids_workPerson != null && !ids_workPerson.isEmpty()) {
						for (String id : ids_workPerson) {
							okrWorkPerson = emc.find(id, OkrWorkPerson.class);
							// 删除所有因为授权引起的干系人信息中的所有责任者身份, 保留观察者身份
							if ("责任者".equals(okrWorkPerson.getProcessIdentity())) {
								emc.remove(okrWorkPerson, CheckRemoveType.all);
							}
							if ("授权者".equals(okrWorkPerson.getProcessIdentity())) {
								emc.remove(okrWorkPerson, CheckRemoveType.all);
							}
						}
					}
				}
			}
			if (check) {
				okrWorkPerson = new OkrWorkPerson();
				okrWorkPerson.setAuthorizeRecordId(okrWorkAuthorizeRecord.getId());
				okrWorkPerson.setCenterId(okrWorkBaseInfo.getCenterId());
				okrWorkPerson.setCenterTitle(okrWorkBaseInfo.getCenterTitle());
				okrWorkPerson.setWorkId(okrWorkBaseInfo.getId());
				okrWorkPerson.setParentWorkId(okrWorkBaseInfo.getParentWorkId());
				okrWorkPerson.setWorkTitle(okrWorkBaseInfo.getTitle());
				okrWorkPerson.setWorkType(okrWorkBaseInfo.getWorkType());
				okrWorkPerson.setWorkDateTimeType(okrWorkBaseInfo.getWorkDateTimeType());
				okrWorkPerson.setWorkLevel(okrWorkBaseInfo.getWorkLevel());
				okrWorkPerson.setWorkProcessStatus(okrWorkBaseInfo.getWorkProcessStatus());
				okrWorkPerson.setEmployeeName(okrWorkAuthorizeRecord.getDelegatorName());
				okrWorkPerson.setEmployeeIdentity(okrWorkAuthorizeRecord.getDelegatorIdentity());
				okrWorkPerson.setUnitName(okrWorkAuthorizeRecord.getDelegatorUnitName());
				okrWorkPerson.setTopUnitName(okrWorkAuthorizeRecord.getDelegatorTopUnitName());
				okrWorkPerson.setDeployMonth(okrWorkBaseInfo.getDeployMonth());
				okrWorkPerson.setDeployYear(okrWorkBaseInfo.getDeployYear());

				okrWorkPerson.setDeployDateStr(okrWorkBaseInfo.getDeployDateStr());
				if (okrWorkBaseInfo.getCreateTime() != null) {
					okrWorkPerson.setWorkCreateDateStr(dateOperation
							.getDateStringFromDate(okrWorkBaseInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					okrWorkPerson.setWorkCreateDateStr(
							dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
				}
				okrWorkPerson.setCompleteDateLimitStr(okrWorkBaseInfo.getCompleteDateLimitStr());
				okrWorkPerson.setCompleteDateLimit(okrWorkBaseInfo.getCompleteDateLimit());
				okrWorkPerson.setRecordType("具体工作");
				okrWorkPerson.setIsCompleted(okrWorkBaseInfo.getIsCompleted());
				okrWorkPerson.setIsOverTime(okrWorkBaseInfo.getIsOverTime());
				okrWorkPerson.setProcessIdentity("责任者");
				okrWorkPerson.setDiscription("对工作授权收回");
				emc.persist(okrWorkPerson, CheckPersistType.all);
			}
			if (check) {
				// 将该工作所有的下级工作的部署者设置为当前授权收回者身份
				subWorkIds = business.okrWorkBaseInfoFactory().listByParentId(okrWorkBaseInfo.getId());
				if (subWorkIds != null && !subWorkIds.isEmpty()) {
					for (String id : subWorkIds) {
						// 修改工作干系人中的部署者身份
						ids_workPerson = business.okrWorkPersonFactory()
								.listByWorkIdAndProcessIdentity(okrWorkBaseInfo.getId(), "部署者", statuses);
						if (ids_workPerson != null && !ids_workPerson.isEmpty()) {
							for (String workPersonId : ids_workPerson) {
								okrWorkPerson_sub = emc.find(workPersonId, OkrWorkPerson.class);
								if (okrWorkPerson_sub != null) {
									okrWorkPerson_sub.setEmployeeName(okrWorkAuthorizeRecord.getDelegatorName());
									okrWorkPerson_sub
											.setEmployeeIdentity(okrWorkAuthorizeRecord.getDelegatorIdentity());
									okrWorkPerson_sub.setUnitName(okrWorkAuthorizeRecord.getDelegatorUnitName());
									okrWorkPerson_sub.setTopUnitName(okrWorkAuthorizeRecord.getDelegatorTopUnitName());
									emc.check(okrWorkPerson, CheckPersistType.all);
								}
							}
						}
						// 修改工作的部署者为当前授权收回者
						okrWorkBaseInfo_sub = emc.find(id, OkrWorkBaseInfo.class);
						if (okrWorkBaseInfo_sub != null) {
							okrWorkBaseInfo_sub
									.setDeployerTopUnitName(okrWorkAuthorizeRecord.getDelegatorTopUnitName());
							okrWorkBaseInfo_sub.setDeployerUnitName(okrWorkAuthorizeRecord.getDelegatorUnitName());
							okrWorkBaseInfo_sub.setDeployerIdentity(okrWorkAuthorizeRecord.getDelegatorIdentity());
							okrWorkBaseInfo_sub.setDeployerName(okrWorkAuthorizeRecord.getDelegatorName());
							emc.check(okrWorkBaseInfo_sub, CheckPersistType.all);
						}
					}
				}
			}
			// 先提交一次
			if (check) {
				emc.commit();
			}

			// 重新组织工作的干系人数据,责任者，更新工作基础信息数据
			if (check) {
				emc.beginTransaction(OkrWorkBaseInfo.class);
				okrWorkBaseInfo = emc.find(okrWorkBaseInfo.getId(), OkrWorkBaseInfo.class);
				composeResponsibilityWorkPersonInfo(okrWorkBaseInfo, null, okrWorkBaseInfo.getId(),
						okrWorkAuthorizeRecord.getDelegatorIdentity());
				emc.check(okrWorkBaseInfo, CheckPersistType.all);
				emc.commit();
			}

			// 处理待办信息
			if (check) {
				emc.beginTransaction(OkrTask.class);
				// 为新的责任者新建待办，如果已经存在待办信息，则不需要添加
				ids_task = business.okrTaskFactory().listIdsByCenterAndPerson(okrWorkPerson.getCenterId(),
						okrWorkAuthorizeRecord.getDelegatorIdentity(), "中心工作");
				if (ids_task == null || ids_task.isEmpty()) {
					// 添加待办信息
					okrTask = new OkrTask();
					okrTask.setTitle(okrWorkPerson.getCenterTitle());
					okrTask.setCenterId(okrWorkPerson.getCenterId());
					okrTask.setCenterTitle(okrWorkPerson.getCenterTitle());
					okrTask.setWorkType(okrWorkPerson.getWorkType());
					okrTask.setTargetIdentity(okrWorkAuthorizeRecord.getDelegatorIdentity());
					okrTask.setTargetName(okrWorkAuthorizeRecord.getDelegatorName());
					okrTask.setTargetUnitName(okrWorkAuthorizeRecord.getDelegatorUnitName());
					okrTask.setTargetTopUnitName(okrWorkAuthorizeRecord.getDelegatorTopUnitName());
					okrTask.setActivityName("工作确认");
					okrTask.setArriveDateTime(new Date());
					okrTask.setArriveDateTimeStr(
							dateOperation.getDateStringFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
					okrTask.setDynamicObjectId(okrWorkPerson.getCenterId());
					okrTask.setDynamicObjectTitle(okrWorkPerson.getCenterTitle());
					okrTask.setDynamicObjectType("中心工作");
					okrTask.setProcessType("TASK");
					okrTask.setStatus("正常");
					okrTask.setViewUrl("");
					emc.persist(okrTask, CheckPersistType.all);
				}

				// 删除原责任者的待办，如果没有待办就不需要处理了
				for (String responsibilityIdentity : responsibilityIdentities) {
					ids_task = business.okrTaskFactory().listIdsByTargetActivityAndObjId("TASK", "中心工作",
							okrWorkBaseInfo.getCenterId(), null, responsibilityIdentity);
					if (ids_task != null && !ids_task.isEmpty()) {
						// 判断该中心工作下是否仍有授权者需要部署和拆解的工作，
						// workPerson表，有责任者是旧授权者oldResponsibilityIdentity的有效记录
						ids_workPerson = business.okrWorkPersonFactory().listWorkByCenterAndIdentity(
								okrWorkBaseInfo.getCenterId(), responsibilityIdentity, "责任者", statuses);
						if (ids_workPerson == null || ids_workPerson.isEmpty()) {// 已经没有需要部署的工作了
							for (String _id : ids_task) {
								okrTask = emc.find(_id, OkrTask.class);
								if (okrTask != null) {
									emc.remove(okrTask, CheckRemoveType.all);
								}
							}
						}
					}
				}
				emc.commit();
			}
		} catch (Exception e) {
			check = false;
			throw e;
		}
	}

	private OkrWorkBaseInfo composeResponsibilityWorkPersonInfo(OkrWorkBaseInfo okrWorkBaseInfo, String centerId,
			String workId, String employeeIdentity) throws Exception {
		OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
		String personNames = "";
		String personIdentities = "";
		String personUnitNames = "";
		String personCompanies = "";
		List<String> statuses = new ArrayList<String>();
		List<String> ids_tmp = null;
		List<OkrWorkPerson> okrWorkPersons = null;

		statuses.add("正常");
		ids_tmp = okrWorkPersonService.listByWorkAndIdentity(centerId, workId, employeeIdentity, "责任者", statuses);
		if (ids_tmp != null && !ids_tmp.isEmpty()) {
			okrWorkPersons = okrWorkPersonService.list(ids_tmp);
		}
		if (okrWorkPersons != null && !okrWorkPersons.isEmpty()) {
			for (OkrWorkPerson okrWorkPerson_tmp : okrWorkPersons) {
				if (personNames == null || personNames.trim().isEmpty()) {
					personNames = personNames + okrWorkPerson_tmp.getEmployeeName();
				} else {
					personNames = "," + personNames + okrWorkPerson_tmp.getEmployeeName();
				}

				if (personIdentities == null || personIdentities.trim().isEmpty()) {
					personIdentities = personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				} else {
					personIdentities = "," + personIdentities + okrWorkPerson_tmp.getEmployeeIdentity();
				}

				if (personUnitNames == null || personUnitNames.trim().isEmpty()) {
					personUnitNames = personUnitNames + okrWorkPerson_tmp.getUnitName();
				} else {
					personUnitNames = "," + personUnitNames + okrWorkPerson_tmp.getUnitName();
				}

				if (personCompanies == null || personCompanies.trim().isEmpty()) {
					personCompanies = personCompanies + okrWorkPerson_tmp.getTopUnitName();
				} else {
					personCompanies = "," + personCompanies + okrWorkPerson_tmp.getTopUnitName();
				}
			}
			okrWorkBaseInfo.setResponsibilityTopUnitName(personCompanies);
			okrWorkBaseInfo.setResponsibilityUnitName(personUnitNames);
			okrWorkBaseInfo.setResponsibilityIdentity(personIdentities);
			okrWorkBaseInfo.setResponsibilityEmployeeName(personNames);
		}
		return okrWorkBaseInfo;
	}
}