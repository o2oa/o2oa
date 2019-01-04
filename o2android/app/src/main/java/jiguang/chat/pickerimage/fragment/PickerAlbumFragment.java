package jiguang.chat.pickerimage.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.pickerimage.adapter.PickerAlbumAdapter;
import jiguang.chat.pickerimage.model.AlbumInfo;
import jiguang.chat.pickerimage.model.PhotoInfo;
import jiguang.chat.pickerimage.utils.MediaDAO;
import jiguang.chat.pickerimage.utils.ThumbnailsUtil;


public class PickerAlbumFragment extends BaseFragment implements OnItemClickListener {
    public interface OnAlbumItemClickListener {
        public void OnAlbumItemClick(AlbumInfo info);
    }

    private OnAlbumItemClickListener onAlbumItemClickListener;
    private LinearLayout loadingLay;
    private TextView loadingTips;
    private TextView loadingEmpty;
    private ListView albumListView;

    public static final String FILE_PREFIX = "file://";

    private List<AlbumInfo> albumInfolist = new ArrayList<AlbumInfo>();

    private PickerAlbumAdapter albumAdapter;

    public PickerAlbumFragment() {
        this.setContainerId(R.id.picker_album_fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (onAlbumItemClickListener == null) {
            onAlbumItemClickListener = (OnAlbumItemClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picker_image_folder_activity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        startImageScanTask();
    }

    private void findViews() {
        loadingLay = findView(R.id.picker_image_folder_loading);
        loadingTips = findView(R.id.picker_image_folder_loading_tips);
        loadingEmpty = findView(R.id.picker_image_folder_loading_empty);
        albumListView = findView(R.id.picker_image_folder_listView);
        albumListView.setOnItemClickListener(this);
    }

    private void startImageScanTask() {
        new ImageScanAsyncTask().execute();
    }

    private class ImageScanAsyncTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            getAllMediaThumbnails();
            getAllMediaPhotos();

            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (getActivity() != null && albumInfolist != null) {
                albumAdapter = new PickerAlbumAdapter(getActivity(), albumInfolist);
                albumListView.setAdapter(albumAdapter);

                if (albumInfolist.size() > 0) {
                    loadingLay.setVisibility(View.GONE);
                } else {
                    loadingLay.setVisibility(View.VISIBLE);
                    loadingTips.setVisibility(View.GONE);
                    loadingEmpty.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void getAllMediaThumbnails() {
        ThumbnailsUtil.clear();
        Cursor cursorThumb = null;
        try {
            cursorThumb = MediaDAO.getAllMediaThumbnails(getActivity());
            if (cursorThumb != null && cursorThumb.moveToFirst()) {
                int imageID;
                String imagePath;
                do {
                    imageID = cursorThumb.getInt(cursorThumb.getColumnIndex(Thumbnails.IMAGE_ID));
                    imagePath = cursorThumb.getString(cursorThumb.getColumnIndex(Thumbnails.DATA));
                    ThumbnailsUtil.put(imageID, FILE_PREFIX + imagePath);
                } while (cursorThumb.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursorThumb != null) {
                    cursorThumb.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private void getAllMediaPhotos() {
        if (albumInfolist == null) {
            albumInfolist = new ArrayList<AlbumInfo>();
        } else {
            albumInfolist.clear();
        }

        Cursor cursorPhotos = null;
        try {
            cursorPhotos = MediaDAO.getAllMediaPhotos(getActivity());
            HashMap<String, AlbumInfo> hash = new HashMap<String, AlbumInfo>();
            AlbumInfo albumInfo = null;
            PhotoInfo photoInfo = null;

            if (cursorPhotos != null && cursorPhotos.moveToFirst()) {
                do {
                    int index = 0;
                    int _id = cursorPhotos.getInt(cursorPhotos.getColumnIndex(MediaStore.Images.Media._ID));
                    String path = cursorPhotos.getString(cursorPhotos.getColumnIndex(MediaStore.Images.Media.DATA));
                    String album = cursorPhotos.getString(cursorPhotos.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    long size = cursorPhotos.getLong(cursorPhotos.getColumnIndex(MediaStore.Images.Media.SIZE));

                    if (!isValidImageFile(path)) {
                        Log.d("PICKER", "it is not a vaild path:" + path);
                        continue;
                    }

                    List<PhotoInfo> photoList = new ArrayList<PhotoInfo>();
                    photoInfo = new PhotoInfo();
                    if (hash.containsKey(album)) {
                        albumInfo = hash.remove(album);
                        if (albumInfolist.contains(albumInfo))
                            index = albumInfolist.indexOf(albumInfo);
                        photoInfo.setImageId(_id);
                        photoInfo.setFilePath(FILE_PREFIX + path);
                        photoInfo.setAbsolutePath(path);
                        photoInfo.setSize(size);
                        albumInfo.getList().add(photoInfo);
                        albumInfolist.set(index, albumInfo);
                        hash.put(album, albumInfo);
                    } else {
                        albumInfo = new AlbumInfo();
                        photoList.clear();
                        photoInfo.setImageId(_id);
                        photoInfo.setFilePath(FILE_PREFIX + path);
                        photoInfo.setAbsolutePath(path);
                        photoInfo.setSize(size);
                        photoList.add(photoInfo);
                        albumInfo.setImageId(_id);
                        albumInfo.setFilePath(FILE_PREFIX + path);
                        albumInfo.setAbsolutePath(path);
                        albumInfo.setAlbumName(album);
                        albumInfo.setList(photoList);
                        albumInfolist.add(albumInfo);
                        hash.put(album, albumInfo);
                    }
                } while (cursorPhotos.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursorPhotos != null) {
                    cursorPhotos.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private boolean isValidImageFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            return true;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        onAlbumItemClickListener.OnAlbumItemClick(albumInfolist.get(position));
    }
}
