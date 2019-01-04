package jiguang.chat.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.BrowserViewPagerActivity;
import jiguang.chat.activity.fragment.ImageFragment;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.FileType;
import jiguang.chat.entity.UpdateSelectedStateListener;
import jiguang.chat.pickerimage.utils.PickerConfig;
import jiguang.chat.view.MyImageView;


public class ImageAdapter extends BaseAdapter {

    private ImageFragment mFragment;
    private List<FileItem> mList;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private UpdateSelectedStateListener mListener;

    public ImageAdapter(ImageFragment fragment, List<FileItem> list) {
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_pick_picture_detail, null);
            holder = new ViewHolder();
            holder.icon = (MyImageView) convertView.findViewById(R.id.child_image);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
            holder.checkBoxLl = (LinearLayout) convertView.findViewById(R.id.checkbox_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //选择文件-选择图片时单击图片进入预览.因为只是预览,就是放大图片查看,这里就使用头像的extra是一样的
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("browserAvatar", true);
                intent.putExtra("avatarPath", item.getFilePath());
                intent.setClass(mFragment.getContext(), BrowserViewPagerActivity.class);
                mFragment.getContext().startActivity(intent);
                Activity context = (Activity) mFragment.getContext();
                context.overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
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
                            mListener.onSelected(item.getFilePath(), item.getLongFileSize(), FileType.image);
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
                    mListener.onUnselected(item.getFilePath(), item.getLongFileSize(), FileType.image);
                }
            }
        });

        holder.checkBox.setChecked(mSelectMap.get(position));
        PickerConfig.checkImageLoaderConfig(mFragment.getContext());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.jmui_picture_not_found)
                .showImageForEmptyUri(R.drawable.jmui_picture_not_found)
                .showImageOnFail(R.drawable.jmui_picture_not_found)
                .cacheInMemory(true) //加载本地图片不需要再做SD卡缓存，只做内存缓存即可
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        //这里原来用的picasso但是picasso不能处理大量图片加载进来oom的问题,换用imageloader解决

        //1.这种方式可以
        ImageAware imageAware = new ImageViewAware(holder.icon, false);
        ImageLoader.getInstance().displayImage("file:///" + item.getFilePath(), imageAware, options);

        //2.这种方式也可以
//        holder.icon.setTag(item.getFilePath());
//        if ((item.getFilePath()).equals(holder.icon.getTag())) {
//            ImageLoader.getInstance().displayImage("file:///" + item.getFilePath(), holder.icon, options);
//        }

        return convertView;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[] {0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
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
        CheckBox checkBox;
        ImageView icon;
        LinearLayout checkBoxLl;
    }
}
