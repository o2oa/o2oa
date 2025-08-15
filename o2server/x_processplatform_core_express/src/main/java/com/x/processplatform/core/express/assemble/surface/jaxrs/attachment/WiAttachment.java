package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Attachment;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengjian
 * @date 2025/08/13 17:55
 **/
public class WiAttachment extends GsonPropertyObject {

    private static final long serialVersionUID = 6570042412000311813L;

    public static final String COPY_FROM_CMS = "cms";
    public static final String COPY_FROM_DRAFT = "draft";
    public static final String COPY_FROM_PAN = "x_pan_assemble_control";

    public static WrapCopier<Attachment, WiAttachment> copier = WrapCopierFactory.wo(Attachment.class,
            WiAttachment.class, null,JpaObject.FieldsInvisible);

    @FieldDescribe("附件标识.")
    private String id;

    @FieldDescribe("附件名称.")
    private String name;

    @FieldDescribe("附件分类.")
    private String site;

    @FieldDescribe("是否软拷贝，默认false，true表示不拷贝真实存储附件，只拷贝路径，共用附件，仅支持流程附件.")
    private Boolean isSoftCopy;

    @FieldDescribe("附件来源(cms|内容管理附件、processPlatform|流程平台附件、pan|企业网盘附件，默认为processPlatform).")
    private String copyFrom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Boolean getSoftCopy() {
        return isSoftCopy;
    }

    public void setSoftCopy(Boolean softCopy) {
        isSoftCopy = softCopy;
    }

    public String getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }
}
