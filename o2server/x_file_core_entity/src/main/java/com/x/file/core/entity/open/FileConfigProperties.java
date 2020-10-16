package com.x.file.core.entity.open;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.ArrayList;
import java.util.List;

public class FileConfigProperties extends JsonProperties {

	private static final long serialVersionUID = -1259157593040432239L;

	@FieldDescribe("只允许上传的文件后缀")
	private List<String> fileTypeIncludes = new ArrayList<>();

	@FieldDescribe("不允许上传的文件后缀")
	private List<String> fileTypeExcludes = new ArrayList<>();

	public List<String> getFileTypeIncludes() {
		return fileTypeIncludes;
	}

	public void setFileTypeIncludes(List<String> fileTypeIncludes) {
		this.fileTypeIncludes = fileTypeIncludes;
	}

	public List<String> getFileTypeExcludes() {
		return fileTypeExcludes;
	}

	public void setFileTypeExcludes(List<String> fileTypeExcludes) {
		this.fileTypeExcludes = fileTypeExcludes;
	}
}
