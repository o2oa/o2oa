package com.x.processplatform.assemble.bam.jaxrs.state;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;

class ActionRunning extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionRunning.class);

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(ThisApplication.state.getRunning());
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		private WoTask task;

		private WoWork work;

		public WoTask getTask() {
			return task;
		}

		public void setTask(WoTask task) {
			this.task = task;
		}

		public WoWork getWork() {
			return work;
		}

		public void setWork(WoWork work) {
			this.work = work;
		}

	}

	public static class WoTask extends GsonPropertyObject {

		private Long duration = 0L;

		private Integer halfDay = 0;
		private Integer oneDay = 0;
		private Integer twoDay = 0;
		private Integer threeDay = 0;
		private Integer moreDay = 0;

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public Integer getHalfDay() {
			return halfDay;
		}

		public void setHalfDay(Integer halfDay) {
			this.halfDay = halfDay;
		}

		public Integer getOneDay() {
			return oneDay;
		}

		public void setOneDay(Integer oneDay) {
			this.oneDay = oneDay;
		}

		public Integer getTwoDay() {
			return twoDay;
		}

		public void setTwoDay(Integer twoDay) {
			this.twoDay = twoDay;
		}

		public Integer getThreeDay() {
			return threeDay;
		}

		public void setThreeDay(Integer threeDay) {
			this.threeDay = threeDay;
		}

		public Integer getMoreDay() {
			return moreDay;
		}

		public void setMoreDay(Integer moreDay) {
			this.moreDay = moreDay;
		}
	}

	public static class WoWork extends GsonPropertyObject {
		private Long duration = 0L;

		private Integer moreMonth = 0;
		private Integer oneMonth = 0;
		private Integer twoWeek = 0;
		private Integer oneWeek = 0;
		private Integer threeDay = 0;

		public Long getDuration() {
			return duration;
		}

		public void setDuration(Long duration) {
			this.duration = duration;
		}

		public Integer getMoreMonth() {
			return moreMonth;
		}

		public void setMoreMonth(Integer moreMonth) {
			this.moreMonth = moreMonth;
		}

		public Integer getOneMonth() {
			return oneMonth;
		}

		public void setOneMonth(Integer oneMonth) {
			this.oneMonth = oneMonth;
		}

		public Integer getTwoWeek() {
			return twoWeek;
		}

		public void setTwoWeek(Integer twoWeek) {
			this.twoWeek = twoWeek;
		}

		public Integer getOneWeek() {
			return oneWeek;
		}

		public void setOneWeek(Integer oneWeek) {
			this.oneWeek = oneWeek;
		}

		public Integer getThreeDay() {
			return threeDay;
		}

		public void setThreeDay(Integer threeDay) {
			this.threeDay = threeDay;
		}

	}

}
