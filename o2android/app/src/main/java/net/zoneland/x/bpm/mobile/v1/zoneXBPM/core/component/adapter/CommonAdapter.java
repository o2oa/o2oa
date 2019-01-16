/**
 * zoneland.net Inc
 * Copyright (c) 2015
 */
package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 通用的Adapter
 *
 * @author FancyLou
 * @date 2015年4月27日 下午9:08:07
 *
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	private Context context;
	private List<T> datas;
	private int itemLayout;

	/**
	 *
	 */
	public CommonAdapter(Context context, List<T> datas, int itemLayout) {
		this.context = context;
		this.datas = datas;
		this.itemLayout = itemLayout;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return datas.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		return datas.get(position);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = ViewHolder.get(context, convertView, parent,
				itemLayout, position);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}




	public abstract void convert(ViewHolder holder, T t);

}
