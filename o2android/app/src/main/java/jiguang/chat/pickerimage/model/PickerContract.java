package jiguang.chat.pickerimage.model;

import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jiguang.chat.pickerimage.utils.Extras;

public class PickerContract {

	public static final Intent makeDataIntent(List<PhotoInfo> photos, boolean origin) {
		Intent intent = new Intent();
		intent.putExtra(Extras.EXTRA_PHOTO_LISTS, new ArrayList<PhotoInfo>(photos));
		intent.putExtra(Extras.EXTRA_IS_ORIGINAL, origin);
		
		return intent;
	}

	public static final Intent makeDataIntent(List<PhotoInfo> photos) {
		Intent intent = new Intent();
		intent.putExtra(Extras.EXTRA_PHOTO_LISTS, new ArrayList<PhotoInfo>(photos));
		
		return intent;
	}
	
	public static final Intent makePreviewDataIntent(List<PhotoInfo> photos, List<PhotoInfo> selectPhotos) {
		Intent intent = new Intent();
		intent.putExtra(Extras.EXTRA_PHOTO_LISTS, new ArrayList<PhotoInfo>(photos));
		intent.putExtra(Extras.EXTRA_SELECTED_IMAGE_LIST, new ArrayList<PhotoInfo>(selectPhotos));
		
		return intent;
	}
	
	public static final Intent makePreviewDataIntent(List<PhotoInfo> photos, List<PhotoInfo> selectPhotos, boolean origin) {
		Intent intent = new Intent();
		intent.putExtra(Extras.EXTRA_PHOTO_LISTS, new ArrayList<PhotoInfo>(photos));
		intent.putExtra(Extras.EXTRA_SELECTED_IMAGE_LIST, new ArrayList<PhotoInfo>(selectPhotos));
		intent.putExtra(Extras.EXTRA_IS_ORIGINAL, origin);
		
		return intent;
	}
	
	public static final Bundle makeDataBundle(List<PhotoInfo> photos, boolean mutiMode, int mutiSelectLimitSize) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(Extras.EXTRA_PHOTO_LISTS, new ArrayList<PhotoInfo>(photos));
		bundle.putBoolean(Extras.EXTRA_MUTI_SELECT_MODE, mutiMode);
		bundle.putInt(Extras.EXTRA_MUTI_SELECT_SIZE_LIMIT, mutiSelectLimitSize);
		
		return bundle;
	}
	
	public static final List<PhotoInfo> getPhotos(Bundle bundle) {
		return toPhotos(bundle.getSerializable(Extras.EXTRA_PHOTO_LISTS));
	}
	
	public static final List<PhotoInfo> getPhotos(Intent intent) {
		return toPhotos(intent.getSerializableExtra(Extras.EXTRA_PHOTO_LISTS));
	}
	
	public static final List<PhotoInfo> getSelectPhotos(Intent intent) {
		return toPhotos(intent.getSerializableExtra(Extras.EXTRA_SELECTED_IMAGE_LIST));
	}
	
	@SuppressWarnings("unchecked")
	private static final List<PhotoInfo> toPhotos(Serializable sPhotos) {
		if (sPhotos != null && sPhotos instanceof List<?>) {
			return (List<PhotoInfo>) sPhotos;
		}
		
		return null;
	}
}
