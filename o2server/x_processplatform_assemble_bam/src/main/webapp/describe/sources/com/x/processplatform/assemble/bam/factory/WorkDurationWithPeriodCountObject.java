package com.x.processplatform.assemble.bam.factory;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WorkDurationWithPeriodCountObject extends GsonPropertyObject {

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

	public Integer getThreeDay() {
		return threeDay;
	}

	public void setThreeDay(Integer threeDay) {
		this.threeDay = threeDay;
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

	public Integer getMoreMonth() {
		return moreMonth;
	}

	public void setMoreMonth(Integer moreMonth) {
		this.moreMonth = moreMonth;
	}

}
