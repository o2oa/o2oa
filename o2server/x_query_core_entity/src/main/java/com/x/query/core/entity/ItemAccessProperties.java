package com.x.query.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 业务字段访问权限扩展配置
 *
 * @author sword
 */
public class ItemAccessProperties extends JsonProperties {

    private static final long serialVersionUID = -1259157593040432239L;

    @FieldDescribe("可查看对象DN列表：人员、组织、群组、角色.")
    private List<String> readerList;

    @FieldDescribe("不可查看对象DN列表：人员、组织、群组、角色.")
    private List<String> excludeReaderList;

    @FieldDescribe("可查看流程活动列表.")
    private List<ItemAccessActivity> readActivityList;

    @FieldDescribe("不可查看流程活动列表.")
    private List<ItemAccessActivity> excludeReadActivityList;

    @FieldDescribe("可编辑对象DN列表：人员、组织、群组、角色.")
    private List<String> editorList;

    @FieldDescribe("不可编辑对象DN列表：人员、组织、群组、角色.")
    private List<String> excludeEditorList;

    @FieldDescribe("可编辑流程活动列表.")
    private List<ItemAccessActivity> editActivityList;

    @FieldDescribe("不可编辑流程活动列表.")
    private List<ItemAccessActivity> excludeEditActivityList;

    @FieldDescribe("扩展信息.")
    private String extension;

    public List<String> getReaderList() {
        return readerList == null ? Collections.emptyList() : readerList;
    }

	public List<String> getReaderAndEditorList() {
		if(!getReaderList().isEmpty()){
			return Stream.concat(getReaderList().stream(), getEditorList().stream())
					.distinct().collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

    public void setReaderList(List<String> readerList) {
        this.readerList = readerList;
    }

    public List<ItemAccessActivity> getReadActivityList() {
        return readActivityList == null ? Collections.emptyList() : readActivityList;
    }

    public void setReadActivityList(
            List<ItemAccessActivity> readActivityList) {
        this.readActivityList = readActivityList;
    }

    public List<String> getReadActivityIdList() {
        if (!getReadActivityList().isEmpty()) {
            return Stream.concat(getReadActivityList().stream(), getEditActivityList().stream())
                    .map(ItemAccessActivity::getUnique).distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<String> getEditorList() {
        return editorList == null ? Collections.emptyList() : editorList;
    }

    public void setEditorList(List<String> editorList) {
        this.editorList = editorList;
    }

    public List<ItemAccessActivity> getEditActivityList() {
        return editActivityList == null ? Collections.emptyList() : editActivityList;
    }

    public List<String> getEditActivityIdList() {
        return getEditActivityList().stream().map(ItemAccessActivity::getUnique).distinct().collect(
                Collectors.toList());
    }

    public void setEditActivityList(
            List<ItemAccessActivity> editActivityList) {
        this.editActivityList = editActivityList;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<String> getExcludeReaderList() {
        return excludeReaderList == null ? Collections.emptyList() : excludeReaderList;
    }

    public List<String> getExcludeReaderListExcludeEditor() {
        if(CollectionUtils.isNotEmpty(excludeReaderList)){
            List<String> editorList2 = new ArrayList<>(getEditorList());
            editorList2.removeAll(getExcludeEditorList());
            return excludeReaderList.stream().filter(o -> !editorList2.contains(o))
                    .distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void setExcludeReaderList(List<String> excludeReaderList) {
        this.excludeReaderList = excludeReaderList;
    }

    public List<ItemAccessActivity> getExcludeReadActivityList() {
        return excludeReadActivityList == null ? Collections.emptyList() : excludeReadActivityList;
    }

    public List<String> getExcludeReadActivityIdList() {
        if(!getExcludeReadActivityList().isEmpty()){
            List<String> excludeEditActivityIdList = getExcludeEditActivityList().stream()
                    .map(ItemAccessActivity::getUnique).collect(Collectors.toList());
            List<String> editActivityIdList = getEditActivityList().stream()
                    .map(ItemAccessActivity::getUnique).filter(o -> !excludeEditActivityIdList.contains(o))
                    .collect(Collectors.toList());
            return getExcludeReadActivityList().stream()
                    .map(ItemAccessActivity::getUnique).filter(o -> !editActivityIdList.contains(o))
                    .distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void setExcludeReadActivityList(
            List<ItemAccessActivity> excludeReadActivityList) {
        this.excludeReadActivityList = excludeReadActivityList;
    }

    public List<String> getExcludeEditorList() {
        return excludeEditorList == null ? Collections.emptyList() : excludeEditorList;
    }

    public void setExcludeEditorList(List<String> excludeEditorList) {
        this.excludeEditorList = excludeEditorList;
    }

    public List<ItemAccessActivity> getExcludeEditActivityList() {
        return excludeEditActivityList == null ? Collections.emptyList() : excludeEditActivityList;
    }

    public List<String> getExcludeEditActivityIdList() {
        return getExcludeEditActivityList().stream().map(ItemAccessActivity::getUnique).distinct().collect(
                Collectors.toList());
    }

    public void setExcludeEditActivityList(
            List<ItemAccessActivity> excludeEditActivityList) {
        this.excludeEditActivityList = excludeEditActivityList;
    }
}
