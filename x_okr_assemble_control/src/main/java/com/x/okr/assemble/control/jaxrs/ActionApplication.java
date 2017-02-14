package com.x.okr.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.okr.assemble.control.jaxrs.login.OkrLoginAction;
import com.x.okr.assemble.control.jaxrs.login.OkrLogoutAction;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.OkrAttachmentFileInfoAction;
import com.x.okr.assemble.control.jaxrs.okrauthorize.OkrWorkAuthorizeAction;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.OkrCenterWorkInfoAction;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.OkrCenterWorkInfoAdminAction;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.OkrConfigSecretaryAction;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.OkrConfigSystemAction;
import com.x.okr.assemble.control.jaxrs.okrconfigworklevel.OkrConfigWorkLevelAction;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.OkrConfigWorkTypeAction;
import com.x.okr.assemble.control.jaxrs.okrpermissioninfo.OkrPermissionInfoAction;
import com.x.okr.assemble.control.jaxrs.okrpersonpermission.OkrPersonPermissionAction;
import com.x.okr.assemble.control.jaxrs.okrroleinfo.OkrRoleInfoAction;
import com.x.okr.assemble.control.jaxrs.okrrolepermission.OkrRolePermissionAction;
import com.x.okr.assemble.control.jaxrs.okrtask.OkrAnonymousTaskAction;
import com.x.okr.assemble.control.jaxrs.okrtask.OkrTaskAction;
import com.x.okr.assemble.control.jaxrs.okrtask.OkrTaskAdminAction;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.OkrTaskHandledAction;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.OkrWorkAuthorizeRecordAction;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.OkrWorkBaseInfoAction;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.OkrWorkBaseInfoAdminAction;
import com.x.okr.assemble.control.jaxrs.okrworkchat.OkrWorkChatAction;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.OkrWorkDetailInfoAction;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.OkrWorkDynamicsAction;
import com.x.okr.assemble.control.jaxrs.okrworkprobleminfo.OkrWorkProblemInfoAction;
import com.x.okr.assemble.control.jaxrs.okrworkproblempersonlink.OkrWorkProblemPersonLinkAction;
import com.x.okr.assemble.control.jaxrs.okrworkproblemprocesslog.OkrWorkProblemProcessLogAction;
import com.x.okr.assemble.control.jaxrs.okrworkprocesslink.OkrWorkProcessLinkAction;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.OkrWorkReportBaseInfoAction;
import com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.OkrWorkReportDetailInfoAction;
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.OkrWorkReportPersonLinkAction;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.OkrWorkReportProcessLogAction;
import com.x.okr.assemble.control.jaxrs.statistic.OkrStatisticAction;
import com.x.okr.assemble.control.jaxrs.uuid.UUIDAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(OkrWorkAuthorizeAction.class);
		this.classes.add(OkrCenterWorkInfoAction.class);
		this.classes.add(OkrWorkBaseInfoAction.class);
		this.classes.add(OkrAttachmentFileInfoAction.class);
		this.classes.add(OkrConfigSystemAction.class);
		this.classes.add(OkrConfigWorkLevelAction.class);
		this.classes.add(OkrConfigWorkTypeAction.class);
		this.classes.add(OkrConfigSecretaryAction.class);
		this.classes.add(OkrTaskAction.class);
		this.classes.add(OkrAnonymousTaskAction.class);
		this.classes.add(OkrTaskHandledAction.class);
		this.classes.add(OkrWorkAuthorizeRecordAction.class);
		this.classes.add(OkrWorkDetailInfoAction.class);
		this.classes.add(OkrWorkDynamicsAction.class);
		this.classes.add(OkrWorkProblemInfoAction.class);
		this.classes.add(OkrWorkProblemPersonLinkAction.class);
		this.classes.add(OkrWorkProblemProcessLogAction.class);
		this.classes.add(OkrWorkProcessLinkAction.class);
		this.classes.add(OkrWorkReportBaseInfoAction.class);
		this.classes.add(OkrWorkReportDetailInfoAction.class);
		this.classes.add(OkrWorkReportPersonLinkAction.class);
		this.classes.add(OkrWorkReportProcessLogAction.class);
		this.classes.add(OkrLoginAction.class);
		this.classes.add(OkrLogoutAction.class);
		this.classes.add(OkrWorkChatAction.class);

		this.classes.add(OkrPermissionInfoAction.class);
		this.classes.add(OkrPersonPermissionAction.class);
		this.classes.add(OkrRoleInfoAction.class);
		this.classes.add(OkrRolePermissionAction.class);

		this.classes.add(OkrStatisticAction.class);

		this.classes.add(OkrCenterWorkInfoAdminAction.class);
		this.classes.add(OkrTaskAdminAction.class);
		this.classes.add(OkrWorkBaseInfoAdminAction.class);

		this.classes.add(UUIDAction.class);

		return this.classes;
	}

}