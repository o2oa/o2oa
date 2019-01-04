package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by FancyLou on 2016/3/29.
 */
public abstract class MeetingScrollListener extends RecyclerView.OnScrollListener  {

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean isMonthView = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        //show views if first item is first visible position and views are hidden
        if (firstVisibleItem == 0) {
            if(!isMonthView) {
                onShow();
                isMonthView = true;
            }
        }else {
            if (scrolledDistance > HIDE_THRESHOLD && isMonthView) {
                onHide();
                isMonthView = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !isMonthView) {
                onShow();
                isMonthView = true;
                scrolledDistance = 0;
            }
        }


        if((isMonthView && dy>0) || (!isMonthView && dy<0)) {
            scrolledDistance += dy;
        }

    }

    public abstract void onHide();
    public abstract void onShow();

}
