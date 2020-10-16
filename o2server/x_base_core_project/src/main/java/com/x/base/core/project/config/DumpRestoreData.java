package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

public class DumpRestoreData extends ConfigObject {

	private static final long serialVersionUID = 8910820385137391619L;

	public static DumpRestoreData defaultInstance() {
		return new DumpRestoreData();
	}

	public static final String TYPE_FULL = "full";
	public static final String TYPE_LITE = "lite";
	public static final String RESTOREOVERRIDE_CLEAN = "clean";
	public static final String RESTOREOVERRIDE_SKIPEXISTED = "skipExisted";

	public static final String DEFAULT_TYPE = TYPE_LITE;
	public static final Boolean DEFAULT_PARALLEL = true;
	public static final Boolean DEFAULT_REDISTRIBUTE = true;
	public static final Boolean DEFAULT_EXCEPTIONINVALIDSTORAGE = true;
	public static final String DEFAULT_ITEMCATEGORY = "";

	public DumpRestoreData() {
		this.enable = false;
		this.includes = new ArrayList<>();
		this.excludes = new ArrayList<>();
		this.mode = DEFAULT_TYPE;
		this.parallel = DEFAULT_PARALLEL;
		this.redistribute = DEFAULT_REDISTRIBUTE;
		this.exceptionInvalidStorage = DEFAULT_EXCEPTIONINVALIDSTORAGE;
		this.itemCategory = DEFAULT_ITEMCATEGORY;
	}

	@FieldDescribe("是否启用.")
	private Boolean enable;

	@FieldDescribe("导出导入包含对象,可以使用通配符*.")
	private List<String> includes;

	@FieldDescribe("导出导入排除对象,可以使用通配符*.")
	private List<String> excludes;

	@FieldDescribe("导出数据模式,lite|full,默认使用lite")
	private String mode;

	@FieldDescribe("使用并行导出,默认true")
	private Boolean parallel;

	@FieldDescribe("是否进行重新分布.")
	private Boolean redistribute;

	@FieldDescribe("无法获取storage是否升起错误.")
	private Boolean exceptionInvalidStorage;

	@FieldDescribe("数据导入方式,clean:清空重新导入,skipExisted:如果有相同id的数据跳过.默认方式为clean.")
	private String restoreOverride;

	@FieldDescribe("对于com.x.query.core.entity.Item的itemCategory进行单独过滤,可选值pp, cms, bbs, pp_dict.")
	private String itemCategory;

	public String getItemCategory() {
		return this.itemCategory;
	}

	public Boolean getRedistribute() {
		return BooleanUtils.isNotFalse(redistribute);
	}

	public Boolean getExceptionInvalidStorage() {
		return BooleanUtils.isNotFalse(exceptionInvalidStorage);
	}

	public Boolean getParallel() {
		return BooleanUtils.isNotFalse(parallel);
	}

	public String getMode() {
		return StringUtils.equals(TYPE_FULL, mode) ? TYPE_FULL : TYPE_LITE;
	}

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

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setParallel(Boolean parallel) {
		this.parallel = parallel;
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

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRestoreOverride() {
		return restoreOverride;
	}

	public void setRestoreOverride(String restoreOverride) {
		this.restoreOverride = restoreOverride;
	}

}
