package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.RecordBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWo;

class V2AddManualTaskIdentityMatrix extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2AddManualTaskIdentityMatrix.class);

    // 当前提交的串号
    private final String series = StringTools.uniqueToken();
    private Wi wi;
    // 当前执行用户
    private EffectivePerson effectivePerson;
    // 根据输入得到的待办
    private Work work = null;
    // 指定的身份
    private String identity = null;
    // 工作记录
    private WorkLog workLog;
    // work活动
    private Manual manual;
    // 返回的ManualTaskIdentityMatrix
    private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
                    () -> jsonElement);
        }
        this.init(effectivePerson, id, jsonElement);

        this.manualTaskIdentityMatrix = add(wi.getOptionList(), wi.getRemove());

        this.processingWork(work);

        List<String> newTaskIds = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            newTaskIds.addAll(emc.idsEqualAndEqual(Task.class, Task.job_FIELDNAME, work.getJob(), Task.work_FIELDNAME,
                    work.getId()));
        }

        Record rec = RecordBuilder.ofWorkProcessing(Record.TYPE_RESET, workLog, effectivePerson, manual, newTaskIds);
        RecordBuilder.processing(rec);

        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = Wo.copier.copy(rec);
        wo.setManualTaskIdentityMatrix(this.manualTaskIdentityMatrix);
        result.setData(wo);
        return result;

    }

    private void init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            this.effectivePerson = effectivePerson;
            this.wi = this.convertToWrapIn(jsonElement, Wi.class);
            this.work = emc.find(id, Work.class);
            if (null == work) {
                throw new ExceptionEntityNotExist(id, Work.class);
            }
            this.workLog = business.entityManagerContainer().firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
                    work.getJob(), WorkLog.FROMACTIVITYTOKEN_FIELDNAME, work.getActivityToken());
            if (null == workLog) {
                throw new ExceptionEntityNotExist(WorkLog.class);
            }
            WoControl control = business.getControl(effectivePerson, work, WoControl.class);
            if (BooleanUtils.isNotTrue(control.getAllowReset())) {
                throw new ExceptionAccessDenied(effectivePerson, work);
            }
            this.manual = (Manual) business.getActivity(work.getActivity(), ActivityType.manual);
            this.identity = business.organization().identity().get(wi.getIdentity());
        }
    }

    private ManualTaskIdentityMatrix add(List<V2AddManualTaskIdentityMatrixWi.Option> options, Boolean remove)
            throws Exception {
        V2AddManualTaskIdentityMatrixWi req = new V2AddManualTaskIdentityMatrixWi();
        req.setIdentity(identity);
        req.setOptionList(options);
        req.setRemove(remove);
        return ThisApplication.context().applications()
                .postQuery(x_processplatform_service_processing.class,
                        Applications.joinQueryUri("work", "v2", work.getId(), "add", "manual", "task", "identity",
                                "matrix"),
                        req, work.getJob())
                .getData(V2AddManualTaskIdentityMatrixWo.class).getManualTaskIdentityMatrix();
    }

    private void processingWork(Work work) throws Exception {
        ProcessingAttributes req = new ProcessingAttributes();
        req.setType(ProcessingAttributes.TYPE_TASKADD);
        req.setSeries(this.series);
        WoId resp = ThisApplication.context().applications()
                .putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
                        Applications.joinQueryUri("work", work.getId(), "processing"), req, work.getJob())
                .getData(WoId.class);
        if (StringUtils.isEmpty(resp.getId())) {
            throw new ExceptionProcessingWork(work.getId());
        }
    }

    public static class Wi extends V2AddManualTaskIdentityMatrixWi {

        private static final long serialVersionUID = -6251874269093504136L;

    }

    public static class WoControl extends WorkControl {

        private static final long serialVersionUID = -8675239528577375846L;

    }

    public static class Wo extends Record {

        private static final long serialVersionUID = 242446941132286179L;

        static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
                JpaObject.FieldsInvisible);

        private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

        public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
            return manualTaskIdentityMatrix;
        }

        public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
            this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
        }
    }

}