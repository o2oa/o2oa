package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

public class DumpRestoreData extends ConfigObject {

	public static DumpRestoreData defaultInstance() {
		return new DumpRestoreData();
	}

	public static final int default_batchSize = 1000;

	public DumpRestoreData() {
		this.enable = false;
		this.includes = new ArrayList<String>();
		this.excludes = new ArrayList<String>();
		this.batchSize = default_batchSize;
	}

	@FieldDescribe("是否启用.")
	private Boolean enable;

	@FieldDescribe("导出导入包含对象,可以使用通配符*.")
	private List<String> includes;

	@FieldDescribe("导出导入排除对象,可以使用通配符*.")
	private List<String> excludes;

	@FieldDescribe("批量对象数量.")
	private Integer batchSize;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public List<String> getIncludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(includes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public List<String> getExcludes() {
		List<String> list = new ArrayList<>();
		for (String str : ListTools.trim(excludes, true, true)) {
			list.add(str);
		}
		return list;
	}

	public Integer getBatchSize() {
		if ((null == this.batchSize) || (this.batchSize < 1)) {
			return 2000;
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

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
