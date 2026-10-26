package com.x.cms.assemble.control.jaxrs.document;

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
import com.x.cms.core.entity.Document;
import com.x.cms.core.express.tools.filter.QueryFilter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class ActionQueryListWithFilterPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionQueryListWithFilterPaging.class);

    protected ActionResult<List<Wo>> execute(HttpServletRequest request, Integer page, Integer size,
            JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception{
        ActionResult<List<Wo>> result = new ActionResult<>();
        Long total = 0L;
        List<Wo> wos = new ArrayList<>();
        boolean isManager = false;
        String personName = effectivePerson.getDistinguishedName();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (wi == null) {
            wi = new Wi();
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

        if (effectivePerson.isManager() || userManagerService.isHasPlatformRole(
                effectivePerson.getDistinguishedName(), "CMSManager")) {
            isManager = true;
        }

        if (!BooleanUtils.isTrue(wi.getJustData())) {
            total = documentQueryService.countWithCondition(personName, queryFilter, false,
                    false, wi.getReadFlag(), isManager);
        }

        List<Document> searchResultList = documentQueryService.listPagingWithCondition(personName,
                wi.getOrderField(), wi.getOrderType(), queryFilter, page, size, false,
                false, wi.getReadFlag(), isManager);

        if (searchResultList != null) {
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
                    if (wo.getCreatorTopUnitName() != null && !wo.getCreatorTopUnitName()
                            .isEmpty()) {
                        wo.setCreatorTopUnitNameShort(wo.getCreatorTopUnitName().split("@")[0]);
                    }
                    if (StringUtils.isNoneBlank(document.getIndexPics())) {
                        wo.setPictureList(ListTools.toList(document.getIndexPics().split(",")));
                    }
                    if (BooleanUtils.isTrue(wi.getNeedData())) {
                        //需要组装数据
                        wo.setData(documentQueryService.getDocumentData(document));
                    }
                    Long count = documentViewRecordServiceAdv.countWithDocIdAndPerson(
                            wo.getId(), effectivePerson.getDistinguishedName());
                    if (count != null && count > 0) {
                        wo.setHasRead(true);
                    }
                } catch (Exception e) {
                    Exception exception = new ExceptionDocumentInfoProcess(e,
                            "系统获取文档数据内容信息时发生异常。Id:"
                                    + document.getCategoryId());
                    result.error(exception);
                    logger.error(e, effectivePerson, request, null);
                }
                wos.add(wo);
            }
        }
        result.setCount(total);
        result.setData(wos);
        return result;
    }

    public static class Wi extends WrapInDocumentFilter {

        @FieldDescribe("仅返回数据不查询总数，默认false")
        private Boolean justData;

        public Boolean getJustData() {
            return justData;
        }

        public void setJustData(Boolean justData) {
            this.justData = justData;
        }
    }

    public static class Wo extends WrapOutDocumentList {

        public static final WrapCopier<Document, Wo> copier = WrapCopierFactory.wo(Document.class,
                Wo.class, null, JpaObject.FieldsInvisible);

    }
}
