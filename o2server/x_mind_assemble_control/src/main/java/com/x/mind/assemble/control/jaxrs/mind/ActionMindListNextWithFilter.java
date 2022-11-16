package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindFolderQuery;
import com.x.mind.entity.MindBaseInfo;

/**
 * 查询脑图信息（下一步）
 *
 * @author O2LEE
 */
public class ActionMindListNextWithFilter extends BaseAction {

    private Logger logger = LoggerFactory.getLogger(ActionMindListNextWithFilter.class);

    protected ActionResult<List<Wo>> execute(HttpServletRequest request, String id, Integer count, JsonElement jsonElement, EffectivePerson effectivePerson) {
        ActionResult<List<Wo>> result = new ActionResult<>();
        Wi wi = null;
        List<Wo> wraps;
        List<MindBaseInfo> mindBaseInfos = null;
        try {
            wi = this.convertToWrapIn(jsonElement, Wi.class);
        } catch (Exception e) {
            Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null ? "None" : XGsonBuilder.instance().toJson(jsonElement));
            result.error(exception);
            logger.error(e, effectivePerson, request, null);
            return result;
        }
        if (StringUtils.isEmpty(wi.getCreator())) {
            wi.setCreator(effectivePerson.getDistinguishedName());
        }
        try {
            mindBaseInfos = mindInfoService.listNextPageWithFilter(id, count, wi.getKey(), wi.getFolderId(), wi.getShared(),
                    wi.getCreator(), wi.getCreatorUnit(), wi.getSharePersons(), wi.getShareUnits(), wi.getShareGroups(), wi.getOrderField(), wi.getOrderType(), null);
        } catch (Exception e) {
            Exception exception = new ExceptionMindFolderQuery(e, "系统在根据个人名称查询所有的脑图文件夹信息ID列表时发生异常。");
            result.error(exception);
            logger.error(e, effectivePerson, request, null);
            return result;
        }

        try {
            if (mindBaseInfos != null && ListTools.isNotEmpty(mindBaseInfos)) {
                wraps = Wo.copier.copy(mindBaseInfos);
            } else {
                wraps = new ArrayList<>();
            }
            result.setData(wraps);
            result.setCount(Long.parseLong(wraps.size() + ""));
            return result;
        } catch (Exception e) {
            Exception exception = new ExceptionMindFolderQuery(e, "系统在转换输出内容时发生异常。");
            result.error(exception);
            logger.error(e, effectivePerson, request, null);
            return result;
        }


    }

    public static class Wi {

        @FieldDescribe("模糊搜索")
        private String key = "";

        @FieldDescribe("所属目录")
        private String folderId = "";

        @FieldDescribe("备注信息")
        private String description = "";

        @FieldDescribe("创建者")
        private String creator = "";

        @FieldDescribe("创建者所属组织")
        private String creatorUnit = "";

        @FieldDescribe("共享者列表")
        private List<String> sharePersons = null;

        @FieldDescribe("共享组织列表")
        private List<String> shareUnits = null;

        @FieldDescribe("共享角色列表")
        private List<String> shareGroups = null;

        @FieldDescribe("是否已经分享")
        private Boolean shared = null;

        @FieldDescribe("排序列：默认为sequence")
        String orderField = JpaObject.sequence_FIELDNAME;

        @FieldDescribe("排序方式：DESC|ASC， 默认为DESC")
        String orderType = "DESC";

        public String getFolderId() {
            return folderId;
        }

        public String getDescription() {
            return description;
        }

        public String getCreator() {
            return creator;
        }

        public String getCreatorUnit() {
            return creatorUnit;
        }

        public Boolean getShared() {
            return shared;
        }

        public void setFolderId(String folderId) {
            this.folderId = folderId;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public void setCreatorUnit(String creatorUnit) {
            this.creatorUnit = creatorUnit;
        }

        public void setShared(Boolean shared) {
            this.shared = shared;
        }

        public String getOrderField() {
            return orderField;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderField(String orderField) {
            this.orderField = orderField;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public List<String> getSharePersons() {
            return sharePersons;
        }

        public List<String> getShareUnits() {
            return shareUnits;
        }

        public List<String> getShareGroups() {
            return shareGroups;
        }

        public void setSharePersons(List<String> sharePersons) {
            this.sharePersons = sharePersons;
        }

        public void setShareUnits(List<String> shareUnits) {
            this.shareUnits = shareUnits;
        }

        public void setShareGroups(List<String> shareGroups) {
            this.shareGroups = shareGroups;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    public static class Wo extends MindBaseInfo {
        private static final long serialVersionUID = -5076990764713538973L;
        public static List<String> Excludes = new ArrayList<String>();
        public static WrapCopier<MindBaseInfo, Wo> copier = WrapCopierFactory.wo(MindBaseInfo.class, Wo.class, null, Wo.Excludes);
    }
}