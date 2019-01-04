package jiguang.chat.entity;


public interface UpdateSelectedStateListener {
    public void onSelected(String path, long fileSize, FileType type);
    public void onUnselected(String path, long fileSize, FileType type);
}
