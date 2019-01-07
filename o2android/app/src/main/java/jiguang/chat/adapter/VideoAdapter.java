package jiguang.chat.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.PlayVideoActivity;
import jiguang.chat.activity.fragment.VideoFragment;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.FileType;
import jiguang.chat.entity.UpdateSelectedStateListener;
import jiguang.chat.utils.NativeImageLoader;
import jiguang.chat.utils.VideoThumbnailLoader;


public class VideoAdapter extends BaseAdapter {

    private List<FileItem> mList;
    private VideoFragment mFragment;
    private LayoutInflater mInflater;
    private int mImgWidth;
    private int mImgHeight;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private UpdateSelectedStateListener mListener;

    public VideoAdapter(VideoFragment fragment, List<FileItem> list, float density) {
        mFragment = fragment;
        this.mList = list;
        this.mInflater = LayoutInflater.from(fragment.getContext());
        mImgWidth = (int) (50 * density);
        mImgHeight = (int) (50 * density);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final FileItem item = mList.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_video, null);
            holder.itemLl = (LinearLayout) convertView.findViewById(R.id.video_item_ll);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.video_cb);
            holder.icon = (ImageView) convertView.findViewById(R.id.video_iv);
            holder.title = (TextView) convertView.findViewById(R.id.video_title);
            holder.size = (TextView) convertView.findViewById(R.id.video_size);
            holder.date = (TextView) convertView.findViewById(R.id.video_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(item.getFileName());
        holder.size.setText(item.getFileSize());
        holder.date.setText(item.getDate());

        Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(item.getFilePath());
        if (null != bitmap) {
            holder.icon.setImageBitmap(bitmap);
        } else {
            holder.icon.setTag(item.getFilePath());
            VideoThumbnailLoader.getIns().display(item.getFileName(), item.getFilePath(), holder.icon,
                    mImgWidth, mImgHeight, new VideoThumbnailLoader.ThumbnailListener() {
                        @Override
                        public void onThumbnailLoadCompleted(String url, ImageView iv, Bitmap bitmap) {
                            String tag = (String) iv.getTag();
                            if (null != bitmap && null != tag && tag.equals(url)) {
                                iv.setImageBitmap(bitmap);
                            }
                        }
                    });
        }

        holder.itemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                } else {
                    if (mFragment.getTotalCount() < 5) {
                        if (mFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            holder.checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                            addAnimation(holder.checkBox);
                        } else {
                            Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                    .file_size_over_limit_hint), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                .size_over_limit_hint), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    if (mFragment.getTotalCount() < 5) {
                        if (mFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            holder.checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                            addAnimation(holder.checkBox);
                        } else {
                            holder.checkBox.setChecked(false);
                            Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                    .file_size_over_limit_hint), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        holder.checkBox.setChecked(false);
                        Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                .size_over_limit_hint), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.video);
                }
            }
        });

        holder.checkBox.setChecked(mSelectMap.get(position));

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mFragment.getActivity(), PlayVideoActivity.class);
                intent.putExtra("videoPath", mList.get(position).getFilePath());
                ArrayList<String> pathList = new ArrayList<String>();
                for (FileItem item : mList) {
                    pathList.add(item.getFilePath());
                }
                intent.putStringArrayListExtra("videoPathList", pathList);
                mFragment.startActivity(intent);
            }
        });
        return convertView;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    public void setUpdateListener(UpdateSelectedStateListener listener) {
        mListener = listener;
    }

    private class ViewHolder {
        LinearLayout itemLl;
        CheckBox checkBox;
        ImageView icon;
        TextView title;
        TextView size;
        TextView date;
    }
}
