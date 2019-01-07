package jiguang.chat.activity.historyfile.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import jiguang.chat.activity.historyfile.activity.BrowserFileImageActivity;
import jiguang.chat.activity.historyfile.fragment.ImageFileFragment;
import jiguang.chat.activity.historyfile.grideviewheader.StickyGridHeadersSimpleAdapter;
import jiguang.chat.activity.historyfile.view.MImageView;
import jiguang.chat.activity.historyfile.view.NativeImageLoaderView;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.SelectedHistoryFileListener;
import jiguang.chat.utils.SharePreferenceManager;

/**
 * Created by ${chenyn} on 2017/8/24.
 */

public class ImageFileAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    private List<FileItem> mItemList;
    private ArrayList<String> mPath;
    private LayoutInflater mInflater;
    private Activity mContext;
    private Point mPoint = new Point(0, 0);
    private GridView mGridView;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private SelectedHistoryFileListener mListener;

    public ImageFileAdapter(ImageFileFragment fragment, List<FileItem> images, ArrayList<String> path,
                            GridView gridView) {
        this.mItemList = images;
        this.mPath = path;
        this.mInflater = LayoutInflater.from(fragment.getContext());
        this.mContext = fragment.getActivity();
        this.mGridView = gridView;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FileItem item = mItemList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.history_file_gride, null);
            holder.mImageView = (MImageView) convertView.findViewById(R.id.grid_item);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
            holder.checkBoxLl = (LinearLayout) convertView.findViewById(R.id.checkbox_ll);
            convertView.setTag(holder);

            holder.mImageView.setOnMeasureListener(new MImageView.OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if ( SharePreferenceManager.getShowCheck()) {
            holder.checkBoxLl.setVisibility(View.VISIBLE);
        }else {
            holder.checkBoxLl.setVisibility(View.GONE);
        }

        String path = mItemList.get(position).getFilePath();
        holder.mImageView.setTag(path);

        Bitmap bitmap = NativeImageLoaderView.getInstance().loadNativeImage(path, mPoint,
                new NativeImageLoaderView.NativeImageCallBack() {

                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            holder.mImageView.setImageBitmap(bitmap);
        } else {
            holder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }


        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BrowserFileImageActivity.class);
                intent.putStringArrayListExtra("historyImagePath", mPath);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    mSelectMap.put(position, true);
                    mListener.onSelected(item.getMsgId(), item.getMsgId());
                } else {
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getMsgId(), item.getMsgId());
                }
            }
        });

        holder.checkBox.setChecked(mSelectMap.get(position));

        //Picasso.with(mFragment.getContext()).load(new File(item.getFilePath())).into(holder.icon);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mItemList.get(position).getSection();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.history_file_time_header, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        mHeaderHolder.mTextView.setText(mItemList.get(position).getDate());

        return convertView;
    }

    public void setUpdateListener(SelectedHistoryFileListener listener) {
        this.mListener = listener;
    }

    private class ViewHolder {
        MImageView mImageView;
        CheckBox checkBox;
        LinearLayout checkBoxLl;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }
}
