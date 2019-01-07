package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.WorkLog;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

import java.util.List;

/**
 * Created by FancyLou on 2016/10/26.
 */

public class WorkLogPopupWindow extends PopupWindow {

    private int width;
    private int height;
    private View mConvertView;
    private RecyclerView workLogListView;
    private List<WorkLog> workLogList;


    public WorkLogPopupWindow(Context context, List<WorkLog> workLogList) {
        super(context);
        this.workLogList = workLogList;
        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.pop_work_log_list, null);
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

        initViews(context);
    }

    private void initViews(Context context) {
        ImageView imageView = (ImageView) mConvertView.findViewById(R.id.image_pop_work_log_list_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        workLogListView = (RecyclerView) mConvertView.findViewById(R.id.recycler_view_pop_work_log_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        workLogListView.setLayoutManager(linearLayoutManager);
        CommonRecycleViewAdapter<WorkLog> adapter = new CommonRecycleViewAdapter<WorkLog>(context, workLogList, R.layout.item_task_complete_work_log_list) {
            @Override
            public void convert(CommonRecyclerViewHolder holder, WorkLog o) {
                String activityName = o.getFromActivityName();
                if (!TextUtils.isEmpty(o.getArrivedActivityName())) {
                    activityName += " -> "+o.getArrivedActivityName();
                }
                holder.setText(R.id.tv_work_log_complete_activityName, activityName);
                String personOpinion = "";
                List<TaskCompleteData> list = o.getTaskCompletedList();
                if (list != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        TaskCompleteData data = list.get(i);
                        personOpinion += data.getPerson() + ":\n   [" + data.getRouteName() + "] " + data.getOpinion() + "    (" + data.getCompletedTime() + ")";
                        if (i != (list.size() - 1)) {
                            personOpinion += "\n";
                        }
                    }
                }
                List<TaskData> taskDataList = o.getTaskList();
                if (taskDataList == null || taskDataList.isEmpty()) {
                    XLog.debug("setupTaskCompleteInfo， complete true");
                    holder.setImageViewResource(R.id.image_work_log_complete_icon, R.mipmap.icon_work_completed);
                } else {
                    XLog.debug("setupTaskCompleteInfo， complete false");
                    holder.setImageViewResource(R.id.image_work_log_complete_icon, R.mipmap.icon_work_uncompleted);
                    for (int j = 0; j < taskDataList.size(); j++) {
                        TaskData data = taskDataList.get(j);
                        personOpinion += data.getPerson() + "  正在处理, 到达时间: "+data.getCreateTime();
                        if (j != (taskDataList.size() - 1)) {
                            personOpinion += "\n";
                        }
                    }
                }

                holder.setText(R.id.tv_work_log_complete_person_opinion, personOpinion);
            }

        };
        workLogListView.setAdapter(adapter);
    }


    /**
     * 计算高度和宽度
     * 宽度和屏幕一样
     * 高度是70%的屏幕
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = (int) (metrics.heightPixels * 0.7 );
    }

}
