package jiguang.chat.pickerimage.utils;

public enum StorageType {
    TYPE_LOG(DirectoryName.LOG_DIRECTORY_NAME),
    TYPE_TEMP(DirectoryName.TEMP_DIRECTORY_NAME),
    TYPE_FILE(DirectoryName.FILE_DIRECTORY_NAME),
    TYPE_AUDIO(DirectoryName.AUDIO_DIRECTORY_NAME),
    TYPE_IMAGE(DirectoryName.IMAGE_DIRECTORY_NAME),
    TYPE_VIDEO(DirectoryName.VIDEO_DIRECTORY_NAME),
    TYPE_THUMB_IMAGE(DirectoryName.THUMB_DIRECTORY_NAME),
    TYPE_THUMB_VIDEO(DirectoryName.THUMB_DIRECTORY_NAME),
    ;
    private DirectoryName storageDirectoryName;
    private long storageMinSize;                
    
    public String getStoragePath() {
		return storageDirectoryName.getPath();
	}

	public long getStorageMinSize() {
		return storageMinSize;
	}

	StorageType(DirectoryName dirName) {
		this(dirName, StorageUtil.THRESHOLD_MIN_SPCAE);
	}
    
	StorageType(DirectoryName dirName, long storageMinSize) {
        this.storageDirectoryName = dirName;
        this.storageMinSize = storageMinSize;    
	}
	
    enum DirectoryName {     	
    	AUDIO_DIRECTORY_NAME("audio/"),
        DATA_DIRECTORY_NAME("data/"),
        FILE_DIRECTORY_NAME("file/"),
        LOG_DIRECTORY_NAME("log/"),
        TEMP_DIRECTORY_NAME("temp/"),
        IMAGE_DIRECTORY_NAME("image/"),
        THUMB_DIRECTORY_NAME("thumb/"),
        VIDEO_DIRECTORY_NAME("video/"),
        ;
        
        private String path;
        
    	public String getPath() {
			return path;
		}

        private DirectoryName(String path) {
			this.path = path;
		}        	
    }       
}
