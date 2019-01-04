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

import java.util.ArrayList;
import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.fragment.AudioFragment;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.FileType;
import jiguang.chat.entity.UpdateSelectedStateListener;
import jiguang.chat.utils.ViewHolder;


public class AudioAdapter extends BaseAdapter {

    private List<FileItem> mList;
    private AudioFragment mFragment;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private List<String> mSelectedList = new ArrayList<String>();
    private UpdateSelectedStateListener mListener;

    public AudioAdapter(AudioFragment fragment, List<FileItem> list) {
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
        final FileItem item = mList.get(position);
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_audio, null);
        }
        LinearLayout itemLl = ViewHolder.get(convertView, R.id.audio_item_ll);
        final CheckBox checkBox = ViewHolder.get(convertView, R.id.audio_cb);
        ImageView icon = ViewHolder.get(convertView, R.id.audio_iv);
        TextView title = ViewHolder.get(convertView, R.id.audio_title);
        TextView size = ViewHolder.get(convertView, R.id.audio_size);
        TextView date = ViewHolder.get(convertView, R.id.audio_date);

        itemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.audio);
                } else {
                    if (mFragment.getTotalCount() < 5) {
                        if (mFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.audio);
                            addAnimation(checkBox);
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

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    if (mFragment.getTotalCount() < 5) {
                        if (mFragment.getTotalSize() + item.getLongFileSize() < 10485760.0) {
                            checkBox.setChecked(true);
                            mSelectMap.put(position, true);
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.audio);
                            addAnimation(checkBox);
                        } else {
                            checkBox.setChecked(false);
                            Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                    .file_size_over_limit_hint), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        checkBox.setChecked(false);
                        Toast.makeText(mFragment.getContext(), mFragment.getString(R.string
                                .size_over_limit_hint), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.audio);
                }
            }
        });

        checkBox.setChecked(mSelectMap.get(position));
        title.setText(item.getFileName());
        size.setText(item.getFileSize());
        date.setText(item.getDate());

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
}
