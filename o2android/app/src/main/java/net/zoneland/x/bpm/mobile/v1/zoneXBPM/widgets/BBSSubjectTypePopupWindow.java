package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

import java.util.List;

/**
 * Created by FancyLou on 2016/10/14.
 */

public class BBSSubjectTypePopupWindow extends PopupWindow {


    private int width;
    private int height;
    private View mConvertView;
    private DividerItemDecoration itemDecoration;
    private CommonRecycleViewAdapter<String> adapter;
    private RecyclerView listView;

    private List<String> typeList;
    private BBSSubjectTypeChooseListener listener;

    public interface BBSSubjectTypeChooseListener {
        void onItemChoose(String type);
    }


    public BBSSubjectTypePopupWindow(Context context, List<String> typeList) {
        super(context);
        this.typeList = typeList;
        calWidthAndHeight(context);
        mConvertView = LayoutInflater
                .from(context).inflate(R.layout.pop_bbs_subject_type, null);
        setContentView(mConvertView);
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initView(context);
    }

    private void initView(Context context) {
        listView = (RecyclerView) mConvertView.findViewById(R.id.recycler_pop_bbs_subject_type_list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        if (itemDecoration==null) {
            itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        }else {
            listView.removeItemDecoration(itemDecoration);
        }
        listView.addItemDecoration(itemDecoration);
        adapter = new CommonRecycleViewAdapter<String>(context, typeList, R.layout.item_pop_okr_detail_work_cycle_list) {
            @Override
            public void convert(CommonRecyclerViewHolder holder, String s) {
                TextView view = holder.getView(R.id.tv_item_pop_okr_detail_work_cycle_title);
                view.setText(s);
                view.setTextColor(Color.DKGRAY);
            }
        };
        adapter.setOnItemClickListener(new CommonRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                XLog.debug("click type item:"+position);
                if (listener!=null) {
                    listener.onItemChoose(typeList.get(position));
                }
            }
        });
        listView.setAdapter(adapter);
    }


    /**
     * 计算高度和宽度
     * 宽度和屏幕一样
     * 高度是60%的屏幕
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = (int) (metrics.heightPixels * 0.6 );
    }


    public BBSSubjectTypeChooseListener getListener() {
        return listener;
    }

    public void setListener(BBSSubjectTypeChooseListener listener) {
        this.listener = listener;
    }
}
