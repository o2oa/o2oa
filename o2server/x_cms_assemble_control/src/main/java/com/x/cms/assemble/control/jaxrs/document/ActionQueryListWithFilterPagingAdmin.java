package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.express.tools.filter.QueryFilter;

/**
 * 管理员用
 */
public class ActionQueryListWithFilterPagingAdmin extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(
            ActionQueryListWithFilterPagingAdmin.class);

    protected ActionResult<List<Wo>> execute(HttpServletRequest request, Integer page, Integer size,
            JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        Business business = new Business(null);
        if (!business.isManager(effectivePerson)) {
            result.setCount(0L);
            result.setData(wos);
            return result;
        }

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        if (StringUtils.isEmpty(wi.getDocumentType())) {
            wi.setDocumentType("信息");
        }

        if (StringUtils.isEmpty(wi.getOrderField())) {
            wi.setOrderField("publishTime");
        }

        if (StringUtils.isEmpty(wi.getOrderType())) {
            wi.setOrderType("DESC");
        }

        if (ListTools.isEmpty(wi.getStatusList())) {
            List<String> status = new ArrayList<>();
            status.add("published");
            wi.setStatusList(status);
        }

        QueryFilter queryFilter = wi.getQueryFilter();
        String personName = wi.getPerson();
        if (StringUtils.isNotBlank(wi.getPerson())) {
            personName = business.organization().person().get(wi.getPerson());
            if (StringUtils.isBlank(personName)) {
                personName = wi.getPerson();
            }
        }
        Long total = 0L;
        if (!BooleanUtils.isTrue(wi.getJustData())) {
            total = documentQueryService.countWithCondition(personName, queryFilter, wi.getAuthor(),
                    wi.getExcludeAllRead(), wi.getReadFlag(), true);
        }
        List<Document> searchResultList = documentQueryService.listPagingWithCondition(personName,
                wi.getOrderField(), wi.getOrderType(), queryFilter, page, size, wi.getAuthor(),
                wi.getExcludeAllRead(), wi.getReadFlag(), true);
        Wo wo = null;
        for (Document document : searchResultList) {
            try {
                wo = Wo.copier.copy(document);
                if (wo.getCreatorPerson() != null && !wo.getCreatorPerson().isEmpty()) {
                    wo.setCreatorPersonShort(wo.getCreatorPerson().split("@")[0]);
                }
                if (wo.getCreatorUnitName() != null && !wo.getCreatorUnitName().isEmpty()) {
                    wo.setCreatorUnitNameShort(wo.getCreatorUnitName().split("@")[0]);
                }
                if (wo.getCreatorTopUnitName() != null && !wo.getCreatorTopUnitName().isEmpty()) {
                    wo.setCreatorTopUnitNameShort(wo.getCreatorTopUnitName().split("@")[0]);
                }
                if (StringUtils.isNoneBlank(document.getIndexPics())) {
                    wo.setPictureList(ListTools.toList(document.getIndexPics().split(",")));
                }
                if (wi.getNeedData()) {
                    //需要组装数据
                    wo.setData(documentQueryService.getDocumentData(document));
                }
                Long count = documentViewRecordServiceAdv.countWithDocIdAndPerson(wo.getId(),
                        personName);
                if (count != null && count > 0) {
                    wo.setHasRead(true);
                }
            } catch (Exception e) {
                logger.error(e, effectivePerson, request, null);
            }
            wos.add(wo);
        }

        result.setCount(total);
        result.setData(wos);
        return result;
    }

    public static class Wi extends WrapInDocumentFilter {

        @FieldDescribe("查询指定用户可阅读的文档")
        private String person;

        @FieldDescribe("是否查询指定用户可编辑的文档，如果为true则person字段必填，默认为否")
        private Boolean isAuthor;

        @FieldDescribe("仅返回数据不查询总数，默认false")
        private Boolean justData;

        @FieldDescribe("是否排除全员可读文档，默认false")
        private Boolean excludeAllRead;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public Boolean getAuthor() {
            return isAuthor;
        }

        public void setAuthor(Boolean author) {
            isAuthor = author;
        }

        public Boolean getJustData() {
            return justData;
        }

        public void setJustData(Boolean justData) {
            this.justData = justData;
        }

        public Boolean getExcludeAllRead() {
            return excludeAllRead;
        }

        public void setExcludeAllRead(Boolean excludeAllRead) {
            this.excludeAllRead = excludeAllRead;
        }
    }

    public static class Wo extends WrapOutDocumentList {

        public static List<String> excludes = new ArrayList<String>();

        public static final WrapCopier<Document, Wo> copier = WrapCopierFactory.wo(Document.class,
                Wo.class, null, JpaObject.FieldsInvisible);

    }
}
