package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleTextView;

/**
 * Created by FancyLou on 2015/12/14.
 */
public class CommonRecyclerViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private View convertView;

    public CommonRecyclerViewHolder(View itemView) {
        super(itemView);
        this.convertView = itemView;
        this.views = new SparseArray<>();
        this.convertView.setTag(this);
    }


    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }


    public CommonRecyclerViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public CommonRecyclerViewHolder setCircleTextView(int viewId, String text, int circleColorResId) {
        CircleTextView view = getView(viewId);
        view.setTextAndCircleColor(text, circleColorResId);
        return this;
    }


    public CommonRecyclerViewHolder setImageViewResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public CommonRecyclerViewHolder setImageViewBackground(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public CommonRecyclerViewHolder setImageViewDrawable(int viewId, Drawable drawable) {
        if (drawable!=null) {
            ImageView view = getView(viewId);
            view.setImageDrawable(drawable);
        }
        return this;
    }

    public CommonRecyclerViewHolder setImageViewBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public CommonRecyclerViewHolder setImageButtonBackground(int viewId, int resId) {
        ImageButton view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public View getConvertView() {
        return convertView;
    }
}

