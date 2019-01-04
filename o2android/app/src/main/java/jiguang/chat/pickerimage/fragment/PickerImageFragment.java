package jiguang.chat.pickerimage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.pickerimage.adapter.PickerPhotoAdapter;
import jiguang.chat.pickerimage.model.PhotoInfo;
import jiguang.chat.pickerimage.utils.Extras;

public class PickerImageFragment extends BaseFragment implements OnItemClickListener {

    public interface OnPhotoSelectClickListener {
        void onPhotoSelectClick(PhotoInfo selectPhoto);

        void onPhotoSingleClick(List<PhotoInfo> photos, int position);
    }

    private GridView pickerImageGridView;

    private OnPhotoSelectClickListener onPhotoSelectClickListener;

    private List<PhotoInfo> photoList;

    private PickerPhotoAdapter photoAdapter;

    private boolean isMutiMode;

    private int mutiSelectLimitSize;

    public PickerImageFragment() {
        this.setContainerId(R.id.picker_photos_fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (onPhotoSelectClickListener == null) {
            onPhotoSelectClickListener = (OnPhotoSelectClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picker_images_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        proceedExtra();
        findViews();
    }

    private void proceedExtra() {
        Bundle bundle = getArguments();
        photoList = new ArrayList<PhotoInfo>();
        photoList.addAll(getPhotos(bundle));
        isMutiMode = bundle.getBoolean(Extras.EXTRA_MUTI_SELECT_MODE);
        mutiSelectLimitSize = bundle.getInt(Extras.EXTRA_MUTI_SELECT_SIZE_LIMIT, 9);
    }

    private void findViews() {
        pickerImageGridView = findView(R.id.picker_images_gridview);
        photoAdapter = new PickerPhotoAdapter(getActivity(), photoList, pickerImageGridView, isMutiMode, 0, mutiSelectLimitSize);
        pickerImageGridView.setAdapter(photoAdapter);
        pickerImageGridView.setOnItemClickListener(this);
    }

    public void resetFragment(List<PhotoInfo> list, int hasSelect) {
        pickerImageGridView.setAdapter(null);
        if (photoList == null) {
            photoList = new ArrayList<>();
        } else {
            photoList.clear();
        }
        if (list != null) {
            photoList.addAll(list);
        }
        photoAdapter = new PickerPhotoAdapter(getActivity(), photoList, pickerImageGridView, isMutiMode, hasSelect, mutiSelectLimitSize);
        pickerImageGridView.setAdapter(photoAdapter);
    }

    public void updateGridview(List<PhotoInfo> list) {
        if (list == null)
            return;
        for (int i = 0; i < list.size(); i++) {
            PhotoInfo info = list.get(i);
            int imageID = info.getImageId();
            boolean choose = info.isChoose();
            for (int j = 0; j < photoList.size(); j++) {
                PhotoInfo phone = photoList.get(j);
                if (phone.getImageId() == imageID) {
                    phone.setChoose(choose);
                    break;
                }
            }
        }
        if (photoAdapter != null) {
            photoAdapter.notifyDataSetChanged();
        }
    }

    public void updateSelectedForAdapter(int hasSelectNum) {
        if (photoAdapter != null) {
            photoAdapter.updateSelectNum(hasSelectNum);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        onPhotoSelectClickListener.onPhotoSingleClick(photoList, position);
    }

    public List<PhotoInfo> getPhotos(Bundle bundle) {
        return toPhotos(bundle.getSerializable(Extras.EXTRA_PHOTO_LISTS));
    }

    @SuppressWarnings("unchecked")
    private List<PhotoInfo> toPhotos(Serializable sPhotos) {
        if (sPhotos != null && sPhotos instanceof List<?>) {
            return (List<PhotoInfo>) sPhotos;
        }

        return null;
    }
}
