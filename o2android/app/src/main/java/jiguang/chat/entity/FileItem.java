package jiguang.chat.entity;


import java.text.NumberFormat;

import cn.jpush.im.android.api.model.Message;

public class FileItem {

    private String mFilePath;
    private String mFileName;
    private String mSize;
    private String mDate;
    private int section;
    private int msgId;
    private String mUserName;
    private Message mMessage;

    public FileItem(String path, String name, String size, String date, int msgId) {
        this.mFilePath = path;
        this.mFileName = name;
        this.mSize = size;
        this.mDate = date;
        this.msgId = msgId;
    }
    public FileItem(String path, String name, String size, String date, int msgId, String userName, Message message) {
        this.mFilePath = path;
        this.mFileName = name;
        this.mSize = size;
        this.mDate = date;
        this.msgId = msgId;
        this.mUserName = userName;
        this.mMessage = message;
    }

    public Message getMessage() {
        return mMessage;
    }

    public String getFromeName() {
        return mUserName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getFileSize() {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        //保留小数点后两位
        ddf1.setMaximumFractionDigits(2);
        long size = Long.valueOf(mSize);
        String sizeDisplay;
        if (size > 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + "M";
        } else if (size > 1024) {
            double result = size / 1024;
            sizeDisplay = ddf1.format(result) + "K";

        } else {
            sizeDisplay = ddf1.format(size) + "B";
        }
        return sizeDisplay;
    }

    public long getLongFileSize() {
        return Long.valueOf(mSize);
    }

    public String getDate() {
        return mDate;
    }

}
