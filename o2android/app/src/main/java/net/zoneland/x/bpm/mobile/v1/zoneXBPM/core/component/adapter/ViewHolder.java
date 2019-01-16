/**
 * zoneland.net Inc
 * Copyright (c) 2015 
 */
package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleTextView;


public class ViewHolder {

	private static final String TAG = "net.zoneland.ViewHolder";

	private SparseArray<View> views;
	@SuppressWarnings("unused")
	private int position;
	private View convertView;

	/**
	 *
	 */
	public ViewHolder(Context context, ViewGroup parent, int itemLayout,
			int position) {
		this.position = position;
		this.views = new SparseArray<View>();
		this.convertView = LayoutInflater.from(context).inflate(itemLayout,
				parent, false);
		convertView.setTag(this);
	}


	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int itemLayout, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, itemLayout, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.position = position;
			return holder;
		}
	}


	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}


	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}


	public ViewHolder setCircleTextView(int viewId, String text, int circleColorResId) {
		Log.d(TAG, "colorRes="+circleColorResId);
		CircleTextView view = getView(viewId);
		view.setTextAndCircleColor(text, circleColorResId);
		return this;
	}


	public ViewHolder setImageViewResource(int viewId, int resId) {
		ImageView view = getView(viewId);
		view.setImageResource(resId);
		return this;
	}

	public ViewHolder setImageViewBackground(int viewId, int resId) {
		ImageView view = getView(viewId);
		view.setBackgroundResource(resId);
		return this;
	}

	 
	public ViewHolder setImageViewBitmap(int viewId, Bitmap bitmap) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}

	public ViewHolder setImageButtonBackground(int viewId, int resId) {
		ImageButton view = getView(viewId);
		view.setBackgroundResource(resId);
		return this;
	}

	/**
	 * @return the convertView
	 */
	public View getConvertView() {
		return convertView;
	}

}
