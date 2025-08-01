package com.x.cms.assemble.control.wrapin;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjian
 * @date 2025/07/31 15:38
 **/
public class WiSiteFileInfo extends GsonPropertyObject {

    public static final String TYPE_CMS = "cms";
    public static final String TYPE_PROCESS_PLATFORM = "processPlatform";

    @FieldDescribe("附件框分类.")
    private String site;

    @FieldDescribe("附件列表.")
    @FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment", fieldValue = "[{'id':'附件id','name':'附件名称'}]")
    private List<WiAttachment> fileInfoList = new ArrayList<>();

    @FieldDescribe("附件来源(cms或processPlatform).")
    private String type;

    public List<WiAttachment> getFileInfoList() {
        return fileInfoList;
    }

    public void setFileInfoList(List<WiAttachment> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
