package com.x.base.core.project.tools;

import java.io.File;
import java.util.*;

import com.x.base.core.project.gson.GsonPropertyObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class FileTools {

	public static String parent(String path) {
		int idx = StringUtils.lastIndexOfAny(path, new String[] { "\\", "/" });
		if (idx > 0) {
			return StringUtils.substring(path, 0, idx);
		} else {
			return path;
		}
	}

	/**
	 * 创建目录-递归父级
	 * @param dist
	 * @throws Exception
	 */
	public static void forceMkdir(File dist) throws Exception {
		if(!dist.exists()){
			File parent = dist.getParentFile();
			forceMkdir(parent);
			FileUtils.forceMkdir(dist);
		}
	}

	/**
	 * 获取文件夹下所有的文件 + 模糊查询（当不需要模糊查询时，queryStr传空或null即可）
	 * @param folderPath 路径
	 * @param queryStr 模糊查询字符串
	 * @return
	 */
	public static Map<String, List<FileInfo>> getFiles(String folderPath, String queryStr, String splitPath) {
		Map<String, List<FileInfo>> map = new HashMap<>();
		List<FileInfo> files = new ArrayList<>();
		List<FileInfo> folders = new ArrayList<>();
		File f = new File(folderPath);
		if (f.exists()) {
			if(!f.isDirectory()){ //路径为文件
				String path = f.getAbsolutePath().replaceAll("\\\\","/");
				if(StringUtils.isNotEmpty(splitPath) && f.getAbsolutePath().indexOf(splitPath) > -1){
					path = StringUtils.substringAfter(path, splitPath);
				}
				if(StringUtils.isEmpty(queryStr) || f.getName().indexOf(queryStr)!=-1) {
					files.add(new FileInfo(path, f.getName(), "file", f.lastModified()));
				}
			}else{ //路径为文件夹
				File fa[] = f.listFiles();
				for (int i = 0; i < fa.length; i++) {
					File fs = fa[i];
					String path = fs.getAbsolutePath().replaceAll("\\\\","/");
					if(StringUtils.isNotEmpty(splitPath) && path.indexOf(splitPath) > -1){
						path = StringUtils.substringAfter(path, splitPath);
					}
					if(StringUtils.isEmpty(queryStr) || fs.getName().indexOf(queryStr)!=-1){
						if (fs.isDirectory()) {
							folders.add(new FileInfo(path, fs.getName(), "folder", f.lastModified()));
						} else {
							files.add(new FileInfo(path, fs.getName(), "file", f.lastModified()));
						}
					}
				}
			}
		}
		map.put("files", files);
		map.put("folders", folders);
		return map;
	}

	public static File parent(File file) {
		return new File(parent(file.getAbsolutePath()));
	}

	public static class FileInfo extends GsonPropertyObject {

		private String filePath;

		private String fileName;

		private String fileType;

		private Date lastModifyTime;

		public FileInfo(){}

		public FileInfo(String filePath, String fileName, String fileType, long time){
			this.filePath = filePath;
			this.fileName = fileName;
			this.fileType = fileType;
			this.lastModifyTime = new Date(time);
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public Date getLastModifyTime() {
			return lastModifyTime;
		}

		public void setLastModifyTime(Date lastModifyTime) {
			this.lastModifyTime = lastModifyTime;
		}
	}

}
