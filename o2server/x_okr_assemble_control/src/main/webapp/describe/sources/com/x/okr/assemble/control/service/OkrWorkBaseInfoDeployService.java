package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrWorkBaseInfoDeployService {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkBaseInfoDeployService.class);
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrSendNotifyService okrNotifyService = new OkrSendNotifyService();
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 正式部署工作
	 * 
	 * @param id
	 * @param deployerName
	 * @throws Exception
	 */
	public void deploy(List<String> workIds, String deployerIdentity) throws Exception {
		if (deployerIdentity == null || deployerIdentity.isEmpty()) {
			throw new Exception("deployerIdentity is null, can not deploy works。");
		}
		// 需要维护工作干系人和工作审核链
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (workIds != null && !workIds.isEmpty()) {
				for (String id : workIds) {
					logger.info("system deploying work, id:" + id);
					okrWorkBaseInfo = emc.find(id, OkrWorkBaseInfo.class);
					if (okrWorkBaseInfo != null) {
						okrCenterWorkInfo = emc.find(okrWorkBaseInfo.getCenterId(), OkrCenterWorkInfo.class);
						if (okrCenterWorkInfo == null) {
							throw new Exception(
									"okrCenterWorkInfo is not exsits{'id':'" + okrWorkBaseInfo.getCenterId() + "'}.");
						}
						if (deployWork(emc, okrWorkBaseInfo, okrCenterWorkInfo, deployerIdentity)) {
							logger.info("work{'id':'" + id + "' deploy completed. send notify.");
							notifyWorkDeployMessage(okrWorkBaseInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private Boolean deployWork(EntityManagerContainer emc, OkrWorkBaseInfo okrWorkBaseInfo,
			OkrCenterWorkInfo okrCenterWorkInfo, String deployerIdentity) throws Exception {
		OkrWorkBaseInfo parentWorkBaseInfo = null;
		List<String> ids = null;
		List<String> userIdentities = null;
		List<String> status = new ArrayList<String>();
		List<OkrWorkPerson> existsWorkPersonList = null;
		List<OkrWorkPerson> workPersonList = new ArrayList<OkrWorkPerson>();
		List<OkrWorkPerson> centerWorkPersonList = new ArrayList<OkrWorkPerson>();
		List<OkrWorkPerson> centerWorkPersonListSave = new ArrayList<OkrWorkPerson>();
		Integer workProcessLevel = 1;
		String workProcessStatus = "执行中";
		String reportAuditorIdentity = null;
		String topUnitWorkAdmin = null;
		OkrWorkPerson workPerson_tmp = null;
		Business business = new Business(emc);

		// 从系统设置中查询全局工作管理员身份

		reportAuditorIdentity = business.okrConfigSystemFactory().getValueWithConfigCode("REPORT_SUPERVISOR");
		topUnitWorkAdmin = business.okrConfigSystemFactory().getValueWithConfigCode("TOPUNIT_WORK_ADMIN");

		status.add("正常");

		emc.beginTransaction(OkrCenterWorkInfo.class);
		emc.beginTransaction(OkrWorkBaseInfo.class);
		emc.beginTransaction(OkrWorkPerson.class);

		// 根据上级工作的审核层级来确认本工作的审核层级，层级加一
		if (okrWorkBaseInfo.getParentWorkId() != null && !okrWorkBaseInfo.getParentWorkId().isEmpty()) {
			parentWorkBaseInfo = emc.find(okrWorkBaseInfo.getParentWorkId(), OkrWorkBaseInfo.class);
			if (parentWorkBaseInfo != null) {
				workProcessLevel = parentWorkBaseInfo.getWorkAuditLevel() + 1;
				okrWorkBaseInfo.setWorkAuditLevel(workProcessLevel);
				// 上级工作的观察者作为本级工作的观察者
				userIdentities = business.okrWorkPersonFactory().listUserIndentityByWorkId(
						okrWorkBaseInfo.getCenterId(), okrWorkBaseInfo.getParentWorkId(), "观察者", status);
			}
		} else {
			// 无上级工作，中心工作的部署者要作为本级工作的观察者
			// 查询中心工作所有观察者
			userIdentities = business.okrWorkPersonFactory()
					.listUserIdentityForCenterWork(okrWorkBaseInfo.getCenterId(), "部署者", status);
		}

		// 查询该工作的所有干系人列表，供后续组织列表使用
		ids = business.okrWorkPersonFactory().listByWorkId(okrWorkBaseInfo.getId(), null);
		existsWorkPersonList = business.okrWorkPersonFactory().list(ids);

		// 根据工作的情况, 组织所有的工作干系人信息列表(真正需要生效的)
		workPersonList = getWorkPersonByWorkInfo(okrWorkBaseInfo, okrCenterWorkInfo, userIdentities, deployerIdentity,
				workProcessStatus, reportAuditorIdentity, topUnitWorkAdmin);

		// 查询该‘中心工作’所有的干系人信息
		ids = business.okrWorkPersonFactory().listIdsForCenterWorkByCenterId(okrWorkBaseInfo.getCenterId(), null, null,
				null);
		centerWorkPersonList = business.okrWorkPersonFactory().list(ids);
		for (OkrWorkPerson okrWorkPerson : centerWorkPersonList) {
			centerWorkPersonListSave.add(okrWorkPerson);
		}

		// 根据所有的工作干系人, 将干系人都组织为中心工作的观察者, 补充观察者
		centerWorkPersonListSave = getCenterWorkPersonByWorkPersonList(workPersonList, centerWorkPersonListSave);

		for (OkrWorkPerson workPerson : existsWorkPersonList) {
			workPerson.setWorkProcessStatus(workProcessStatus);
			emc.persist(workPerson, CheckPersistType.all);
		}

		// 对 centerWorkPersonList 里的中心工作干系人进行存储
		for (OkrWorkPerson workPerson : centerWorkPersonListSave) {
			ids = business.okrWorkPersonFactory().listByCenterAndPerson(workPerson.getCenterId(),
					workPerson.getEmployeeIdentity(), workPerson.getProcessIdentity(), status);
			if (ids == null || ids.isEmpty()) {
				workPerson.setWorkProcessStatus(workProcessStatus);
				emc.persist(workPerson, CheckPersistType.all);
			} else {
				for (String id : ids) {
					workPerson_tmp = emc.find(id, OkrWorkPerson.class);
					if (workPerson_tmp != null) {
						workPerson_tmp.setWorkProcessStatus(workProcessStatus);
						emc.check(workPerson_tmp, CheckPersistType.all);
					}
				}
			}
		}

		// 判断原来的干系人信息里是否存在需要添加的干系人信息,如果存在,则不用添加,将状态标识改为正常
		for (OkrWorkPerson workPerson : workPersonList) {
			ids = business.okrWorkPersonFactory().listDistinctWorkIdsByWorkAndIdentity(workPerson.getCenterId(),
					workPerson.getWorkId(), workPerson.getEmployeeIdentity(), workPerson.getProcessIdentity(), status);
			if (ids == null || ids.isEmpty()) {
				workPerson.setWorkProcessStatus(workProcessStatus);
				emc.persist(workPerson, CheckPersistType.all);
			} else {
				for (String id : ids) {
					workPerson_tmp = emc.find(id, OkrWorkPerson.class);
					if (workPerson_tmp != null) {
						workPerson_tmp.setWorkProcessStatus(workProcessStatus);
						emc.check(workPerson_tmp, CheckPersistType.all);
					}
				}
			}
		}

		// 部署完成后是待员工执行的工作
		okrWorkBaseInfo.setWorkAuditLevel(workProcessLevel);
		okrWorkBaseInfo.setWorkProcessStatus(workProcessStatus);
		okrWorkBaseInfo.setDeployDateStr(dateOperation.getNowDateTime());
		okrCenterWorkInfo.setProcessStatus(workProcessStatus);
		okrCenterWorkInfo.setDeployDateStr(dateOperation.getNowDateTime());
		emc.check(okrCenterWorkInfo, CheckPersistType.all);
		emc.check(okrWorkBaseInfo, CheckPersistType.all);
		emc.commit();
		return true;

	}

	private List<OkrWorkPerson> getCenterWorkPersonByWorkPersonList(List<OkrWorkPerson> workPersonList,
			List<OkrWorkPerson> centerWorkPersonList) throws Exception {
		if (centerWorkPersonList == null) {
			centerWorkPersonList = new ArrayList<>();
		}
		if (workPersonList == null) {
			workPersonList = new ArrayList<>();
		}
		OkrWorkPerson _workPerson = null;
		for (OkrWorkPerson workPerson : workPersonList) {
			// 根据工作干系人信息组织一个中心工作干系人信息
			try {
				_workPerson = okrWorkPersonService.createCenterWorkPersonByWorkPersonInfo(workPerson, "观察者",
						workPerson.getCreateTime());
				if (!exists(_workPerson, centerWorkPersonList)) {
					centerWorkPersonList.add(_workPerson);
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return centerWorkPersonList;
	}

	/**
	 * 根据工作和中心工作信息来组织所有的工作干系人信息
	 * 
	 * @param okrWorkBaseInfo
	 * @param okrCenterWorkInfo
	 * @param deployerIdentity
	 * @param workProcessStatus
	 * @param reportAuditorIdentity
	 * @param topUnitWorkAdmin
	 * @return
	 * @throws Exception
	 */
	private List<OkrWorkPerson> getWorkPersonByWorkInfo(OkrWorkBaseInfo okrWorkBaseInfo,
			OkrCenterWorkInfo okrCenterWorkInfo, List<String> parentWorkWatcherIdentities, String deployerIdentity,
			String workProcessStatus, String reportAuditorIdentity, String topUnitWorkAdmin) throws Exception {
		List<OkrWorkPerson> workPersonList = new ArrayList<OkrWorkPerson>();
		OkrWorkPerson okrWorkPerson = null;
		String[] employeeIdentities = null;
		String personSplitFlag = ",";
		String identity = null;

		// 创建者 - 根据工作信息组织
		if (okrWorkBaseInfo.getCreatorIdentity() != null && !okrWorkBaseInfo.getCreatorIdentity().isEmpty()) {
			identity = "创建者";
			okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo,
					okrWorkBaseInfo.getCreatorIdentity(), identity);
			if (okrWorkPerson != null) {
				okrWorkPerson.setWorkProcessStatus(workProcessStatus);
				addWorkPersonToList(okrWorkPerson, workPersonList);
			}
			identity = "观察者";
			okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo,
					okrWorkBaseInfo.getCreatorIdentity(), identity);
			if (okrWorkPerson != null) {
				okrWorkPerson.setWorkProcessStatus(workProcessStatus);
				okrWorkPerson.setDiscription("具体工作创建者");
				addWorkPersonToList(okrWorkPerson, workPersonList);
			}
		}

		// 部署者 - 根据当前部署者身份组织
		if (okrWorkBaseInfo.getDeployerName() != null && !okrWorkBaseInfo.getDeployerName().isEmpty()) {
			identity = "部署者";
			okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo,
					okrWorkBaseInfo.getDeployerIdentity(), identity);
			if (okrWorkPerson != null) {
				okrWorkPerson.setWorkProcessStatus(workProcessStatus);
				addWorkPersonToList(okrWorkPerson, workPersonList);
			}
			identity = "观察者";
			okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo,
					okrWorkBaseInfo.getDeployerIdentity(), identity);
			if (okrWorkPerson != null) {
				okrWorkPerson.setWorkProcessStatus(workProcessStatus);
				okrWorkPerson.setDiscription("具体工作部署者");
				addWorkPersonToList(okrWorkPerson, workPersonList);
			}
		}
		// 责任者
		if (okrWorkBaseInfo.getResponsibilityIdentity() != null
				&& !okrWorkBaseInfo.getResponsibilityIdentity().isEmpty()) {
			// 责任者多个值一般使用“,”分隔
			employeeIdentities = okrWorkBaseInfo.getResponsibilityIdentity().split(personSplitFlag);
			if (employeeIdentities != null && employeeIdentities.length > 0) {
				for (String identityName : employeeIdentities) {
					identity = "责任者";
					okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
							identity);
					if (okrWorkPerson != null) {
						okrWorkPerson.setWorkProcessStatus(workProcessStatus);
						addWorkPersonToList(okrWorkPerson, workPersonList);
					}
					identity = "观察者";
					okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
							identity);
					if (okrWorkPerson != null) {
						okrWorkPerson.setWorkProcessStatus(workProcessStatus);
						okrWorkPerson.setDiscription("具体工作责任者");
						addWorkPersonToList(okrWorkPerson, workPersonList);
					}
				}
			}
		}
		// 协助者
		if (ListTools.isNotEmpty( okrWorkBaseInfo.getCooperateIdentityList() )) {
			for (String identityName : okrWorkBaseInfo.getCooperateIdentityList()) {
				identity = "协助者";
				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
						identity);
				if (okrWorkPerson != null) {
					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
					addWorkPersonToList(okrWorkPerson, workPersonList);
				}
				identity = "观察者";
				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
						identity);
				if (okrWorkPerson != null) {
					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
					okrWorkPerson.setDiscription("具体工作协助者");
					addWorkPersonToList(okrWorkPerson, workPersonList);
				}
			}
		}
		// 工作阅知者
		if (ListTools.isNotEmpty(okrWorkBaseInfo.getReadLeaderIdentityList())) {
			for (String identityName : okrWorkBaseInfo.getReadLeaderIdentityList()) {
				identity = "阅知者";
				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
						identity);
				if (okrWorkPerson != null) {
					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
					addWorkPersonToList(okrWorkPerson, workPersonList);
				}
				identity = "观察者";
				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
						identity);
				if (okrWorkPerson != null) {
					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
					okrWorkPerson.setDiscription("具体工作阅知者");
					addWorkPersonToList(okrWorkPerson, workPersonList);
				}
			}
		}

		// 中心工作阅知领导
