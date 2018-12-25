package com.x.okr.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {
	
	public static class OkrStatisticReportContent {
		/**
		 * 中心工作汇报情况统计信息实体类
		 */
		public static final String table = "OKR_STATISTIC_REPORT_CONTENT";
	}
	
	public static class OkrStatisticReportStatus {
		/**
		 * 所有工作汇报情况状态信息实体类
		 */
		public static final String table = "OKR_STATISTIC_REPORT_STATUS";
	}
	
	public static class OkrCenterWorkInfo {
		/**
		 * 中心工作信息管理表
		 */
		public static final String table = "OKR_CENTERWORKINFO";
	}
	
	public static class OkrWorkBaseInfo {
		/**
		 * 具体工作基础信息管理表
		 */
		public static final String table = "OKR_WORKBASEINFO";
	}
	
	public static class OkrWorkDetailInfo {
		/**
		 * 具体工作详细信息管理表
		 */
		public static final String table = "OKR_WORKDETAILINFO";
	}
	
	public static class OkrWorkPerson {
		/**
		 * 工作干系人关系管理表
		 */
		public static final String table = "OKR_WORK_PERSON";
	}
	
	public static class OkrWorkReportBaseInfo {
		/**
		 * 工作汇报信息关系管理表
		 */
		public static final String table = "OKR_WORK_REPORTBASEINFO";
	}
	
	public static class OkrWorkReportDetailInfo {
		/**
		 * 工作汇报详细信息管理表
		 */
		public static final String table = "OKR_WORK_REPORTDETAILINFO";
	}
	
	public static class OkrWorkReportPersonLink {
		/**
		 * 工作汇报审批链
		 */
		public static final String table = "OKR_WORK_REPORT_PERSONLINK";
	}
	
	public static class OkrWorkProcessPersonLink {
		/**
		 * 工作部署审批链
		 */
		public static final String table = "OKR_WORK_PROCESS_PERSONLINK";
	}
	
	public static class OkrConfigWorkLevel {
		/**
		 * 工作等级配置表
		 */
		public static final String table = "OKR_CONFIG_WORKLEVEL";
	}
	
	public static class OkrConfigWorkType {
		/**
		 * 工作类别配置表
		 */
		public static final String table = "OKR_CONFIG_WORKTYPE";
	}
	
	public static class OkrWorkAuthorizeRecord {
		/**
		 * 工作委托记录信息表
		 */
		public static final String table = "OKR_WORK_AUTHORIZE_RECORD";
	}
	
	public static class OkrWorkReportProcessLog {
		/**
		 * 工作汇报处理记录表
		 */
		public static final String table = "OKR_WORK_REPORT_PROCESSLOG";
	}
	
	public static class OkrWorkDynamics {
		/**
		 * 工作动态信息记录表
		 */
		public static final String table = "OKR_WORKDYNAMICS";
	}
	
	public static class OkrConfigSecretary {
		/**
		 * 领导秘书配置管理表
		 */
		public static final String table = "OKR_CONFIG_SECRETARY";
	}
	
	public static class OkrConfigSystem {
		/**
		 * 系统配置信息表
		 */
		public static final String table = "OKR_CONFIG_SYSTEM";
	}
	
	public static class OkrTask {
		/**
		 * 工作待办信息表
		 */
		public static final String table = "OKR_TASK";
	}
	
	public static class OkrTaskHandled {
		/**
		 * 工作已办信息表
		 */
		public static final String table = "OKR_TASKHANDLED";
	}
	
	public static class OkrRead {
		/**
		 * 工作待阅信息表
		 */
		public static final String table = "OKR_READ";
	}
	
	public static class OkrReadHandled {
		/**
		 * 工作已阅信息表
		 */
		public static final String table = "OKR_READHANDLED";
	}
	
	public static class OkrAttachmentFileInfo {
		/**
		 * 工作附件信息管理表
		 */
		public static final String table = "OKR_ATTACHMENTFILEINFO";
	}
	
	public static class OkrWorkChat {
		/**
		 * 人员权限配置信息表
		 */
		public static final String table = "OKR_WORKCHAT";
	}
	public static class OkrUserInfo {
		/**
		 * 人员信息表
		 */
		public static final String table = "OKR_USERINFO";
	}
	
	public static class OkrErrorSystemIdentityInfo {
		/**
		 * 系统问题人员身份记录表
		 */
		public static final String table = "OKR_ERRROIDENTITY";
	}
	
	public static class OkrErrorIdentityRecords {
		/**
		 * 系统问题人员身份涉及数据记录表
		 */
		public static final String table = "OKR_ERRROIDENTITYRECORDS";
	}

	public static class OkrWorkAppraiseInfo {
		/**
		 * 工作考核信息表
		 */
		public static final String table = "OKR_WORKAPPRAISEINFO";
	}
}