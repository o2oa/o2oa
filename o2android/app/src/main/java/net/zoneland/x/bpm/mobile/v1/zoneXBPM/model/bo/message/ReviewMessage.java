package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * review
 *
 * Created by FancyLou on 2016/8/10.
 */
public class ReviewMessage extends BaseMessage {

    private String work;//workId
    private String review;//reviewId

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
