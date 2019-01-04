package jiguang.chat.pickerimage.model;

import java.io.Serializable;

public class PhotoInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int imageId;
	private String filePath;
	private String absolutePath;
	private long size;
	private boolean choose = false;
	
	public int getImageId() {
		return imageId;
	}
	
	public void setImageId(int id) {
		this.imageId = id;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	public boolean isChoose() {
		return choose;
	}
	
	public void setChoose(boolean choose) {
		this.choose = choose;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
