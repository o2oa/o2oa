package com.x.collaboration.core.message.notification;

public class ReviewMessage extends NotificationMessage {

	private String work;
	private String review;

	public ReviewMessage(String person, String work, String review) {
		super(NotificationType.review, person);
		this.work = work;
		this.review = review;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

}
