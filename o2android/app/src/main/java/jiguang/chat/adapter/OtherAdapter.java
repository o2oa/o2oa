package jiguang.chat.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.fragment.OtherFragment;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.FileType;
import jiguang.chat.entity.UpdateSelectedStateListener;


public class OtherAdapter extends BaseAdapter {

    private List<FileItem> mList;
    private OtherFragment mFragment;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private UpdateSelectedStateListener mListener;

    public OtherAdapter(OtherFragment fragment, List<FileItem> list) {
        this.mFragment = fragment;
        this.mList = list;
        this.mInflater = LayoutInflater.from(fragment.getContext());
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
            convertView = mInflater.inflate(R.layout.item_other, null);
            holder.itemLl = (LinearLayout) convertView.findViewById(R.id.other_item_ll);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.other_cb);
            holder.icon = (ImageView) convertView.findViewById(R.id.other_iv);
            holder.title = (TextView) convertView.findViewById(R.id.other_title);
            holder.size = (TextView) convertView.findViewById(R.id.other_size);
            holder.date = (TextView) convertView.findViewById(R.id.other_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.itemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.other);
                } else {
                    if (mFragment.getTotalCount() < 5) {
                        if (mFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            holder.checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.other);
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
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.other);
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
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.other);
                }
            }
        });

        holder.checkBox.setChecked(mSelectMap.get(position));
        holder.title.setText(item.getFileName());
        holder.size.setText(item.getFileSize());
        holder.date.setText(item.getDate());
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
        this.mListener = listener;
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
