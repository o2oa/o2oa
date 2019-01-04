package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast;

import java.util.List;

/**
 * Created by FancyLou on 2016/12/27.
 */

public class AttachPopupWindow extends PopupWindow {

    private int width;
    private int height;
    private View mConvertView;

    private DividerItemDecoration itemDecoration;
    private CommonRecycleViewAdapter<AttachmentItemVO> adapter;
    private RecyclerView recyclerView;
    private List<AttachmentItemVO> attachList;

    private AttachListener listener;



    public AttachPopupWindow(Context context, List<AttachmentItemVO> attachList) {
        super(context);
        this.attachList = attachList;
        calWidthAndHeight(context);
        mConvertView = LayoutInflater
                .from(context).inflate(R.layout.pop_bbs_subject_attach_list, null);
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

    /**
     * 刷新状态
     */
    public void notifyStatusChanged() {
        XLog.debug("notifyStatusChanged~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        adapter.notifyDataSetChanged();
    }



    private void initView(final Context context) {
        ImageView close = (ImageView) mConvertView.findViewById(R.id.image_pop_bbs_subject_attach_list_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        recyclerView = (RecyclerView) mConvertView.findViewById(R.id.recycler_pop_bbs_subject_attach_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (itemDecoration==null) {
            itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        }else {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new CommonRecycleViewAdapter<AttachmentItemVO>(context, attachList, R.layout.item_bbs_subject_attach_list) {
            @Override
            public void convert(CommonRecyclerViewHolder holder,
                                AttachmentItemVO bbsSubjectAttachmentData) {
                int res = FileExtensionHelper.getImageResourceByFileExtension(bbsSubjectAttachmentData.getExtension());
                holder.setText(R.id.tv_item_bbs_subject_attach_title, bbsSubjectAttachmentData.getName())
                        .setImageViewResource(R.id.image_item_bbs_subject_attach_icon, res);
                if (listener!=null) {
                    AttachStatus status = listener.getAttachStatus(bbsSubjectAttachmentData.getId());
                    ImageView statusImage = holder.getView(R.id.image_item_bbs_subject_attach_status);
                    CircleProgressBar circle = holder.getView(R.id.image_item_bbs_subject_attach_downloading);
                    if (AttachStatus.ONCLOUD.equals(status)) {
                        statusImage.setImageResource(R.mipmap.icon_bbs_attach_download);
                        statusImage.setVisibility(View.VISIBLE);
                        circle.setVisibility(View.GONE);
                    }else if (AttachStatus.DOWNLOADCOMPLETED.equals(status)) {
                        statusImage.setImageResource(R.mipmap.icon_bbs_attach_open);
                        statusImage.setVisibility(View.VISIBLE);
                        circle.setVisibility(View.GONE);
                    }else if (AttachStatus.DOWNLOADING.equals(status)) {
                        statusImage.setVisibility(View.GONE);
                        circle.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CommonRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AttachmentItemVO data = attachList.get(position);
                if (listener!=null) {
                    AttachStatus status = listener.getAttachStatus(data.getId());
                    if (AttachStatus.ONCLOUD.equals(status)) {
                        ImageView statusImage = (ImageView) view.findViewById(R.id.image_item_bbs_subject_attach_status);
                        CircleProgressBar circle = (CircleProgressBar) view.findViewById(R.id.image_item_bbs_subject_attach_downloading);
                        statusImage.setVisibility(View.GONE);
                        circle.setVisibility(View.VISIBLE);
                        listener.startDownLoadFile(data.getId());
                    }else if (AttachStatus.DOWNLOADCOMPLETED.equals(status)) {
                        listener.openCompletedFile(data.getId());
                    }else if (AttachStatus.DOWNLOADING.equals(status)) {
                        XToast.INSTANCE.toastShort(context, "附件下载中，请稍后再试！");
                    }
                }
            }
        });

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


    public AttachListener getListener() {
        return listener;
    }

    public void setListener(AttachListener listener) {
        this.listener = listener;
    }

    public interface AttachListener {

        AttachStatus getAttachStatus(String id);
        void openCompletedFile(String id);
        void startDownLoadFile(String id);

    }


    public enum AttachStatus {
        ONCLOUD,
        DOWNLOADING,
        DOWNLOADCOMPLETED;
    }
}
