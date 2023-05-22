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

    public static final String MODE_FULL = "full";
    public static final String MODE_LITE = "lite";
    public static final String RESTOREOVERRIDE_CLEAN = "clean";
    public static final String RESTOREOVERRIDE_SKIPEXISTED = "skipExisted";

    public static final Boolean DEFAULT_EXCEPTIONINVALIDSTORAGE = false;
    public static final Boolean DEFAULT_ATTACHSTORAGE = true;
    public static final String DEFAULT_ITEMCATEGORY = "";
    public static final Boolean DEFAULT_PARALLEL = true;
    public static final Boolean DEFAULT_REDISTRIBUTESTORAGE = false;

    public DumpRestoreData() {
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.mode = MODE_LITE;
        this.attachStorage = DEFAULT_ATTACHSTORAGE;
        this.exceptionInvalidStorage = DEFAULT_EXCEPTIONINVALIDSTORAGE;
        this.itemCategory = DEFAULT_ITEMCATEGORY;
        this.parallel = DEFAULT_PARALLEL;
        this.redistributeStorage = DEFAULT_REDISTRIBUTESTORAGE;
        this.restoreOverride = RESTOREOVERRIDE_CLEAN;
    }

    @FieldDescribe("导出导入包含对象,可以使用通配符*,如仅导出待办数据:com.x.processplatform.core.entity.content.Task.")
    private List<String> includes;

    @FieldDescribe("导出导入排除对象,可以使用通配符*,如不导出流程实例数据com.x.processplatform.core.entity.content.*")
    private List<String> excludes;

    @FieldDescribe("导出数据模式,lite|full,默认使用lite")
    private String mode;

    @FieldDescribe("无法获取storage是否升起错误.")
    private Boolean exceptionInvalidStorage;

    @FieldDescribe("是否同时导入导出storage中存储的文件.")
    private Boolean attachStorage;

    @FieldDescribe("是否进行重新分布.")
    private Boolean redistributeStorage;

    @FieldDescribe("使用并行导出,默认true")
    private Boolean parallel;

    @FieldDescribe("数据导入方式,clean:清空重新导入,skipExisted:如果有相同id的数据跳过.默认方式为clean.")
    private String restoreOverride;

    @FieldDescribe("对于com.x.query.core.entity.Item的itemCategory进行单独过滤,可选值pp, cms, bbs, pp_dict.")
    private String itemCategory;

    public Boolean getParallel() {
        return BooleanUtils.isNotFalse(parallel);
    }

    public Boolean getRedistributeStorage() {
        return BooleanUtils.isTrue(redistributeStorage);
    }

    public void setRedistributeStorage(Boolean redistributeStorage) {
        this.redistributeStorage = redistributeStorage;
    }

    public Boolean getAttachStorage() {
        return attachStorage;
    }

    public void setAttachStorage(Boolean attachStorage) {
        this.attachStorage = attachStorage;
    }

    public String getItemCategory() {
        return this.itemCategory;
    }

    public Boolean getExceptionInvalidStorage() {
        return BooleanUtils.isNotFalse(exceptionInvalidStorage);
    }

    public String getMode() {
        return StringUtils.equals(MODE_FULL, mode) ? MODE_FULL : MODE_LITE;
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

    public void setExceptionInvalidStorage(Boolean exceptionInvalidStorage) {
        this.exceptionInvalidStorage = exceptionInvalidStorage;
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
