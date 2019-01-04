package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo;

import java.util.ArrayList;

/**
 * Created by FancyLou on 2016/2/26.
 */
public class PictureViewerData  {

    public static final String TRANSFER_FILE_ID_KEY = "transfer_file_id_key";
    public static final String TRANSFER_TITLE_KEY = "transfer_title_key";
    public static final String TRANSFER_CURRENT_FILE_ID_KEY = "transfer_current_file_id_key";

    private ArrayList<String> fileIdList;
    private ArrayList<String> titleList;

    public PictureViewerData() {
        fileIdList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    public void addItem(String title, String fileId) {
            fileIdList.add(fileId);
            titleList.add(title);
    }

    public void clearItems() {
        fileIdList.clear();
        titleList.clear();
    }

    public ArrayList<String> getFileIdList() {
        return fileIdList;
    }

    public void setFileIdList(ArrayList<String> fileIdList) {
        this.fileIdList = fileIdList;
    }

    public ArrayList<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(ArrayList<String> titleList) {
        this.titleList = titleList;
    }


}
