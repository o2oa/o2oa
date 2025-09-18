package com.x.processplatform.assemble.surface.jaxrs.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.core.entity.ItemAccess;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;

class ActionGetWorkOrWorkCompleted extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionGetWorkOrWorkCompleted.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted)
            throws Exception {

        LOGGER.debug("execute:{}, workOrWorkCompleted:{}.", effectivePerson::getDistinguishedName,
                () -> workOrWorkCompleted);

        ActionResult<Wo> result = new ActionResult<>();
        Control ctrl = new Control();
        String process = "";
        String application = "";
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Work work = emc.find(workOrWorkCompleted, Work.class);
            if (null != work) {
                ctrl = new WorkControlBuilder(effectivePerson, business, work).enableAll().build();
                process = work.getProcess();
                application = work.getApplication();
            } else {
                WorkCompleted workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
                if (null != workCompleted) {
                    ctrl = new WorkCompletedControlBuilder(effectivePerson, business,
                            workCompleted).enableAll()
                            .build();
                    process = workCompleted.getProcess();
                    application = workCompleted.getApplication();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        if (BooleanUtils.isFalse(ctrl.getAllowVisit())) {
            throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
        }
        Wo wo = Wo.copier.copy(ctrl);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            wo.setItemAccessList(WoItemAccess.copier.copy(
                    business.itemAccess().listWithProcessOrApp(process, application)));
        }
        result.setData(wo);
        return result;
    }

    public static class Wo extends Control {

        private static final long serialVersionUID = 6785048441528902022L;

        static WrapCopier<Control, Wo> copier = WrapCopierFactory.wo(Control.class, Wo.class, null,
                null);

        @FieldDescribe("字段权限配置列表")
        private List<WoItemAccess> itemAccessList;

        public List<WoItemAccess> getItemAccessList() {
            return itemAccessList;
        }

        public void setItemAccessList(List<WoItemAccess> itemAccessList) {
            this.itemAccessList = itemAccessList;
        }
    }

    public static class WoItemAccess extends ItemAccess {

        public static final WrapCopier<ItemAccess, WoItemAccess> copier = WrapCopierFactory.wo(
                ItemAccess.class, WoItemAccess.class, null,
                ListTools.toList(JpaObject.FieldsInvisibleIncludeProperites));
    }

}
