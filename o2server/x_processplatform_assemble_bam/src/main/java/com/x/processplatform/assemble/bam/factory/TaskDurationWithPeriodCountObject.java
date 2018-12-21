package com.x.processplatform.assemble.bam.factory;

import com.x.base.core.project.gson.GsonPropertyObject;

public class TaskDurationWithPeriodCountObject extends GsonPropertyObject {

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
