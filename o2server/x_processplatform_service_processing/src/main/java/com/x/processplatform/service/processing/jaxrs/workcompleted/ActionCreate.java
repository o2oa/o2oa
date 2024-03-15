package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;
import com.x.processplatform.core.entity.element.util.ProjectionFactory;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.query.core.entity.Item;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String processId, JsonElement jsonElement)
            throws Exception {

        LOGGER.debug("execute:{}, processId:{}.", effectivePerson::getDistinguishedName, () -> processId);

        String executorSeed = processId;
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(new CallableImpl(wi, processId)).get(300,
                TimeUnit.SECONDS);
    }

    public class CallableImpl implements Callable<ActionResult<Wo>> {

        private String processId;

        private Wi wi;

        private CallableImpl(Wi wi, String processId) {
            this.wi = wi;
            this.processId = processId;
        }

        public ActionResult<Wo> call() throws Exception {

            ActionResult<Wo> result = new ActionResult<>();

            Process process;

            WorkCompleted workCompleted;

            Data data;

            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

                Business business = new Business(emc);

                process = business.element().get(processId, Process.class);
                if (null == process) {
                    throw new ExceptionEntityNotExist(processId, Process.class);
                }

                Application application = business.element().get(process.getApplication(), Application.class);
                if (null == application) {
                    throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
                }

                String identity = business.organization().identity().get(wi.getIdentity());
                if (StringUtils.isEmpty(identity)) {
                    throw new ExceptionIdentityNotExist(wi.getIdentity());
                }
                String person = business.organization().person().getWithIdentity(identity);
                String unit = business.organization().unit().getWithIdentity(identity);
                Unit unitObject = business.organization().unit().getObject(unit);

                emc.beginTransaction(Item.class);
                emc.beginTransaction(WorkCompleted.class);
                Date now = new Date();
                workCompleted = new WorkCompleted();
                workCompleted.setApplication(application.getId());
                workCompleted.setApplicationAlias(application.getAlias());
                workCompleted.setApplicationName(application.getName());
                workCompleted.setCompletedTime((wi.getCompletedTime() == null) ? now : wi.getCompletedTime());
                workCompleted.setCreatorIdentity(identity);
                workCompleted.setCreatorPerson(person);
                workCompleted.setCreatorUnit(unit);
                workCompleted.setCreatorUnitLevelName(unitObject.getLevelName());
                workCompleted.setDuration(0L);
                workCompleted.setExpired(false);
                workCompleted.setExpireTime(null);
                workCompleted.setForm(wi.getForm());
                workCompleted.setJob(StringTools.uniqueToken());
                workCompleted.setProcess(process.getId());
                workCompleted.setProcessAlias(process.getAlias());
                workCompleted.setProcessName(process.getName());
                workCompleted.setSerial(wi.getSerial());
                workCompleted.setStartTime((wi.getStartTime() == null) ? now : wi.getStartTime());
                workCompleted.setTitle(wi.getTitle());
                workCompleted.setWork(null);
                emc.persist(workCompleted, CheckPersistType.all);
                data = gson.fromJson(wi.getData(), Data.class);
                data.setWork(workCompleted);
                DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
                List<Item> adds = converter.disassemble(gson.toJsonTree(data));
                for (Item o : adds) {
                    fill(o, workCompleted);
                    business.entityManagerContainer().persist(o);
                }
                emc.commit();

                Wo wo = new Wo();
                wo.setId(workCompleted.getId());
                result.setData(wo);

            }

            // projection
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                this.projection(business, workCompleted.getJob(), data, process);
            }

            return result;
        }

        private void projection(Business business, String job, Data data, Process process) throws Exception {
            List<Projection> projections = listProjections(process);
            if (ListTools.isNotEmpty(projections)) {
                projection(business, job, data, projections);
            }
        }

        private void projection(Business business, String job, Data data, List<Projection> projections)
                throws Exception {
            EntityManagerContainer emc = business.entityManagerContainer();
            emc.beginTransaction(WorkCompleted.class);
            emc.beginTransaction(TaskCompleted.class);
            emc.beginTransaction(Read.class);
            emc.beginTransaction(ReadCompleted.class);
            emc.beginTransaction(Review.class);
            for (WorkCompleted o : emc.listEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job)) {
                ProjectionFactory.projectionWorkCompleted(projections, data, o);
            }
            for (TaskCompleted o : emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job)) {
                ProjectionFactory.projectionTaskCompleted(projections, data, o);
            }
            for (Read o : emc.listEqual(Read.class, Read.job_FIELDNAME, job)) {
                ProjectionFactory.projectionRead(projections, data, o);
            }
            for (ReadCompleted o : emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job)) {
                ProjectionFactory.projectionReadCompleted(projections, data, o);
            }
            for (Review o : emc.listEqual(Review.class, Review.job_FIELDNAME, job)) {
                ProjectionFactory.projectionReview(projections, data, o);
            }
            emc.commit();
        }

        private List<Projection> listProjections(Process process) {
            List<Projection> list = new ArrayList<>();
            String text = process.getProjection();
            if (XGsonBuilder.isJsonArray(text)) {
                list = XGsonBuilder.instance().fromJson(text, new TypeToken<List<Projection>>() {
                }.getType());
            }
            return list;
        }

        private void fill(Item o, WorkCompleted workCompleted) {
            /** 将DateItem与Work放在同一个分区 */
            o.setDistributeFactor(workCompleted.getDistributeFactor());
            o.setBundle(workCompleted.getJob());
            o.setItemCategory(ItemCategory.pp);
        }
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -8304167991098790325L;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 7817819508116690487L;

        @FieldDescribe("标题.")
        @Schema(description = "标题.")
        private String title;

        @FieldDescribe("序号.")
        @Schema(description = "序号.")
        private String serial;

        @FieldDescribe("指定表单.")
        @Schema(description = "指定表单.")
        private String form;

        @FieldDescribe("启动人员身份.")
        @Schema(description = "启动人员身份.")
        private String identity;

        @FieldDescribe("开始日期.")
        @Schema(description = "开始日期.")
        private Date startTime;

        @FieldDescribe("结束日期.")
        @Schema(description = "结束日期.")
        private Date completedTime;

        @FieldDescribe("工作数据.")
        @Schema(description = "工作数据.")
        private JsonElement data;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public JsonElement getData() {
            return data;
        }

        public void setData(JsonElement data) {
            this.data = data;
        }

        public Date getCompletedTime() {
            return completedTime;
        }

        public void setCompletedTime(Date completedTime) {
            this.completedTime = completedTime;
        }

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

    }

}
