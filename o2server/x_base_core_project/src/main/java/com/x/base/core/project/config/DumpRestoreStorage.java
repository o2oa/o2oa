package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class DumpRestoreStorage extends GsonPropertyObject {

	public static DumpRestoreStorage defaultInstance() {
		return new DumpRestoreStorage();
	}

	public static final int default_batchSize = 100;

	public DumpRestoreStorage() {
		this.includes = new ArrayList<String>();
		this.excludes = new ArrayList<String>();
		this.batchSize = default_batchSize;
		this.redistribute = true;
		this.exceptionInvalidStorage = true;
	}

	@FieldDescribe("是否启用.")
	private Boolean enable;

	@FieldDescribe("导出导入包含对象,可以使用通配符*.")
	private List<String> includes;

	@FieldDescribe("导出导入排除对象,可以使用通配符*.")
	private List<String> excludes;

	@FieldDescribe("批量对象数量.")
	private Integer batchSize;

	@FieldDescribe("是否进行重新分布.")
	private Boolean redistribute;

	@FieldDescribe("无法获取storage是否升起错误.")
	private Boolean exceptionInvalidStorage;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public List<String> getIncludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(this.includes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public List<String> getExcludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(this.excludes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public Boolean getRedistribute() {
		return BooleanUtils.isNotFalse(this.redistribute);
	}

	/** 默认为true */
	public Boolean getExceptionInvalidStorage() {
		return BooleanUtils.isNotFalse(this.exceptionInvalidStorage);
	}

	public Integer getBatchSize() {
		if ((null == this.batchSize) || (this.batchSize < 1)) {
			return 500;
		}
		return this.batchSize;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setRedistribute(Boolean redistribute) {
		this.redistribute = redistribute;
	}

	public void setExceptionInvalidStorage(Boolean exceptionInvalidStorage) {
		this.exceptionInvalidStorage = exceptionInvalidStorage;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
