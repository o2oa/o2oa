package jiguang.chat.pickerimage.model;

import java.io.Serializable;
import java.util.List;

public class AlbumInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int imageID;
	private String pathAbsolute;
	private String pathFile;
	private String nameAlbum;
	private List<PhotoInfo> list;
	
	public int getImageId() {
		return imageID;
	}
	
	public void setImageId(int imageID) {
		this.imageID = imageID;
	}
	
	public String getAbsolutePath() {
		return pathAbsolute;
	}
	
	public void setAbsolutePath(String pathAbsolute) {
		this.pathAbsolute = pathAbsolute;
	}
	
	public String getFilePath() {
		return pathFile;
	}
	
	public void setFilePath(String pathFile) {
		this.pathFile = pathFile;
	}
	
	public String getAlbumName() {
		return nameAlbum;
	}
	
	public void setAlbumName(String nameAlbum) {
		this.nameAlbum = nameAlbum;
	}
	
	public List<PhotoInfo> getList() {
		return list;
	}
	
	public void setList(List<PhotoInfo> list) {
		this.list = list;
	}
}
