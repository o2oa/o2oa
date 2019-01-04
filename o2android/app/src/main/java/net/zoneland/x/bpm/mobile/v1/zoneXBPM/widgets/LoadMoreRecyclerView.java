package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

/**
 * Created by fancy on 2017/3/15.
 */

public class LoadMoreRecyclerView extends RecyclerView {


    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        XLog.info("recycler view scroll state :"+state);
    }


}
