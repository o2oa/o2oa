package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionOrganization extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionOrganization.class);

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(ThisApplication.state.getOrganization());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("按组织统计")
		private List<WoUnit> unit = new ArrayList<>();

		@FieldDescribe("按个人统计")
		private List<WoPerson> person = new ArrayList<>();

		public List<WoUnit> getUnit() {
			return unit;
		}

		public void setUnit(List<WoUnit> unit) {
			this.unit = unit;
		}

		public List<WoPerson> getPerson() {
			return person;
		}

		public void setPerson(List<WoPerson> person) {
			this.person = person;
		}

	}

	public static class WoUnit extends GsonPropertyObject {

		@FieldDescribe("组织名称")
		String name;
		@FieldDescribe("组织标识")
		String value;
		@FieldDescribe("待办数量")
		Long count;
		@FieldDescribe("超时待办数量")
		Long expiredCount;
		@FieldDescribe("待办停留总时长(分钟)")
		Long duration;
		@FieldDescribe("已办数量")
		Long completedCount;
		@FieldDescribe("超时已办数量")
		Long completedExpiredCount;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public Long getExpiredCount() {
			return expiredCount;
		}

		public void setExpiredCount(Long expiredCount) {
			this.expiredCount = expiredCount;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public Long getCompletedCount() {
			return completedCount;
		}

		public void setCompletedCount(Long completedCount) {
			this.completedCount = completedCount;
		}

		public Long getCompletedExpiredCount() {
			return completedExpiredCount;
		}

		public void setCompletedExpiredCount(Long completedExpiredCount) {
			this.completedExpiredCount = completedExpiredCount;
		}

	}

	public static class WoPerson extends GsonPropertyObject {

		@FieldDescribe("个人名称")
		String name;
		@FieldDescribe("个人标识")
		String value;
		@FieldDescribe("待办数量")
		Long count;
		@FieldDescribe("超时待办数量")
		Long expiredCount;
		@FieldDescribe("待办停留总时长(分钟)")
		Long duration;
		@FieldDescribe("已办数量")
		Long completedCount;
		@FieldDescribe("超时已办数量")
		Long completedExpiredCount;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public Long getExpiredCount() {
			return expiredCount;
		}

		public void setExpiredCount(Long expiredCount) {
			this.expiredCount = expiredCount;
		}

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public Long getCompletedCount() {
			return completedCount;
		}

		public void setCompletedCount(Long completedCount) {
			this.completedCount = completedCount;
		}

		public Long getCompletedExpiredCount() {
			return completedExpiredCount;
		}

		public void setCompletedExpiredCount(Long completedExpiredCount) {
			this.completedExpiredCount = completedExpiredCount;
		}

	}

}
