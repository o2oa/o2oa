package com.x.message.assemble.communicate.jaxrs.im;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_GROUP;
import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;

import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.x_jpush_assemble_control;
import com.x.base.core.project.x_program_center;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMConversation;

public class ActionConversationCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            IMConversation conversation = this.convertToWrapIn(jsonElement, IMConversation.class);
            if (conversation.getType() == null || conversation.getType().isEmpty()
                || !(conversation.getType().equals(CONVERSATION_TYPE_SINGLE)
                     || conversation.getType().equals(CONVERSATION_TYPE_GROUP))) {
                throw new ExceptionConversationTypeError();
            }
            if (conversation.getPersonList() == null || conversation.getPersonList().isEmpty()) {
                throw new ExceptionEmptyMember();
            }
            if (!conversation.getPersonList().contains(effectivePerson.getDistinguishedName())) {
                List<String> list = conversation.getPersonList();
                list.add(effectivePerson.getDistinguishedName());
                conversation.setPersonList(list);
            }
            if ((conversation.getType().equals(CONVERSATION_TYPE_GROUP)
                 && conversation.getPersonList().size() < 3)
                || (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)
                    && conversation.getPersonList().size() != 2)) {
                throw new ExceptionGroupConversationEmptyMember();
            }
            // 单聊 判断会话是否存在
            if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                Business business = new Business(emc);
                List<IMConversation> list = business.imConversationFactory()
                        .listConversationWithPerson(effectivePerson.getDistinguishedName());
                if (list != null && !list.isEmpty()) {
                    for (IMConversation c : list) {
                        if (ListTools.isSameList(c.getPersonList(), conversation.getPersonList())) {
                            ActionResult<Wo> result = new ActionResult<>();
                            Wo wo = Wo.copier.copy(c);
                            result.setData(wo);
                            return result;
                        }
                    }
                }
            }
            // 群聊添加管理员
            if (conversation.getType().equals(CONVERSATION_TYPE_GROUP)) {
                conversation.setAdminPerson(effectivePerson.getDistinguishedName());
            }
            // 处理标题
            if (StringUtils.isEmpty(conversation.getTitle())) {
                String title = "";
                if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) {
                    for (int i = 0; i < conversation.getPersonList().size(); i++) {
                        String person = conversation.getPersonList().get(i);
                        if (!effectivePerson.getDistinguishedName().equals(person)) {
                            title = person.substring(0, person.indexOf("@"));
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < conversation.getPersonList().size(); i++) {
                        String person = conversation.getPersonList().get(i);
                        if (i == 3) {
                            title += person.substring(0, person.indexOf("@")) + "...";
                            break;
                        } else {
                            title += person.substring(0, person.indexOf("@")) + "、";
                        }
                    }
                    if (title.endsWith("、")) {
                        title = title.substring(0, title.length() - 1);
                    }
                }
                conversation.setTitle(title);
            }
            // 处理业务对象
            if (StringUtils.isNotEmpty(conversation.getBusinessId())) {
                if (StringUtils.isEmpty(conversation.getBusinessType())
                    || !conversation.getBusinessType()
                        .equals(IMConversation.CONVERSATION_BUSINESS_TYPE_PROCESS)) {
                    throw new ExceptionEmptyBusinessType();
                }
                // 当前只有流程
                FindByJobIdWo jobIdWo = getProcessWork(conversation.getBusinessId());
                if (jobIdWo == null) {
                    throw new ExceptionEmptyBusinessObject(conversation.getBusinessId());
                }
                String businessBody = "";
                if (jobIdWo.getWorkList() != null && !jobIdWo.getWorkList().isEmpty()) {
                    WorkWo workWo = jobIdWo.getWorkList().get(0);
                    businessBody = workWo.toString();
                }
                if (StringUtils.isEmpty(businessBody)) {
                    if (jobIdWo.getWorkCompletedList() != null && !jobIdWo.getWorkCompletedList()
                            .isEmpty()) {
                        WorkCompletedWo workWo = jobIdWo.getWorkCompletedList().get(0);
                        businessBody = workWo.toString();
                    }
                }
                if (StringUtils.isEmpty(businessBody)) {
                    throw new ExceptionEmptyBusinessObject(conversation.getBusinessId());
                }
                conversation.setBusinessBody(businessBody);
            }

            ConversationInvokeValue value = checkConversationInvoke(effectivePerson, "create",
                    conversation.getType(), conversation.getPersonList(), null, null, null);
            if (BooleanUtils.isFalse(value.getResult())) {
                LOGGER.warn("没有通过脚本校验, {} ", value.toString());
                throw new ExceptionConversationCheckError(
                        value.getMsg() == null ? "脚本校验不通过" : value.getMsg());
            }

            emc.beginTransaction(IMConversation.class);
            emc.persist(conversation, CheckPersistType.all);
            emc.commit();

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = Wo.copier.copy(conversation);
            result.setData(wo);
            return result;
        }
    }

    /**
     * 根据jobId查询工作
     *
     * @param jobId
     * @return
     * @throws Exception
     */
    private FindByJobIdWo getProcessWork(String jobId) throws Exception {
        Application process = ThisApplication.context().applications()
                .randomWithWeight(x_processplatform_assemble_surface.class.getName());
        return ThisApplication.context().applications()
                .getQuery(process, "job/" + jobId + "/find/work/workcompleted")
                .getData(FindByJobIdWo.class);
    }

    public static class Wo extends IMConversation {

        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);
    }

    public static class FindByJobIdWo extends GsonPropertyObject {

        private static final long serialVersionUID = 3066257383780754041L;

        private List<WorkWo> workList;
        private List<WorkCompletedWo> workCompletedList;

        public List<WorkWo> getWorkList() {
            return workList;
        }

        public void setWorkList(List<WorkWo> workList) {
            this.workList = workList;
        }

        public List<WorkCompletedWo> getWorkCompletedList() {
            return workCompletedList;
        }

        public void setWorkCompletedList(List<WorkCompletedWo> workCompletedList) {
            this.workCompletedList = workCompletedList;
        }
    }

    public static class WorkWo extends GsonPropertyObject {

        private static final long serialVersionUID = 8302245715651493780L;

        private String id;
        private String job;
        private String title;
        private String startTime;
        private String startTimeMonth;
        private String creatorPerson;
        private String creatorIdentity;
        private String creatorUnit;
        private String application;
        private String applicationName;
        private String applicationAlias;
        private String process;
        private String processName;
        private String processAlias;
        private String activity;
        private String activityType;
        private String activityName;
        private String activityAlias;
        private String activityDescription;
        private String activityToken;
        private String activityArrivedTime;
        private String serial;
        private boolean dataChanged;
        private boolean workThroughManual;
        private String workCreateType;
        private String workStatus;
        private boolean beforeExecuted;
        private String manualTaskIdentityText;
        private boolean splitting;
        private String form;
        private String destinationRoute;
        private String destinationRouteName;
        private String destinationActivityType;
        private String destinationActivity;
        private String createTime;
        private String updateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStartTimeMonth() {
            return startTimeMonth;
        }

        public void setStartTimeMonth(String startTimeMonth) {
            this.startTimeMonth = startTimeMonth;
        }

        public String getCreatorPerson() {
            return creatorPerson;
        }

        public void setCreatorPerson(String creatorPerson) {
            this.creatorPerson = creatorPerson;
        }

        public String getCreatorIdentity() {
            return creatorIdentity;
        }

        public void setCreatorIdentity(String creatorIdentity) {
            this.creatorIdentity = creatorIdentity;
        }

        public String getCreatorUnit() {
            return creatorUnit;
        }

        public void setCreatorUnit(String creatorUnit) {
            this.creatorUnit = creatorUnit;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getApplicationAlias() {
            return applicationAlias;
        }

        public void setApplicationAlias(String applicationAlias) {
            this.applicationAlias = applicationAlias;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public String getProcessAlias() {
            return processAlias;
        }

        public void setProcessAlias(String processAlias) {
            this.processAlias = processAlias;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public String getActivityType() {
            return activityType;
        }

        public void setActivityType(String activityType) {
            this.activityType = activityType;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getActivityAlias() {
            return activityAlias;
        }

        public void setActivityAlias(String activityAlias) {
            this.activityAlias = activityAlias;
        }

        public String getActivityDescription() {
            return activityDescription;
        }

        public void setActivityDescription(String activityDescription) {
            this.activityDescription = activityDescription;
        }

        public String getActivityToken() {
            return activityToken;
        }

        public void setActivityToken(String activityToken) {
            this.activityToken = activityToken;
        }

        public String getActivityArrivedTime() {
            return activityArrivedTime;
        }

        public void setActivityArrivedTime(String activityArrivedTime) {
            this.activityArrivedTime = activityArrivedTime;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public boolean isDataChanged() {
            return dataChanged;
        }

        public void setDataChanged(boolean dataChanged) {
            this.dataChanged = dataChanged;
        }

        public boolean isWorkThroughManual() {
            return workThroughManual;
        }

        public void setWorkThroughManual(boolean workThroughManual) {
            this.workThroughManual = workThroughManual;
        }

        public String getWorkCreateType() {
            return workCreateType;
        }

        public void setWorkCreateType(String workCreateType) {
            this.workCreateType = workCreateType;
        }

        public String getWorkStatus() {
            return workStatus;
        }

        public void setWorkStatus(String workStatus) {
            this.workStatus = workStatus;
        }

        public boolean isBeforeExecuted() {
            return beforeExecuted;
        }

        public void setBeforeExecuted(boolean beforeExecuted) {
            this.beforeExecuted = beforeExecuted;
        }

        public String getManualTaskIdentityText() {
            return manualTaskIdentityText;
        }

        public void setManualTaskIdentityText(String manualTaskIdentityText) {
            this.manualTaskIdentityText = manualTaskIdentityText;
        }

        public boolean isSplitting() {
            return splitting;
        }

        public void setSplitting(boolean splitting) {
            this.splitting = splitting;
        }

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public String getDestinationRoute() {
            return destinationRoute;
        }

        public void setDestinationRoute(String destinationRoute) {
            this.destinationRoute = destinationRoute;
        }

        public String getDestinationRouteName() {
            return destinationRouteName;
        }

        public void setDestinationRouteName(String destinationRouteName) {
            this.destinationRouteName = destinationRouteName;
        }

        public String getDestinationActivityType() {
            return destinationActivityType;
        }

        public void setDestinationActivityType(String destinationActivityType) {
            this.destinationActivityType = destinationActivityType;
        }

        public String getDestinationActivity() {
            return destinationActivity;
        }

        public void setDestinationActivity(String destinationActivity) {
            this.destinationActivity = destinationActivity;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class WorkCompletedWo extends GsonPropertyObject {

        private static final long serialVersionUID = -1892810977031482330L;

        private String id;
        private String job;
        private String title;
        private String startTime;
        private String startTimeMonth;
        private String creatorPerson;
        private String creatorIdentity;
        private String creatorUnit;
        private String application;
        private String applicationName;
        private String applicationAlias;
        private String process;
        private String processName;
        private String processAlias;
        private String serial;
        private String form;
        private String createTime;
        private String updateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStartTimeMonth() {
            return startTimeMonth;
        }

        public void setStartTimeMonth(String startTimeMonth) {
            this.startTimeMonth = startTimeMonth;
        }

        public String getCreatorPerson() {
            return creatorPerson;
        }

        public void setCreatorPerson(String creatorPerson) {
            this.creatorPerson = creatorPerson;
        }

        public String getCreatorIdentity() {
            return creatorIdentity;
        }

        public void setCreatorIdentity(String creatorIdentity) {
            this.creatorIdentity = creatorIdentity;
        }

        public String getCreatorUnit() {
            return creatorUnit;
        }

        public void setCreatorUnit(String creatorUnit) {
            this.creatorUnit = creatorUnit;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getApplicationAlias() {
            return applicationAlias;
        }

        public void setApplicationAlias(String applicationAlias) {
            this.applicationAlias = applicationAlias;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public String getProcessAlias() {
            return processAlias;
        }

        public void setProcessAlias(String processAlias) {
            this.processAlias = processAlias;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
