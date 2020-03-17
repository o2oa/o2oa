package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.project.tools.ListTools;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class OkrSendNotifyService {

	/**
	 * 通知部署者中心工作部署完成
	 * 
	 * @param OkrCenterWorkInfo
	 * @throws Exception
	 */
	public void notifyDeployerForCenterWorkDeploySuccess(OkrCenterWorkInfo okrCenterWorkInfo) throws Exception {
		if (okrCenterWorkInfo != null) {
			String targetName = okrCenterWorkInfo.getDeployerName();
			String messageContent = "中心工作[" + okrCenterWorkInfo.getTitle() + "]已经部署成功。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrCenterWorkDeployAcceptMessage message = new OkrCenterWorkDeployAcceptMessage( name, okrCenterWorkInfo.getId(), okrCenterWorkInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrCenterWorkInfo is null, can not send message!");
		}
	}

	/**
	 * 通知部署者工作部署完成
	 * 
	 * @param OkrCenterWorkInfo
	 * @throws Exception
	 */
	public void notifyDeployerForWorkDeploySuccess(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (okrWorkBaseInfo != null) {
			String targetName = okrWorkBaseInfo.getDeployerName();
			String messageContent = "工作[" + okrWorkBaseInfo.getTitle() + "]已经部署成功。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkDeployAcceptMessage message = new OkrWorkDeployAcceptMessage( name, okrWorkBaseInfo.getId(), okrWorkBaseInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrWorkBaseInfo is null, can not send message!");
		}
	}

	/**
	 * 通知工作主责人收到一个新工作，需要进行确认
	 * 
	 * @param okrWorkBaseInfo
	 * @throws Exception
	 */
	public void notifyResponsibilityForGetWork(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (okrWorkBaseInfo != null) {
			String targetName = okrWorkBaseInfo.getResponsibilityEmployeeName();
			String messageContent = "收到一个新的工作[" + okrWorkBaseInfo.getTitle() + "]，请及时确认工作。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkGetAcceptMessage message = new OkrWorkGetAcceptMessage( name, okrWorkBaseInfo.getId(), okrWorkBaseInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrWorkBaseInfo is null, can not send message!");
		}
	}

	/**
	 * 通知工作协助人收到一个新工作
	 * 
	 * @param okrWorkBaseInfo
	 * @throws Exception
	 */
	public void notifyCooperaterForGetWork(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (okrWorkBaseInfo != null) {
			List<String> targetNames = okrWorkBaseInfo.getCooperateEmployeeNameList();
			String messageContent = "收到一个需要您协助的工作[" + okrWorkBaseInfo.getTitle() + "]，责任者：["
					+ okrWorkBaseInfo.getResponsibilityEmployeeName() + "]，请协助执行。";
			if (ListTools.isNotEmpty(targetNames)) {
				for (String name : targetNames) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkGetAcceptMessage message = new OkrWorkGetAcceptMessage( name, okrWorkBaseInfo.getId(), okrWorkBaseInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrWorkBaseInfo is null, can not send message!");
		}
	}

	/**
	 * 通知工作协助人工作已经被删除
	 * 
	 * @param okrWorkBaseInfo
	 * @throws Exception
	 */
	public void notifyCooperaterForWorkDeleted(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (okrWorkBaseInfo != null) {
			List<String> targetNames = okrWorkBaseInfo.getCooperateEmployeeNameList();
			String messageContent = "您协助的工作[" + okrWorkBaseInfo.getTitle() + "]已被删除。";
			if (ListTools.isNotEmpty(targetNames)) {
				for (String name : targetNames) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkDeletedAcceptMessage message = new OkrWorkDeletedAcceptMessage( name, okrWorkBaseInfo.getId(), okrWorkBaseInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrWorkBaseInfo is null, can not send message!");
		}
	}

	/**
	 * 通知部署者工作已被删除
	 * 
	 * @param okrWorkBaseInfo
	 * @throws Exception
	 */
	public void notifyDeployerForWorkDeletedSuccess(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (okrWorkBaseInfo != null) {
			String targetName = okrWorkBaseInfo.getDeployerName();
			String messageContent = "您部署的工作[" + okrWorkBaseInfo.getTitle() + "]已被成功删除。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkDeletedAcceptMessage message = new OkrWorkDeletedAcceptMessage( name, okrWorkBaseInfo.getId(), okrWorkBaseInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrWorkBaseInfo is null, can not send message!");
		}
	}

	/**
	 * 通知工作主责人工作已经被删除
	 * 
	 * @param okrWorkBaseInfo
	 * @throws Exception
	 */
	public void notifyResponsibilityForWorkDeleted(OkrWorkBaseInfo okrWorkBaseInfo) throws Exception {
		if (okrWorkBaseInfo != null) {
			String targetName = okrWorkBaseInfo.getResponsibilityEmployeeName();
			String messageContent = "您负责的工作[" + okrWorkBaseInfo.getTitle() + "]已被删除。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkDeletedAcceptMessage message = new OkrWorkDeletedAcceptMessage( name, okrWorkBaseInfo.getId(), okrWorkBaseInfo.getTitle(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("okrWorkBaseInfo is null, can not send message!");
		}
	}

	/**
	 * 通知操作人，汇报信息删除成功
	 * 
	 * @param okrWorkReportBaseInfo
	 * @throws Exception
	 */
	public void notifyReportDeleteSuccess(OkrWorkReportBaseInfo okrWorkReportBaseInfo, String operator)
			throws Exception {
		if (okrWorkReportBaseInfo != null) {
			String targetName = operator;
			String messageContent = "工作汇报[" + okrWorkReportBaseInfo.getTitle() + "]已成功删除。";
			if (targetName != null && !targetName.isEmpty()) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if (name != null && !name.trim().isEmpty()) {
//						OkrWorkReportDeletedAcceptMessage message = new OkrWorkReportDeletedAcceptMessage( name, okrWorkReportBaseInfo.getId(), okrWorkReportBaseInfo.getTitle(), messageContent );
//						Collaboration.send( message );
					}
				}
			}
		} else {
			throw new Exception("okrWorkReportBaseInfo is null, can not send message!");
		}
	}
}