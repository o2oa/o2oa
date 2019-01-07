package jiguang.chat.activity.historyfile.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.content.FileContent;
import jiguang.chat.activity.DownLoadActivity;
import jiguang.chat.adapter.StickyListHeadersAdapter;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.SelectedHistoryFileListener;
import jiguang.chat.utils.FileHelper;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.ViewHolder;
import jiguang.chat.view.MyImageView;

/**
 * Created by ${chenyn} on 2017/8/29.
 */

public class AudioFileAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<FileItem> mList;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private SelectedHistoryFileListener mListener;
    private Context mContext;

    public AudioFileAdapter(Activity fragment, List<FileItem> documents) {
        this.mList = documents;
        this.mContext = fragment;
        this.mInflater = LayoutInflater.from(fragment);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.section_tv);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.text.setText(mList.get(position).getDate());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FileItem item = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_file_document, null);
        }
        final CheckBox checkBox = ViewHolder.get(convertView, R.id.document_cb);
        TextView title = ViewHolder.get(convertView, R.id.document_title);
        TextView size = ViewHolder.get(convertView, R.id.document_size);
        TextView date = ViewHolder.get(convertView, R.id.document_date);
        LinearLayout ll_document = ViewHolder.get(convertView, R.id.document_item_ll);
        MyImageView imageView = ViewHolder.get(convertView, R.id.document_iv);

        if (SharePreferenceManager.getShowCheck()) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        title.setText(item.getFileName());
        size.setText(item.getFileSize());
        date.setText(item.getFromeName() + "  " + item.getDate());
        imageView.setImageResource(R.drawable.jmui_audio);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    mSelectMap.put(position, true);
                    mListener.onSelected(item.getMsgId(), item.getMsgId());
                } else {
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getMsgId(), item.getMsgId());
                }
            }
        });
        final FileContent content = (FileContent) item.getMessage().getContent();
        ll_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getLocalPath() == null) {
                    org.greenrobot.eventbus.EventBus.getDefault().postSticky(item.getMessage());
                    Intent intent = new Intent(mContext, DownLoadActivity.class);
                    mContext.startActivity(intent);
                } else {
                    final String newPath = "sdcard/JChatDemo/recvFiles/" + content.getFileName();
                    File file = new File(newPath);
                    if (file.exists() && file.isFile()) {
                        browseDocument(content.getFileName(), newPath);
                    } else {
                        final String finalFileName = content.getFileName();
                        FileHelper.getInstance().copyFile(item.getFileName(), content.getLocalPath(), (Activity) mContext,
                                new FileHelper.CopyFileCallback() {
                                    @Override
                                    public void copyCallback(Uri uri) {
                                        browseDocument(finalFileName, newPath);
                                    }
                                });
                    }
                }
            }
        });
        return convertView;
    }

    private void browseDocument(String fileName, String path) {
        try {
//            String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
//            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//            String mime = mimeTypeMap.getMimeTypeFromExtension(ext);
            File file = new File(path);
            AndroidUtils.INSTANCE.openFileWithDefaultApp((Activity) mContext, file);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.file_not_support_hint, Toast.LENGTH_SHORT).show();
        }
    }

    public void setUpdateListener(SelectedHistoryFileListener listener) {
        this.mListener = listener;
    }

    private static class HeaderViewHolder {
        TextView text;
    }
}
