package com.x.query.assemble.surface.jaxrs.statement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;

class ActionListWithQuery extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithQuery.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String queryFlag, JsonElement jsonElement)
            throws Exception {

        LOGGER.debug("execute:{}, queryFlag:{}.", effectivePerson::getDistinguishedName, () -> queryFlag);
        ClassLoader classLoader = Business.getDynamicEntityClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Query query = emc.flag(queryFlag, Query.class);
            if (null == query) {
                throw new ExceptionEntityNotExist(queryFlag);
            }
            List<Wo> wos = new ArrayList<>();
            for (String id : emc.idsEqual(Statement.class, Statement.QUERY_FIELDNAME, query.getId())) {
                Statement o = business.pick(id, Statement.class);
                if (null != o) {
                    if (BooleanUtils.isTrue(wi.getHasView()) && StringUtils.isBlank(o.getView())) {
                        continue;
                    }
//                    if (BooleanUtils.isTrue(wi.getJustSelect()) && !Statement.TYPE_SELECT.equals(o.getType())) {
//                        continue;
//                    }
                    if (business.readable(effectivePerson, o)) {
                        wos.add(Wo.copier.copy(o));
                    }
                }
            }
            SortTools.asc(wos, View.orderNumber_FIELDNAME);
            result.setData(wos);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

//        @FieldDescribe("是否只查询select语句.")
//        private Boolean justSelect;
        @FieldDescribe("是否只查询含有视图的语句.")
        private Boolean hasView;

//        public Boolean getJustSelect() {
//            return justSelect;
//        }
//
//        public void setJustSelect(Boolean justSelect) {
//            this.justSelect = justSelect;
//        }

        public Boolean getHasView() {
            return hasView;
        }

        public void setHasView(Boolean hasView) {
            this.hasView = hasView;
        }
    }

    public static class Wo extends Statement {

        private static final long serialVersionUID = -5755898083219447939L;

        static WrapCopier<Statement, Wo> copier = WrapCopierFactory.wo(Statement.class, Wo.class,
                JpaObject.singularAttributeField(Statement.class, true, true), JpaObject.FieldsInvisible);
    }
}