//		if (ListTools.isNotEmpty(okrCenterWorkInfo.getAuditLeaderIdentityList() )) {
//			for ( String identityName : okrCenterWorkInfo.getAuditLeaderIdentityList() ) {
//				identity = "观察者";
//				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
//						identity);
//				if (okrWorkPerson != null) {
//					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
//					okrWorkPerson.setDiscription("工作所属的中心工作阅知领导");
//					addWorkPersonToList(okrWorkPerson, workPersonList);
//				}
//			}
//		}

		// 中心工作汇报审核领导
		if ( ListTools.isNotEmpty( okrCenterWorkInfo.getReportAuditLeaderIdentityList() )) {
			// 工作汇报审批领导多个值一般使用“,”分隔
			for (String identityName : okrCenterWorkInfo.getReportAuditLeaderIdentityList()) {
				identity = "观察者";
				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName, identity);
				if (okrWorkPerson != null) {
					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
					okrWorkPerson.setDiscription("工作所属的中心工作汇报审核领导");
					addWorkPersonToList(okrWorkPerson, workPersonList);
				}
			}
		}

		// 顶层组织管理员
		if (topUnitWorkAdmin != null && !topUnitWorkAdmin.isEmpty()) {
			// 工作管理员多个值一般使用“,”分隔
			employeeIdentities = topUnitWorkAdmin.split(personSplitFlag);
			if (employeeIdentities != null && employeeIdentities.length > 0) {
				for (String identityName : employeeIdentities) {
					identity = "观察者";
					okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
							identity);
					if (okrWorkPerson != null) {
						okrWorkPerson.setWorkProcessStatus(workProcessStatus);
						okrWorkPerson.setDiscription("顶层组织工作管理员");
						addWorkPersonToList(okrWorkPerson, workPersonList);
					}
				}
			}
		}

		// 顶层组织管理员
		if (reportAuditorIdentity != null && !reportAuditorIdentity.isEmpty()) {
			// 工作管理员多个值一般使用“,”分隔
			employeeIdentities = reportAuditorIdentity.split(personSplitFlag);
			if (employeeIdentities != null && employeeIdentities.length > 0) {
				for (String identityName : employeeIdentities) {
					identity = "观察者";
					okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
							identity);
					if (okrWorkPerson != null) {
						okrWorkPerson.setWorkProcessStatus(workProcessStatus);
						okrWorkPerson.setDiscription("工作汇报督办员");
						addWorkPersonToList(okrWorkPerson, workPersonList);
					}
				}
			}
		}

		// 上级工作的观察者 parentWorkWatcherIdentities
		// 上级工作的观察者作为本级工作的观察者，如果无上级工作，则中心工作的部署者要作为本级工作的观察者
		if (parentWorkWatcherIdentities != null && !parentWorkWatcherIdentities.isEmpty()) {
			for (String identityName : parentWorkWatcherIdentities) {
				identity = "观察者";
				okrWorkPerson = okrWorkPersonService.createWorkPersonByWorkInfo(okrWorkBaseInfo, identityName,
						identity);
				if (okrWorkPerson != null) {
					okrWorkPerson.setWorkProcessStatus(workProcessStatus);
					okrWorkPerson.setDiscription("继承自工作所属的上级工作观察者");
					addWorkPersonToList(okrWorkPerson, workPersonList);
				}
			}
		}
		return workPersonList;
	}

	private void addWorkPersonToList(OkrWorkPerson okrWorkPerson, List<OkrWorkPerson> workPersonList) {
		if (workPersonList == null) {
			workPersonList = new ArrayList<>();
		}
		if (!exists(okrWorkPerson, workPersonList)) {
			workPersonList.add(okrWorkPerson);
		}
	}

	private Boolean exists(OkrWorkPerson okrWorkPerson, List<OkrWorkPerson> workPersonList) {
		for (OkrWorkPerson _okrWorkPerson : workPersonList) {
			if (isSameWorkPersonInfo(okrWorkPerson, _okrWorkPerson)) {
				return true;
			}
		}
		return false;
	}

	private Boolean isSameWorkPersonInfo(OkrWorkPerson okrWorkPerson, OkrWorkPerson _okrWorkPerson) {
		if (_okrWorkPerson.getCenterId().equalsIgnoreCase(okrWorkPerson.getCenterId())
				&& _okrWorkPerson.getEmployeeIdentity().equalsIgnoreCase(okrWorkPerson.getEmployeeIdentity())
				&& _okrWorkPerson.getProcessIdentity().equalsIgnoreCase(okrWorkPerson.getProcessIdentity())) {
			if ((okrWorkPerson.getWorkId() == null || okrWorkPerson.getWorkId().isEmpty())
					&& (_okrWorkPerson.getWorkId() == null || _okrWorkPerson.getWorkId().isEmpty())) {// 如果是中心工作
				return true;
			} else {// 如果是具体工作
				if (okrWorkPerson.getWorkId() != null && _okrWorkPerson.getWorkId() != null
						&& !_okrWorkPerson.getWorkId().isEmpty()
						&& _okrWorkPerson.getWorkId().equalsIgnoreCase(okrWorkPerson.getWorkId())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 工作部署成功，进行工作消息通知
	 * 
	 * @param okrWorkBaseInfo
	 */
	private void notifyWorkDeployMessage(OkrWorkBaseInfo okrWorkBaseInfo) {
		// 工作部署成功，通知部署者
		try {
			okrNotifyService.notifyDeployerForWorkDeploySuccess(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.warn("工作[" + okrWorkBaseInfo.getTitle() + "]部署成功，通知部署者发生异常！");
			logger.error(e);
		}
		// 收到一个新工作，通知责任者
		try {
			okrNotifyService.notifyResponsibilityForGetWork(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.warn("工作[" + okrWorkBaseInfo.getTitle() + "]部署成功，通知责任者发生异常！");
			logger.error(e);
		}
		// 收到一个新工作，通知协助者
		try {
			okrNotifyService.notifyCooperaterForGetWork(okrWorkBaseInfo);
		} catch (Exception e) {
			logger.warn("工作[" + okrWorkBaseInfo.getTitle() + "]部署成功，通知协助者发生异常！");
			logger.error(e);
		}
	}
}