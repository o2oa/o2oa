package jiguang.chat.pickerimage.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.pickerimage.model.AlbumInfo;
import jiguang.chat.pickerimage.utils.PickerImageLoadTool;
import jiguang.chat.pickerimage.utils.RotateImageViewAware;
import jiguang.chat.pickerimage.utils.ThumbnailsUtil;


public class PickerAlbumAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<AlbumInfo> mList;
	private Context mContext;
	
	public PickerAlbumAdapter(Context context, List<AlbumInfo> list){
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.picker_photofolder_item, null);
			viewHolder.folderCover = (ImageView)convertView.findViewById(R.id.picker_photofolder_cover);
			viewHolder.folderName = (TextView)convertView.findViewById(R.id.picker_photofolder_info);
			viewHolder.folderFileNum = (TextView)convertView.findViewById(R.id.picker_photofolder_num);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final AlbumInfo albumInfo = mList.get(position);
		String thumbPath = ThumbnailsUtil.getThumbnailWithImageID(albumInfo.getImageId(), albumInfo.getFilePath());
		PickerImageLoadTool.disPlay(thumbPath, new RotateImageViewAware(viewHolder.folderCover, albumInfo.getAbsolutePath()),
				R.drawable.image_default);
		viewHolder.folderName.setText(albumInfo.getAlbumName());
		viewHolder.folderFileNum.setText(String.format(mContext.getResources().getString(
				R.string.picker_image_folder_info), mList.get(position).getList().size()));
		return convertView;
	}
	
	public class ViewHolder{
		public ImageView folderCover;
		public TextView folderName;
		public TextView folderFileNum;
	}
}
