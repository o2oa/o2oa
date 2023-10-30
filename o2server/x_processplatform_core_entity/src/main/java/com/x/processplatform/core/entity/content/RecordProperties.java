package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.element.ActivityType;

public class RecordProperties extends JsonProperties {

	private static final long serialVersionUID = 4021727898101550914L;

	@FieldDescribe("record中记录的处理人身份排序值.")
	private Integer identityOrderNumber = null;

	@FieldDescribe("record中记录的处理人身份所在组织排序值.")
	private Integer unitOrderNumber = null;

	@FieldDescribe("record中记录的处理人身份所在组织层级排序值.")
	private String unitLevelOrderNumber = "";

	@FieldDescribe("record中记录的处理人身份所在组织层级名.")
	private String unitLevelName = "";

	@FieldDescribe("record中记录的处理人身份所拥有的组织职务,向上递归.")
	private List<String> unitDutyList = new ArrayList<>();

	@FieldDescribe("后续人工环节")
	private List<NextManual> nextManualList = new ArrayList<>();

	@FieldDescribe("后续人工环节处理人")
	private List<String> nextManualTaskIdentityList = new ArrayList<>();

	@FieldDescribe("授权给处理人")
	private String empowerToPerson;

	@FieldDescribe("授权给处理人的身份")
	private String empowerToIdentity;

	@FieldDescribe("授权自身份")
	private String empowerFromIdentity;

	@FieldDescribe("授权给处理人的组织")
	private String empowerToUnit;

	@FieldDescribe("路由名称")
	private String routeName;

	@FieldDescribe("意见")
	private String opinion;

	@FieldDescribe("多媒体意见")
	private String mediaOpinion;

	@FieldDescribe("开始时间.")
	private Date startTime;

	@FieldDescribe("耗时")
	private Long elapsed;

	@FieldDescribe("活动组")
	private String fromGroup;

	@FieldDescribe("意见组")
	private String fromOpinionGroup;

	public List<String> getNextManualTaskIdentityList() {
		if (null == this.nextManualTaskIdentityList) {
			this.nextManualTaskIdentityList = new ArrayList<>();
		}
		return this.nextManualTaskIdentityList;
	}
	

	public void setNextManualTaskIdentityList(List<String> nextManualTaskIdentityList) {
		this.nextManualTaskIdentityList = nextManualTaskIdentityList;
	}

	public List<NextManual> getNextManualList() {
		if (null == this.nextManualList) {
			this.nextManualList = new ArrayList<>();
		}
		return this.nextManualList;
	}

	public static class NextManual {

		public NextManual() {
			// nothing
		}

		@FieldDescribe("活动")
		private String activity;
		@FieldDescribe("活动类型")
		private ActivityType activityType;
		@FieldDescribe("活动名称")
		private String activityName;
		@FieldDescribe("活动别名")
		private String activityAlias;
		@FieldDescribe("活动活动令牌")
		private String activityToken;
		@FieldDescribe("待办处理人")
		private List<String> taskIdentityList = new ArrayList<>();

		public List<String> getTaskIdentityList() {
			if (null == this.taskIdentityList) {
				this.taskIdentityList = new ArrayList<>();
			}
			return this.taskIdentityList;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivityAlias() {
			return activityAlias;
		}

		public void setActivityAlias(String activityAlias) {
			this.activityAlias = activityAlias;
		}

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public void setTaskIdentityList(List<String> identityList) {
			this.taskIdentityList = identityList;
		}

	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getMediaOpinion() {
		return mediaOpinion;
	}

	public void setMediaOpinion(String mediaOpinion) {
		this.mediaOpinion = mediaOpinion;
	}

	public void setNextManualList(List<NextManual> nextManualList) {
		this.nextManualList = nextManualList;
	}


	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getElapsed() {
		return elapsed;
	}

	public void setElapsed(Long elapsed) {
		this.elapsed = elapsed;
	}

	public String getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}

	public String getFromOpinionGroup() {
		return fromOpinionGroup;
	}

	public void setFromOpinionGroup(String fromOpinionGroup) {
		this.fromOpinionGroup = fromOpinionGroup;
	}

	public String getEmpowerToIdentity() {
		return empowerToIdentity;
	}

	public void setEmpowerToIdentity(String empowerToIdentity) {
		this.empowerToIdentity = empowerToIdentity;
	}

	public String getEmpowerToPerson() {
		return empowerToPerson;
	}

	public void setEmpowerToPerson(String empowerToPerson) {
		this.empowerToPerson = empowerToPerson;
	}

	public String getEmpowerToUnit() {
		return empowerToUnit;
	}

	public void setEmpowerToUnit(String empowerToUnit) {
		this.empowerToUnit = empowerToUnit;
	}

	public String getEmpowerFromIdentity() {
		return empowerFromIdentity;
	}

	public void setEmpowerFromIdentity(String empowerFromIdentity) {
		this.empowerFromIdentity = empowerFromIdentity;
	}

	public List<String> getUnitDutyList() {
		return unitDutyList;
	}

	public void setUnitDutyList(List<String> unitDutyList) {
		this.unitDutyList = unitDutyList;
	}

	public Integer getIdentityOrderNumber() {
		return identityOrderNumber;
	}

	public void setIdentityOrderNumber(Integer identityOrderNumber) {
		this.identityOrderNumber = identityOrderNumber;
	}

	public Integer getUnitOrderNumber() {
		return unitOrderNumber;
	}

	public void setUnitOrderNumber(Integer unitOrderNumber) {
		this.unitOrderNumber = unitOrderNumber;
	}

	public String getUnitLevelOrderNumber() {
		return unitLevelOrderNumber;
	}

	public void setUnitLevelOrderNumber(String unitLevelOrderNumber) {
		this.unitLevelOrderNumber = unitLevelOrderNumber;
	}

	public String getUnitLevelName() {
		return unitLevelName;
	}

	public void setUnitLevelName(String unitLevelName) {
		this.unitLevelName = unitLevelName;
	}

}
